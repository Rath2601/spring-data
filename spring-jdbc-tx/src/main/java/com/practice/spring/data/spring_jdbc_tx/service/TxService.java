package com.practice.spring.data.spring_jdbc_tx.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
public class TxService {

    private final JdbcTemplate jdbc;
    private final PlatformTransactionManager txManager;

    public TxService(JdbcTemplate jdbc, PlatformTransactionManager txManager) {
        this.jdbc = jdbc;
        this.txManager = txManager;
    }


    public void requiredExample() {
        TransactionDefinition def =
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = txManager.getTransaction(def);

        try {
            jdbc.update("UPDATE account SET balance = balance - 100 WHERE id = 1");
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
            jdbc.update("UPDATE account SET balance = balance + 200 WHERE id = 2");
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
            jdbc.update("UPDATE account SET balance = balance - 50 WHERE id = 1");

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
            jdbc.update("UPDATE account SET balance = balance - 999 WHERE id = 1");
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
            jdbc.update("UPDATE account SET balance = balance + 10 WHERE id = 2");

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

        jdbc.update("UPDATE account SET balance = balance + 5 WHERE id = 2);");

    }

    public void mandatoryExample() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_MANDATORY);
        TransactionStatus status = txManager.getTransaction(def);

        jdbc.update("UPDATE account SET balance = balance + 1 WHERE id = 1");
    }

    public void neverExample() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_NEVER);

        txManager.getTransaction(def);

        jdbc.update("UPDATE account SET balance = balance + 1 WHERE id = 1");
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
            readBalance(1);
            sleep();
            readBalance(1);

            txManager.commit(status);

        } catch (Exception e) {
            txManager.rollback(status);
        }
    }

    public void checkedExceptionRollback() throws Exception {
        TransactionStatus status = txManager.getTransaction(
                new DefaultTransactionDefinition());

        try {
            jdbc.update("UPDATE account SET balance = balance - 10 WHERE id = 1");
            throw new Exception("Checked exception rollback");

        } catch (Exception e) {
            txManager.rollback(status);
            throw e;
        }
    }

    private void readBalance(long id) {
        Integer bal = jdbc.queryForObject(
                "SELECT balance FROM account WHERE id = ?",
                Integer.class, id);
        System.out.println("Balance = " + bal);
    }

    private void log(long accId, int amt, String status) {
        jdbc.update(
            "INSERT INTO transaction_log(account_id, amount, status) VALUES (?, ?, ?)",
            accId, amt, status
        );
    }

    private void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {}
    }
}

