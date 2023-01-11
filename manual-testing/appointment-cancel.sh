#!/bin/bash
set -x

curl --location --request DELETE 'http://localhost:8080/appointment/cancel?id=4' \
--header "x-auth-token: ${SESSION_ID}"