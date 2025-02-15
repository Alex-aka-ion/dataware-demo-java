package com.example.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

/**
 * Класс `SearchByProductIdRequest` представляет данные для поиска заказов по идентификатору продукта.
 *
 * Этот DTO используется для передачи параметра `productId` при выполнении запроса
 * на поиск заказов, содержащих указанный продукт.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Запрос для поиска заказов, содержащих определенный продукт")
public class SearchByProductIdRequest {

    /**
     * UUID идентификатор продукта.
     *
     * Обязательный параметр для поиска заказов. Должен быть строкой в формате UUID.
     */
    @NotNull(message = "Параметр productId обязателен.")
    @Schema(description = "UUID идентификатор продукта",
            example = "0194bd9a-d8b5-7de7-873d-db4907a13836")
    private String productId;
}

