# General decisions
## Decision 1
It was created a microservice fully REACTIVE, in order to handle high traffic and use resources more efficient.
Trade-off: code complexity.

## Decision 2
The microservice consist of 1 API with 3 endpoints.
The API returns 2 types of responses: 200 OK & 400 Bad request.

## Decision 3
It was created a custom exception handler to handle bad requests.

## Decision 4
Parameters values are validated. Order numbers must contain 9-digits only and country codes must contain 2 alphabet characters (ISO-2).

# AS-1 decisions
## Decision 5
It was created /v1 endpoint.

# AS-2 decisions
## Decision 6
To handle the throttling logic it was created an in-memory cache and a /v2 endpoint. The logic was added on top of /v1 endpoint.

# AS-3 decisions
## Decision 7
To handle the time box of 5 sec logic it was created a /v3 endpoint. The logic was added on top of /v2 endpoint.
