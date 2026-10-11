package org.example.sessionmanager;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class TransactionManagerSpring implements TransactionManager {

    private final TransactionTemplate transactionTemplate;

    public TransactionManagerSpring(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public <T> T doInTransaction(TransactionAction<T> action) {
        return transactionTemplate.execute(status -> action.get());
    }
}

