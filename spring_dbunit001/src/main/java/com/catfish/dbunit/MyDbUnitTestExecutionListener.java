package com.catfish.dbunit;

import com.github.springtestdbunit.DatabaseConnections;
import com.github.springtestdbunit.DbUnitRunner;
import com.github.springtestdbunit.DbUnitTestContext;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import com.github.springtestdbunit.dataset.DataSetLoader;
import com.github.springtestdbunit.dataset.FlatXmlDataSetLoader;
import com.github.springtestdbunit.operation.DatabaseOperationLookup;
import com.github.springtestdbunit.operation.DefaultDatabaseOperationLookup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.database.IDatabaseConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Conventions;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by lcy on 17/6/19.
 */
public class MyDbUnitTestExecutionListener extends AbstractTestExecutionListener {


    private static final Log logger = LogFactory.getLog(DbUnitTestExecutionListener.class);

    private static final String[] COMMON_DATABASE_CONNECTION_BEAN_NAMES = { "dbUnitDatabaseConnection", "dataSource" };

    private static final String DATA_SET_LOADER_BEAN_NAME = "dbUnitDataSetLoader";

    protected static final String CONNECTION_ATTRIBUTE = Conventions
            .getQualifiedAttributeName(DbUnitTestExecutionListener.class, "connection");

    protected static final String DATA_SET_LOADER_ATTRIBUTE = Conventions
            .getQualifiedAttributeName(DbUnitTestExecutionListener.class, "dataSetLoader");

    protected static final String DATABASE_OPERATION_LOOKUP_ATTRIBUTE = Conventions
            .getQualifiedAttributeName(DbUnitTestExecutionListener.class, "databseOperationLookup");

