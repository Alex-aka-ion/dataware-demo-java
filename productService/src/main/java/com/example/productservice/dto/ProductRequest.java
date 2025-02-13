package com.example.productservice.dto;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Класс ProductRequest предназначен для передачи данных при создании или обновлении продукта.
 * Этот DTO (Data Transfer Object) используется для валидации данных, поступающих от клиента.
 * Он содержит основную информацию о продукте, такую как название, описание, цена и категории.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    title = "ProductRequest",
    description = "Запрос для создания или обновления продукта",
    requiredProperties = {"name", "price", "categories"}
)
public class ProductRequest {

    /**
     * Название продукта.
     */
    @NotBlank(message = "Название продукта обязательно.")
    @Size(min = 3, max = 255, message = "Название продукта должно содержать от 3 до 255 символов.")
    @Schema(
        description = "Название продукта",
        maxLength = 255,
        minLength = 3,
        example = "Ноутбук ASUS"
    )
    private String name;

    /**
     * Описание продукта (необязательное поле).
     */
    @Size(max = 1000, message = "Описание не может превышать 1000 символов.")
    @Schema(
        description = "Описание продукта",
        maxLength = 1000,
        example = "Мощный игровой ноутбук."
    )
    private String description;

    /**
     * Цена продукта.
     */
    @NotNull(message = "Цена продукта обязательна.")
    @Positive(message = "Цена должна быть положительным числом.")
    @Max(value = 100000000, message = "Цена не может превышать 100 000 000")
    @Schema(
        description = "Цена продукта",
        minimum = "0",
        example = "1499.99"
    )
    private Float price;

    /**
     * Список категорий продукта.
     */
    @NotNull(message = "Категории обязательны.")
    @Size(min = 1, message = "Необходимо указать хотя бы одну категорию.")
    @Schema(
        description = "Список категорий продукта",
        example = "[\"Электроника\", \"Компьютеры\"]"
    )
    private List<@NotBlank @Size(max = 100, message = "Категория не может быть длиннее 100 символов") String> categories;
}
