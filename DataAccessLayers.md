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
| Flow                 | Spring Data Repository ↓ JPA EntityManager (standard API) ↓ Hibernate Session (actual implementation) ↓ JDBC Driver ↓ Database                     | Spring Data Repository ↓ JPA EntityManager (standard API) ↓ Hibernate Session (actual implementation) ↓ JDBC Driver ↓ Database     | Spring Data Repository ↓ JPA EntityManager (standard API) ↓ Hibernate Session (actual implementation) ↓ JDBC Driver ↓ Database |

