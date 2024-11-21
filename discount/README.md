Discount 

A Spring Boot RESTful API to manage and apply discount coupons for an e-commerce platform. The system supports multiple coupon types (cart-wise, product-wise, and BxGy) with extensibility to add more in the future.

Features

Cart-wise Coupons: Apply discounts to the entire cart if the total amount exceeds a specific threshold.

Product-wise Coupons: Apply discounts to specific products in the cart.

BxGy Coupons: "Buy X, Get Y" deals with a repetition limit, applicable to specific product sets.

CRUD operations for managing coupons.

Integration with MySQL for persistent storage.

Designed to handle real-world edge cases and extensible for future coupon types.


Technologies Used

Java: Language used for development.
Spring Boot: Framework for building RESTful APIs.
MySQL: Database for storing coupon details.
Lombok: For reducing boilerplate code.
Jackson: For JSON processing.

Installation
Prerequisites
JDK 17+
MySQL
Maven
Postman (or any HTTP client for testing)

Steps:

  1. Clone the repository
     git clone https://github.com/your-username/discount.git

  2. Navigate to the project directory:
     cd discount

  3. Configure the database in src/main/resources/application.properties:
     spring.datasource.url=jdbc:mysql://localhost:3306/coupons_db
     spring.datasource.username=<your-username>
     spring.datasource.password=<your-password>

  4. Create the database in MySQL:
     CREATE DATABASE coupons_db;

  5. Build and run the project:
     mvn spring-boot:run

API Endpoints

Coupon Management

Method	Endpoint	       Description
POST	/api/coupons	   Create a new coupon.
GET	    /api/coupons	   Retrieve all coupons.
GET	    /api/coupons/{id}  Retrieve a coupon by ID.
PUT	    /api/coupons/{id}  Update a coupon.
DELETE	/api/coupons/{id}  Delete a coupon.

Coupon Application

Method	Endpoint	              Description
POST	/api/coupons/apply/{id}	  Apply a specific coupon to a given cart.

Example Payloads

Cart-Wise Coupon

Create Coupon

{
"type": "cart-wise",
"details": "{\"threshold\":100,\"discount\":10}",
"expirationDate": "2024-12-31"
}

Cart

{
"items": [
{"productId": 1, "quantity": 2, "price": 50},
{"productId": 2, "quantity": 3, "price": 30}
],
"totalAmount": 190
}

Response

{
"discountAmount": 19.0,
"finalAmount": 171.0
}

Product-Wise Coupon

Create Coupon

{
"type": "product-wise",
"details": "{\"product_id\":1,\"discount\":20}",
"expirationDate": "2024-12-31"
}

Cart 

{
"items": [
{"productId": 1, "quantity": 2, "price": 50},
{"productId": 2, "quantity": 3, "price": 30}
],
"totalAmount": 190
}

Response

{
"discountAmount": 20.0,
"finalAmount": 170.0
}

BxGy Coupon

Create Coupon

{
"type": "bxgy",
"details": "{\"buy_products\":[{\"product_id\":1,\"quantity\":2}],\"get_products\":[{\"product_id\":2,\"quantity\":1}],\"repetition_limit\":1}",
"expirationDate": "2024-12-31"
}

Cart

{
"items": [
{"productId": 1, "quantity": 4, "price": 50},
{"productId": 2, "quantity": 1, "price": 30}
],
"totalAmount": 230
}

Response 

{
"discountAmount": 30.0,
"finalAmount": 200.0
}





