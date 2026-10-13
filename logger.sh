BLUE='\033[0;34m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m'

info() {
  echo -e "[${BLUE}INFO${NC}]: $1"
}

success() {
  echo -e "[${GREEN}SUCCESS${NC}]: $1"
}

warn() {
  echo -e "[${YELLOW}WARN${NC}]: $1"
}

error() {
  echo -e "[${RED}ERROR${NC}]: $1"
}

log_as_info() {
  while IFS= read -r line; do
    info "$line"
  done
}