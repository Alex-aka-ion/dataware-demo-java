package com.example.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Класс `OrderItemRequest` представляет данные для добавления товара в заказ.
 * Этот DTO используется для передачи данных при создании или обновлении заказа,
 * включая идентификатор товара и количество.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос для добавления товара в заказ")
public class OrderItemRequest {

    /**
     * Идентификатор товара (UUID).
     * Поле обязательно для заполнения и должно быть корректным UUID.
     */
    @NotNull(message = "Идентификатор товара обязателен.")
    @Schema(description = "Идентификатор товара (UUID)", example = "123e4567-e89b-12d3-a456-426614174001")
    private String productId;

    /**
     * Количество товара.
     * Поле обязательно для заполнения и должно быть положительным целым числом.
     */
    @NotNull(message = "Количество товара обязательно.")
    @Positive(message = "Количество товара должно быть больше 0.")
    @Schema(description = "Количество товара", example = "2", minimum = "1")
    private Integer quantity;
}

