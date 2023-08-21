-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8;
USE `mydb`;

-- -----------------------------------------------------
-- Table `mydb`.`member`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`member`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `email`       VARCHAR(45)  NOT NULL,
    `login_id`    VARCHAR(45)  NOT NULL,
    `password`    VARCHAR(512) NOT NULL,
    `profile_url` VARCHAR(45)  NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`item`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`item`
(
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `name`           VARCHAR(100) NOT NULL,
    `price`          INT          NOT NULL,
    `trading_region` VARCHAR(100) NOT NULL,
    `created_at`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
    `view_count`     INT          NOT NULL DEFAULT 0,
    `like_count`     INT          NOT NULL DEFAULT 0,
    `chat_count`     INT          NOT NULL DEFAULT 0,
    `state`          VARCHAR(45)  NOT NULL,
    `category_name`  VARCHAR(45)  NOT NULL,
    `thumbnail_url`  VARCHAR(512) NOT NULL,
    `seller_id`      BIGINT       NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`region`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`region`
(
    `id`        BIGINT       NOT NULL AUTO_INCREMENT,
    `addr_name` VARCHAR(100) NOT NULL comment '동명(읍, 면)',
    `full_addr` VARCHAR(100) NOT NULL comment '전체주소',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`category`
(
    `id`        BIGINT       NOT NULL AUTO_INCREMENT,
    `name`      VARCHAR(45)  NOT NULL,
    `image_url` VARCHAR(512) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`item_image`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`item_image`
(
    `id`        BIGINT       NOT NULL AUTO_INCREMENT,
    `image_url` VARCHAR(512) NOT NULL,
    `item_id`   BIGINT       NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`residence`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`residence`
(
    `id`        BIGINT NOT NULL AUTO_INCREMENT,
    `region_id` BIGINT NOT NULL,
    `member_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`chat_room`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`chat_room`
(
    `id`         BIGINT    NOT NULL AUTO_INCREMENT,
    `created_at` TIMESTAMP NOT NULL,
    `member_id`  BIGINT    NOT NULL,
    `item_id`    BIGINT    NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`chat_log`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`chat_log`
(
    `id`           BIGINT        NOT NULL AUTO_INCREMENT,
    `message`      VARCHAR(1000) NOT NULL,
    `sender`       VARCHAR(45)   NOT NULL,
    `receiver`     VARCHAR(45)   NOT NULL,
    `created_at`   TIMESTAMP     NOT NULL,
    `chat_room_id` BIGINT        NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`like_item`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`like_item`
(
    `id`        BIGINT NOT NULL AUTO_INCREMENT,
    `item_id`   BIGINT NOT NULL,
    `member_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;


SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;
