package org.seona.point.controller.dto;

import org.seona.point.application.dto.PointUseCancelCommand;

public record PointUseCancelRequest(String requestId) {

    public PointUseCancelCommand toCommand() {
        return new PointUseCancelCommand(requestId);
    }
}
