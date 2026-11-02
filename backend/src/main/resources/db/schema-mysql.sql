CREATE DATABASE IF NOT EXISTS paperly
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE paperly;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_username UNIQUE (username)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS subjects (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    PRIMARY KEY (id),
    CONSTRAINT uk_subjects_name UNIQUE (name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS questions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    question_text TEXT NOT NULL,
    subject_id BIGINT NOT NULL,
    topic VARCHAR(255) NOT NULL,
    difficulty_level VARCHAR(255) NOT NULL,
    marks INT NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_questions_subject_id (subject_id),
    CONSTRAINT fk_questions_subject
        FOREIGN KEY (subject_id) REFERENCES subjects (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS generated_papers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    subject_id BIGINT NOT NULL,
    generated_by BIGINT NOT NULL,
    generated_date DATETIME(6) NOT NULL,
    total_marks INT NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_generated_papers_subject_id (subject_id),
    INDEX idx_generated_papers_generated_by (generated_by),
    CONSTRAINT fk_generated_papers_subject
        FOREIGN KEY (subject_id) REFERENCES subjects (id),
    CONSTRAINT fk_generated_papers_user
        FOREIGN KEY (generated_by) REFERENCES users (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS generated_paper_questions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    generated_paper_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    topic VARCHAR(255) NOT NULL,
    difficulty_level VARCHAR(255) NOT NULL,
    marks INT NOT NULL,
    display_order INT NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_generated_paper_questions_paper_id (generated_paper_id),
    CONSTRAINT fk_generated_paper_questions_paper
        FOREIGN KEY (generated_paper_id) REFERENCES generated_papers (id)
        ON DELETE CASCADE
) ENGINE=InnoDB;
