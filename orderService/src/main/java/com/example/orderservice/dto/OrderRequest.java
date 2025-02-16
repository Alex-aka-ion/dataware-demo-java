package com.example.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/**
 * Класс `OrderRequest` представляет данные для создания нового заказа.
 * Этот DTO используется для передачи информации о заказе, включая адрес доставки и список товаров.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Данные для создания нового заказа")
public class OrderRequest {

    /**
     * Адрес доставки.
     * Поле обязательно для заполнения и должно быть строкой.
     */
    @NotBlank(message = "Адрес доставки обязателен.")
    @Size(min = 5, max = 255, message = "Адрес должен содержать от 5 до 255 символов.")
    @Schema(description = "Адрес доставки", example = "123 улица, город, страна")
    private String deliveryAddress;

    /**
     * Список товаров в заказе.
     * Должен содержать хотя бы один товар. Каждый товар проверяется на валидность.
     */
    @NotEmpty(message = "Необходимо указать товары в заказе.")
    @Valid // Проверка каждого OrderItemRequest
    @Schema(description = "Список товаров в заказе")
    private List<OrderItemRequest> products;
}

