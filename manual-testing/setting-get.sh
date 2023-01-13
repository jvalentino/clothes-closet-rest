#!/bin/bash
set -x
curl --location --request GET 'http://localhost:8080/settings' \
--header "x-auth-token: ${SESSION_ID}"

