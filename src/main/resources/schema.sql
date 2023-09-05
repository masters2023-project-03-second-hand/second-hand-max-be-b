-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema second_hand
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema second_hand
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `second_hand` DEFAULT CHARACTER SET utf8;
USE `second_hand`;

-- -----------------------------------------------------
-- Table `second_hand`.`member`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `second_hand`.`member`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `login_id`    VARCHAR(45)  NOT NULL,
    `email`       VARCHAR(45)  NOT NULL,
    `profile_url` VARCHAR(512) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `second_hand`.`refresh_token`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `second_hand`.`refresh_token`
(
    `member_id` BIGINT       NOT NULL,
    `token`     VARCHAR(256) NOT NULL,
    PRIMARY KEY (`member_id`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `second_hand`.`item`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `second_hand`.`item`
(
    `id`             BIGINT        NOT NULL AUTO_INCREMENT,
    `title`          VARCHAR(100)  NOT NULL,
    `content`        VARCHAR(2000) NULL,
    `price`          BIGINT        NULL,
    `trading_region` VARCHAR(100)  NOT NULL COMMENT '거래지역',
    `created_at`     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
    `updated_at`     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
    `view_count`     INT           NOT NULL DEFAULT 0 COMMENT '조회수',
    `wish_count`     INT           NOT NULL DEFAULT 0 COMMENT '관심수',
    `chat_count`     INT           NOT NULL DEFAULT 0 COMMENT '채팅수',
    `status`         VARCHAR(45)   NOT NULL COMMENT 'ON_SALE, SOLD_OUT, RESERVED ',
    `category_name`  VARCHAR(45)   NOT NULL,
    `thumbnail_url`  VARCHAR(512)  NOT NULL,
    `seller_id`      BIGINT        NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_item_trading_region` (`trading_region`(5))
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `second_hand`.`category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `second_hand`.`category`
(
    `id`        BIGINT       NOT NULL AUTO_INCREMENT,
    `name`      VARCHAR(45)  NOT NULL,
    `image_url` VARCHAR(512) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `second_hand`.`item_image`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `second_hand`.`item_image`
(
    `id`        BIGINT       NOT NULL AUTO_INCREMENT,
    `image_url` VARCHAR(512) NOT NULL,
    `item_id`   BIGINT       NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `second_hand`.`residence`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `second_hand`.`residence`
(
    `id`           BIGINT      NOT NULL AUTO_INCREMENT,
    `address_name` VARCHAR(50) NOT NULL COMMENT '읍/면/동 주소',
    `member_id`    BIGINT      NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `second_hand`.`chat_room`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `second_hand`.`chat_room`
(
    `id`         BIGINT    NOT NULL AUTO_INCREMENT,
    `created_at` TIMESTAMP NOT NULL,
    `member_id`  BIGINT    NOT NULL,
    `item_id`    BIGINT    NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `second_hand`.`chat_log`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `second_hand`.`chat_log`
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
-- Table `second_hand`.`wish_item`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `second_hand`.`wish_item`
(
    `id`        BIGINT NOT NULL AUTO_INCREMENT,
    `item_id`   BIGINT NOT NULL,
    `member_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;


SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;
