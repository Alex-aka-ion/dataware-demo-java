-- Liquibase: Основной файл миграции для OrderService

-- Создание таблицы заказов (orders)
CREATE TABLE orders (
                        id UUID PRIMARY KEY,
                        delivery_address VARCHAR(255) NOT NULL,
                        created_at TIMESTAMP NOT NULL
);

-- Создание таблицы элементов заказа (order_items)
CREATE TABLE order_items (
                             id UUID PRIMARY KEY,
                             order_id UUID NOT NULL,
                             product_id UUID NOT NULL,
                             quantity INTEGER NOT NULL CHECK (quantity > 0),
                             price INTEGER NOT NULL CHECK (price >= 0),
                             FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Индекс для ускорения поиска заказов по товару
CREATE INDEX idx_order_items_product_id ON order_items(product_id);