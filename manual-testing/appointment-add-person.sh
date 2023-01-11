#!/bin/bash
set -x

curl --location --request POST 'http://localhost:8080/appointment/person' \
--header 'Content-Type: application/json' \
--header "x-auth-token: ${SESSION_ID}" \
--data-raw '{
    "appointmentId":45,
    "person": {
        "relation":"sister"
    }
}'
