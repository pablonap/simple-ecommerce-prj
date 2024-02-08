# SIMPLE ECOMMERCE PRJ REST API

## Endpoints

### Order Endpoints
| Controller      | Method | Endpoint                       |
|-----------------|--------|--------------------------------|
| OrderController | POST   | /api/orders                    |
|                 | PUT    | /api/orders/{id}               |
|                 | GET    | /api/orders                    |
|                 | GET    | /api/orders/{id}               |
|                 | DELETE | /api/orders/{id}               |

### Order Item Endpoints
| Controller         | Method | Endpoint                            |
|--------------------|--------|-------------------------------------|
| OrderItemController| POST   | /api/order-items                   |
|                     | PUT    | /api/order-items/{id}              |
|                     | GET    | /api/order-items                   |
|                     | GET    | /api/order-items/{id}              |
|                     | DELETE | /api/order-items/{id}              |

### Product Endpoints
| Controller      | Method | Endpoint                    |
|-----------------|--------|-----------------------------|
| ProductController | POST   | /api/products              |
|                 | GET    | /api/products              |
|                 | GET    | /api/products/{id}         |
|                 | PUT    | /api/products/{id}         |
|                 | DELETE | /api/products/{id}         |


## Instructions for Docker usage

**For development**

1. In the volumes section add a route into the docker-compose-dev.yaml : [SOME_PATH]/db:/var/lib/postgresql/data  
For example:  
`volumes:`  
      `- /home/my-username/Desktop/project/simple-ecommerce-prj/db:/var/lib/postgresql/data`

2. Then, in the project root folder, run: `docker-compose -f docker-compose-dev.yaml up`

3. In your IDE, add the following environment variables:  
DB_USERNAME=postgres;DB_PASSWORD=root;DB_PORT=5432

4. Run the application from your IDE.

**For testing:**
1. Add a route into the docker-compose.yaml in the volumes section: [SOME_PATH]/db:/var/lib/postgresql/data  
For example:  
`volumes:`  
      `- /home/my-username/Desktop/project/simple-ecommerce-prj/db:/var/lib/postgresql/data`

2. In the project root folder, run: `mvn clean; mvn install -DskipTests`

3. Then run: `docker-compose -f docker-compose.yaml up`


## How to use it - Sample flow

0. This sample project consists of three main entities 'Product', 'Order' and 'OrderItem'. Although there is pre-loaded data, next it's shown the entire flow from creating a product to close a purchase.

1. Create a Product via a POST request as follows:  
`
curl --location 'http://localhost:8080/api/products' \
--header 'Content-Type: application/json' \
--data '{
    "name": "laptop",
    "code": "#202411111",
    "description": "some laptop",
    "price": 1800
}'
`

2. Create an Order via a POST request as follows:  
`
curl --location 'http://localhost:8080/api/orders' \
--header 'Content-Type: application/json' \
--data '{
    "shippingAddress": "x street"
}'
`  
For the Order, we only provide the shipping address, which will be created with the current time; OrderItems remain empty; the total amount is zero, and most importantly, the state is set to 'ON_PROCESS'.  

3. Create an OrderItem via a POST request as follows:  
`
curl --location 'http://localhost:8080/api/order-items' \
--header 'Content-Type: application/json' \
--data '{
    "productId": 11,
    "orderId": 3,
    "quantity": 2
}'
`  
The OrderItem entity links an Order with a Product and the quantity of the same products chosen. Later, this will be used to calculate the total amount.

4. Finally, we can 'make a purchase' by modifying the Order by updating its state from ON_PROCESS to FINISHED via a PUT request as follows:  
`
curl --location --request PUT 'http://localhost:8080/api/orders/3' \
--header 'Content-Type: application/json' \
--data '{
    "shippingAddress": "456 Elm St",
    "state": "FINISHED"
}'
`  
Now two fields from the Order will be updated, 'totalAmount' and 'state' and it will no longer be possible to add new OrderItems to this Order.  

## Test coverage
The project is tested with Junit5:  
| Package      | Class          | Method         | Line           |
|--------------|----------------|----------------|----------------|
| all classes  | 78.8% (26/33)  | 83.1% (103/124)| 82.3% (242/294)|  


| Package                    | Class         | Method        | Line          |
|----------------------------|---------------|---------------|---------------|
| com.agileengine.controller | 100% (3/3)    | 100% (18/18)  | 100% (30/30)  |
| com.agileengine.dto        | 62.5% (10/16) | 51.9% (14/27) | 51.9% (27/52) |
| com.agileengine.exception  | 100% (6/6)    | 100% (12/12)  | 100% (34/34)  |
| com.agileengine.model      | 100% (4/4)    | 87.2% (41/47) | 72.8% (67/92) |
| com.agileengine.service    | 100% (3/3)    | 100% (18/18)  | 100% (84/84)  |  

For running the test cases from the command line, in the project root folder run: `mvn test`.

## REST API endpoints via OpenAPI
To access the REST API endpoints via the browser go to: `localhost:8080/swagger-ui.html`

## Technologies involved
### • Java 21
### • Spring boot 3+
### • Spring Web
### • Spring Data JPA
### • PostgreSql
### • FlyWay
### • JUnit5
### • Docker
### • OpenAPI