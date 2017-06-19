package com.catfish.dbunit;

import com.github.springtestdbunit.TestExecutionListenerChain;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 * Created by lcy on 17/6/19.
 */
public class MyTransactionDbUnitTestExecutionListener  extends TestExecutionListenerChain {

    private static final Class<?>[] CHAIN = {TransactionalTestExecutionListener.class,
            MyDbUnitTestExecutionListener.class};

    @Override
    protected Class<?>[] getChain() {
        return CHAIN;
    }
}