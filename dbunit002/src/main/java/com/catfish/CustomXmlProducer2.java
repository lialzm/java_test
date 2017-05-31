package com.catfish;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.stream.DefaultConsumer;
import org.dbunit.dataset.stream.IDataSetConsumer;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by lcy on 17/5/26.
 */
public class CustomXmlProducer2 extends DefaultHandler implements
        IDataSetProducer, ContentHandler, ErrorHandler {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory
            .getLogger(CustomXmlProducer2.class);

    private static final IDataSetConsumer EMPTY_CONSUMER = new DefaultConsumer();

    private static final String DATASET = "dataset";
    private static final String TABLE = "table";
    private static final String NAME = "name";
    private static final String ROW = "row";
    private static final String COLUMN = "column";

    //    private static final String NULL = "null";
    //    private static final String NONE = "none";


    private final InputSource _inputSource;
    private boolean _validating = false;

    private IDataSetConsumer _consumer = EMPTY_CONSUMER;

    private String _activeTableName;
    private ITableMetaData _activeMetaData;

    private List<String> _activeColumnNames;
    private StringBuffer _activeCharacters;
    //    private List _activeRowValues;

    private Map<String, String> _activeRowValues;
    private String _activeColumnName;
    private List<String> __activeColumnNames;

    public CustomXmlProducer2(InputSource inputSource) {
        _inputSource = inputSource;
    }

    private ITableMetaData createMetaData(String tableName, List<String> columnNames) {
        logger.debug("createMetaData(tableName={}, _columnNames={}) - start",
                tableName, columnNames);

        Column[] columns = new Column[columnNames.size()];
        for (int i = 0; i < columns.length; i++) {
            String columnName = (String) columnNames.get(i);
            columns[i] = new Column(columnName, DataType.UNKNOWN);
        }
        DefaultTableMetaData metaData = new DefaultTableMetaData(tableName,
                columns);
        return metaData;
    }

    public void setValidating(boolean validating) {
        _validating = validating;
    }

    // //////////////////////////////////////////////////////////////////////////
    // IDataSetProducer interface

    public void setConsumer(IDataSetConsumer consumer) throws DataSetException {
        logger.debug("setConsumer(consumer={}) - start", consumer);
        _consumer = consumer;
    }

    public void produce() throws DataSetException {
        logger.debug("produce() - start");

        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setValidating(_validating);
            XMLReader xmlReader = saxParserFactory.newSAXParser()
                    .getXMLReader();

            xmlReader.setContentHandler(this);
            xmlReader.setEntityResolver(this);
            xmlReader.setErrorHandler(this);
            xmlReader.parse(_inputSource);
        } catch (ParserConfigurationException e) {
            throw new DataSetException(e);
        } catch (SAXException e) {
            DataSetException exceptionToRethrow = CustomXmlProducer2
                    .buildException(e);
            throw exceptionToRethrow;
        } catch (IOException e) {
            throw new DataSetException(e);
        }
    }

    /**
     * Wraps a {@link SAXException} into a {@link DataSetException}
     *
     * @param cause The cause to be wrapped into a {@link DataSetException}
     * @return A {@link DataSetException} that wraps the given
     * {@link SAXException}
     */
    protected final static DataSetException buildException(SAXException cause) {
        int lineNumber = -1;
        if (cause instanceof SAXParseException) {
            lineNumber = ((SAXParseException) cause).getLineNumber();
        }
        Exception exception = cause.getException() == null ? cause : cause
                .getException();
        String message;

        if (lineNumber >= 0) {
            message = "Line " + lineNumber + ": " + exception.getMessage();
        } else {
            message = exception.getMessage();
        }

        if (exception instanceof DataSetException) {
            return (DataSetException) exception;
        } else {
            return new DataSetException(message, exception);
        }
    }

    // //////////////////////////////////////////////////////////////////////////
    // EntityResolver interface

    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException {
        logger.debug("resolveEntity(publicId={}, systemId={}) - start",
                publicId, systemId);

        InputStream in = getClass().getClassLoader().getResourceAsStream(
                "org/dbunit/dataset/xml/dataset.dtd");
        return (new InputSource(in));
    }

    // //////////////////////////////////////////////////////////////////////
    // ContentHandler interface

    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "startElement(uri={}, localName={}, qName={}, attributes={}) - start",
                    new Object[]{uri, localName, qName, attributes});
        }

        try {
            // dataset
            if (qName.equals(DATASET)) {
                _consumer.startDataSet();
                return;
            }

            // table
            if (qName.equals(TABLE)) {
                _activeTableName = attributes.getValue(NAME);
                _activeColumnNames = new LinkedList<String>();
                return;
            }

            //添加字段
            if (qName.equals(COLUMN)) {
                _activeCharacters = new StringBuffer();
                return;
            }
            // row
            if (qName.equals(ROW)) {
                if (_activeColumnNames != null) {
                    _activeMetaData = createMetaData(_activeTableName,
                            _activeColumnNames);
                    _consumer.startTable(_activeMetaData);

                    __activeColumnNames = _activeColumnNames;
                    _activeColumnNames = null;
                }
                _activeRowValues = new HashMap<String, String>();

                return;
            }

            _activeColumnName = qName;
            _activeCharacters = new StringBuffer();

        } catch (DataSetException e) {
            throw new SAXException(e);
        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (logger.isDebugEnabled()) {
            logger.debug("endElement(uri={}, localName={}, qName={}) - start",
                    new Object[]{uri, localName, qName});
        }

        try {
            // dataset
            if (qName.equals(DATASET)) {
                _consumer.endDataSet();
                return;
            }

            // table
            if (qName.equals(TABLE)) {
                _consumer.endTable();
                _activeTableName = null;
                _activeMetaData = null;
                return;
            }

            if (qName.equals(COLUMN)) {
                _activeColumnNames.add(_activeCharacters.toString());
                _activeCharacters = null;
                return;
            }

            // row
            if (qName.equals(ROW)) {
                final int length = __activeColumnNames.size();
                Object[] values = new Object[length];
                for (int i = 0; i < length; i++) {
                    values[i] = _activeRowValues.get(__activeColumnNames.get(i));
                }
                _consumer.row(values);
                _activeRowValues = null;
                return;
            }

            _activeRowValues.put(_activeColumnName, _activeCharacters.toString());


        } catch (DataSetException e) {
            throw new SAXException(e);
        }
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {
        if (_activeCharacters != null) {
            _activeCharacters.append(ch, start, length);
        }
    }

    public void error(SAXParseException e) throws SAXException {
        throw e;
    }

}
