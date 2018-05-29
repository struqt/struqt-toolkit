
DROP TABLE IF EXISTS customers;
CREATE TABLE customers (
  customerNumber         INT            NOT NULL,
  customerName           VARCHAR(50)    NOT NULL,
  contactLastName        VARCHAR(50)    NOT NULL,
  contactFirstName       VARCHAR(50)    NOT NULL,
  phone                  VARCHAR(50)    NOT NULL,
  addressLine1           VARCHAR(50)    NOT NULL,
  addressLine2           VARCHAR(50)    DEFAULT NULL,
  city                   VARCHAR(50)    NOT NULL,
  state                  VARCHAR(50)    DEFAULT NULL,
  postalCode             VARCHAR(15)    DEFAULT NULL,
  country                VARCHAR(50)    NOT NULL,
  salesRepEmployeeNumber INT            DEFAULT NULL,
  creditLimit            DECIMAL(10, 2) DEFAULT NULL,
  PRIMARY KEY (customerNumber)
);


DROP TABLE IF EXISTS employees;
CREATE TABLE employees (
  employeeNumber INT          NOT NULL,
  lastName       VARCHAR(50)  NOT NULL,
  firstName      VARCHAR(50)  NOT NULL,
  extension      VARCHAR(10)  NOT NULL,
  email          VARCHAR(100) NOT NULL,
  officeCode     VARCHAR(10)  NOT NULL,
  reportsTo      INT          DEFAULT NULL,
  jobTitle       VARCHAR(50)  NOT NULL,
  PRIMARY KEY (employeeNumber)
);


DROP TABLE IF EXISTS offices;
CREATE TABLE offices (
  officeCode   VARCHAR(10) NOT NULL,
  city         VARCHAR(50) NOT NULL,
  phone        VARCHAR(50) NOT NULL,
  addressLine1 VARCHAR(50) NOT NULL,
  addressLine2 VARCHAR(50) DEFAULT NULL,
  state        VARCHAR(50) DEFAULT NULL,
  country      VARCHAR(50) NOT NULL,
  postalCode   VARCHAR(15) NOT NULL,
  territory    VARCHAR(10) NOT NULL,
  PRIMARY KEY (officeCode)
);


DROP TABLE IF EXISTS orderdetails;
CREATE TABLE orderdetails (
  orderNumber     INT            NOT NULL,
  productCode     VARCHAR(15)    NOT NULL,
  quantityOrdered INT            NOT NULL,
  priceEach       DECIMAL(10, 2) NOT NULL,
  orderLineNumber SMALLINT       NOT NULL,
  PRIMARY KEY (orderNumber, productCode)
);


DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
  orderNumber    INT         NOT NULL,
  orderDate      DATE        NOT NULL,
  requiredDate   DATE        NOT NULL,
  shippedDate    DATE        DEFAULT NULL,
  status         VARCHAR(15) NOT NULL,
  comments       text,
  customerNumber INT         NOT NULL,
  PRIMARY KEY (orderNumber)
);


DROP TABLE IF EXISTS payments;
CREATE TABLE payments (
  customerNumber INT            NOT NULL,
  checkNumber    VARCHAR(50)    NOT NULL,
  paymentDate    DATE           NOT NULL,
  amount         DECIMAL(10, 2) NOT NULL,
  PRIMARY KEY (customerNumber, checkNumber)
);


DROP TABLE IF EXISTS products;
CREATE TABLE products (
  productCode        VARCHAR(15)    NOT NULL,
  productName        VARCHAR(70)    NOT NULL,
  productLine        VARCHAR(50)    NOT NULL,
  productScale       VARCHAR(10)    NOT NULL,
  productVendor      VARCHAR(50)    NOT NULL,
  productDescription text           NOT NULL,
  quantityInStock    SMALLINT       NOT NULL,
  buyPrice           DECIMAL(10, 2) NOT NULL,
  MSRP               DECIMAL(10, 2) NOT NULL,
  -- CONSTRAINT products_ibfk_1 FOREIGN KEY (productLine) REFERENCES productlines (productLine),
  PRIMARY KEY (productCode)
);


DROP TABLE IF EXISTS productlines;
CREATE TABLE productlines (
  productLine     VARCHAR(50)   NOT NULL,
  textDescription VARCHAR(4000) DEFAULT NULL,
  htmlDescription text,
  image           text,
  PRIMARY KEY (productLine)
);
