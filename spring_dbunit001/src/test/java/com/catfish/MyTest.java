package com.catfish;

import com.catfish.dao.UserDao;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by lcy on 17/6/16.
 */
@DbUnitConfiguration(dataSetLoader = XmlDataSetLoader.class, databaseOperationLookup = CustomDatabaseOperationLookup.class)
public class MyTest extends BaseDaoTest{

    @Autowired
    UserDao userDao;

    @DatabaseSetup
    @Test
    public void aa(){
        userDao.queryByAge123(3);
    }

    @DatabaseSetup(value = "bb.xml")
    @Test
    public void dd(){

    }


}
