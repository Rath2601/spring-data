### DB Transaction Principles (ACID)

* **Atomicity (All or nothing)** : operations all succeed or all fail as a **single unit**. If fails, the entire transaction is rolled back.
* **Consistency (Rules are preserved)** : moves the database from a state to another state, ensuring all constraints and rules are maintained. If something goes wrong, changes rolled back
* **Isolation (Transactions don’t interfere)** : Multiple transactions running at the same time do not negatively affect each other. (READ COMMITTED, REPEATABLE READ, SERIALIZABLE)
* **Durability (Changes are permanent)** : Once a transaction is committed, its changes are permanently saved, even if the system crashes afterward.
---

### Transaction Isolation & Propagation

#### Isolation
Isolation defines how concurrent transactions interact to maintain data consistency and integrity.

##### Common Read Problems

**Dirty Read**  
Reading data from another transaction that has not yet been committed.

**Non-Repeatable Read**  
Reading the same data twice within a transaction and getting different results because another transaction committed changes in between.

**Phantom Read**  
Re-executing a query returns additional or missing rows because another transaction inserted or deleted data.

---

##### Isolation Levels

**READ UNCOMMITTED**  
Allows dirty reads, non-repeatable reads, and phantom reads.  
(Not supported by most databases)

**READ COMMITTED**  
Prevents dirty reads.  
Non-repeatable reads and phantom reads may occur.

**REPEATABLE READ**  
Prevents dirty reads and non-repeatable reads.  
Phantom reads may still occur.

**SERIALIZABLE**  
Prevents dirty reads, non-repeatable reads, and phantom reads.  
Transactions execute as if they run sequentially.

---

#### Propagation

Propagation defines how a transaction behaves when called within another transaction.  
It determines transaction boundaries and interaction with existing transactions.

**REQUIRED**  
Join existing transaction if present, otherwise create a new one.

**REQUIRES_NEW**  
Suspend existing transaction and create a new independent transaction.

**SUPPORTS**  
Join existing transaction if present, otherwise execute without a transaction.

**MANDATORY**  
Must run inside an existing transaction. Throws exception if none exists.

**NOT_SUPPORTED**  
Suspend existing transaction and run without a transaction.

**NEVER**  
Throws exception if a transaction already exists.

**NESTED**  
Runs inside parent transaction using savepoints.  
Can roll back independently without rolling back the parent transaction.  
(Only supported by databases that support savepoints)

---
