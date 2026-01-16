CREATE TABLE employee (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      first_name VARCHAR(255) NOT NULL,
      last_name VARCHAR(255) NOT NULL,
      position VARCHAR(255),
      hire_date DATE
);

CREATE TABLE user_account (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      employee_id BIGINT UNIQUE,
      username VARCHAR(255) UNIQUE NOT NULL,
      password_hash VARCHAR(255) NOT NULL,
      role VARCHAR(50) NOT NULL,
      is_active BOOLEAN DEFAULT TRUE,
      CONSTRAINT fk_user_employee FOREIGN KEY (employee_id) REFERENCES employee(id)
);

CREATE TABLE customer (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      first_name VARCHAR(255) NOT NULL,
      last_name VARCHAR(255) NOT NULL,
      phone VARCHAR(50) NOT NULL,
      discount_rate DECIMAL(4, 2) DEFAULT 0.00
);

CREATE TABLE supplier (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      name VARCHAR(255),
      contact_person VARCHAR(255),
      email VARCHAR(255) UNIQUE,
      phone VARCHAR(50)
);

CREATE TABLE medicine (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      name VARCHAR(255) NOT NULL,
      manufacturer VARCHAR(255),
      description TEXT,
      price DECIMAL(10, 2) NOT NULL,
      prescription_required BOOLEAN DEFAULT FALSE,
      is_archived BOOLEAN DEFAULT FALSE
);

CREATE TABLE inventory (
       id BIGINT AUTO_INCREMENT PRIMARY KEY,
       medicine_id BIGINT NOT NULL,
       suppler_id BIGINT,
       batch_number VARCHAR(255) NOT NULL,
       stock_quantity INT NOT NULL,
       expiration_date DATE NOT NULL,
       version BIGINT DEFAULT 0,
       CONSTRAINT fk_inventory_medicine FOREIGN KEY (medicine_id) REFERENCES medicine(id),
       CONSTRAINT fk_inventory_supplier FOREIGN KEY (suppler_id) REFERENCES supplier(id)
);

CREATE TABLE sale (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      employee_id BIGINT,
      customer_id BIGINT,
      sale_date_time DATETIME(6),
      total_amount DECIMAL(10, 2),
      CONSTRAINT fk_sale_employee FOREIGN KEY (employee_id) REFERENCES employee(id),
      CONSTRAINT fk_sale_customer FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE sale_item (
       id BIGINT AUTO_INCREMENT PRIMARY KEY,
       sale_id BIGINT NOT NULL,
       medicine_id BIGINT NOT NULL,
       quantity INT NOT NULL,
       unit_price DECIMAL(10, 2) NOT NULL,
       CONSTRAINT fk_sale_item_sale FOREIGN KEY (sale_id) REFERENCES sale(id),
       CONSTRAINT fk_sale_item_medicine FOREIGN KEY (medicine_id) REFERENCES medicine(id)
);
