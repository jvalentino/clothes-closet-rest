#!/bin/bash
set -x

curl --location --request POST 'http://localhost:8080/appointment/update' \
--header 'Content-Type: application/json' \
--header "x-auth-token: ${SESSION_ID}" \
--data-raw '{
    "appointmentId":45,
    "visits": [
        {
            "id": 56,
            "student": null,
            "person": {
                "id": 55,
                "relation": "sister"
            },
            "socks": 100,
            "underwear": 1,
            "shoes": 2,
            "happened": true,
            "coats": 4,
            "backpacks": 5,
            "misc": 6
        },
        {
            "id": 54,
            "student": null,
            "person": {
                "id": 53,
                "relation": "brother"
            },
            "socks": 7,
            "underwear": 8,
            "shoes": 9,
            "happened": true,
            "coats": 11,
            "backpacks": 12,
            "misc": 13
        },
        {
            "id": 46,
            "student": {
                "id": "123",
                "school": "Not Listed/Unknown",
                "gender": "Female",
                "grade": "1"
            },
            "person": null,
            "socks": 14,
            "underwear": 15,
            "shoes": 16,
            "happened": true,
            "coats": 18,
            "backpacks": 19,
            "misc": 20
        }
    ]
}'