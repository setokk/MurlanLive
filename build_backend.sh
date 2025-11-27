#!/bin/bash
set -eo pipefail

MAN=$(cat <<-END
Usage: $0 [--skip-gameserver <BUILD_GAMESERVER: boolean>] [--skip-umserver <BUILD_UMSERVER: boolean>]
                          [--skip-db <BUILD_DB: boolean>] [--skip-drop-create <DROP_CREATE_DB: boolean>]

List of available options:

    --skip-gameserver       Skips the build process of the game server (WebSocket real-time server).
                            Default is false.
    --skip-umserver         Skips the build process of the user management server.
                            Default is false.
    --skip-db               Skips the build process of the database.
                            Default is false.
    --skip-drop-create      Skips dropping and recreating all database entities.
                            Default is false.
END
)

source build_backend.env.sh

MIGRATION_SQL_SCRIPTS="./MuLiveUM/src/main/resources/migration"
BUILD_GAMESERVER=true
BUILD_UMSERVER=true
BUILD_DB=true
DROP_CREATE_DB=true
while [[ "$#" -gt 0 ]]; do
  if [[ "$1" = "--skip-gameserver" ]]; then
    BUILD_GAMESERVER=false
  elif [[ "$1" = "--skip-umserver" ]]; then
    BUILD_UMSERVER=false
  elif [[ "$1" = "--skip-db" ]]; then
    BUILD_DB=false
  elif [[ "$1" = "--skip-drop-create" ]]; then
    DROP_CREATE_DB=false
  elif [[ "$1" = "--help" || "$1" = "-h" ]]; then
    echo "$MAN"
    exit 0
  fi
  shift
done

cp ./wait-for-it.sh ./MuLiveUM/
cp ./wait-for-it.sh ./MuLiveGameServer/

declare -a SERVICES_TO_BUILD
if [ $BUILD_DB = true ]; then
  SERVICES_TO_BUILD+=("db")
fi
if [ $BUILD_UMSERVER = true ]; then
  SERVICES_TO_BUILD+=("umserver")
fi
if [ $BUILD_GAMESERVER = true ]; then
  SERVICES_TO_BUILD+=("gameserver")
fi
docker-compose up -d --build "${SERVICES_TO_BUILD[@]}"

if [ $DROP_CREATE_DB = true ]; then
  chmod +x ./wait-for-it.sh && ./wait-for-it.sh localhost:5432 && sleep 2
  docker exec -it mulive-db psql -U postgres -c "DROP DATABASE IF EXISTS ${POSTGRES_DB};"
  docker exec -it mulive-db psql -U postgres -c "CREATE DATABASE ${POSTGRES_DB} WITH ENCODING 'UTF8' LC_COLLATE 'en_US.UTF-8' LC_CTYPE 'en_US.UTF-8' TEMPLATE template0;"
  docker cp "${MIGRATION_SQL_SCRIPTS}" mulive-db:/ # Copy migration scripts to database container
  sql_scripts=($(ls -1v ${MIGRATION_SQL_SCRIPTS}))
  for script in "${sql_scripts[@]}"; do
    echo -e "Executing SQL script: ${script}"
    docker exec -it mulive-db psql -U postgres -d "${POSTGRES_DB}" --set ON_ERROR_QUIT=1 -f "migration/${script}"
  done

  # After running DB scripts, check if umserver is to be built.
  # If it is, stop and start again in order for SpringBoot to start-up with the updated DB
  for service in "${SERVICES_TO_BUILD[@]}"; do
      if [[ "$service" == "umserver" ]]; then
          docker stop mulive-umserver
          docker start mulive-umserver
          break
      fi
  done
fi