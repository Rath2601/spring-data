* **EntityManager** is the JPA-defined interface **Session** is Hibernate’s concrete implementation.
* **First-Level Cache (L1)**: A temporary memory inside a single Hibernate session that stores objects fetched during that session so the same data isn’t read from the database again within the same transaction. 
**Second-Level Cache (L2)**: A shared memory store across multiple Hibernate sessions that keeps frequently used data so different transactions don’t repeatedly hit the database for the same records. 
**Query Cache**: A cache that stores the results of executed queries (usually record IDs), so when the same query runs again, Hibernate can skip re-executing the SQL if the data hasn’t changed. (Recommended only for static data)

* **Flyway / Liquibase** —>
    - Database migration tools that manage schema versioning using versioned change scripts
    - Maintain a DATABASECHANGELOG / flyway_schema_history table to track executed migrations.
    - Run only new change sets on application startup Support incremental schema evolution (CREATE, ALTER, DROP) without data loss.
* Liquibase: supports Rollback (if anything goes wrong undo migrations safely), Preconditions (ex: if table exist already no tables be created), multiple formats (SQL, YAML, XML, JSON), and declarative changeSets Tags : mark DB states & can roll back to a specific release , Labels : used for grouping changeSets , Liquibase internally converts other formats to DB-specific SQL
* Flyway: SQL-first, simpler and lightweight , convention-based naming (V1__, V2__),(001_, 002_), 


* **DB optimizations** → Use vendor SQL for better performance
* **HikariCP tuning** → Efficient DB connection reuse
* **Read/Write routing** → Scale reads using replicas
* **Multi-tenancy** → Isolate customer data in shared systems
  Tenant Count	Typical Choice	Reason
     - 1 – 1,000 tenants	Database-per-Tenant	Easy to manage, strong isolation
     - 1,000 – 50,000 tenants	Schema-per-Tenant	Balance of isolation & cost
     - 50,000 – Millions of tenants	Shared DB (tenant_id column)	Only cost-feasible approach
