#!/usr/bin/env bash
# =============================================================================
# Validate Required Secrets
# =============================================================================
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

SECRETS_FILE="$SCRIPT_DIR/app-secrets.env"
ERRORS=0

echo "============================================"
echo "  Checking Required Secrets"
echo "============================================"

if [[ ! -f "$SECRETS_FILE" ]]; then
    echo "ERROR: app-secrets.env not found!"
    echo "Copy app-secrets.env.example to app-secrets.env and fill in values."
    exit 1
fi

source "$SECRETS_FILE"

check_var() {
    local VAR_NAME="$1"
    local VAR_VALUE="${!VAR_NAME:-}"
    if [[ -z "$VAR_VALUE" || "$VAR_VALUE" == *"placeholder"* || "$VAR_VALUE" == *"your-"* || "$VAR_VALUE" == *"generate-"* ]]; then
        echo "  MISSING: $VAR_NAME"
        ERRORS=$((ERRORS + 1))
    else
        echo "  OK:      $VAR_NAME"
    fi
}

echo ""
echo "--- Core Secrets ---"
check_var "JWT_SECRET"


echo ""
echo "--- Google OAuth ---"
check_var "GOOGLE_CLIENT_ID"
check_var "GOOGLE_CLIENT_SECRET"



echo ""
echo "--- Apple Sign-In ---"
check_var "APPLE_CLIENT_ID"
check_var "APPLE_TEAM_ID"
check_var "APPLE_KEY_ID"
check_var "APPLE_PRIVATE_KEY"






echo ""
if [[ $ERRORS -gt 0 ]]; then
    echo "RESULT: $ERRORS secret(s) missing or have placeholder values."
    echo "Edit app-secrets.env before deploying."
    exit 1
else
    echo "RESULT: All required secrets are set."
fi
