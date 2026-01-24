* EAGER: loads relations automatically → hidden joins → N+1 → ❌ production risk LAZY: loads on access via proxy → predictable → ✅ preferred Defaults are dangerous (@ManyToOne / @OneToOne = EAGER)
* N+1 problem: 1 query for parents + N queries for children Happens with EAGER or LAZY (when accessed in loops) Scales badly in production 1 query → select * from university N queries → select * from student where university_id = ?


* Cache reality: Reduces DB hits , Does not remove N+1 (queries still execute), Optimization, not a fix
* Correct fix: Use JOIN FETCH for entities, Use DTO projections for APIs (Interface projections are perfect for simple reads; JPQL DTOs win for complex APIs. Choosing what fields to return at runtime instead of always returning full entities)
* Golden rule: Everything LAZY , Fetch explicitly per use-case , Never rely on defaults or cache
* @EntityGraph --> hibernate generation of JOIN query instead of manual JPQL query ex: @EntityGraph(attributePaths = "customer")        List<Order> findByStatus(String status);Batch fetching
* @BatchSize --> loading children object in group (Legacy N+1 mitigation) ex: @ManyToOne(fetch = FetchType.LAZY)       @BatchSize(size = 10)        Customer customer;   // SELECT * FROM customer WHERE id IN (1,2,3,4,5,6,7,8,9,10); Batch sizing mitigate 1+N query issue but doesn't avoids it.
* Pagination pitfall : DO NOT paginate with JOIN FETCH on @OneToMany / @ManyToMany
