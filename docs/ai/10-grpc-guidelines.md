# gRPC Guidelines

Use this guide for protobuf definitions, gRPC services, and their clients. Adapter responsibilities belong in [Infrastructure](08-infrastructure-guidelines.md); use [Design](02-design.md) to resolve behavior, compatibility, versioning, and rollout choices.

## Locate The RPC Boundary

Start with the `.proto` source, generation configuration, service implementation, mapper, exception handler, and known clients. Include clients outside the repository and report any that cannot be inspected.

Accounts' [bank-account lookup protobuf](../../services/accounts/src/main/proto/banking/accounts/v1/bank_account_lookup_service.proto) is an existing entry point, alongside `BankAccountLookupGrpcController`, `BankAccountGrpcMapper`, and `AccountsGrpcExceptionHandler`.

Generated Java files are not the contract's editing source. Follow the service and message definitions through the application mapping and client interpretation.

## Review The RPC Contract

For the affected RPC, check:

- proto package, service, method, and request/response messages
- field numbers, types, presence/defaults, repeated fields, and enum values
- identifier, date, and timestamp mappings and their meaning
- input validation and status/error translation
- ownership, authorization, and sensitive-data exposure

A new RPC does not automatically inherit the exposure or error decisions of another delivery mechanism.

## Compatibility And Evolution

Compare previous and proposed definitions and behavior against the affected generated clients. Assess field-number, type, and presence changes explicitly, including mixed client/server versions and deployment order. Do not infer compatibility from successful code generation or a versioned proto package.

For intentional incompatibilities, resolve client regeneration/updates, coexistence, and rollout dependencies during Design. When retries or timeouts are affected, inspect whether an RPC can have completed its business operation before the caller observes failure.

## Verification

Use [Testing](04-testing.md) for test style and scope. Accounts' gRPC mapper, controller, and exception-handler tests provide local examples; inspect what each verifies before claiming client compatibility or runtime wiring coverage.

Verify the affected protobuf representation, service behavior, and client interpretation. Keep the schema, mappings, and tests aligned, and report unverified client assumptions during [Validation](05-validation.md).
