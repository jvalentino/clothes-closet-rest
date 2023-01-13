#!/bin/bash
set -x

curl --location --request DELETE 'http://localhost:8080/settings?id=83' \
--header "x-auth-token: ${SESSION_ID}"
