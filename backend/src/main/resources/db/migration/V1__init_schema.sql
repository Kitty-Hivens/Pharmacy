-- =====================================================
-- Pharmacy POS System - Initial Schema
-- Generated from actual JPA entities
-- Database: MariaDB 10.6+
-- Admin user will be created by InitAdmin.java
-- =====================================================

-- Employees
CREATE TABLE employee (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        first_name VARCHAR(255),
        last_name VARCHAR(255),
        position VARCHAR(255),
        hire_date DATE,
        INDEX idx_name (last_name, first_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User Accounts (linked to employees)
CREATE TABLE user_account (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        employee_id BIGINT UNIQUE,
        username VARCHAR(255) UNIQUE NOT NULL,
        password_hash VARCHAR(255) NOT NULL,
        role VARCHAR(50) NOT NULL,
        is_active BOOLEAN DEFAULT TRUE,
        CONSTRAINT fk_user_employee FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE,
        INDEX idx_username (username),
        INDEX idx_role (role),
        INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Customers
CREATE TABLE customer (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        first_name VARCHAR(255),
        last_name VARCHAR(255),
        phone VARCHAR(255),
        discount_rate DECIMAL(4, 2),
        INDEX idx_phone (phone),
        INDEX idx_name (last_name, first_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Suppliers
CREATE TABLE supplier (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(255),
        contact_person VARCHAR(255),
        email VARCHAR(255) UNIQUE,
        phone VARCHAR(255),
        INDEX idx_email (email),
        INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Medicines
CREATE TABLE medicine (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(255),
        manufacturer VARCHAR(255),
        description TEXT,
        price DECIMAL(10, 2),
        prescription_required BOOLEAN,
        is_archived BOOLEAN,
        INDEX idx_name (name),
        INDEX idx_archived (is_archived),
        INDEX idx_manufacturer (manufacturer)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Inventory (batches with FEFO support)
CREATE TABLE inventory (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        medicine_id BIGINT,
        supplier_id BIGINT, -- Fixed typo from 'suppler_id'
        batch_number VARCHAR(255),
        stock_quantity INT,
        expiration_date DATE,
        version BIGINT DEFAULT 0,
        CONSTRAINT fk_inventory_medicine FOREIGN KEY (medicine_id) REFERENCES medicine(id) ON DELETE RESTRICT,
        CONSTRAINT fk_inventory_supplier FOREIGN KEY (supplier_id) REFERENCES supplier(id) ON DELETE SET NULL,
        INDEX idx_medicine_id (medicine_id),
        INDEX idx_expiration_date (expiration_date),
        INDEX idx_stock (stock_quantity),
        INDEX idx_fefo_lookup (medicine_id, expiration_date, stock_quantity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sales
CREATE TABLE sale (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      employee_id BIGINT,
                      customer_id BIGINT,
                      sale_date_time DATETIME(6),
                      total_amount DECIMAL(10, 2),
                      CONSTRAINT fk_sale_employee FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE RESTRICT,
                      CONSTRAINT fk_sale_customer FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE SET NULL,
                      INDEX idx_sale_date_time (sale_date_time),
                      INDEX idx_employee_id (employee_id),
                      INDEX idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sale Items
CREATE TABLE sale_item (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           sale_id BIGINT,
                           medicine_id BIGINT,
                           quantity INT,
                           unit_price DECIMAL(10, 2),
                           CONSTRAINT fk_sale_item_sale FOREIGN KEY (sale_id) REFERENCES sale(id) ON DELETE CASCADE,
                           CONSTRAINT fk_sale_item_medicine FOREIGN KEY (medicine_id) REFERENCES medicine(id) ON DELETE RESTRICT,
                           INDEX idx_sale_id (sale_id),
                           INDEX idx_medicine_id (medicine_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
