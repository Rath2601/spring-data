#### Data Access Layers

| Aspects                | JDBC                 | MyBatis           | JPA                           |
| -------------------- | -------------------- | ----------------- | ----------------------------- |
| Mapping              | Manual (ResultSet)   | XML / Annotations | Automatic ORM                 |
| Relationships        | Manual joins         | Manual joins      | Annotations (@OneToMany etc.) |
| Fetching             | Always explicit      | Always explicit   | Lazy / Eager                  |
| Updates              | Explicit SQL         | Explicit SQL      | Dirty checking (automatically detects changed entity fields)               |
| Transaction handling | Manual               | Spring-managed    | Spring-managed                |
| Boilerplate          | Very high            | Medium            | Low                           |
| Control              | Fullest              | High              | Abstracted                    |
| Performance tuning   | Fully manual         | Manual & flexible | Needs understanding           |
| Learning curve       | Low API, high effort | Moderate          | Steep initially               |
| Surprise behavior    | Very low             | Low               | Possible if unaware           |
| Flow                 | Spring Service ↓ Spring JDBC (JdbcTemplate / NamedParameterJdbcTemplate) ↓ JDBC Driver ↓ Database |Spring Service ↓ MyBatis Mapper Interface ↓ MyBatis SQL Session ↓ JDBC Driver ↓ Database | Spring Data Repository ↓ JPA EntityManager (standard API) ↓ Hibernate Session (actual implementation) ↓ JDBC Driver ↓ Database |


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
