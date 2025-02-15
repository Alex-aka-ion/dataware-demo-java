package com.example.orderservice.controller;

import com.example.orderservice.dto.*;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderItem;
import com.example.orderservice.repository.OrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Контроллер для управления заказами.
 *
 * Предоставляет функциональность для создания, получения, обновления и удаления заказов.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;

    /**
     * Получить список всех заказов.
     *
     * @return ResponseEntity Список заказов в формате JSON.
     */
    @Operation(summary = "Получить список всех заказов")
    @ApiResponse(responseCode = "200", description = "Список заказов",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Order.class)))
    @GetMapping
    public ResponseEntity<List<Order>> getOrders() {
        List<Order> orders = orderRepository.findAll();
        return ResponseEntity.ok(orders);
    }

    /**
     * Создать новый заказ.
     *
     * @param orderRequest Данные заказа.
     * @param bindingResult Объект для проверки ошибок валидации.
     * @return ResponseEntity Результат создания заказа.
     */
    @Operation(summary = "Создать новый заказ")
    @ApiResponse(responseCode = "201", description = "Заказ создан успешно")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации данных")
    @PostMapping
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody OrderRequest orderRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        Order order = Order.builder()
                .deliveryAddress(orderRequest.getDeliveryAddress())
                .build();

        for (OrderItemRequest itemRequest : orderRequest.getProducts()) {
            OrderItem item = OrderItem.builder()
                    .productId(UUID.fromString(itemRequest.getProductId()))
                    .quantity(itemRequest.getQuantity())
                    .order(order)
                    .build();
            order.getOrderItems().add(item);
        }

        orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /**
     * Найти заказы по ID продукта.
     *
     * @param productId Идентификатор продукта, передаваемый в запросе.
     * @return ResponseEntity Список найденных заказов или ошибка.
     */
    @Operation(summary = "Найти заказы по ID товара")
    @ApiResponse(responseCode = "200", description = "Список заказов")
    @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    @ApiResponse(responseCode = "404", description = "Заказы не найдены")
    @GetMapping("/search")
    public ResponseEntity<?> searchByProductId(
            @RequestParam(name = "productId") String productId) {

        List<Order> orders = orderRepository.findByProductId(productId);

        if (orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Заказы не найдены");
        }
        return ResponseEntity.ok(orders);
    }


    /**
     * Получить заказ по его ID.
     *
     * @param id Идентификатор заказа.
     * @return ResponseEntity Информация о заказе или ошибка, если не найден.
     */
    @Operation(summary = "Получить заказ по ID")
    @ApiResponse(responseCode = "200", description = "Информация о заказе")
    @ApiResponse(responseCode = "404", description = "Заказ не найден")
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable UUID id) {
        Optional<Order> order = orderRepository.findById(id);

        if (order.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Заказ не найден");
        }
        return ResponseEntity.ok(order.get());
    }

    /**
     * Обновить адрес доставки для существующего заказа.
     *
     * @param id Идентификатор заказа.
     * @param updateRequest Данные нового адреса.
     * @param bindingResult Объект для проверки ошибок валидации.
     * @return ResponseEntity Результат обновления заказа.
     */
    @Operation(summary = "Обновить адрес доставки")
    @ApiResponse(responseCode = "200", description = "Адрес доставки успешно обновлен")
    @ApiResponse(responseCode = "404", description = "Заказ не найден")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderRequest updateRequest,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Заказ не найден");
        }

        Order order = optionalOrder.get();
        order.setDeliveryAddress(updateRequest.getDeliveryAddress());
        orderRepository.save(order);

        return ResponseEntity.ok("Адрес доставки успешно обновлен");
    }

    /**
     * Удалить заказ по его ID.
     *
     * @param id Идентификатор заказа.
     * @return ResponseEntity Результат удаления заказа.
     */
    @Operation(summary = "Удалить заказ")
    @ApiResponse(responseCode = "204", description = "Заказ удален успешно")
    @ApiResponse(responseCode = "404", description = "Заказ не найден")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable UUID id) {
        Optional<Order> order = orderRepository.findById(id);

        if (order.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Заказ не найден");
        }

        orderRepository.delete(order.get());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

