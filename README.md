# Aggregation-Service API
The microservice has the responsibility to collect information from 3 external APIs provided by: 
- Shipments-Service 
- Track-Service 
- Pricing-Service

It is build using REACTIVE paradigm and contains 3 GET endpoints which aggregates data regarding shipments, tracking and pricing.
The endpoint supports 3 optional parameters: 
- shipments 
- track
- pricing

Each parameter accepts multiple values. For _shipments_ and _track_, the values are 9-digit order numbers (comma separated), 
while for _pricing_, the values are ISO-2 country codes (comma separated).

## Specification API
### Request
E.g.:
<br>
GET http://localhost:8081/aggregation/v1?shipments=109347263,123456891&track=109347263,123456891&pricing=NL,CN
<br>
GET http://localhost:8081/aggregation/v2?shipments=109347263,123456891&track=109347263,123456891&pricing=NL,CN
<br>
GET http://localhost:8081/aggregation/v3?shipments=109347263,123456891&track=109347263,123456891&pricing=NL,CN

### Response
200 OK
```
{
  "shipments": {
    "109347263": [
      "pallet",
      "pallet",
      "envelope"
    ],
    "123456891": [
      "box"
    ]
  },
  "track": {
    "109347263": "DELIVERING",
    "123456891": "DELIVERING"
  },
  "pricing": {
    "CN": 47.77199578349818,
    "NL": 78.49275414316895
  }
}
```
400 Bad Request
```
{
    "message": "Order number [10934726] must have 9 digits",
    "code": 400,
    "timestamp": "2023-04-17T15:51:29.25217"
}
```
```
{
    "message": "Order number [109347263a] must be an integer",
    "code": 400,
    "timestamp": "2023-04-17T15:52:33.007789"
}
```
```
{
    "message": "Country code [CNA] must have 2 characters",
    "code": 400,
    "timestamp": "2023-04-17T15:53:13.47396"
}
```

## Tech stack
- Java 17
- Spring Boot
- Spring WebFlux
- Docker
- Maven

## Running the application
### Running Backend services
Pull Docker image from: https://hub.docker.com/r/xyzassessment/backend-services.
<br>
Navigate in the _{root}/docker-compose_ directory.

Execute command: `docker-compose up -d`

### Running Aggregation-Service
Navigate in the _{root}_ directory.

Execute command: `mvn spring-boot:run`