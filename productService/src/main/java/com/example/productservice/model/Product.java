package com.example.productservice.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "products")
@Schema(description = "Модель продукта для системы управления товарами.")
@Data // Генерирует геттеры, сеттеры, toString, equals и hashCode
@NoArgsConstructor // Генерирует конструктор без параметров
@AllArgsConstructor // Генерирует конструктор со всеми параметрами
@EntityListeners(AuditingEntityListener.class)  // Нужен для @CreatedDate
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Hibernate 6+ поддерживает UUID напрямую
    @Column(updatable = false, nullable = false)
    @Schema(description = "Уникальный идентификатор продукта (UUID).", type = "string", format = "uuid")
    private String id;

    @NotBlank(message = "Название не может быть пустым")
    @Size(min = 3, max = 255, message = "Название должно быть от 3 до 255 символов")
    @Schema(description = "Название продукта.", example = "Ноутбук ASUS", maxLength = 255)
    @Column(nullable = false)
    private String name;

    @Size(max = 1000, message = "Описание не может быть длиннее 1000 символов")
    @Schema(description = "Описание продукта.", example = "Мощный игровой ноутбук.")
    @Column(nullable = true)
    private String description;

    @Positive(message = "Цена должна быть положительным числом")
    @Column(nullable = false)
    @Max(value = 100000000, message = "Цена не может превышать 100 000 000")
    @Schema(description = "Цена продукта в копейках.", example = "1499.99", minimum = "0")
    private Integer price; // Хранится в копейках

    @Column(columnDefinition = "json")
    @Schema(description = "Список категорий продукта в формате JSON.", example = "[\"Электроника\", \"Компьютеры\"]")
    private String categories;  // Храним JSON-строку

    @CreatedDate
    @Column(updatable = false, nullable = false)
    @Schema(description = "Дата создания продукта.", example = "2024-05-01T12:34:56Z")
    private Instant createdAt;

    public float getPrice() {
        return price != null ? price / 100f : 0; // Возвращает цену в формате с плавающей точкой
    }

    public void setPrice(float price) {
        this.price = (int) (price * 100); // Устанавливает цену в копейках
    }

    public List<String> getCategories() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(this.categories, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Метод для преобразования List<String> в JSON строку перед сохранением
    public void setCategories(List<String> categories) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.categories = objectMapper.writeValueAsString(categories);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
