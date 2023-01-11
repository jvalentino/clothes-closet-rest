#!/bin/bash
set -x

curl --location --request GET 'http://localhost:8080/appointment/search?date=2023-01-11&name=John' \
--header "x-auth-token: ${SESSION_ID}"
