### DB Transaction Principles (ACID)

#### Atomicity (All or nothing) : A transaction’s operations all succeed or all fail as a single unit. If any step fails, the entire transaction is rolled back.
#### Consistency (Rules are preserved) : A transaction moves the database from one valid state to another valid state, ensuring all constraints and rules are maintained. If something goes wrong, changes are rolled back to keep data consistent.
#### Isolation (Transactions don’t interfere) : Multiple transactions running at the same time do not negatively affect each other. Controlled by isolation levels like READ COMMITTED, REPEATABLE READ, SERIALIZABLE.
#### Durability (Changes are permanent) : Once a transaction is committed, its changes are permanently saved, even if the system crashes afterward.
