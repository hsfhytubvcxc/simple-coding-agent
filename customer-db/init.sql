CREATE SCHEMA catalog;

CREATE TABLE catalog.categories (
    id integer PRIMARY KEY,
    name varchar(100) NOT NULL UNIQUE
);

CREATE TABLE catalog.products (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category_id integer NOT NULL REFERENCES catalog.categories (id),
    sku varchar(50) NOT NULL UNIQUE,
    name varchar(200) NOT NULL,
    description text NOT NULL,
    price numeric(12, 2) NOT NULL,
    stock integer NOT NULL,
    is_active boolean NOT NULL DEFAULT true
);

INSERT INTO catalog.categories (id, name)
VALUES (1, 'Электроника'),
       (2, 'Канцтовары'),
       (3, 'Дом и кухня'),
       (4, 'Спорт'),
       (5, 'Одежда');

INSERT INTO catalog.products (category_id, sku, name, description, price, stock, is_active)
SELECT category.id,
       format('SKU-%s-%s', category.id, lpad(item.number::text, 3, '0')),
       format('%s, модель %s',
              CASE category.id
                  WHEN 1 THEN 'Беспроводные наушники'
                  WHEN 2 THEN 'Ежедневник'
                  WHEN 3 THEN 'Электрический чайник'
                  WHEN 4 THEN 'Спортивная бутылка'
                  WHEN 5 THEN 'Футболка'
              END,
              item.number),
       format('Демонстрационный товар из категории «%s».', category.name),
       category.id * 100 + item.number * 79.90,
       CASE WHEN item.number % 7 = 0 THEN 0 ELSE (category.id * 11 + item.number * 3) % 50 END,
       item.number % 9 <> 0
FROM catalog.categories category
CROSS JOIN generate_series(1, 20) AS item(number);
