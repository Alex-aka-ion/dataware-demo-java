package com.example.orderservice.dto;

import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO `ProductResponse` используется для получения информации о товаре из `ProductService`.
 */
@Getter
@Setter
@Schema(description = "Ответ от ProductService с информацией о товаре")
public class ProductResponse {

    @Schema(description = "UUID идентификатор товара", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Название товара", example = "Ноутбук ASUS")
    private String name;

    @Schema(description = "Описание товара", example = "Мощный игровой ноутбук.")
    private String description;

    @Schema(description = "Цена товара в копейках", example = "1499.99")
    private Float price; // Цена в копейках
}
