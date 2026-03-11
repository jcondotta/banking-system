#!/bin/bash

set -e

awslocal dynamodb update-table \
  --table-name bank-accounts \
  --attribute-definitions \
      AttributeName=gsi1pk,AttributeType=S \
      AttributeName=gsi1sk,AttributeType=S \
  --global-secondary-index-updates '[
    {
      "Create": {
        "IndexName": "gsi-outbox-status",
        "KeySchema": [
          { "AttributeName": "gsi1pk", "KeyType": "HASH" },
          { "AttributeName": "gsi1sk", "KeyType": "RANGE" }
        ],
        "Projection": {
          "ProjectionType": "ALL"
        }
      }
    }
  ]'