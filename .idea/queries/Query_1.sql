SELECT w.id AS warehouse_id,
       u.fullname AS usuario,
       o.id AS order_id,
       o.product_name,
       o.status
FROM warehouses w
         JOIN users u ON w.user_id = u.id
         JOIN order_items o ON o.warehouse_id = w.id
WHERE w.id = 'WAREHOUSE_UUID_AQUI';
