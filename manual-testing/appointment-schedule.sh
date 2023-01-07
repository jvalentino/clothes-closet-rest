#!/bin/bash
set -x

curl --location --request POST 'http://localhost:8080/appointment/schedule' \
--header 'Content-Type: application/json' \
--data-raw '{
    "datetime":"2023-05-01T00:00:00.000+0000",
    "guardian": {
        "email": "foo@bar.com",
        "firstName": "foo",
        "lastName": "bar",
        "phoneNumber":"555-555-1111",
        "phoneTypeLabel": "mobile"
    },
    "students":[
        {
            "id":"000",
            "school":"Not Listed/Unknown",
            "gender":"Male",
            "grade":"K"
        },
        {
            "id":"111",
            "school":"Not Listed/Unknown",
            "gender":"Female",
            "grade":"10"
        }
    ]
}'