CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    role VARCHAR(30) NOT NULL,
    timezone VARCHAR(100) NOT NULL,
    created_at DATETIME
);

CREATE TABLE IF NOT EXISTS courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    created_at DATETIME
);

CREATE TABLE IF NOT EXISTS offerings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    teacher_timezone VARCHAR(100) NOT NULL,
    capacity INT,
    booked_count INT,
    created_at DATETIME,
    course_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    CONSTRAINT fk_offering_course FOREIGN KEY (course_id) REFERENCES courses(id),
    CONSTRAINT fk_offering_teacher FOREIGN KEY (teacher_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS class_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_time_utc DATETIME(6) NOT NULL,
    end_time_utc DATETIME(6) NOT NULL,
    offering_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    CONSTRAINT fk_session_offering FOREIGN KEY (offering_id) REFERENCES offerings(id),
    CONSTRAINT fk_session_teacher FOREIGN KEY (teacher_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(30) NOT NULL,
    created_at DATETIME,
    parent_id BIGINT NOT NULL,
    offering_id BIGINT NOT NULL,
    CONSTRAINT fk_booking_parent FOREIGN KEY (parent_id) REFERENCES users(id),
    CONSTRAINT fk_booking_offering FOREIGN KEY (offering_id) REFERENCES offerings(id),
    CONSTRAINT uq_parent_offering UNIQUE (parent_id, offering_id)
);
