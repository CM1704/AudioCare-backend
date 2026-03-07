# AudioCare Backend

REST API for the AudioCare hearing aid inventory management system.

## Tech Stack
Java 17 · Spring Boot 3.5 · Spring Security · MySQL 8 · JWT (JJWT 0.12.5) · Lombok · Maven

## Setup

1. Run `AudioCare_v3.sql` to create the schema and triggers.
2. Configure `application.properties`:

3. Run with `mvn spring-boot:run`.

## API Base URL
`/audiocare/api` — all endpoints require `Authorization: Bearer <token>` except login.

| Module | Endpoint |
|---|---|
| Auth (public) | `POST /auth/login` |
| Admins | `/admins` |
| Clients | `/clients` |
| Product Models | `/models` |
| Supplier Orders | `/supplier-orders` |
| Products | `/products` |
| Sales | `/orders` |
| Inventory Log | `/movements` (read-only) |

## Team
| Name | Role |
|---|---|
| Carlos Marín | Backend & database |
| Diego Herrera | Database & frontend |
| Joseph Gray | Frontend |
| Valentina Quesada | Frontend |

**Universidad Latina de Costa Rica · Ingeniería de Software IV · Prof. Roberto Corrales**
