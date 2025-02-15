package com.example.orderservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Класс `Order` представляет сущность заказа в системе.
 * Этот класс управляет данными о заказе, включая адрес доставки,
 * список товаров и дату создания. Он также обеспечивает связь с сущностью `OrderItem`.
 */
@Entity
@Table(name = "orders") // "order" - зарезервированное слово в PostgreSQL
@Data // Генерирует геттеры, сеттеры, toString, equals и hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Информация о заказе")
@EntityListeners(AuditingEntityListener.class)  // Нужен для @CreatedDate
public class Order {

    /**
     * Уникальный идентификатор заказа в формате UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    @Schema(description = "Идентификатор заказа", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    /**
     * Адрес доставки для данного заказа.
     */
    @Column(nullable = false, length = 255)
    @NotBlank(message = "Адрес доставки обязателен")
    @Size(min = 5, max = 255, message = "Адрес должен содержать от 5 до 255 символов")
    @Schema(description = "Адрес доставки", example = "Москва, ул. Ленина, д. 10, кв. 5")
    private String deliveryAddress;

    /**
     * Список товаров, включённых в данный заказ.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid // Валидируем вложенные объекты OrderItem
    @Schema(description = "Список товаров в заказе",
            example = "[{ \"productId\": \"123e4567-e89b-12d3-a456-426614174001\", \"quantity\": 2, \"price\": 1500.00 }]")
    private List<OrderItem> orderItems;

    /**
     * Дата и время создания заказа.
     */
    @CreatedDate
    @Column(updatable = false, nullable = false)
    @Schema(description = "Дата создания заказа", example = "2024-05-01T12:34:56Z")
    private Instant createdAt;

    /**
     * Добавить товар в заказ.
     *
     * @param orderItem Товар для добавления в заказ.
     */
    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
}


