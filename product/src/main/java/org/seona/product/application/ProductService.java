package org.seona.product.application;

import org.seona.product.application.dto.ProductBuyCancelCommand;
import org.seona.product.application.dto.ProductBuyCancelResult;
import org.seona.product.application.dto.ProductBuyCommand;
import org.seona.product.application.dto.ProductBuyResult;
import org.seona.product.domain.Product;
import org.seona.product.domain.ProductTransactionHistory;
import org.seona.product.infrastructure.ProductRepository;
import org.seona.product.infrastructure.ProductTransactionHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductTransactionHistoryRepository productTransactionHistoryRepository;

    public ProductService(ProductRepository productRepository, ProductTransactionHistoryRepository productTransactionHistoryRepository) {
        this.productRepository = productRepository;
        this.productTransactionHistoryRepository = productTransactionHistoryRepository;
    }

    @Transactional
    public ProductBuyResult buy(ProductBuyCommand command) {
        // 구매 이력 조회
        List<ProductTransactionHistory> histories = productTransactionHistoryRepository.findAllByRequestIdAndTransactionType(command.requestId(), ProductTransactionHistory.TransactionType.PURCHASE);

        if (!histories.isEmpty()) {
            System.out.println("이미 구매한 이력이 있습니다. ");

            Long totalPrice = histories.stream()
                    .mapToLong(ProductTransactionHistory::getPrice)
                    .sum();

            return new ProductBuyResult(totalPrice);
        }

        // 총 구매금액 반환
        Long totalPrice = 0L;

        // 구매 이력 없을 경우 구매 진행
        List<ProductTransactionHistory> newHistories = new ArrayList<>();
        for (ProductBuyCommand.ProductInfo productInfo : command.productInfos()) {
            Product product = productRepository.findById(productInfo.productId()).orElseThrow(() -> new RuntimeException("Product not found"));

            product.buy(productInfo.quantity());
            Long price = product.calculatePrice(productInfo.quantity());
            totalPrice += price;

            newHistories.add(new ProductTransactionHistory(command.requestId(), productInfo.productId(), productInfo.quantity(), price, ProductTransactionHistory.TransactionType.PURCHASE));
        }
        productTransactionHistoryRepository.saveAll(newHistories);

        return new ProductBuyResult(totalPrice);
    }

    @Transactional
    public ProductBuyCancelResult cancel(ProductBuyCancelCommand command) {
        List<ProductTransactionHistory> buyHistories = productTransactionHistoryRepository.findAllByRequestIdAndTransactionType(command.requestId(), ProductTransactionHistory.TransactionType.PURCHASE);

        if (buyHistories.isEmpty()) {
            throw new RuntimeException("구매 이력이 존재하지 않습니다.");
        }

        List<ProductTransactionHistory> cancelHistories = productTransactionHistoryRepository.findAllByRequestIdAndTransactionType(command.requestId(), ProductTransactionHistory.TransactionType.CANCEL);
        if (!cancelHistories.isEmpty()) {
            System.out.println("이미 취소되었습니다.");
            Long totalPrice = cancelHistories.stream()
                    .mapToLong(ProductTransactionHistory::getPrice)
                    .sum();

            return new ProductBuyCancelResult(totalPrice);
        }

        Long totalPrice = 0L;

        // 취소 이력이 없다면 취소를 진행하고 총 구매금액 반환
        for (ProductTransactionHistory history : buyHistories) {
            Product product = productRepository.findById(history.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));

            product.cancel(history.getQuantity());
            totalPrice += history.getPrice();

            productTransactionHistoryRepository.save(
                    new ProductTransactionHistory(
                            command.requestId(),
                            history.getProductId(),
                            history.getQuantity(),
                            history.getPrice(),
                            ProductTransactionHistory.TransactionType.CANCEL
                    )
            );
        }

        return new ProductBuyCancelResult(totalPrice);
    }
}
