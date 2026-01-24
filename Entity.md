### Entity
#### Object Mapping Annotations 
* **@Entity** --> seperate object and individual table
* **@Embeddable** -> seperate object, but belong to another entity table as columns
* **@ElementCollection** -> stores multiple values in a separate table linked to the parent entity primary key , but the seperate table doesn't have any primary key

#### Relationship key values

1. **@MapsId** maps **one entity’s primary key** value to **another entity’s primary key** and **also acts as a foreign key in the child entity**. (**@JoinColumn defines PK/FK column name in child entity table.**)
2.If a composite key is used, the entity referenced by @MapsId  represents the foreign-key portion of the composite primary key
     (e.g. Order referenced in OrderItem , PK = (itemId, orderId), where **orderId** from order is both part of the primary key and a foreign key to Order.orderId).
3. In an @Embeddable composite key, **@GeneratedValue cannot be used — JPA does not support auto-generation inside @EmbeddedId.**
4. To maintain bidirectional relationship consistency, both sides must be set manually in code. (Persistence Context behaves predictably)
5. @EmbeddedId must always referenced in an @Embeddable class — it cannot be used on primitive or wrapper types.
6. Every field inside an @Embeddable used by @EmbeddedId automatically becomes part of the entity’s primary key.
7. @MapsId("fieldName") is used only with @EmbeddedId mappings. @MapsId without parameter is used only with a single @Id (shared primary-key one-to-one).
8. When using @EmbeddedId + @MapsId, the embedded id object must be instantiated before persisting, otherwise Hibernate throws a null-id exception.
9. Hibernate fills only the foreign key fields referenced by @MapsId; remaining fields in a composite key must be set manually.
10. cascade = PERSIST (or ALL) is required if child entities should be saved automatically when persisting the parent.
11. orphanRemoval = true , deletes child rows when removed from the parent collection within the same transaction.
12. @MapsId should be used only when a child’s primary key is derived from a parent’s primary key — not when the child has its own generated ID.


#### Equals and Hashcode 
* JPA entities must have stable equals() and hashCode() implementations. Using auto-generated identifiers in equals() / hashCode() is unsafe because the identifier value changes after persistence, breaking hash-based collections and Hibernate’s internal behavior.

```java
    @Column(nullable = false, unique = true, updatable = false)
    private String uuid = java.util.UUID.randomUUID().toString();

    private String status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order other)) return false;
        return uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode(); // either use constant value here / persist the UUID in DB
    }
```
**If two objects are equal according to equals(), they must have the same hashCode()**. they would be called in Set collections when we use add, contains methods

#### Inheritance Strategies (if we have some entities of same type and we can use any of these approach)
* **SINGLE_TABLE** ->All subclasses in one table + discriminator column, **(all subclass field + main class fields + discriminator column in single table)**
    - pros : Performance efficient,  Fast queries, manage only one table
    - cons : many nullable columns, cannot have NOT NULL constraints on subclass columns
    - use case : Notification Systems (with subclasses Email, SMS, and Push)
* **JOINED** —> Parent table + child tables, Normalized, but requires joins. (Normalized) **(all subclass will have seperate table , but with main class primary key as both PK & FK)**
    - pros : can use not null constraints, no redundant null values
    - cons : need join to fetch details form child tables ("joins" overhead slows down your application as data grows)
    - use case : BankTransaction hierarchy with WireTransfer, ATMWithdrawal, and CheckDeposit
* **TABLE_PER_CLASS** —> Each subclass has its own table duplicating parent columns. Good for independent entities, bad for polymorphic queries.  **(all classes & subclass will have seperate table with seperate PK)**
    - pros : No null columns, no joins
    - cons : searching across all tables is very "expensive”, forced to use sequence , UNION queries needed for polymorphic retrieval
**polymorphic retrieval -> the ability to write a single query against a Base Class (or interface) and have Hibernate automatically fetch all its Subclasses**


#### Misc
**@Convert** : performs value transformation between an entity field and a single database column value (and back).
**@Type** : performs custom type mapping, allowing Hibernate to store and retrieve complex or database-specific data types


* **Proxy  accessed**-> Fires SQL query —> loads real data —> Replaces proxy with real data (thus hibernate delay loading data ~ LAZY LOADING)
* **Bytecode** also do the same , but with feature like removes the need for runtime proxy subclasses, improves performance, supports final classes, and avoids equals/hashCode issues — at the cost of slightly longer build time
  (Bytecode enhancement rewrites entity classes at build time to inject **lazy-loading and dirty-tracking** logic directly into the class.)
