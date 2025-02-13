-- Миграция для создания таблицы products
-- Создание таблицы и колонок с типом JSON для категорий
-- Другие SQL миграции

-- Создание таблицы products
CREATE TABLE IF NOT EXISTS products
(
    id          UUID PRIMARY KEY,      -- Уникальный идентификатор (UUID)
    name        VARCHAR(255) NOT NULL, -- Название продукта
    description VARCHAR(1000),         -- Описание продукта (необязательное поле)
    price       INT,                   -- Цена продукта в копейках
    categories  TEXT         NOT NULL, -- Категории продукта (JSON-формат)
    created_at  TIMESTAMP    NOT NULL  -- Дата создания продукта
);