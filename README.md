# 🚚 Transport Management System (TMS) – Backend API

A **Spring Boot microservices–style backend** to manage **Loads, Bids, Bookings, and Transporters** with a complete business lifecycle, strict state validation, and **concurrency-safe truck allocation**.

This project is designed to reflect **real-world logistics workflows**, including bid competition, capacity management, and safe booking execution.

---

## 📌 Key Highlights

* End‑to‑end **Load → Bid → Booking lifecycle**
* Strong **state validation** (no invalid transitions)
* **Optimistic locking & transactional safety** for truck allocation
* **Redis caching** for frequently accessed reads
* **Prometheus metrics** for backend observability
* **Grafana dashboards** for real-time monitoring
* Dockerized PostgreSQL setup

---

## 🧩 System Overview

The system models the interaction between **Shippers** and **Transporters**:

1. Shipper posts a Load
2. Transporters place competitive Bids
3. Best bid is accepted → Booking created
4. Trucks are atomically allocated
5. Booking can be cancelled with full rollback

---


* UUID-based primary keys
* Strict foreign key relationships
* Status-driven lifecycle management

---

## ✨ Features

### 🚛 Load Management

* Create new loads (initial status: `POSTED`)
* Paginated load listing with filters
* Fetch load details with active bids
* Cancel loads safely with status validation
* Get best bid suggestions sorted by lowest rate

### 🏢 Transporter Management

* Register transporters with truck capacity
* Fetch transporter details
* Update truck availability dynamically

### 💰 Bid Management

* Submit bids for active loads
* Filter bids by load, transporter, or status
* Reject bids explicitly
* Fetch bid details

### 📦 Booking Management

* Accept bid and create booking
* Deduct trucks with concurrency handling
* Cancel booking and restore trucks atomically

---

## 📦 API Endpoints

### Load APIs

| Method | Endpoint                               | Description                |
| ------ | -------------------------------------- | -------------------------- |
| POST   | `/load/register`                       | Create a new load          |
| GET    | `/load/{loadId}`                       | Get load with active bids  |
| GET    | `/load?shipperId=&status=&page=&size=` | List loads with pagination |
| PATCH  | `/load/{loadId}/cancel`                | Cancel a load              |
| GET    | `/load/{loadId}/best-bids`             | Best bid suggestions       |

**Sample Load Request**

```json
{
  "shipperId": "shipper-123",
  "loadingCity": "Mumbai",
  "unloadingCity": "Pune",
  "loadingDate": "2025-12-10",
  "productType": "Electronics",
  "weight": 1200.5,
  "weightUnit": "KG",
  "truckType": "Container",
  "noOfTrucks": 3
}
```

---

### Bid APIs

| Method | Endpoint                              | Description     |
| ------ | ------------------------------------- | --------------- |
| POST   | `/bid/register`                       | Submit a bid    |
| GET    | `/bid?loadId=&transporterId=&status=` | Filter bids     |
| GET    | `/bid/{bidId}`                        | Get bid details |
| PATCH  | `/bid/{bidId}/reject`                 | Reject a bid    |

**Sample Bid Request**

```json
{
  "loadId": "uuid-of-load",
  "transporterId": "uuid-of-transporter",
  "proposedRate": 1800.0,
  "trucksOffered": 2
}
```

---

### Transporter APIs

| Method | Endpoint                              | Description               |
| ------ | ------------------------------------- | ------------------------- |
| POST   | `/transporter/register`               | Register transporter      |
| GET    | `/transporter/{transporterId}`        | Fetch transporter         |
| PUT    | `/transporter/{transporterId}/trucks` | Update truck availability |

**Sample Transporter Request**

```json
{
  "companyName": "Speedy Logistics",
  "rating": 4.5,
  "availableTrucks": [
    {"truckType": "Container", "count": 5},
    {"truckType": "Open", "count": 2}
  ]
}
```

---

### Booking APIs

| Method | Endpoint                      | Description                 |
| ------ | ----------------------------- | --------------------------- |
| POST   | `/booking/register`           | Accept bid & create booking |
| GET    | `/booking/{bookingId}`        | Get booking details         |
| PATCH  | `/booking/{bookingId}/cancel` | Cancel booking              |

**Sample Booking Request**

```json
{
  "bidId": "uuid-of-bid",
  "allocatedTrucks": 2
}
```

---

## 🧠 Caching (Redis)

* Redis is used for **read-heavy endpoints** (loads, transporters)
* Prevents caching of null / empty values
* TTL-based eviction strategy
* Reduces DB load significantly

---

## 📊 Observability

### Prometheus

* Application metrics exposed at:

```
/actuator/prometheus
```

* JVM, HTTP, and custom business metrics included

### Grafana

* Preconfigured dashboards for:

    * Request latency
    * Error rates
    * JVM memory & threads
    * API throughput

---

## 🗄️ Tech Stack

* Java 21
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Redis
* Prometheus
* Grafana
* Docker
* UUID-based entity IDs

---

## 🏗️ Project Setup

### 1️⃣ Clone Repository

```bash
git clone https://github.com/<your-username>/<repo-name>.git
cd <repo-name>
```

### 2️⃣ Start Infrastructure (Docker)

```bash
docker compose up -d
```

### 3️⃣ Run Application

```bash
mvn spring-boot:run
```

---

## 📮 API Testing

A complete Postman collection is provided:

🔗 **Postman Collection**
[https://tms888-2292.postman.co/workspace/238dca93-b7b2-4a57-ad7f-c22a42518bb7/collection/41341913-23d9d5dc-8b28-4dcd-a1dd-0df2ddc62b07](https://tms888-2292.postman.co/workspace/238dca93-b7b2-4a57-ad7f-c22a42518bb7/collection/41341913-23d9d5dc-8b28-4dcd-a1dd-0df2ddc62b07)

---

## 🚀 Future Enhancements

* OAuth2 / Social Login integration
* Role-based access control (RBAC)
* Kafka-based event-driven workflows
* API Gateway (Kong)
* Multi-instance deployment with Eureka

---

## 🤝 Contributions

Contributions, improvements, and frontend collaboration are welcome.
If you are interested in building the **frontend** or enhancing the system, feel free to open an issue or PR.

---

## 📄 License

This project is open-source and available under the MIT License.
