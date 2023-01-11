#!/bin/bash
set -x

curl --location --request GET 'http://localhost:8080/appointment/details?id=45' \
--header "x-auth-token: ${SESSION_ID}"
