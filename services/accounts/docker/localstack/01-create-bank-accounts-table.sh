#!/bin/bash

set -e

echo "🚀 Creating DynamoDB table: bank-accounts..."

awslocal dynamodb create-table \
  --table-name bank-accounts \
  --attribute-definitions \
    AttributeName=partitionKey,AttributeType=S \
    AttributeName=sortKey,AttributeType=S \
    AttributeName=iban,AttributeType=S \
  --key-schema \
    AttributeName=partitionKey,KeyType=HASH \
    AttributeName=sortKey,KeyType=RANGE \
  --global-secondary-indexes '[
    {
      "IndexName": "gsi-iban",
      "KeySchema": [{ "AttributeName": "iban", "KeyType": "HASH" }],
      "Projection": { "ProjectionType": "ALL" },
      "ProvisionedThroughput": { "ReadCapacityUnits": 5, "WriteCapacityUnits": 5 }
    }
  ]' \
  --billing-mode PROVISIONED \
  --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1

echo "✅ DynamoDB table created: bank-accounts"
