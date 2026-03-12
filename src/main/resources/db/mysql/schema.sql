-- Mini Task Management System - MySQL Schema
-- Generated to match current JPA entities (users, tasks)

CREATE DATABASE IF NOT EXISTS `mini-task-db`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `mini-task-db`;

CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(255) NOT NULL,
  `first_name` VARCHAR(255) NOT NULL,
  `last_name` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `role` VARCHAR(20) NOT NULL,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_email` (`email`),
  CONSTRAINT `chk_users_role`
    CHECK (`role` IN ('ADMIN', 'USER'))
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `tasks` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT NULL,
  `status` VARCHAR(20) NOT NULL,
  `priority` VARCHAR(20) NOT NULL,
  `due_date` DATETIME(6) NOT NULL,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  KEY `idx_tasks_user_id` (`user_id`),
  KEY `idx_tasks_status` (`status`),
  KEY `idx_tasks_priority` (`priority`),
  KEY `idx_tasks_due_date` (`due_date`),
  KEY `idx_tasks_user_status_priority` (`user_id`, `status`, `priority`),
  CONSTRAINT `fk_tasks_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `chk_tasks_status`
    CHECK (`status` IN ('TODO', 'IN_PROGRESS', 'DONE')),
  CONSTRAINT `chk_tasks_priority`
    CHECK (`priority` IN ('LOW', 'MEDIUM', 'HIGH'))
) ENGINE=InnoDB;

