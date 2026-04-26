#!/bin/bash
set -e

echo "🚀 Creating DynamoDB table: recipients..."

awslocal dynamodb create-table \
  --table-name recipients \
  --attribute-definitions \
      AttributeName=partitionKey,AttributeType=S \
      AttributeName=sortKey,AttributeType=S \
      AttributeName=gsi1pk,AttributeType=S \
      AttributeName=gsi1sk,AttributeType=S \
  --key-schema \
      AttributeName=partitionKey,KeyType=HASH \
      AttributeName=sortKey,KeyType=RANGE \
  --global-secondary-indexes '[
    {
      "IndexName": "gsi1-recipients-by-account",
      "KeySchema": [
        { "AttributeName": "gsi1pk", "KeyType": "HASH" },
        { "AttributeName": "gsi1sk", "KeyType": "RANGE" }
      ],
      "Projection": {
        "ProjectionType": "ALL"
      }
    }
  ]' \
  --billing-mode PAY_PER_REQUEST

echo "✅ DynamoDB table 'recipients' created successfully."