package org.seona.point.controller.dto;

import org.seona.point.application.dto.PointUseCommand;

public record PointUseRequest(
        String requestId,
        Long userId,
        Long amount
) {

    public PointUseCommand toCommand() {
        return new PointUseCommand(requestId, userId, amount);
    }
}
