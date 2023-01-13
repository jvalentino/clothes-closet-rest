#!/bin/bash
set -x

curl --location --request POST 'http://localhost:8080/settings' \
--header "x-auth-token: ${SESSION_ID}" \
--header 'Content-Type: application/json' \
--data-raw '{
        "id": null,
        "gender": "Female",
        "quantity": 30,
        "label": "Delete Me"
}
'

