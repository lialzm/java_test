package com.catfish;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.stream.IDataSetConsumer;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lcy on 17/5/26.
 */
public class CustomXmlProducer extends DefaultHandler implements
        IDataSetProducer, ContentHandler, ErrorHandler {

    private static final String DATASET = "dataset";
    private static final String TABLE = "table";
    private static final String NAME = "name";
    private List<String> _activeColumnNames = new ArrayList<String>();
    private String _activeTableName;

    public void setConsumer(IDataSetConsumer consumer) throws DataSetException {

    }

    public void produce() throws DataSetException {

    }


    /*private ITableMetaData createTableMetaData(String tableName, Attributes attributes) throws DataSetException


}*/

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        _activeColumnNames.add(qName);

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);

    }
}
