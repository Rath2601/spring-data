### Hibernate Notes
#### General working
* Flush = Synchronize in-memory changes → Database SQL.
* Commit always triggers flush. Flush alone does not permanently save until commit.
* AUTO flush ensures JPQL queries always run on up-to-date database state
* Hibernate tracks changes to persistent entities. No need to write UPDATE SQL manually. (Active transaction & Entity must be persistent needed)
* the EntityManager is usually created and destroyed per transaction
* Repositories are transactional by default. Services usually control transactions with @Transactional
* We can use Hibernate alone instead of using Spring data JPA
* During transaction commit flush automatically happens
* persist(), flush(), merge(), remove(), refresh() methods require transaction compulsorily.  (TransactionRequiredException)
* em.merge(order) -> Creates a new copy and makes it Persistent, ID for order remains null since it is in detached state. 
* Merge is to sync new changes in same entry in DB meanwhile persist used for new entry in DB
* Use LAZY by default. Use EAGER only when always needed and small.

| Step             | JDBC / MyBatis   | JPA / Hibernate        |
| ---------------- | ---------------- | ---------------------- |
| Load data        | You write SELECT | JPA writes SELECT      |
| Change value     | Change variable  | Change entity field    |
| Write UPDATE SQL | You must         | ❌ Not needed           |
| Execute UPDATE   | You must         | JPA does automatically |
| Forget UPDATE    | No DB change     | DB still updates       |


| Situation              | Parent Loaded | Child Loaded |
| ---------------------- | ------------- | ------------ |
| `find()` + LAZY        | ✅ Yes         | ❌ No         |
| `find()` + EAGER       | ✅ Yes         | ✅ Yes        |
| LAZY + `getChildren()` | ✅ Yes         | ✅ Yes        |

#### Entity States

* Manually setting an ID makes it detached , we can’t persist detached object (EntityExistsException)

| State          | Description                                         |
| -------------- | --------------------------------------------------- |
| **Transient**  | New object, not associated with persistence context |
| **Persistent** | Managed by EntityManager, tracked for changes       |
| **Detached**   | Previously persistent, but EntityManager closed     |
| **Removed**    | Marked for deletion                                 |

Note : Manually setting an ID on a new entity makes it Detached, so persist() throws EntityExistsException

#### EntityManager

| Method      | Purpose                             | Requires Transaction |
| ----------- | ----------------------------------- | -------------------- |
| `persist()` | Insert new entity                   | ✅ Yes                |
| `merge()`   | Update detached entity / copy state | ✅ Yes                |
| `remove()`  | Delete entity                       | ✅ Yes                |
| `flush()`   | Sync persistence context → DB       | ✅ Yes                |
| `refresh()` | Reload from DB                      | ✅ Yes                |

| Step            | `persist(order)`      | `merge(order)`             |
| --------------- | --------------------- | -------------------------- |
| Initial State   | Transient             | Transient                  |
| Action          | Becomes persistent    | New managed copy created   |
| ID Assignment   | `order.id` gets value | Only copy gets ID          |
| Returned Object | Same instance         | Must use returned instance |

