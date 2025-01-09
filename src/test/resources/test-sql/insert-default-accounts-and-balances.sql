INSERT INTO account (id, number, owner_type, owner_id, account_type, currency, status, version)
OVERRIDING SYSTEM VALUE
VALUES (1, '408124878517', 'USER', 1, 'DEBIT', 'USD', 'OPEN', 0),
       (2, '408124878518', 'USER', 2, 'DEBIT', 'USD', 'OPEN', 0);

INSERT INTO balance (id, auth_balance, actual_balance, account_id)
OVERRIDING SYSTEM VALUE
VALUES
       (3, 0.00, 1000.00, 1),
       (4, 0.00, 0.00, 2);
