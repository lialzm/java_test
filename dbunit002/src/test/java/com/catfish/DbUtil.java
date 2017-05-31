package com.catfish;

import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.IMetadataHandler;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.mysql.MySqlConnection;
import org.dbunit.ext.mysql.MySqlMetadataHandler;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.util.fileloader.DataFileLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * Created by lcy on 17/5/16.
 */

public class DbUtil {


    protected IDatabaseConnection getConnection()
            throws Exception {

        Class.forName("com.mysql.cj.jdbc.Driver");

        Connection jdbcConnection =
                DriverManager.getConnection(
                        "jdbc:mysql://192.168.30.88:3306/java_test?useUnicode=true&characterEncoding=UTF-8", "root", "123456");

        return new MySqlConnection(jdbcConnection, "java_test");
    }

    protected IDataSet getDataSet() throws Exception {
        return new CustomXmlDataSet(
                new FileInputStream(DbUtil.class.getResource("/").getPath()+"test2.xml"));
    }


    @Test
    public void install() throws Exception {

        //插入准备数据
        IDatabaseConnection connection = getConnection();
        DatabaseConfig databaseConfig = connection.getConfig();
        IMetadataHandler metadataHandler = new MySqlMetadataHandler();
        databaseConfig.setProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER, metadataHandler);
        databaseConfig.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);

        IDataSet dataSet = getDataSet();
        ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
        DatabaseOperation.CLEAN_INSERT.execute(connection, replacementDataSet);
        System.out.println("success");
        //关闭连接
        connection.close();
    }

}
