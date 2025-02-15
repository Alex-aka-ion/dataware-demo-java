package com.example.orderservice.controller;

import com.example.orderservice.dto.*;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderItem;
import com.example.orderservice.repository.OrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Контроллер для управления заказами.
 * Предоставляет функциональность для создания, получения, обновления и удаления заказов.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
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

        RestTemplate restTemplate = new RestTemplate();
        //String productServiceUrl = "http://product-service/api/products/"; // URL для запроса продукта по ID
        String productServiceUrl = "http://localhost:8081/api/products/"; // URL для запроса продукта по ID

        for (OrderItemRequest itemRequest : orderRequest.getProducts()) {
            UUID productId = UUID.fromString(itemRequest.getProductId());
            Integer quantity = itemRequest.getQuantity();

            try {
                // Запрашиваем информацию о продукте по его ID
                ProductResponse productResponse = restTemplate.getForObject(
                        productServiceUrl + productId, // Формируем URL: http://product-service/api/products/{id}
                        ProductResponse.class
                );

                if (productResponse == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Продукт с ID " + productId + " не найден в ProductService");
                }

                // Создаём `OrderItem` с проверенной ценой
                OrderItem item = new OrderItem();
                item.setProductId(productId);
                item.setQuantity(quantity);
                item.setPrice(productResponse.getPrice());
                item.setOrder(order);

                order.addOrderItem(item);

            } catch (HttpClientErrorException.NotFound e) {
                log.error("Продукт не найден: {}", productId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Продукт с ID " + productId + " не найден в ProductService");
            } catch (HttpServerErrorException e) {
                log.error("Ошибка сервера ProductService: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Ошибка на стороне ProductService");
            } catch (ResourceAccessException e) {
                log.error("ProductService недоступен: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("ProductService недоступен");
            }
//            catch (Exception e) {
//                log.error("Неожиданная ошибка: {}", e.getMessage());
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body("Неожиданная ошибка при запросе к ProductService");
//            }
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

        List<Order> orders = orderRepository.findByProductId(UUID.fromString(productId));

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

