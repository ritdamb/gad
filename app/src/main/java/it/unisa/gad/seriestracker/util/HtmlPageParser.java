package it.unisa.gad.seriestracker.util;

import android.util.Log;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Created by ludimar on 09/11/15.
 */
public class HtmlPageParser implements HtmlParserInterface {

    private XPathFactory xPathFactory = XPathFactory.newInstance();
    private URL url;
    private String xpathExpression;
    private TagNode rootNode;
    private Document doc;
    private XPath  xPathObj;
    private  NodeList nodeList;
    private Document resultXmlDocument;


    @Override
    public void perform() {
        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        props.setAllowHtmlInsideAttributes(true);
        props.setAllowMultiWordAttributes(true);
        props.setRecognizeUnicodeChars(true);
        props.setOmitComments(true);
        try {
            URLConnection conn = url.openConnection();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            rootNode = cleaner.clean(url);
            doc = new DomSerializer(new CleanerProperties()).createDOM(rootNode);
            xPathObj = XPathFactory.newInstance().newXPath();
            nodeList  = (NodeList) xPathObj.compile(xpathExpression).evaluate(doc, XPathConstants.NODESET);

            ///// Crezione di un documento xml con i nodi selezionati dall'xpath principale.
            resultXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = resultXmlDocument.createElement("root");
            resultXmlDocument.appendChild(root);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Node copyNode = resultXmlDocument.importNode(node, true);
                root.appendChild(copyNode);
            }

            //printDocument(resultXmlDocument,System.out);

            /////
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            Log.e("Error", e.getMessage());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("Error", e.getMessage());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public NodeList getElements() {
        return nodeList;
    }

    public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(new DOMSource(doc),
                new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    }

    @Override
    public void printElements() {
    }


    public Document getResultXmlDocument() {
        return resultXmlDocument;
    }

    public void setResultXmlDocument(Document resultXmlDocument) {
        this.resultXmlDocument = resultXmlDocument;
    }

    @Override
    public void setXPath(String xpath) {
        this.xpathExpression = xpath;
    }

    @Override
    public String getXPath() {
        return xpathExpression;
    }

    @Override
    public void setUrl(URL url) {
        this.url = url;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public TagNode getDocumentRootNode() {
        return rootNode;
    }

    @Override
    public Document getDomDocument() {
        return doc;
    }
}
