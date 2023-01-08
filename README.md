# Clothes Closet REST 

# Developer Setup

## Database

This is a liquibase project for managing a PostgreSQL database as it runs on Heroku, but uses Docker Compose locally for a development environment.

SpringBoot within the REST app is otherwise use to execute it as needed on startup, which is great.

### Prerequisites

You need Docker, pgadmin, Docker Compose, and LiquidBase:

```bash
brew install --cask docker
brew install --cask pgadmin4
brew install liquibase
brew install docker-compose
brew install pssql
```

When in doubt, run https://github.com/jvalentino/setup-automation

### Setup Script

You need to set some environmment variables for the local database username and password:

```bash
./setup.sh
source ~/.zshrc
```

This will create environment variables for:

- CC_DB_USERNAME
- CC_DB_PASSWORD
- CC_JDBC_URL

You then launch the local container:

```bash
docker compose up -d
```

You then have to create the initial database using pgadmin

![01](./wiki/01.png)

You can then verify the connectivity via:

```bash
~/workspaces/personal/clothes-closet-db $ ./status.sh    

+ liquibase --username=postgres --password=postgres --changelog-file=changelog.sql --url=jdbc:postgresql://localhost:5432/ccdb status
####################################################
##   _     _             _ _                      ##
##  | |   (_)           (_) |                     ##
##  | |    _  __ _ _   _ _| |__   __ _ ___  ___   ##
##  | |   | |/ _` | | | | | '_ \ / _` / __|/ _ \  ##
##  | |___| | (_| | |_| | | |_) | (_| \__ \  __/  ##
##  \_____/_|\__, |\__,_|_|_.__/ \__,_|___/\___|  ##
##              | |                               ##
##              |_|                               ##
##                                                ## 
##  Get documentation at docs.liquibase.com       ##
##  Get certified courses at learn.liquibase.com  ## 
##  Free schema change activity reports at        ##
##      https://hub.liquibase.com                 ##
##                                                ##
####################################################
Starting Liquibase at 07:43:55 (version 4.18.0 #5864 built at 2022-12-02 18:02+0000)
Liquibase Version: 4.18.0
Liquibase Community 4.18.0 by Liquibase
1 changesets have not been applied to postgres@jdbc:postgresql://localhost:5432/ccdb
Liquibase command 'status' was executed successfully.
~/workspaces/personal/clothes-closet-db $
```

## Google Calendar





# Had to do it Once

## Heroku Setup

FIrst, this has to be attached to a specific app, so you first have to go about creating the application., which I called `clothes-closet-rest` and is associated with https://github.com/jvalentino/clothes-closet-rest.

```bash
$ heroku addons:create heroku-postgresql:basic --app clothes-closet-rest

Creating heroku-postgresql:basic on ⬢ clothes-closet-rest... $9/month
Database has been created and is available
 ! This database is empty. If upgrading, you can transfer
 ! data from another database with pg:copy

Created postgresql-adjacent-91726 as DATABASE_URL
Use heroku addons:docs heroku-postgresql to view documentation
```

You then can only access it via the Heroku CLI:

```bash
$ heroku pg:psql --app clothes-closet-rest
 ›   Warning: heroku update available from 7.60.2 to 7.67.1.
--> Connecting to postgresql-adjacent-91726
psql (14.6 (Homebrew))
SSL connection (protocol: TLSv1.3, cipher: TLS_AES_256_GCM_SHA384, bits: 256, compression: off)
Type "help" for help.

clothes-closet-rest::DATABASE=> 

```

Doing this generated a DATABASE_URL env var on the app, which will rotate automatically with the credentials. Unforuntatley this URL is actually wrong for JDBC, so I had to write code to extract the creds and generate the URL correctly.

## Calendar Setup

This only had to be done once, but it is worth noting how I did it.

This one one of those things where most of the internet was garbage in terms of figuring this out.

1. You have to have a Google Application
2. That application needs a service account, where you have the JSON export of those credentials
3. You have to grant that application the abilty to use the calendar API
4. You have to add the service account by its email to that calendar, in this case `clothes-closet-rest@clothes-closet-374119.iam.gserviceaccount.com` to the calendar you want to use under sharing
5. You have to then access that calendar by its ID, for eample for dev I use `2dbcdac838ad46afef97271b63c8dc213a523a33f85f1b83ea3cc162d14e6963@group.calendar.google.com`





