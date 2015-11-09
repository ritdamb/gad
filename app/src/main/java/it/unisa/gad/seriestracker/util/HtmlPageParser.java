package it.unisa.gad.seriestracker.util;

import android.util.Log;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Objects;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import it.unisa.gad.seriestracker.Constant.XPathConstant;

/**
 * Created by ludimar on 09/11/15.
 */
public class HtmlPageParser implements HtmlParserInterface {

    private XPathFactory xPathFactory = XPathFactory.newInstance();
    private URL url;
    private String xpath;
    private TagNode rootNode;


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
            rootNode = cleaner.clean(new InputStreamReader(conn.getInputStream()));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            Log.e("Error", e.getMessage());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("Error", e.getMessage());
        }
    }

    @Override
    public ArrayList<Object> getElements() {
        return null;
    }

    @Override
    public void printElements() {
        try {
            Object tags[] = rootNode.evaluateXPath(getXPath());
            System.out.println("############# Numeri Oggetti : " + tags.length);
            for (int i = 0; i < tags.length; i++) {
                System.out.println("## : " + tags[i].toString());
            }
        } catch (XPatherException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setXPath(String xpath) {
        this.xpath = xpath;
    }

    @Override
    public String getXPath() {
        return xpath;
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
}
