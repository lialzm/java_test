package com.catfish;

import com.github.springtestdbunit.dataset.AbstractDataSetLoader;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.core.io.Resource;

import java.io.InputStream;

/**
 * Created by new on 17/5/11.
 */
public class XmlDataSetLoader extends AbstractDataSetLoader {
    @Override
    protected IDataSet createDataSet(Resource resource) throws Exception {
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        InputStream inputStream = resource.getInputStream();

        FlatXmlDataSet var4;
        try {
            var4 = builder.build(inputStream);
        } finally {
            inputStream.close();
        }

        return var4;
    }

    @Override
    public IDataSet loadDataSet(Class<?> testClass, String location) throws Exception {

        System.out.println(111);

        return super.loadDataSet(testClass, location);
    }
}
