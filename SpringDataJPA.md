#### SPRING DATA JPA FLOW
```
**Persistence Unit (logical configuration)**
        ↓ built by
LocalContainerEntityManagerFactoryBean
        ↓ produces
EntityManagerFactory interface
        ↓ implemented by
**SessionFactoryImpl**  (HIBERNATE , thread-safe, shared)

------------------------------------------------

**EntityManager** interface
        ↓ injected as
SharedEntityManagerCreator proxy (thread-safe, Routes calls to correct target)

------------------------------------------------

When @Transactional method runs, **JpaTransactionManager** does the following:
1. Asks the EntityManagerFactory to create a new SessionImpl
2. Wraps that session in an EntityManagerHolder
3. Binds that holder to the current thread using **TransactionSynchronizationManager.bindResource(factory, holder)**

SharedEntityManagerCreator finds
        ↓
**SessionImpl** (implements EntityManager , not thread safe, performs JPA operations)
(Persistence Context, non-thread-safe worker)

when we call `em.persist(obj)`, 
1.it calls TransactionSynchronizationManager.getResource(factory) & retrieves sessionImpl

------------------------------------------------

Thread safety rule:
Each transaction/thread gets its own SessionImpl.
No SessionImpl is ever shared.
**SessionImpl** kept safe by binding **one SessionImpl per thread/transaction** via SharedEntityManagerCreator and JpaTransactionManager.
```
