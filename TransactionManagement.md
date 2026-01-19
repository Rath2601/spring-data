### DB Transaction Principles (ACID)

* **Atomicity (All or nothing)** : operations all succeed or all fail as a **single unit**. If fails, the entire transaction is rolled back.
* **Consistency (Rules are preserved)** : moves the database from a state to another state, ensuring all constraints and rules are maintained. If something goes wrong, changes rolled back
* **Isolation (Transactions don’t interfere)** : Multiple transactions running at the same time do not negatively affect each other. (READ COMMITTED, REPEATABLE READ, SERIALIZABLE)
* **Durability (Changes are permanent)** : Once a transaction is committed, its changes are permanently saved, even if the system crashes afterward.
---

### Transaction Isolation & Propagation

#### Isolation
Isolation defines how concurrent transactions interact to maintain data consistency and integrity.

##### Transaction management in Spring
* Spring only requests an isolation level. The database actually enforces it.
* Each request usually runs in its own transaction, and isolation decides, Whether it sees old data, new data, waits or fails

##### Common Read Problems

* **Dirty Read** : Reading data from another transaction that has not yet been committed.
* **Non-Repeatable Read** : Reading the same data twice within a transaction and getting different results because another transaction committed changes in between.
* **Phantom Read** : Re-executing a query returns additional or missing rows because another transaction inserted or deleted data.
---
##### Isolation Levels

* **READ UNCOMMITTED** : Allows dirty reads, non-repeatable reads, and phantom reads. (Not supported by most databases)
* **READ COMMITTED** : Prevents dirty reads. (Non-repeatable reads and phantom reads may occur)
* **REPEATABLE READ** : Prevents dirty reads and non-repeatable reads. (Phantom reads may still occur)
* **SERIALIZABLE** : Prevents dirty reads, non-repeatable reads, and phantom reads. (Transactions execute as if they run sequentially) Guarantees consistent snapshot & Detects conflict

---
#### Propagation
Propagation defines how a transaction behaves when called within another transaction.  
It determines transaction boundaries and interaction with existing transactions.

* **REQUIRED** : Join existing transaction if present, otherwise create a new one.
* **REQUIRES_NEW** : Suspend existing transaction and create a new independent transaction.
* **SUPPORTS** : Join existing transaction if present, otherwise execute without a transaction.
* **MANDATORY** : Must run inside an existing transaction. Throws exception if none exists.
* **NOT_SUPPORTED** : Suspend existing transaction and run without a transaction.
* **NEVER** : Throws exception if a transaction already exists.
* **NESTED** : Runs inside parent transaction using savepoints. Can roll back independently without rolling back the parent transaction.(Only supported by databases that support savepoints)
---

### MVCC (Modern Database Behavior)
* MVCC stores multiple versions of the same row.

Most modern databases (PostgreSQL, MySQL InnoDB, Oracle) use **MVCC – Multi-Version Concurrency Control**.

Because of MVCC:
- Each transaction sees a **snapshot** of data (Reads from a consistent snapshot)
- Dirty reads never occur (Never sees uncommitted data)
- Is isolated from concurrent writers
- `REPEATABLE_READ` usually hides both updates and new rows
- `SERIALIZABLE` adds **conflict detection** instead of heavy locking

Isolation level defines:
- Which committed versions are visible
- Whether new rows appear
- Whether repeated reads change

Result:
- Fast reads  
- High consistency  
- Minimal blocking (Non-blocking reads)
- No dirty reads
- High concurrency

Each transaction lives in its own **visibility bubble**.

---
### Key Isolation Truths

- Each transaction sees data based on when it **starts**
- `READ_COMMITTED` → sees latest committed version per query
- `REPEATABLE_READ` / `SERIALIZABLE` → sees snapshot from transaction start
- `SERIALIZABLE` additionally checks conflicts before commit
- Higher isolation protects only the **current transaction**
- Higher isolation does **not** restrict lower isolation transactions

---
### What Isolation & MVCC Does NOT Solve

Eventhough with Isolation in Transaction & MVCC, it does **not** prevent:
- Lost updates (Two transactions updating the same row)
- Double debit (two transactions spending same balance) (Logical business conflicts)
- Write–write conflicts (Overwriting each other's changes)
---

### Locking vs Isolation (Practical Use)

**Isolation controls what you see.**  
**Locking controls what you can safely change.**

MVCC and isolation provide consistent snapshots, but they **do not fully prevent** lost updates, double debits, or write–write conflicts at default isolation levels.  
Locking and versioning are required to guarantee business correctness.

---

#### Common Locking Strategies
##### Pessimistic Lock
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT p FROM Product p")
```
* Locks selected rows so other transactions must wait before modifying them.
* Prevents lost updates and double-debit problems.
* Used for money transfers, inventory, reservations.
##### Optimistic Lock
```java
 @Version
 private Long version;
```
* Adds a version field to detect if another transaction updated the row first.
* If a conflict occurs, the transaction fails and must retry. (First update succeeds, Second update fails with exception)
* Prevents silent overwriting of updates.
* Used for user profiles, CMS edits
##### Read-only Transaction 
```java
@Transactional(readOnly = true)
```
* Hint to ORM & DB that no writes will occur
* Skips dirty checking
* Avoids flush
* May use lighter locks
* Improves performance

### Final Memory Line
 | Component | What it provides | What happens if it's missing | 
 | MVCC | High Concurrency | The DB would be incredibly slow because readers would have to wait for writers to finish | 
 | Isolation | Consistency Rules | "You would see ""partial"" data or data that changes mid-calculation, leading to logic errors." | 
 | Locks | Data Integrity | "Multiple transactions would overwrite each other’s changes, leading to ""Lost Updates.""" | 
