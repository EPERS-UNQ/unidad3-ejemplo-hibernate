package ar.edu.unq.unidad3.dao.helper;

import ar.edu.unq.unidad3.service.runner.HibernateTransactionRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionConcurrencyHelper {

    private final ExecutorService executor;
    private final CountDownLatch startThread2Latch;
    private final CountDownLatch resumeThread1Latch;
    private final CountDownLatch resumeThread2Latch;
    private final CountDownLatch testCompletionLatch;

    public TransactionConcurrencyHelper() {
        this.executor = Executors.newFixedThreadPool(2);
        this.startThread2Latch = new CountDownLatch(1);  // Signal for Thread 2 to start
        this.resumeThread1Latch = new CountDownLatch(1); // Signal for Thread 1 to resume
        this.resumeThread2Latch = new CountDownLatch(1); // Signal for Thread 2 to resume after Thread 1
        this.testCompletionLatch = new CountDownLatch(2); // Ensure both threads finish
    }

    public void runInTransaction(Runnable transactionLogic, Runnable afterTransactionLogic) {
        runInTransaction(null, transactionLogic, afterTransactionLogic);
    }

    public void runInTransaction(Integer isolationLevel, Runnable transactionLogic, Runnable afterTransactionLogic) {
        executor.submit(() -> {
            HibernateTransactionRunner.runTrx(isolationLevel, () -> {
                transactionLogic.run();
                return null;
            });
            if (afterTransactionLogic != null) {
                afterTransactionLogic.run();
            }
            completeTransaction();
        });
    }

    public void signalThread2ToStart() {
        startThread2Latch.countDown();
    }

    public void signalThread1ToResume() {
        resumeThread1Latch.countDown();
    }

    public void signalThread2ToResume() {
        resumeThread2Latch.countDown();
    }

    public void waitForThread2ToStart() {
        try {
            startThread2Latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void waitForThread1ToResume() {
        try {
            resumeThread1Latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void waitForThread2ToResume() {
        try {
            resumeThread2Latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void completeTransaction() {
        testCompletionLatch.countDown();
    }

    public void shutdown() throws InterruptedException {
        testCompletionLatch.await();
        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.sleep(100);
        }
    }
}
