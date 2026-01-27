* **@NamedQuery** -> Before Spring Data JPA existed  ,used in Legacy JPA apps (i.e Jakarta EE apps)
* **@RepositoryDefinition**(domainClass = User.class, idClass = Long.class) To prevent developers from accidentally using dangerous methods like deleteAll().  To keep your repository interfaces "clean". we would access only methods defined in this interface
```
@RepositoryDefinition(domainClass = Note.class, idClass = Long.class)
interface NoteRepository {

    List<Note> findByTitleContainingIgnoreCase(String title);

}
```
* **@NoRepositoryBean** used on base interface to Prevents bean creation but carry out the similar functionality. Spring skips creating a bean for BaseRepository and only creates beans for UserRepository, OrderRepository
```
  @NoRepositoryBean
public interface BaseRepository<T, ID> extends CrudRepository<T, ID> {
    default void printEntityInfo(T entity) {
        System.out.println("Entity: " + entity);
    }
}
```
* A Spring Boot app can use multiple Spring Data modules (JPA, MongoDB, etc.). When multiple modules exist, Spring needs help deciding which repository belongs to which module.
* Solutions: 
    - Define **separate base packages per module** in configuration.
    - Use module-specific domain annotations (@Entity, @Document).
    - Prefer module-specific repository interfaces (JpaRepository, MongoRepository).
    - Avoid using only generic repositories (Repository, CrudRepository) with multiple modules — causes ambiguity.
* **LocalContainerEntityManagerFactoryBean** does exception translation mechanisms  in addition to creating EntityManagerFactory.

* **@SQLDelete** = single-row soft delete logic : `@SQLDelete(sql = "UPDATE orders SET deleted = true WHERE order_id = ?”` 
   Bulk soft delete = custom @Modifying query (otherwise SQLDelete query applied to all row which leads to slowness)
* **@Where**(clause = "deleted = false")—> filters out soft-deleted rows

* **Specifications API** = a reusable, composable wrapper over CriteriaBuilder for dynamic queries / filters (Spring wrapper over CriteriaQuery) CriteriaBuilder (no Spring dependency) (type safety, but high verbosity, range query cb.between(root.get("amount"), 1000, 5000);, Full Joins support) Query-by-example : pass an example object, and Spring builds a query from non-null fields. (No joins, limited range query, limited dynamic query)
* **Custom repository implementation** : own implementation class for custom logic in addition to spring Data JPA methods
* **SpEL** : Using Spring Expression Language inside @Query to inject dynamic values. @Query("select o from Order o where o.status = :#{#status.toUpperCase()}")
* **Native Query Result Mapping** is used when your SQL result does not match any entity, and you want Hibernate to automatically convert raw SQL rows into clean DTO objects
