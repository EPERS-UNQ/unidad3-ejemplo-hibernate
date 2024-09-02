package ar.edu.unq.unidad3.dao.helper;

import ar.edu.unq.unidad3.service.runner.HibernateTransactionRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionConcurrencyHelper {

    private final ExecutorService executor;
    private final CountDownLatch latch;

    public TransactionConcurrencyHelper() {
        this.executor = Executors.newFixedThreadPool(2);
        this.latch = new CountDownLatch(1);
    }

    public void runInTransaction(Runnable transactionLogic) {
        executor.submit(() ->
                {
                    HibernateTransactionRunner.runTrx(() -> {
                        transactionLogic.run();
                        return null;
                    });
                    signalOtherTransactions();
                }
        );
    }

    public void runInTransaction(int isolationLevel, Runnable transactionLogic) {
        executor.submit(() ->
        {
            HibernateTransactionRunner.runTrx(isolationLevel, () -> {
                transactionLogic.run();
                return null;
            });
            signalOtherTransactions();
        });
    }

    public void waitForOtherTransactions() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void signalOtherTransactions() {
        if (latch.getCount() > 0) {
            latch.countDown();
        }
    }

    public void shutdown() throws InterruptedException {
        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.sleep(100);
        }
    }
}
