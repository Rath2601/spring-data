### spring-data-integration

#### major components
* **Database Connection** : To interact with a database Key Elements needed: **Database URL, Username & Password, JDBC Driver**
* **Connection pooling** : reuses existing connections (constant connections already made during startup) (**Faster DB access, Better resource usage**)
* **Data Access Layer** : database operations on entity/model classes
   * Spring JDBC → Direct SQL with helper classes
   * MyBatis → SQL mapping framework
   * Spring Data JPA → ORM-based data access
* **Transaction Management** : DB operations execute safely as a single unit (Rollback on failure, and maintain consistency)
* **ORM** : Maps Java objects to database tables. Converts objects ↔ rows automatically and **reduce manual SQL**
* **Database Migration** : Used to version-control database schema changes
