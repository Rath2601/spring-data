### SPRING DATA JPA FLOW

#### Persistence Unit and EntityManager Flow
Persistence Unit (logical configuration)  
&nbsp;&nbsp;&nbsp;&nbsp;↓ built by  
**LocalContainerEntityManagerFactoryBean**  
&nbsp;&nbsp;&nbsp;&nbsp;↓ produces  
**EntityManagerFactory** interface  
&nbsp;&nbsp;&nbsp;&nbsp;↓ implemented by  
**SessionFactoryImpl** *(Hibernate, thread-safe, shared)*

---
#### EntityManager Injection
**EntityManager** interface  
&nbsp;&nbsp;&nbsp;&nbsp;↓ injected as  
**SharedEntityManagerCreator Proxy** *(thread-safe, routes calls to correct SessionImpl)*

---
#### Transaction Management Flow
When a `@Transactional` method runs, **JpaTransactionManager** performs:
1. Asks **EntityManagerFactory** to create a new **SessionImpl**
2. Wraps that session in an **EntityManagerHolder**
3. Binds the holder to the current thread using:  
   `TransactionSynchronizationManager.bindResource(factory, holder)`

---
#### Runtime Resolution
**SharedEntityManagerCreator** finds  
&nbsp;&nbsp;&nbsp;&nbsp;↓  
**SessionImpl** *(implements EntityManager, not thread-safe, performs JPA operations)*  
*(Persistence Context – non-thread-safe worker)*

When calling `em.persist(obj)`:
1. It calls `TransactionSynchronizationManager.getResource(factory)`
2. Retrieves the bound **SessionImpl**

---
#### Thread Safety Rule
- Each transaction/thread gets its own **SessionImpl**
- **No SessionImpl is ever shared**
- Thread safety is ensured by:
  - **JpaTransactionManager** binding one SessionImpl per transaction
  - **SharedEntityManagerCreator** routing calls to the correct thread-bound SessionImpl
