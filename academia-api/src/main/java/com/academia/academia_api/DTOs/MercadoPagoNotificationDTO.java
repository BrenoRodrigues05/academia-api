package com.academia.academia_api.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MercadoPagoNotificationDTO(
        String action,
        @JsonProperty("api_version") String apiVersion,
        DataNotification data,
        String type
) {
    public record DataNotification(String id){}
}
