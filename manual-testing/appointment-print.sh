#!/bin/bash
set -x

curl --location --request GET 'http://localhost:8080/appointment/print?id=77' \
--header "x-auth-token: ${SESSION_ID}"
