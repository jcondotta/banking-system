#!/bin/bash

set -e

echo "⏳ Enabling TTL on DynamoDB table: bank-accounts..."

awslocal dynamodb update-time-to-live \
  --table-name bank-accounts \
  --time-to-live-specification \
      "Enabled=true, AttributeName=timeToLive"

echo "✅ TTL enabled on table: bank-accounts (attribute: timeToLive)"