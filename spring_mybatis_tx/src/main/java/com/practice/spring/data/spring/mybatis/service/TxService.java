package com.practice.spring.data.spring.mybatis.service;

import com.practice.spring.data.spring.mybatis.repository.mapper.AccountMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
public class TxService {

    private final AccountMapper mapper;
    private final PlatformTransactionManager txManager;

    public TxService(AccountMapper mapper, PlatformTransactionManager txManager) {
        this.mapper = mapper;
        this.txManager = txManager;
    }


    public void requiredExample() {
        TransactionDefinition def =
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = txManager.getTransaction(def);

        try {
            mapper.debit(1, 100);
            log(1, -100, "REQUIRED");

            // RuntimeException => FULL rollback
            throw new RuntimeException("Force rollback REQUIRED");

        } catch (Exception ex) {
            txManager.rollback(status);
            throw ex;
        }
    }

    public void requiresNewExample() {
        TransactionDefinition outerDef =
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus outerTx = txManager.getTransaction(outerDef);

        try {
            innerRequiresNew();
            throw new RuntimeException("Outer rollback");

        } catch (Exception e) {
            txManager.rollback(outerTx);
        }
    }

    private void innerRequiresNew() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        TransactionStatus status = txManager.getTransaction(def);

        try {
            mapper.credit(2,200);
            log(2, 200, "REQUIRES_NEW");

            txManager.commit(status);

        } catch (Exception e) {
            txManager.rollback(status);
            throw e;
        }
    }

    public void nestedExample() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = txManager.getTransaction(def);

        try {
            mapper.debit(1 ,50);

            try {
                nestedInner(status);
            } catch (Exception ignored) {}

            txManager.commit(status); // outer commits

        } catch (Exception e) {
            txManager.rollback(status);
        }
    }

    private void nestedInner(TransactionStatus outerStatus) {
        Object savepoint = outerStatus.createSavepoint();

        try {
            mapper.debit(1, 999);
            throw new RuntimeException("Rollback to savepoint");

        } catch (Exception e) {
            outerStatus.rollbackToSavepoint(savepoint);
        }
    }

    public void supportsExample() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);

        TransactionStatus status = txManager.getTransaction(def);

        try {
            mapper.credit(2, 10);

            if (status.isNewTransaction()) {
                txManager.commit(status);
            }

        } catch (Exception e) {
            if (status.isNewTransaction()) {
                txManager.rollback(status);
            }
        }
    }

    public void notSupportedExample() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);

        TransactionStatus status = txManager.getTransaction(def);

        mapper.credit(2,5);

    }

    public void mandatoryExample() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_MANDATORY);
        TransactionStatus status = txManager.getTransaction(def);

        mapper.credit(1,1);
    }

    public void neverExample() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_NEVER);

        txManager.getTransaction(def);

        mapper.credit(1,1);
    }

    public void readCommitted() {
        runWithIsolation(TransactionDefinition.ISOLATION_READ_COMMITTED);
    }

    public void repeatableRead() {
        runWithIsolation(TransactionDefinition.ISOLATION_REPEATABLE_READ);
    }

    public void serializable() {
        runWithIsolation(TransactionDefinition.ISOLATION_SERIALIZABLE);
    }

    private void runWithIsolation(int isolation) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(isolation);

        TransactionStatus status = txManager.getTransaction(def);

        try {
            readBalance();
            sleep();
            readBalance();

            txManager.commit(status);

        } catch (Exception e) {
            txManager.rollback(status);
        }
    }

    public void checkedExceptionRollback() throws Exception {
        TransactionStatus status = txManager.getTransaction(
                new DefaultTransactionDefinition());

        try {
            mapper.debit(1,10);
            throw new Exception("Checked exception rollback");

        } catch (Exception e) {
            txManager.rollback(status);
            throw e;
        }
    }

    private void readBalance() {
        Integer bal = mapper.getBalance(1);
        System.out.println("Balance = " + bal);
    }

    private void log(long accId, int amt, String status) {
        mapper.log(accId, amt, status);
    }

    private void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {}
    }
}
