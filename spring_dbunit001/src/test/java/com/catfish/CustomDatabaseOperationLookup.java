package com.catfish;

import com.github.springtestdbunit.operation.DefaultDatabaseOperationLookup;
import org.dbunit.operation.DatabaseOperation;

/**
 * Created by new on 17/5/11.
 */
public class CustomDatabaseOperationLookup extends DefaultDatabaseOperationLookup {
    @Override
    public DatabaseOperation get(com.github.springtestdbunit.annotation.DatabaseOperation databaseOperation) {
        return super.get(com.github.springtestdbunit.annotation.DatabaseOperation.CLEAN_INSERT);
    }
}
