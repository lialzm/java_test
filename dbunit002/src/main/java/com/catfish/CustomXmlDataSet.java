package com.catfish;

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.DataSetException;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.io.Reader;

/**
 * Created by lcy on 17/5/31.
 */
public class CustomXmlDataSet extends CachedDataSet {


    /**
     * Creates an XmlDataSet with the specified xml reader.
     */
    public CustomXmlDataSet(Reader reader) throws DataSetException {
        super(new CustomXmlProducer2(new InputSource(reader)));
    }

    /**
     * Creates an XmlDataSet with the specified xml input stream.
     */
    public CustomXmlDataSet(InputStream in) throws DataSetException {
        super(new CustomXmlProducer2(new InputSource(in)));
    }

}
