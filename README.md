# Account Service

**Account Service** is a microservice responsible for managing payment accounts and their associated operations. It handles the creation of payment accounts, balance management, cumulative (savings) accounts with tariff management, generation and allocation of unique account numbers, auditing of balance changes, and payment processing via a Dual Message System (DMS).

---

## Overview

The main responsibilities of the service include:

- **Payment Account Creation:**  
  - Create and manage payment accounts for both users and projects.
  - Each account includes fields such as account number (12–20 digits), owner (user or project), type (e.g., current, currency), currency (RUB, EUR, USD, etc.), status (active, frozen, closed), timestamps for creation, modification and closing, and version for optimistic locking.

- **Balance Management:**  
  - Every account has a balance with two components: the current authorized balance and the current actual balance.
  - Supports a two-phase payment process: Authorization (reserve funds) and Clearing (actual deduction).
  - Timestamps and versioning are maintained for auditing and conflict detection.

- **Cumulative Accounts & Tariffs:**  
  - Special savings accounts allow users to accumulate funds with interest.
  - A savings account reuses fields from the main account but adds extra fields: balance, tariff history (as a JSON list or separate table), last interest calculation date, and version/timestamps.
  - Tariffs include an identifier, a name/type (e.g., basic, promo, subscription), and a history of interest rates (stored as JSON or in a dedicated table).

- **Unique Account Numbers Generation:**  
  - Pre-generation and management of unique account numbers to speed up account creation.
  - Uses dedicated tables to store free account numbers and an account number sequence per account type.
  - Integrates with a scheduled job to ensure a sufficient pool of pre-generated account numbers.

- **Balance Audit:**  
  - Maintains an immutable audit trail for any changes to an account's balance.
  - Audit records store the account number, balance version, authorized and actual amounts, operation identifier, and a timestamp.
  - Ensures traceability for financial operations and supports compliance, reporting, and dispute resolution.

- **Payment via DMS (Dual Message System):**  
  - Implements a two-stage payment process:
    - **Authorization Message:** Reserves funds on the payer’s account.
    - **Clearing Message:** Finalizes the transaction and transfers funds from the payer to the payee.
  - Supports cancellation messages in case of payment failure or cancellation.
  - Uses asynchronous processing (e.g., via Redis messaging, scheduled jobs) to handle pending operations and trigger confirmation or cancellation actions.

---

## Technical Details

### Database & Migrations

- **Payment Account Table:**  
  A migration script creates the `account` table with all necessary fields (account number, owner, type, currency, status, creation/update/closing timestamps, version) and proper indexing (e.g., for fast search by user).

- **Balance Table:**  
  A migration creates the `balance` table with fields for authorized and actual balance, timestamps, and version. A one-to-one mapping with the `account` entity is set up.

- **Savings Account & Tariff Tables:**  
  Migrations for `savings_account` and `tariff` tables include fields for tariff history, last interest calculation date, and other required details. Savings accounts are linked to accounts via a one-to-one relationship.

- **Free Account Numbers & Sequence Tables:**  
  Migrations to create tables for managing free account numbers (`free_account_numbers`) and for storing the current counter (`account_numbers_sequence`) ensure unique account number generation per account type.

- **Balance Audit Table:**  
  A migration for `balance_audit` stores immutable records for every change to an account’s balance, including versioning and timestamps.

### Entities & Repository Layer

- **Entities:**  
  - `Account` – represents payment accounts.
  - `Balance` – holds account balance information.
  - `SavingsAccount` – extends account functionality for savings, linked to tariff.
  - `Tariff` – stores tariff details and history.
  - `BalanceAudit` – records every change to balances.
  
- **Repositories:**  
  Standard repositories (using Spring Data JPA’s `CrudRepository` or `JpaRepository`) are used for CRUD operations, with custom repository methods for operations such as fetching and atomically deleting free account numbers.

### Services & Controllers

- **Account Service:**  
  Exposes endpoints for:
  - **get:** Retrieving account details.
  - **open:** Creating a new account (with integration of free account number allocation).
  - **block:** Freezing or blocking an account.
  - **close:** Closing an account.  
  Optimistic locking is applied to prevent concurrent update conflicts.

- **Balance Service:**  
  Provides methods to create and update account balances while maintaining audit records.

- **Savings Account & Tariff Service:**  
  Includes operations for:
  - Opening a savings account.
  - Retrieving a savings account by account ID or client ID.
  - Managing tariff changes and retrieving current tariff information.
  - Scheduled jobs for generating unique numbers and processing interest accruals.

- **FreeAccountNumbersService:**  
  Manages the generation and allocation of unique account numbers, integrating with scheduled tasks to maintain a sufficient pool of numbers.

- **Payment via DMS:**  
  - Implements asynchronous payment operations with support for initiating, cancelling, and confirming payments.
  - Integrates with messaging systems (e.g., Redis) and scheduled jobs to process pending operations and handle errors.

---

## Conclusion

The **Account Service** is designed to be a robust and scalable component within the payment ecosystem. It integrates advanced techniques like optimistic locking, asynchronous processing, and detailed auditing to ensure reliability and consistency in managing payment accounts and transactions.

For any questions or further contributions, please refer to the contribution guidelines in this repository.
