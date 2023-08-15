package com.mallang.commoin;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionHelper {

    @Transactional(readOnly = true)
    public void doAssert(final TransactionalAssert transactionalAssert) {
        transactionalAssert.execute();
    }
}
