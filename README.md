## Test task

### Install and run

git clone https://github.com/alexeybogdanov/demo

$ mvnw spring-boot:run (from project directory)

### Start test
$ mwnw test

(integration test loads the data sequentially from src/test/resources/ and performs Sell and List operations)

### BASE URL
http://localhost:8080

### REST API
* POST `/inventory`  load articles json
* POST `/products`  load products json
* GET  `/products/list` return list of products and quantity of each that is available
* POST `/products/sell/{id}` sell product and update the inventory accordingly

### HTTP Status codes
* HTTP 200 in case of success operations
* HTTP 404 in case of resource not found or sell operation is not possible
* HTTP 500 internal server error response

### Error Details
Application provides detailed error object in next format
```json
{
     "url": "http://localhost:8080/products/sell/1",
     "details": "Not enough articles: leg with id: 1.",
     "timestamp": "2021-03-07T21:42:50.709"
 }
```

### H2 DB access
http://localhost:8080/h2

JDBC URL: jdbc:h2:mem:test

* username: sa
* without password


