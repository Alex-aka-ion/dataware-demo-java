package com.example.orderservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

/**
 * Класс `OrderItem` представляет товар в составе заказа.
 * Этот класс управляет данными о товаре, связанном с заказом, включая:
 * - Идентификатор заказа
 * - Идентификатор товара (UUID)
 * - Количество товара
 * - Цена товара на момент оформления заказа
 * Валидация данных осуществляется с использованием групп "OrderItem" и "StrictValidation".
 */
@Entity
@Table(name = "order_items")
@Data // Генерирует геттеры, сеттеры, toString, equals и hashCode
@NoArgsConstructor // Генерирует конструктор без параметров
@AllArgsConstructor // Генерирует конструктор со всеми параметрами
@Builder
@Schema(description = "Информация о товаре в заказе")
public class OrderItem {

    /**
     * Уникальный идентификатор элемента заказа (UUID).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    @Schema(description = "Идентификатор элемента заказа", example = "123e4567-e89b-12d3-a456-426614174002")
    private UUID id;

    /**
     * Ссылка на заказ, к которому относится товар.
     */
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_item_order", value = ConstraintMode.CONSTRAINT))
    @Schema(description = "Связанный заказ")
    private Order order;

    /**
     * UUID идентификатор товара.
     */
    @Column(nullable = false)
    @NotBlank(message = "Идентификатор товара обязателен")
    @Schema(description = "UUID идентификатор товара", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID productId;

    /**
     * Количество товара в заказе.
     */
    @Column(nullable = false)
    @Positive(message = "Количество должно быть больше 0")
    @Schema(description = "Количество товара", example = "2", minimum = "1")
    private Integer quantity;

    /**
     * Цена товара в момент оформления заказа (в копейках).
     * Для хранения используется целое число для избежания ошибок округления.
     * При выводе делится на 100 для получения значения в рублях.
     */
    @Column(nullable = false)
    @Positive(message = "Цена должна быть положительным числом")
    @Schema(description = "Цена товара в момент заказа", example = "1500.00")
    private Integer price; // Цена в момент заказа (в копейках)

    public float getPrice() {
        return price != null ? price / 100f : 0; // Возвращает цену в формате с плавающей точкой
    }

    public void setPrice(float price) {
        this.price = (int) (price * 100); // Устанавливает цену в копейках
    }
}

