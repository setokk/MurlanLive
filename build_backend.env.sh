#!/bin/bash
set -eo pipefail

export POSTGRES_DB="mulive"
export POSTGRES_USER="postgres"
export POSTGRES_PASSWORD="root!238Ji*"
export JDBC_DATASOURCE_URL="jdbc:postgresql://db:5432/${POSTGRES_DB}"
export MULIVE_GAMESERVER_SECRET_HEADER="X-ML-GAMESERVER"
export MULIVE_GAMESERVER_SECRET_HEADER_VAL="test"
