#!/bin/bash
set -eo pipefail

export POSTGRES_DB="mulive"
export POSTGRES_USER="postgres"
export POSTGRES_PASSWORD="root!238Ji*"
export JDBC_DATASOURCE_URL="jdbc:postgresql://db:5432/${POSTGRES_DB}"
export MULIVE_GAMESERVER_SECRET_KEY="&<yDuTf;$]lmYrgleaL+Na@%Lg8KA(Kr!F_-B:ZbmGR1sa]phygge#9nL089XcG:"
