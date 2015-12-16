package it.unisa.gad.seriestracker.util;

import android.app.Application;
import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import it.unisa.gad.seriestracker.Domain.Series;

/**
 * Created by ludimar on 16/11/15.
 * Utilizzeremo questa classe per fare una sorta di Storage temporaneo, quindi esempio eseguo xpath, memorizzo in questa classe, e poi graficamente
 * richiamo questa classe e mi prendo l'oggetto di interesse
 */
public  class ApplicationVariables {

    private static ApplicationVariables singletonApp;
    private Document todaySeries;
    //qui si possono definire nuove liste di serie, che poi verranno richiamate dalla parte grafica per costruire gli adapter nelle activity
    //Questi verranno settati, creati, durante le xpath.

    public ApplicationVariables(){

    }

    public static synchronized void initInstance() {
        if (singletonApp == null) {
            singletonApp = new ApplicationVariables();
        }
    }

    public static ApplicationVariables getInstance() {
        return singletonApp;
    }

    public void setTodaySeries(Document doc ) {
        this.todaySeries = doc;
    }

    public Document getTodaySeries() {
        return todaySeries;
    }

    public ArrayList<Series> getPreferiteSeries(Context context) {
        try {
            FileInputStream fileInputStream = context.openFileInput("preferiteSeries.xml");
            if(fileInputStream == null) return null;
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            ArrayList<Series> series = (ArrayList<Series>) objectInputStream.readObject();
            return series;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean savePreferiteSeries(Context context,Series series) {
        ArrayList<Series> preferiteSeries = getPreferiteSeries(context);
        if(preferiteSeries == null) return false;
        else {
            preferiteSeries.add(series);
            return createPreferiteFile(context,preferiteSeries);
        }
    }

    public boolean checkPreferiteSeries(Context context, Series series){
        ArrayList<Series> preferiteSeries = getPreferiteSeries(context);
        if(preferiteSeries == null) return false;
        else {
            for(int i = 0 ; i < preferiteSeries.size(); i++ ) {
                if(preferiteSeries.get(i).getName().equals(series.getName())) return true;
            }
            return false;
        }
    }

    public boolean createPreferiteFile(Context context, ArrayList<Series> seriesList) {
        try{
            File preferiteLists = new File(context.getFilesDir(), "preferiteSeries.xml");
            FileOutputStream outputStream;
            outputStream = context.openFileOutput("preferiteSeries.xml",Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(seriesList);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkDataWarehouse(Context context){
        try{
            FileInputStream fileInputStream = context.openFileInput("seriesList.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document original = db.parse(new InputSource(fileInputStream));

            printDocument(original,System.out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void createDataWareHouse(Context context){
        try{
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("seriesList");

            // series elements
            Element series = doc.createElement("series");
            rootElement.appendChild(series);



            // name elements
            Element name = doc.createElement("name");
            name.appendChild(doc.createTextNode("Gotham"));
            series.appendChild(name);

            // genre elements
            Element genre = doc.createElement("genre");
            genre.appendChild(doc.createTextNode("Thriller,Action,Drama"));
            series.appendChild(genre);

            //description Element
            Element description = doc.createElement("description");
            description.appendChild(doc.createTextNode("Description TEXT"));
            series.appendChild(description);




            doc.appendChild(rootElement);


            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);


            File seriesLists = new File(context.getFilesDir(), "seriesList.xml");

            StreamResult result = new StreamResult(seriesLists);
            transformer.transform(source, result);

            System.out.println("File saved!");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

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

    public void updateDataWarehouse(Context context){

    }
}
