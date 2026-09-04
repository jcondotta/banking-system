resource "aws_dynamodb_table" "banking_entities" {
  name         = local.banking_entities_table_name
  billing_mode = "PAY_PER_REQUEST"

  hash_key  = "partitionKey"
  range_key = "sortKey"

  attribute {
    name = "partitionKey"
    type = "S"
  }

  attribute {
    name = "sortKey"
    type = "S"
  }

  attribute {
    name = "iban"
    type = "S"
  }

  global_secondary_index {
    name            = "gsi-iban"
    hash_key        = "iban"
    projection_type = "ALL"
  }

  tags = {
    Name = local.banking_entities_table_name
    Tier = local.tier
  }
}