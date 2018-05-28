/*
*********************************************************************
http://www.mysqltutorial.org
*********************************************************************
Name: MySQL Sample Database
Link: http://www.mysqltutorial.org/mysql-sample-database.aspx
Version 3.1
+ changed data type from DOUBLE to DECIMAL for amount columns
Version 3.0
+ changed DATETIME to DATE for some colunmns
Version 2.0
+ changed table type from MyISAM to InnoDB
+ added foreign keys for all tables
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;
/*!40101 SET SQL_MODE = '' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;


CREATE DATABASE /*!32312 IF NOT EXISTS */ `db_classic_models` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `db_classic_models`;


/*Table structure for table `customers` */
DROP TABLE IF EXISTS `customers`;
CREATE TABLE `customers` (
  `customerNumber`         INT(11)     NOT NULL,
  `customerName`           VARCHAR(50) NOT NULL,
  `contactLastName`        VARCHAR(50) NOT NULL,
  `contactFirstName`       VARCHAR(50) NOT NULL,
  `phone`                  VARCHAR(50) NOT NULL,
  `addressLine1`           VARCHAR(50) NOT NULL,
  `addressLine2`           VARCHAR(50)    DEFAULT NULL,
  `city`                   VARCHAR(50) NOT NULL,
  `state`                  VARCHAR(50)    DEFAULT NULL,
  `postalCode`             VARCHAR(15)    DEFAULT NULL,
  `country`                VARCHAR(50) NOT NULL,
  `salesRepEmployeeNumber` INT(11)        DEFAULT NULL,
  `creditLimit`            DECIMAL(10, 2) DEFAULT NULL,
  PRIMARY KEY (`customerNumber`),
  KEY `salesRepEmployeeNumber` (`salesRepEmployeeNumber`),
  CONSTRAINT `customers_ibfk_1` FOREIGN KEY (`salesRepEmployeeNumber`) REFERENCES `employees` (`employeeNumber`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = latin1;


/*TABLE STRUCTURE FOR TABLE `EMPLOYEES` */
DROP TABLE IF EXISTS `employees`;
CREATE TABLE `employees` (
  `employeeNumber` INT(11)      NOT NULL,
  `lastName`       VARCHAR(50)  NOT NULL,
  `firstName`      VARCHAR(50)  NOT NULL,
  `extension`      VARCHAR(10)  NOT NULL,
  `email`          VARCHAR(100) NOT NULL,
  `officeCode`     VARCHAR(10)  NOT NULL,
  `reportsTo`      INT(11) DEFAULT NULL,
  `jobTitle`       VARCHAR(50)  NOT NULL,
  PRIMARY KEY (`employeeNumber`),
  KEY `reportsTo` (`reportsTo`),
  KEY `officeCode` (`officeCode`),
  CONSTRAINT `employees_ibfk_1` FOREIGN KEY (`reportsTo`) REFERENCES `employees` (`employeeNumber`),
  CONSTRAINT `employees_ibfk_2` FOREIGN KEY (`officeCode`) REFERENCES `offices` (`officeCode`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = latin1;


/*TABLE STRUCTURE FOR TABLE `OFFICES` */
DROP TABLE IF EXISTS `offices`;
CREATE TABLE `offices` (
  `officeCode`   VARCHAR(10) NOT NULL,
  `city`         VARCHAR(50) NOT NULL,
  `phone`        VARCHAR(50) NOT NULL,
  `addressLine1` VARCHAR(50) NOT NULL,
  `addressLine2` VARCHAR(50) DEFAULT NULL,
  `state`        VARCHAR(50) DEFAULT NULL,
  `country`      VARCHAR(50) NOT NULL,
  `postalCode`   VARCHAR(15) NOT NULL,
  `territory`    VARCHAR(10) NOT NULL,
  PRIMARY KEY (`officeCode`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = latin1;


/*Table structure for table `orderdetails` */
DROP TABLE IF EXISTS `orderdetails`;
CREATE TABLE `orderdetails` (
  `orderNumber`     INT(11)        NOT NULL,
  `productCode`     VARCHAR(15)    NOT NULL,
  `quantityOrdered` INT(11)        NOT NULL,
  `priceEach`       DECIMAL(10, 2) NOT NULL,
  `orderLineNumber` SMALLINT(6)    NOT NULL,
  PRIMARY KEY (`orderNumber`, `productCode`),
  KEY `productCode` (`productCode`),
  CONSTRAINT `orderdetails_ibfk_1` FOREIGN KEY (`orderNumber`) REFERENCES `orders` (`orderNumber`),
  CONSTRAINT `orderdetails_ibfk_2` FOREIGN KEY (`productCode`) REFERENCES `products` (`productCode`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = latin1;


/*Table structure for table `orders` */
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `orderNumber`    INT(11)     NOT NULL,
  `orderDate`      DATE        NOT NULL,
  `requiredDate`   DATE        NOT NULL,
  `shippedDate`    DATE DEFAULT NULL,
  `status`         VARCHAR(15) NOT NULL,
  `comments`       text,
  `customerNumber` INT(11)     NOT NULL,
  PRIMARY KEY (`orderNumber`),
  KEY `customerNumber` (`customerNumber`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`customerNumber`) REFERENCES `customers` (`customerNumber`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = latin1;


/*Table structure for table `payments` */
DROP TABLE IF EXISTS `payments`;
CREATE TABLE `payments` (
  `customerNumber` INT(11)        NOT NULL,
  `checkNumber`    VARCHAR(50)    NOT NULL,
  `paymentDate`    DATE           NOT NULL,
  `amount`         DECIMAL(10, 2) NOT NULL,
  PRIMARY KEY (`customerNumber`, `checkNumber`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`customerNumber`) REFERENCES `customers` (`customerNumber`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = latin1;


/*Table structure for table `productlines` */
DROP TABLE IF EXISTS `productlines`;
CREATE TABLE `productlines` (
  `productLine`     VARCHAR(50) NOT NULL,
  `textDescription` VARCHAR(4000) DEFAULT NULL,
  `htmlDescription` mediumtext,
  `image`           mediumblob,
  PRIMARY KEY (`productLine`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = latin1;


/*Table structure for table `products` */
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products` (
  `productCode`        VARCHAR(15)    NOT NULL,
  `productName`        VARCHAR(70)    NOT NULL,
  `productLine`        VARCHAR(50)    NOT NULL,
  `productScale`       VARCHAR(10)    NOT NULL,
  `productVendor`      VARCHAR(50)    NOT NULL,
  `productDescription` text           NOT NULL,
  `quantityInStock`    SMALLINT(6)    NOT NULL,
  `buyPrice`           DECIMAL(10, 2) NOT NULL,
  `MSRP`               DECIMAL(10, 2) NOT NULL,
  PRIMARY KEY (`productCode`),
  KEY `productLine` (`productLine`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`productLine`) REFERENCES `productlines` (`productLine`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = latin1;


/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;