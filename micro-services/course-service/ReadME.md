# 📚 Course Service API

A Spring Boot-based RESTful service to manage courses and their associated content. This application provides endpoints for creating, retrieving, updating, and deleting courses and course content with cloud-based media uploads.

---

## 🚀 Features

- Add and manage **courses**.
- Upload and associate **course media** (e.g. images, files).
- Organize **course content** tied to specific courses.
- Get **approved/unapproved courses**.
- Filter courses by **conductor ID**.
- Fully RESTful API with structured **JSON responses**.
- Custom **exception handling** for validation and duplicate checks.

---

## 🧩 Technologies Used

- Java 17+
- Spring Boot
- Spring Web
- Spring Data MongoDB
- Cloudinary (via `ImageUploadService`)
- JUnit & Mockito (for testing)
- Maven

---

## 📂 API Endpoints

### 🎓 Course Controller

**Base Path:** `/api/v1/courses`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/add-course` | Add a new course with an image |
| `GET`  | `/get-all-courses` | Retrieve all courses |
| `GET`  | `/get-course-by-code/{code}` | Get course by course code |
| `GET`  | `/Get-all-courses-by-conductorId/{conductorId}` | Get all courses by conductor ID |
| `GET`  | `/get-approved-courses` | Get all approved courses |
| `GET`  | `/get-unapproved-courses` | Get all unapproved courses |

---

### 📄 Course Content Controller

**Base Path:** `/api/v1/content`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/` | Upload a file (e.g., image or video) |
| `POST` | `/add-course-content/{courseID}` | Add course content to a course with media |
| `GET`  | `/get-course-content/{courseID}` | Get content for a specific course |
| `PUT`  | `/update-course-content/{courseID}` | Update course content |
| `DELETE` | `/delete-course-content/{courseID}` | Delete course content |

---


