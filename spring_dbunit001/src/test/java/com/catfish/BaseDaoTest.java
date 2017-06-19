/*
 * -------------------------------------------------------
 * Copyright (c) 2015, 北京易易科技有限公司
 * All rights reserved.
 * 
 * FileName：BaseDaoTest.java.java
 * Description：简要描述本文件的内容
 * History：
 * Date           Author               Desc
 * -------------------------------------------------------
 */
package com.catfish;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.catfish.dbunit.MyTransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring-mvc-context.xml", "classpath:/spring-mvc.xml"})
@WebAppConfiguration
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        MyTransactionDbUnitTestExecutionListener.class})
public class BaseDaoTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 过滤不要的字段
     * @param strings
     * @return
     */
    private PropertyFilter exclude(final String... strings) {

        logger.info(strings.toString());

        return new PropertyFilter() {


            public boolean apply(Object object, String name, Object value) {
                if (strings != null && strings.length >= 0) {
                    String[] ss = strings;
                    if (Arrays.asList(ss).contains(name)) {
                        return false;
                    }
                }
                //false表示将被排除在外
                return true;
            }

        };
    }

    /**
     * 比较json
     * @param object 实际json对应的对象
     * @param excludes 需要过滤的字段
     */
    protected void assertJson(Object object, String... excludes) {
        String actual = JSONArray.toJSONString(object);
        //获取调用该方法的地方
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String className = stackTraceElements[2].getClassName();
        String[] sp = className.split("\\.");
        className = sp[sp.length - 1];
        String methodName = stackTraceElements[2].getMethodName();
        logger.info(className + "," + methodName);
        //读取xml
        String json = readXml(className, methodName);

        Assert.assertEquals(JSON.toJSONString(json,exclude(excludes)),JSON.toJSONString(actual,exclude(excludes)));
    }

    private String readXml(String className, String methodName) {
        SAXReader saxReader = new SAXReader();
        try {
            Document document = null;
            try {
                document = saxReader.read(new File(this.getClass().getResource("/").getPath() + "dbtest/" + className + ".xml")).getDocument();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            List<Element> elements = document.getRootElement().elements();
            for (Element element : elements
                    ) {
                if (element.getName().equals(methodName)) {
                    return element.getTextTrim();
                }
            }
        } catch (DocumentException e) {
            logger.error("", e);

        }
        return null;
    }

}
