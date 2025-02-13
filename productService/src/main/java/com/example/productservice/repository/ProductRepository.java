package com.example.productservice.repository;

import com.example.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для управления сущностью Product.
 *
 * Этот интерфейс предоставляет методы для взаимодействия с таблицей продуктов в базе данных,
 * включая поиск по имени и другие операции, связанные с продуктами.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    /**
     * Поиск продуктов по имени.
     *
     * Метод выполняет поиск продуктов, имя которых содержит указанную подстроку.
     * Поиск регистронезависимый и упорядочивает результаты в алфавитном порядке.
     *
     * @param name Имя продукта или часть имени для поиска.
     * @return Список найденных продуктов.
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY p.name ASC")
    List<Product> findByName(@Param("name") String name);
}
