#### Data Access Layers

| Category | What this category means | JDBC | MyBatis | JPA / JPQL |
|----------|---------------------------|------|---------|------------|
| Abstraction level | How far the framework hides database details from you | Very low (you see everything) | Medium (SQL hidden slightly) | High (DB mostly hidden) |
| Primary philosophy | What the framework is designed around | Database-first | SQL-first | Object-first |
| Who writes SQL | Do you write SQL manually? | Always | Always | Mostly no (JPQL instead) |
| SQL predictability | Can you know exactly what SQL runs? | 100% predictable | 100% predictable | Not always predictable |
| Boilerplate code | Extra repetitive code needed | Very high | Low | Very low |
| Learning curve | How hard it is to learn | Medium | Medium | High |
| Magic / implicit behavior | Does the framework do hidden things? | None | Very little | A lot |
| ORM support | Automatic mapping between objects & tables | ❌ No | ❌ No | ✅ Yes |
| Entity lifecycle | Does framework track object state? | ❌ | ❌ | ✅ |
| Dirty checking | Does it auto-detect object changes? | ❌ | ❌ | ✅ |
| Automatic INSERT/UPDATE | Do DB writes happen automatically? | ❌ | ❌ | ✅ |
| Cascading operations | Related objects auto-saved/deleted? | ❌ | ❌ | ✅ |
| First-level cache | Same data reused inside a transaction? | ❌ | ❌ | ✅ |
| Second-level cache | Data reused across transactions? | ❌ | ⚠️ Optional | ⚠️ Optional |
| Query cache | Query results cached? | ❌ | ⚠️ Limited | ⚠️ Limited |
| Dynamic SQL support | Can SQL change based on conditions? | ❌ Manual | ✅ Powerful | ⚠️ Limited |
| Complex queries | Handling very complex SQL | Excellent | Excellent | Average |
| Stored procedures | Can call DB stored procedures | ✅ | ✅ | ⚠️ Limited |
| Batch operations | Efficient bulk inserts/updates | Excellent | Very good | Risky unless tuned |
| Transaction management | Who controls commit/rollback | You / Spring | Spring | Spring (automatic) |
| DB portability | Can you switch databases easily? | ❌ | ⚠️ Partial | ✅ |
| Vendor lock-in | Tied to a specific DB vendor? | High | Medium | Low |
| Performance predictability | Performance behaves as expected? | Very high | High | Medium |
| Memory usage | Runtime memory consumption | Low | Low | High |
| Startup time | App startup speed | Fast | Fast | Slower |
| Debugging ease | Ease of finding bugs | Easy | Easy | Hard |
| Schema evolution | Changing DB structure safely | Manual | Manual | Semi-automatic |
| Testing simplicity | Ease of writing tests | Medium | Easy | Harder |
| Best use cases | Where it shines | Low-level systems | SQL-heavy apps | CRUD-heavy apps |
| Worst use cases | Where it struggles | Rapid dev | Pure CRUD | SQL-critical apps |
| Typical users | Who usually uses it | System devs | Backend engineers | Enterprise devs |
| Spring integration | How well it works with Spring | Good | Very good | Excellent |
| Flow | Request processing path | Spring Service → Spring JDBC (JdbcTemplate / NamedParameterJdbcTemplate) → JDBC Driver → Database | Spring Service → MyBatis Mapper Interface → MyBatis SQL Session → JDBC Driver → Database | Spring Data Repository → JPA EntityManager → Hibernate Session → JDBC Driver → Database |


NOTE : 
* Spring Data JDBC :  
    - we write queries in plain SQL.
    - We don’t use any higher-level query language like JPQL.
    - supports query methods & derived methods in SQL
    - @Modifying used to annotate the query method that modifies the entity
    - becomes tightly coupled with the database vendor.
    - does not support the referencing of parameters with index numbers. We’re able only to reference parameters by name.
```
 @Modifying
 @Query("UPDATE person SET first_name = :name WHERE id = :id")
 boolean updateByFirstName(@Param("id") Long id, @Param("name") String name);
```

#### Universal JDBC Interaction Flow

```
// **1. Import packages**
// **2. Load JDBC Driver ** (mostly in the properties file, Driver auto-loaded by Service Loader)
public class DbConfig {

    private static final String URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER = "root";
    private static final String PASS = "password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);  // **3. Create and establish Database Connection**
    }
}

public class UserDao {

    public User findById(int id) throws SQLException {

        String sql = "SELECT id, name FROM users WHERE id=?";  // **4. Create Statement / PreparedStatement**
        Connection con = DbConfig.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery(); // **5. Send SQL Query to Database**

        User user = null;
        if (rs.next()) {  // **6. Retrieve ResultSet**
            user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
        }

        rs.close(); // **7. Close Resources / Return Connection to Pool**
        ps.close();
        con.close();

        return user;
    }
}


```
