### LOADING ENTITY FROM DB
* **EAGER**: loads relations automatically,hidden joins, **N+1 Queries (production risk)**
* **LAZY**: loads on access via proxy, predictable, **preferred Defaults are dangerous (@ManyToOne / @OneToOne = EAGER)**
* **N+1 problem**: **1 query for parents + N queries for children** Happens with EAGER or LAZY (when accessed in loops) Scales badly in production
* (1 query → select * from university , N queries → select * from student where university_id = ?)

| Situation              | Parent Loaded | Child Loaded |
| ---------------------- | ------------- | ------------ |
| `find()` + LAZY        | ✅ Yes         | ❌ No         |
| `find()` + EAGER       | ✅ Yes         | ✅ Yes        |
| LAZY + `getChildren()` | ✅ Yes         | ✅ Yes        |

#### FIX
* **Cache** : Reduces DB hits , Does not remove N+1 (queries still execute), Optimization, not a fix
* **Use JOIN FETCH for entities, Use DTO projections for APIs** (Interface projections are perfect for simple reads; JPQL DTOs win for complex APIs. Choosing what fields to return at runtime instead of always returning full entities)
* **Golden rule**: Everything LAZY , Fetch explicitly per use-case , **Never rely on defaults or cache**
* **@EntityGraph** --> hibernate generation of JOIN query instead of manual JPQL query **(prevent N+1)**
```java
@EntityGraph(attributePaths = "customer")        
List<Order> findByStatus(String status); //Batch fetching

SELECT o.*, c.*
FROM orders o
LEFT JOIN customer c ON o.customer_id = c.id   // hibernate generate single SQL query
WHERE o.status = ?
```
* **@BatchSize** --> loading children object in group (**N+1 mitigation**) 
```java
@ManyToOne(fetch = FetchType.LAZY)
@BatchSize(size = 10)
Customer customer;   // SELECT * FROM customer WHERE id IN (1,2,3,4,5,6,7,8,9,10); Batch sizing mitigate 1+N query issue but doesn't avoids it.
```
* **Pagination pitfall** : **DO NOT paginate with JOIN FETCH on @OneToMany/@ManyToMany**
  (Pagination must work on parent objects, not on joined rows : if I have 2 Orders and 3 orderItems I'd have 6 rows total and pagination based on that, Parents get split across pages)
