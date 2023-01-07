#!/bin/bash
set -x
curl --location --request POST 'http://localhost:8080/appointment/schedule' \
--header 'Content-Type: application/json' \
--data-raw '{
    "guardian": {
        "email": "foo@bar.com",
        "firstName": "",
        "lastName": "bar",
        "phoneNumber":"555-555-5555",
        "phoneTypeLabel": "mobile"
    }
}'