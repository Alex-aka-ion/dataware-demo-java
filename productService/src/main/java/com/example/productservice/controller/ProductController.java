package com.example.productservice.controller;

import com.example.productservice.dto.ProductRequest;
import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Контроллер для управления продуктами.*
 * Этот контроллер предоставляет REST API для управления продуктами,
 * включая создание, получение, обновление и удаление записей о продуктах.
 */
@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {

    private final ProductRepository productRepository;

    /**
     * Конструктор контроллера продуктов.
     *
     * @param productRepository Репозиторий для работы с сущностью Product.
     */
    @Autowired
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Получение списка всех продуктов.
     *
     * @return ResponseEntity Список продуктов в формате JSON.
     */
    @Operation(summary = "Получить список всех продуктов")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список продуктов")
    })
    @GetMapping
    public ResponseEntity<List<Product>> index() {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }

    /**
     * Поиск продуктов по имени.*
     * Метод выполняет поиск продуктов, имя которых содержит указанную подстроку.
     * Поиск регистронезависимый и упорядочивает результаты в алфавитном порядке.
     *
     * @param name Имя продукта или часть имени для поиска.
     * @return ResponseEntity Результаты поиска или сообщение об ошибке.
     */
    @Operation(summary = "Поиск продуктов по имени")
    @Parameter(name = "name", description = "Имя продукта", required = true)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Результаты поиска"),
        @ApiResponse(responseCode = "400", description = "Ошибка запроса")
    })
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchByName(@RequestParam String name) {
        if (name == null || name.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        List<Product> products = productRepository.findByName(name);
        return ResponseEntity.ok(products);
    }

    /**
     * Получение информации о продукте по его ID.
     *
     * @param id Идентификатор продукта (UUID).
     * @return ResponseEntity Информация о продукте или сообщение об ошибке.
     */
    @Operation(summary = "Получить продукт по ID")
    @Parameter(name = "id", description = "UUID идентификатор продукта", required = true)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Информация о продукте"),
        @ApiResponse(responseCode = "400", description = "Некорректный формат UUID"),
        @ApiResponse(responseCode = "404", description = "Продукт не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> show(@PathVariable String id) {
        if (isNotValidUUID(id)) {
            return ResponseEntity.badRequest().body(null);
        }

        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(product);
    }

    /**
     * Создание нового продукта.
     *
     * @param productRequest HTTP-запрос с данными продукта.
     * @return ResponseEntity Созданный продукт или сообщение об ошибке.
     */
    @Operation(summary = "Создать новый продукт")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Продукт успешно создан"),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации данных")
    })
    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody ProductRequest productRequest) throws JsonProcessingException {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCategories(productRequest.getCategories());

        productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    /**
     * Обновление данных продукта.
     *
     * @param id Идентификатор продукта (UUID).
     * @param productRequest Обновленные данные продукта.
     * @param bindingResult Объект для проверки ошибок валидации.
     * @return Обновленный продукт или сообщение об ошибке.
     */
    @Operation(summary = "Обновить продукт", description = "Позволяет обновить данные существующего продукта по его UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Продукт успешно обновлен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Продукт не найден",
                    content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @Parameter(description = "UUID идентификатор продукта", required = true) @PathVariable String id,
            @Valid @RequestBody ProductRequest productRequest,
            BindingResult bindingResult) {

        // Проверка корректности UUID
        if (isNotValidUUID(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Некорректный формат UUID");
        }

        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Продукт не найден");
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        Product product = optionalProduct.get();

        // Обновляем только те поля, которые переданы в запросе
        if (productRequest.getName() != null) {
            product.setName(productRequest.getName());
        }
        if (productRequest.getDescription() != null) {
            product.setDescription(productRequest.getDescription());
        }
        if (productRequest.getPrice() != null) {
            product.setPrice(productRequest.getPrice());
        }
        if (productRequest.getCategories() != null) {
            product.setCategories(productRequest.getCategories());
        }

        productRepository.save(product);
        return ResponseEntity.ok(product);
    }

    /**
     * Удаление продукта по его ID.
     *
     * @param id Идентификатор продукта (UUID).
     * @return ResponseEntity Сообщение об успешном удалении или ошибка.
     */
    @Operation(summary = "Удалить продукт")
    @Parameter(name = "id", description = "UUID идентификатор продукта", required = true)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Продукт успешно удалён"),
        @ApiResponse(responseCode = "400", description = "Некорректный формат UUID"),
        @ApiResponse(responseCode = "404", description = "Продукт не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (isNotValidUUID(id)) {
            return ResponseEntity.badRequest().build();
        }

        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        productRepository.delete(product);
        return ResponseEntity.noContent().build();
    }

    private boolean isNotValidUUID(String uuid) {
        try {
            java.util.UUID.fromString(uuid);
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }
}
