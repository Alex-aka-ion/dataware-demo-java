package com.example.orderservice.repository;

import com.example.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью `Order`.
 * Этот интерфейс предоставляет методы для выполнения запросов к базе данных,
 * связанных с сущностью `Order`, используя возможности Spring Data JPA.
 */
public interface OrderRepository extends JpaRepository<Order, UUID> {

    /**
     * Находит заказы по идентификатору продукта.
     * Этот метод выполняет запрос для поиска всех заказов, которые содержат
     * указанный идентификатор продукта в списке товаров заказа.
     *
     * @param productId Идентификатор продукта (UUID).
     * @return Список заказов, содержащих указанный продукт.
     */
    @Query("SELECT o FROM Order o JOIN o.orderItems oi WHERE oi.productId = :productId")
    List<Order> findByProductId(@Param("productId") UUID productId);
}
