### Hibernate

#### Entity States

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

#### General working
* Flush = Synchronize in-memory changes → Database SQL.
* 
