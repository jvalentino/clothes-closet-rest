#!/bin/bash
set -x

curl --location --request POST 'http://localhost:8080/settings/upload/accepted' \
--header 'Content-Type: application/json' \
--header "x-auth-token: ${SESSION_ID}" \
--data-raw '{
    "payloadBase64":"Q3VycmVudCBCdWlsZGluZyBOYW1lLFN0dWRlbnQgSWQsR3JhZGUsTWVhbCBTdGF0dXMgTmFtZQpBcmJvciBDcmVlayBFbGVtZW50YXJ5LFQwMSAgICAsMDEsRWxpZ2libGUgRm9yIEZyZWUgTWVhbHMKQmVkZm9yZCBIZWlnaHRzIEVsZW1lbnRhcnksVDAyICAgICwwMixFbGlnaWJsZSBGb3IgRnJlZSBNZWFscwpCZWRmb3JkIEp1bmlvciBIaWdoLFQwMyAgICAsMDMsRWxpZ2libGUgRm9yIEZyZWUgTWVhbHMK"
}'

