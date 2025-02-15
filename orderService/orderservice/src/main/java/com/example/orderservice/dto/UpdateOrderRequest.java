package com.example.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Класс `UpdateOrderRequest` представляет данные для обновления адреса доставки в существующем заказе.
 *
 * Этот DTO используется для передачи нового адреса доставки при обновлении информации о заказе.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос для обновления адреса доставки в существующем заказе")
public class UpdateOrderRequest {

    /**
     * Новый адрес доставки.
     *
     * Этот параметр обязателен и должен содержать от 5 до 255 символов.
     * Используется для обновления адреса доставки в заказе.
     */
    @NotBlank(message = "Адрес доставки обязателен.")
    @Size(min = 5, max = 255, message = "Адрес должен содержать от 5 до 255 символов.")
    @Schema(description = "Новый адрес доставки", example = "ул. Пушкина, д. 10, кв. 5", minLength = 5, maxLength = 255)
    private String deliveryAddress;
}