    private static MyDbUnitRunner runner = new MyDbUnitRunner();

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        prepareTestInstance(new DbUnitTestContextAdapter(testContext));
    }

    public void prepareTestInstance(MyDbUnitTestExecutionListener.DbUnitTestContextAdapter testContext) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Preparing test instance " + testContext.getTestClass() + " for DBUnit");
        }
        String[] databaseConnectionBeanNames = null;
        String dataSetLoaderBeanName = null;
        Class<? extends DataSetLoader> dataSetLoaderClass = FlatXmlDataSetLoader.class;
        Class<? extends DatabaseOperationLookup> databaseOperationLookupClass = DefaultDatabaseOperationLookup.class;

        DbUnitConfiguration configuration = testContext.getTestClass().getAnnotation(DbUnitConfiguration.class);
        if (configuration != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Using @DbUnitConfiguration configuration");
            }
            databaseConnectionBeanNames = configuration.databaseConnection();
            dataSetLoaderClass = configuration.dataSetLoader();
            dataSetLoaderBeanName = configuration.dataSetLoaderBean();
            databaseOperationLookupClass = configuration.databaseOperationLookup();
        }

        if (ObjectUtils.isEmpty(databaseConnectionBeanNames)
                || ((databaseConnectionBeanNames.length == 1) && StringUtils.isEmpty(databaseConnectionBeanNames[0]))) {
            databaseConnectionBeanNames = new String[] { getDatabaseConnectionUsingCommonBeanNames(testContext) };
        }

        if (!StringUtils.hasLength(dataSetLoaderBeanName)) {
            if (testContext.getApplicationContext().containsBean(DATA_SET_LOADER_BEAN_NAME)) {
                dataSetLoaderBeanName = DATA_SET_LOADER_BEAN_NAME;
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("DBUnit tests will run using databaseConnection \""
                    + StringUtils.arrayToCommaDelimitedString(databaseConnectionBeanNames)
                    + "\", datasets will be loaded using " + (StringUtils.hasLength(dataSetLoaderBeanName)
                    ? "'" + dataSetLoaderBeanName + "'" : dataSetLoaderClass));
        }
        prepareDatabaseConnection(testContext, databaseConnectionBeanNames);
        prepareDataSetLoader(testContext, dataSetLoaderBeanName, dataSetLoaderClass);
        prepareDatabaseOperationLookup(testContext, databaseOperationLookupClass);
    }

    private String getDatabaseConnectionUsingCommonBeanNames(DbUnitTestContextAdapter testContext) {
        for (String beanName : COMMON_DATABASE_CONNECTION_BEAN_NAMES) {
            if (testContext.getApplicationContext().containsBean(beanName)) {
                return beanName;
            }
        }
        throw new IllegalStateException(
                "Unable to find a DB Unit database connection, missing one the following beans: "
                        + Arrays.asList(COMMON_DATABASE_CONNECTION_BEAN_NAMES));
    }

    private void prepareDatabaseConnection(DbUnitTestContextAdapter testContext, String[] connectionBeanNames)
            throws Exception {
        IDatabaseConnection[] connections = new IDatabaseConnection[connectionBeanNames.length];
        for (int i = 0; i < connectionBeanNames.length; i++) {
            Object databaseConnection = testContext.getApplicationContext().getBean(connectionBeanNames[i]);
            if (databaseConnection instanceof DataSource) {
                databaseConnection = DatabaseDataSourceConnectionFactoryBean
                        .newConnection((DataSource) databaseConnection);
            }
            Assert.isInstanceOf(IDatabaseConnection.class, databaseConnection);
            connections[i] = (IDatabaseConnection) databaseConnection;
        }
        testContext.setAttribute(CONNECTION_ATTRIBUTE, new DatabaseConnections(connectionBeanNames, connections));
    }

    private void prepareDataSetLoader(DbUnitTestContextAdapter testContext, String beanName,
                                      Class<? extends DataSetLoader> dataSetLoaderClass) {
        if (StringUtils.hasLength(beanName)) {
            testContext.setAttribute(DATA_SET_LOADER_ATTRIBUTE,
                    testContext.getApplicationContext().getBean(beanName, DataSetLoader.class));
        } else {
            try {
                testContext.setAttribute(DATA_SET_LOADER_ATTRIBUTE, dataSetLoaderClass.newInstance());
            } catch (Exception ex) {
                throw new IllegalArgumentException(
                        "Unable to create data set loader instance for " + dataSetLoaderClass, ex);
            }
        }
    }

    private void prepareDatabaseOperationLookup(DbUnitTestContextAdapter testContext,
                                                Class<? extends DatabaseOperationLookup> databaseOperationLookupClass) {
        try {
            testContext.setAttribute(DATABASE_OPERATION_LOOKUP_ATTRIBUTE, databaseOperationLookupClass.newInstance());
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "Unable to create database operation lookup instance for " + databaseOperationLookupClass, ex);
        }
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        runner.beforeTestMethod(new DbUnitTestContextAdapter(testContext));
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        runner.afterTestMethod(new DbUnitTestContextAdapter(testContext));
    }

    /**
     * Adapter class to convert Spring's {@link TestContext} to a {@link DbUnitTestContext}. Since Spring 4.0 change the
     * TestContext class from a class to an interface this method uses reflection.
     */
    private static class DbUnitTestContextAdapter implements DbUnitTestContext {

        private static final Method GET_TEST_CLASS;
        private static final Method GET_TEST_INSTANCE;
        private static final Method GET_TEST_METHOD;
        private static final Method GET_TEST_EXCEPTION;
        private static final Method GET_APPLICATION_CONTEXT;
        private static final Method GET_ATTRIBUTE;
        private static final Method SET_ATTRIBUTE;

        static {
            try {
                GET_TEST_CLASS = TestContext.class.getMethod("getTestClass");
                GET_TEST_INSTANCE = TestContext.class.getMethod("getTestInstance");
                GET_TEST_METHOD = TestContext.class.getMethod("getTestMethod");
                GET_TEST_EXCEPTION = TestContext.class.getMethod("getTestException");
                GET_APPLICATION_CONTEXT = TestContext.class.getMethod("getApplicationContext");
                GET_ATTRIBUTE = TestContext.class.getMethod("getAttribute", String.class);
                SET_ATTRIBUTE = TestContext.class.getMethod("setAttribute", String.class, Object.class);
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        }

        private TestContext testContext;

        public DbUnitTestContextAdapter(TestContext testContext) {
            this.testContext = testContext;
        }

        public DatabaseConnections getConnections() {
            return (DatabaseConnections) getAttribute(CONNECTION_ATTRIBUTE);
        }

        public DataSetLoader getDataSetLoader() {
            return (DataSetLoader) getAttribute(DATA_SET_LOADER_ATTRIBUTE);
        }

        public DatabaseOperationLookup getDatbaseOperationLookup() {
            return (DatabaseOperationLookup) getAttribute(DATABASE_OPERATION_LOOKUP_ATTRIBUTE);
        }

        public Class<?> getTestClass() {
            return (Class<?>) ReflectionUtils.invokeMethod(GET_TEST_CLASS, this.testContext);
        }

        public Method getTestMethod() {
            return (Method) ReflectionUtils.invokeMethod(GET_TEST_METHOD, this.testContext);
        }

        public Object getTestInstance() {
            return ReflectionUtils.invokeMethod(GET_TEST_INSTANCE, this.testContext);
        }

        public Throwable getTestException() {
            return (Throwable) ReflectionUtils.invokeMethod(GET_TEST_EXCEPTION, this.testContext);
        }

        public ApplicationContext getApplicationContext() {
            return (ApplicationContext) ReflectionUtils.invokeMethod(GET_APPLICATION_CONTEXT, this.testContext);
        }

        public Object getAttribute(String name) {
            return ReflectionUtils.invokeMethod(GET_ATTRIBUTE, this.testContext, name);
        }

        public void setAttribute(String name, Object value) {
            ReflectionUtils.invokeMethod(SET_ATTRIBUTE, this.testContext, name, value);
        }

    }

}
