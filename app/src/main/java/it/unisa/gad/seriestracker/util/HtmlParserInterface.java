package it.unisa.gad.seriestracker.util;


import java.net.URL;
import java.util.ArrayList;

import javax.xml.xpath.XPath;

import org.htmlcleaner.*;

interface HtmlParserInterface {
    public void perform();

    public ArrayList<Object> getElements();

    public void printElements();

    public void setXPath(String xpath);

    public String getXPath();

    public void setUrl(URL url);

    public URL getUrl();

    public TagNode getDocumentRootNode();
}
