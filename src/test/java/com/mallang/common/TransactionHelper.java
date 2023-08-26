package com.mallang.common;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionHelper {

    @Transactional(readOnly = true)
    public void doAssert(TransactionalAssert transactionalAssert) {
        transactionalAssert.execute();
    }
}
