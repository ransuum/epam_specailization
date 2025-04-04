package org.epam.transaction.logging;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Log4j2
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionContext {
    private static final ThreadLocal<String> transactionId = new ThreadLocal<>();

    public static String getTransactionId() {
        String id = transactionId.get();
        if (id == null) {
            id = UUID.randomUUID().toString();
            transactionId.set(id);
        }
        return id;
    }

    public static void clear() {
        transactionId.remove();
    }
}
