# Global Class Booking System

Backend service for a global live-learning platform where teachers create class offerings, add sessions, and parents book full offerings. The system supports timezone conversion, booking conflict detection, and concurrency-safe booking.

---

## GitHub Repository

```text
https://github.com/Pawan7320030/global-class-booking-system
```

---

## Project Overview

Teachers can create course offerings such as Saturday Batch, Weekday Camp, or Evening Batch. Each offering contains multiple sessions.

Parents/students can view available offerings, book an entire offering, and view their booked offerings.

The system stores all session times in UTC and converts them into the teacher or parent timezone while displaying the response.

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
- View teacher offerings with sessions

### Parent APIs

- View available offerings
- Book complete offering
- View booked offerings
- Prevent overlapping bookings
- Display session timings in parent timezone

### Engineering Features

- Clean layered architecture
- DTO-based request and response handling
- Global exception handling
- UTC-based timezone handling
- Conflict detection using session overlap logic
- Concurrency-safe booking using pessimistic locking

---

## Project Structure

```text
global-class-booking-system
│
├── postman
│   └── Global-Class-Booking-System.postman_collection.json
│
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── global_class_booking_system
│       │           ├── controller
│       │           ├── dto
│       │           │   ├── request
│       │           │   └── response
│       │           ├── entity
│       │           │   └── enums
│       │           ├── exception
│       │           ├── repository
│       │           ├── service
│       │           └── GlobalClassBookingSystemApplication.java
│       │
│       └── resources
│           ├── application.properties
│           ├── schema.sql
│           └── data.sql
│
├── .gitignore
├── pom.xml
└── README.md
```

---

## Database Schema Overview

### users

Stores teacher and parent details.

Important fields:

- id
- name
- email
- role
- timezone
- created_at

### courses

Stores course/class details.

Important fields:

- id
- title
- description
- created_at

### offerings

Stores schedulable course sections.

Important fields:

- id
- title
- teacher_timezone
- capacity
- booked_count
- course_id
- teacher_id
- created_at

### class_sessions

Stores actual meeting times of an offering.

Important fields:

- id
- start_time_utc
- end_time_utc
- offering_id
- teacher_id

### bookings

Stores parent bookings at offering level.

Important fields:

- id
- parent_id
- offering_id
- status
- created_at

---

## Setup Instructions

### 1. Clone Repository

```bash
git clone https://github.com/Pawan7320030/global-class-booking-system.git
cd global-class-booking-system
```

### 2. Create MySQL Database

```sql
CREATE DATABASE global_class_booking;
```

### 3. Configure Database

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

Example full configuration:

```properties
spring.application.name=global-class-booking-system

server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/global_class_booking
spring.datasource.username=root
spring.datasource.password=your_mysql_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true
```

### 4. Run Application

Run the main class from IntelliJ:

```text
GlobalClassBookingSystemApplication.java
```

Or run from terminal:

```bash
mvn spring-boot:run
```

Application will start at:

```text
http://localhost:8080
```

---

## Sample Data

Sample data is available in:

```text
src/main/resources/data.sql
```

Sample users:

```text
Teacher ID: 1
Parent ID: 2
```

Sample courses:

```text
Course ID: 1 - Minecraft Coding
Course ID: 2 - Roblox Game Design
```

---

## API Documentation

Base URL:

```text
http://localhost:8080
```

---

## Teacher APIs

### 1. Create Offering

```http
POST /api/teacher/offerings
```

Full URL:

```text
http://localhost:8080/api/teacher/offerings
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

Success Response:

```json
{
  "offeringId": 1,
  "message": "Offering created successfully"
}
```

---

### 2. Add Sessions to Offering

```http
POST /api/teacher/offerings/{offeringId}/sessions
```

Full URL:

```text
http://localhost:8080/api/teacher/offerings/1/sessions
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

Success Response:

```json
{
  "message": "Sessions added successfully"
}
```

---

### 3. Get Teacher Offerings

