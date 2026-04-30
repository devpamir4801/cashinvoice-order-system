# cashinvoice-order-system
 maintainable backend system using Java and  Spring Boot. A bonus section evaluates familiarity with Apache Camel, file-based  integration, and ActiveMQ/RabbitMQ/IBM MQ

 A Spring Boot REST API for order management with Apache Camel integration (Bonus).

## Features

- Create Order (POST)
- Get Order by ID (GET)
- List Orders by Customer (GET)
- In-memory storage
- Apache Camel + File polling + ActiveMQ integration (Bonus 1)
- Basic JWT Security structure (Bonus 2 ready)

## Prerequisites

- Java 17+
- Maven
- ActiveMQ (running on `tcp://localhost:61616`)

## Setup

1. Start ActiveMQ broker
2. Create directories:
   ```bash
   mkdir -p input/orders error/orders

Build and run:Bashmvn clean install
mvn spring-boot:run

API Endpoints
1. Create Order
httpPOST http://localhost:8080/api/orders
Content-Type: application/json

{
  "customerId": "CUST1001",
  "product": "Laptop",
  "amount": 75000
}
Response (201 Created):
JSON{
  "orderId": "123e4567-e89b-12d3-a456-426614174000",
  "status": "CREATED"
}
2. Get Order by ID
GET /api/orders/{orderId}
3. List Orders by Customer
GET /api/orders?customerId=CUST1001
Camel Flow

Order created → JSON file written to input/orders/order-{uuid}.json
Camel polls the folder → validates → sends to ORDER.CREATED.QUEUE
Consumer logs the processed order

Project Structure

Clean layered architecture
Separation of concerns
Proper logging and exception handling
