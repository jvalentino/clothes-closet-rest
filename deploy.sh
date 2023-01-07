#!/bin/bash
set -x

GIT_URL=https://heroku:$CC_HEROKU_API_KEY@git.heroku.com/clothes-closet-rest.git
git remote add PROD $GIT_URL || true

# To push a specific tag: git push -f PROD 1.0.41^{}:refs/heads/master
git push -f PROD