```http
GET /api/teacher/{teacherId}/offerings
```

Full URL:

```text
http://localhost:8080/api/teacher/1/offerings
```

This response displays sessions in the teacher timezone.

---

## Parent APIs

### 4. Get Available Offerings

```http
GET /api/parent/{parentId}/offerings
```

Full URL:

```text
http://localhost:8080/api/parent/2/offerings
```

This response displays sessions in the parent timezone.

---

### 5. Book Offering

```http
POST /api/parent/bookings
```

Full URL:

```text
http://localhost:8080/api/parent/bookings
```

Request:

```json
{
  "parentId": 2,
  "offeringId": 1
}
```

Success Response:

```json
{
  "bookingId": 1,
  "message": "Offering booked successfully"
}
```

---

### 6. Get Parent Bookings

```http
GET /api/parent/{parentId}/bookings
```

Full URL:

```text
http://localhost:8080/api/parent/2/bookings
```

This response returns all confirmed bookings of the parent.

---

## Conflict Detection Testing

After booking offering `1`, create another offering using course `2`.

### Create Overlapping Offering

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

Assume the new offering ID is `2`.

### Add Overlapping Session

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

This overlaps with offering `1` session:

```text
2026-06-13T18:00:00 to 2026-06-13T19:00:00
```

### Try Booking Overlapping Offering

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

Expected Error Response:

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

Before saving, the backend converts the teacher local time into UTC and stores UTC time in the database.

When a parent views available offerings or booked offerings, the backend converts the stored UTC time into the parent's timezone.

Example:

```text
Parent timezone: America/New_York
```

This ensures correct schedule display for users from different countries.

---

## Booking Conflict Handling Approach

Parents book the complete offering, not individual sessions.

Before creating a booking, the system compares every session of the new offering with every already booked session of the parent.

Overlap condition:

```text
existing_session.start_time_utc < new_session.end_time_utc
AND
existing_session.end_time_utc > new_session.start_time_utc
```

If any overlap exists, the booking is rejected with a time conflict error.

---

## Concurrency Handling Approach

The booking API is executed inside a transaction using:

```java
@Transactional
```

During booking:

1. Parent row is locked using pessimistic write lock.
2. Offering row is locked using pessimistic write lock.
3. Duplicate booking is checked.
4. Time conflict is checked.
5. Capacity is checked.
6. Booking is created only if all validations pass.

This prevents invalid booking records during simultaneous booking attempts.

---

## Error Handling

The project uses a global exception handler to return clean error responses.

Common errors:

| Error | Reason |
|---|---|
| NOT_FOUND | Course, teacher, parent, or offering not found |
| BAD_REQUEST | Invalid role, invalid session, duplicate booking, full offering |
| TIME_CONFLICT | Parent already has an overlapping booking |
| VALIDATION_ERROR | Required fields are missing or invalid |

---

## Postman Collection

Postman collection is included in:

```text
postman/Global-Class-Booking-System.postman_collection.json
```

Import this file into Postman and test all APIs.

---

## Assumptions

- Teachers and parents are pre-created in the users table.
- Courses are pre-created in the courses table.
- Parents book the entire offering.
- Parents cannot book individual sessions separately.
- All session times are stored in UTC.
- Teacher timezone is used while creating sessions.
- Parent timezone is used while displaying schedules.
- A parent cannot book the same offering twice.
- A parent cannot book overlapping offerings.
- Capacity is checked during booking.

---

## Screen Recording Checklist

The screen recording should show:

1. Project structure
2. README file
3. Application startup
4. Database tables
5. Postman collection
6. Create offering API
7. Add sessions API
8. Get teacher offerings API
9. Get parent offerings API
10. Book offering API
11. Get parent bookings API
12. Conflict detection
13. Timezone conversion explanation
14. Concurrency handling explanation

---

## Submission

Submit these links in the Google Form:

```text
1. GitHub repository link
2. Screen recording link
```

Make sure both links are publicly accessible.
