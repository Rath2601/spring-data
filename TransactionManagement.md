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
## Transaction Visibility Matrix (MVCC + Isolation)

### Read + Write  
**Question:** Does transaction **X** see changes made by transaction **Y**?

| X Isolation | Y Isolation | X sees Y’s change? | Detailed Reason |
|------------|------------|--------------------|-----------------|
| RC | RC | ✔ | RC reads the latest committed data. If Y commits before X reads again, X sees Y’s changes (non-repeatable read possible). |
| RC | RR | ✔ | Even though Y uses a snapshot, once Y commits, RC always reads the latest committed version. |
| RC | S | ✔ | Serializable also commits changes normally; RC always sees latest committed data. |
| RR | RC | ✖ | RR uses a snapshot taken at transaction start, so later commits from Y are hidden. |
| RR | RR | ✖ | Both use snapshots from their own start time; X never sees Y’s later commits. |
| RR | S | ✖ | Serializable uses snapshot visibility; X does not see Y’s committed changes during its transaction. |
| S | RC | ✖ / ⚠ | Serializable X uses a snapshot, so it does not see Y’s changes. If Y modified data X read, X may fail at commit due to conflict detection. |
| S | RR | ✖ / ⚠ | Same snapshot behavior hides Y’s changes. If conflicting read/write pattern detected, X aborts at commit. |
| S | S | ✖ / ⚠ | Both run on snapshots. If they conflict on same data, one transaction will be aborted to preserve serial order. |

---

### Write + Read  
**Question:** Does transaction **Y** see changes made by transaction **X**?

| X Isolation | Y Isolation | Y sees X’s change? | Detailed Reason |
|------------|------------|--------------------|-----------------|
| RC | RC | ✔ | Y reads latest committed data. If X commits before Y reads, Y sees X’s changes. |
| RC | RR | ✖ | Y’s snapshot was taken before X committed, so X’s changes are hidden. |
| RC | S | ✖ | Serializable Y uses snapshot isolation; it does not see X’s later commit. |
| RR | RC | ✔ | RC always reads latest committed data, so Y sees X’s commit. |
| RR | RR | ✖ | Y works on its own snapshot and does not see X’s later commit. |
| RR | S | ✖ / ⚠ | Y does not see X’s changes due to snapshot. If both updated same data, one may abort at commit. |
| S | RC | ✔ | RC reads latest committed data, so Y sees X’s commit. |
| S | RR | ✖ | RR snapshot hides X’s later commit. |
| S | S | ✖ / ⚠ | Both use snapshots. If X and Y update same data, one transaction will abort at commit. |

---

## Legend

✔ → Change is visible  
✖ → Change is not visible due to snapshot rules  
⚠ → Transaction may abort at commit due to SERIALIZABLE conflict detection

---

## Key Takeaway

- **READ COMMITTED** → Always reads latest committed data.  
- **REPEATABLE READ** → Always reads snapshot from transaction start.  
- **SERIALIZABLE** → Same as snapshot + prevents conflicts by aborting one transaction.

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
##### Pessimistic Lock (Real-time use case: Account balance / wallet credit) 
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT p FROM Product p")
```
* Two transactions read same row, DB row locked on read Second transaction waits, No lost updates, Strong consistency guaranteed
*  Locks selected rows so other transactions must wait before modifying them.
* Prevents lost updates and double-debit problems.
* Used for money transfers, inventory, reservations.
  
##### Optimistic Lock (Real-time use case: User profile / CMS edit)
```java
 @Version
 private Long version; // in entity
```
* Adds a version field to detect if another transaction updated the row first.
* If a conflict occurs, the transaction fails and must retry. (First update succeeds, Second update fails with exception)
* Prevents silent overwriting of updates. (if both transactions modifies the entity)

##### Deadlock

* Trans A locks row X and waits for row Y, Trans B locks row Y and waits for row X
* Database detects this and kills one transaction
* The database throws an error that is translated by Spring into exceptions like: DeadlockLoserDataAccessException, PessimisticLockingFailureException, CannotAcquireLockException
* **Common causes**:
    - Concurrent updates on same rows
    - Different locking order across transactions
    - Pessimistic locking (@Lock(LockModeType.PESSIMISTIC_WRITE))
    - Long-running transactions
 * Using Spring Retry with @Retryable annotation we can retry certain transactional failure
 * **Preventing Deadlocks** :
    - Always lock rows in the same order
    - Keep transactions short
    - Prefer optimistic locking
    - Use pessimistic locks only when necessary
    - Use proper indexes to avoid table scans


##### Read-only Transaction (used in pagination & reporting APIs)
```java
@Transactional(readOnly = true)
```
* Hint to ORM & DB that no writes will occur
* Does not block write queries , but Does not throw exceptions on modification
* Skips dirty (Better performance)
* Avoids flush (Sets Hibernate FlushMode = MANUAL)
* May use lighter locks
* Improves performance

### Final Memory Line
 | Component | What it provides | What happens if it's missing | 
 |----------|------------------|---------------------------|
 | MVCC | High Concurrency | The DB would be incredibly slow because readers would have to wait for writers to finish | 
 | Isolation | Consistency Rules | You would see **partial** data or data that changes mid-calculation, leading to logic errors. | 
 | Locks | Data Integrity | Multiple transactions would overwrite each other’s changes, leading to Lost Updates. | 
