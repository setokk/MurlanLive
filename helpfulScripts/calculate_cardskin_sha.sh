#!/usr/bin/env bash

set -eo pipefail

IMG_EXTENSIONS="jpg|jpeg|png"
ROOT_DIR="$1"

combined_sha=$(find "${ROOT_DIR}" -maxdepth 1 -type f \
    | grep -Ei "\.($IMG_EXTENSIONS)$" \
    | sort \
    | xargs cat \
    | sha256sum \
    | awk '{print $1}')

echo "Combined SHA: $combined_sha"