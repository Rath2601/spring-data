#### SPRING DATA JPA FLOW

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

When @Transactional method runs:
SharedEntityManagerCreator finds
        ↓
**SessionImpl** (implements EntityManager , not thread safe, performs JPA operations)
(Persistence Context, non-thread-safe worker)

------------------------------------------------

Thread safety rule:
Each transaction/thread gets its own SessionImpl.
No SessionImpl is ever shared.
**SessionImpl** kept safe by binding **one SessionImpl per thread/transaction** via SharedEntityManagerCreator and JpaTransactionManager.
