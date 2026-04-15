#!/bin/bash
set -e

echo "🚀 Creating DynamoDB table: account-recipients..."

awslocal dynamodb create-table \
  --table-name account-recipients \
  --attribute-definitions \
      AttributeName=partitionKey,AttributeType=S \
      AttributeName=sortKey,AttributeType=S \
  --key-schema \
      AttributeName=partitionKey,KeyType=HASH \
      AttributeName=sortKey,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST

echo "✅ DynamoDB table 'account-recipients' created successfully."