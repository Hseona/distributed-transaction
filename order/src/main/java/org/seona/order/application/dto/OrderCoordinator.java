package org.seona.order.application.dto;

import org.seona.order.application.OrderService;
import org.seona.order.infrastructure.point.PointApiClient;
import org.seona.order.infrastructure.point.PointUseApiRequest;
import org.seona.order.infrastructure.point.PointUseCancelApiRequest;
import org.seona.order.infrastructure.product.*;
import org.springframework.stereotype.Component;

@Component
public class OrderCoordinator {
    private final OrderService orderService;
    private final ProductApiClient productApiClient;
    private final PointApiClient pointApiClient;

    public OrderCoordinator(OrderService orderService, ProductApiClient productApiClient, PointApiClient pointApiClient) {
        this.orderService = orderService;
        this.productApiClient = productApiClient;
        this.pointApiClient = pointApiClient;
    }

    public void placeOrder(PlaceOrderCommand command) {
        orderService.request(command.orderId());
        OrderDto orderDto = orderService.getOrder(command.orderId());

        try {
            // productApiClient를 활용해서 재고 차감
            ProductBuyApiRequest productBuyApiRequest = new ProductBuyApiRequest(
                    command.orderId().toString(),
                    orderDto.orderItems().stream()
                            .map(item -> new ProductBuyApiRequest.ProductInfo(item.productId(), item.quantity()))
                            .toList()
            );

            // productApiClient로 재고 차감 요청
            ProductBuyApiResponse buyApiResponse = productApiClient.buy(productBuyApiRequest);

            // PointApiClient를 활용해서 포인트 사용 처리, 사용자는 1로 함
            PointUseApiRequest pointUseApiRequest = new PointUseApiRequest(
                    command.orderId().toString(),
                    1L,
                    buyApiResponse.totalPrice()
            );

            pointApiClient.use(pointUseApiRequest);

            // 주문 완료
            orderService.complete(command.orderId());
        } catch (Exception e) {
            // 주문 처리 중 오류 발생 시 보상 트랜잭션 로직 실행
            // 1. 재고 차감 취소
            ProductBuyCancelApiRequest productBuyCancelApiRequest = new ProductBuyCancelApiRequest(command.orderId().toString());
            ProductBuyCancelApiResponse productBuyCancelApiResponse = productApiClient.cancel(productBuyCancelApiRequest);

            // 2. 재고차감 결과의 금액이 0보다 클 경우에만 포인트 취소
            if (productBuyCancelApiResponse.totalPrice() > 0) {
                PointUseCancelApiRequest pointUseCancelApiRequest = new PointUseCancelApiRequest(command.orderId().toString());
                pointApiClient.cancel(pointUseCancelApiRequest);
            }

            // 3. order 상태 fail로 변경
            orderService.fail(command.orderId());
        }
    }
}
