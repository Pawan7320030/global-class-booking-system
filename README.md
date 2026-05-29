# Global Class Booking System

## Project Overview

This is a backend service for a global live-learning platform where teachers can create course offerings and add multiple sessions. Parents can view available offerings, book an entire offering, and view their booked offerings.

The system handles timezone conversion, booking conflict detection, and concurrent booking attempts.

---

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- MySQL
- Maven
- Lombok
- Bean Validation
- Postman

---

## Features

### Teacher APIs

- Create offering
- Add sessions to offering
- View teacher offerings and sessions

### Parent APIs

- View available offerings
- Book offering at offering level
- View booked offerings
- Prevent overlapping bookings
- Show session timings in parent timezone

---

## Database Tables

- `users` - stores teacher and parent details
- `courses` - stores course details
- `offerings` - stores class offering or section details
- `class_sessions` - stores session timings in UTC
- `bookings` - stores parent bookings

Database schema file is available at:

```text
src/main/resources/schema.sql
```

Sample data file is available at:

```text
src/main/resources/data.sql
```

---

## Setup Instructions

### 1. Clone Repository

```bash
git clone <your-github-repository-link>
cd global-class-booking-system
```

### 2. Create MySQL Database

```sql
CREATE DATABASE global_class_booking;
```

### 3. Update Database Configuration

Open:

```text
src/main/resources/application.properties
```

Update your MySQL username and password:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/global_class_booking
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

### 4. Run Application

Run the main class:

```text
GlobalClassBookingSystemApplication.java
```

Or run from terminal:

```bash
mvn spring-boot:run
```

Application will start on:

```text
http://localhost:8080
```

---

## API Documentation

Base URL:

```text
http://localhost:8080
```

### Create Offering

```http
POST /api/teacher/offerings
```

Request:

```json
{
  "courseId": 1,
  "teacherId": 1,
  "title": "Saturday Batch",
  "teacherTimezone": "Asia/Kolkata",
  "capacity": 20
}
```

### Add Sessions

```http
POST /api/teacher/offerings/1/sessions
```

Request:

```json
{
  "teacherId": 1,
  "sessions": [
    {
      "startTime": "2026-06-06T18:00:00",
      "endTime": "2026-06-06T19:00:00"
    },
    {
      "startTime": "2026-06-13T18:00:00",
      "endTime": "2026-06-13T19:00:00"
    },
    {
      "startTime": "2026-06-20T18:00:00",
      "endTime": "2026-06-20T19:00:00"
    }
  ]
}
```

### Get Teacher Offerings

```http
GET /api/teacher/1/offerings
```

### Get Available Offerings for Parent

```http
GET /api/parent/2/offerings
```

### Book Offering

```http
POST /api/parent/bookings
```

Request:

```json
{
  "parentId": 2,
  "offeringId": 1
}
```

### Get Parent Bookings

```http
GET /api/parent/2/bookings
```

---

## Conflict Detection Testing

Create another offering:

```http
POST /api/teacher/offerings
```

Request:

```json
{
  "courseId": 2,
  "teacherId": 1,
  "title": "Overlap Batch",
  "teacherTimezone": "Asia/Kolkata",
  "capacity": 20
}
```

Add an overlapping session:

```http
POST /api/teacher/offerings/2/sessions
```

Request:

```json
{
  "teacherId": 1,
  "sessions": [
    {
      "startTime": "2026-06-13T18:30:00",
      "endTime": "2026-06-13T19:30:00"
    }
  ]
}
```

Try booking the second offering:

```http
POST /api/parent/bookings
```

Request:

```json
{
  "parentId": 2,
  "offeringId": 2
}
```

Expected error:

```json
{
  "error": "TIME_CONFLICT",
  "message": "You already have another booked offering that overlaps with this offering"
}
```

---

## Timezone Handling Approach

Teachers create sessions in their own timezone.

Example:

```text
Teacher timezone: Asia/Kolkata
Session time: 2026-06-06T18:00:00 to 2026-06-06T19:00:00
```

Before saving, the backend converts teacher local time to UTC and stores UTC time in the database.

When a parent views offerings or bookings, UTC time is converted into the parent's timezone.

Example:

```text
Parent timezone: America/New_York
```

This ensures correct schedule display for users in different countries.

---

## Booking Conflict Handling Approach

Parents book the complete offering, not individual sessions.

Before booking a new offering, the system checks whether any session of the new offering overlaps with any already booked session of the parent.

Overlap logic:

```text
existing_session.start_time_utc < new_session.end_time_utc
AND
existing_session.end_time_utc > new_session.start_time_utc
```

If overlap exists, the booking is rejected.

---

## Concurrency Handling Approach

The booking API runs inside a database transaction using `@Transactional`.

During booking:

1. Parent row is locked using pessimistic write lock.
2. Offering row is locked using pessimistic write lock.
3. Existing booking and conflict checks are performed.
4. Capacity is checked.
5. Booking is created only if all validations pass.

This prevents invalid bookings during simultaneous booking requests.

---

## Assumptions Made

- Teachers and parents already exist in the users table.
- Courses already exist in the courses table.
- Parents book the entire offering, not individual sessions.
- All session times are stored in UTC.
- Teacher timezone is used while creating sessions.
- Parent timezone is used while displaying offerings and bookings.
- A parent cannot book overlapping offerings.
- A parent cannot book the same offering twice.

---

## Postman Collection

Postman collection is included at:

```text
postman/Global-Class-Booking-System.postman_collection.json
```

Import this file in Postman and test all APIs.
