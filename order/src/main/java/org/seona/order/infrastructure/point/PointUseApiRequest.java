package org.seona.order.infrastructure.point;

public record PointUseApiRequest(
        String requestId,
        Long userId,
        Long amount
) {
}
