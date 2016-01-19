package it.unisa.gad.seriestracker.util;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import javax.xml.xpath.XPath;
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

    public ApplicationVariables() {

    }

    public static synchronized void initInstance() {
        if (singletonApp == null) {
            singletonApp = new ApplicationVariables();
        }
    }

    public static ApplicationVariables getInstance() {
        return singletonApp;
    }

    public void setTodaySeries(Document doc) {
        this.todaySeries = doc;
    }

    public Document getTodaySeries() {
        return todaySeries;
    }

    public ArrayList<Series> getPreferiteSeries(Context context) {
        try {
            FileInputStream fileInputStream = context.openFileInput("preferiteSeries.xml");
            if (fileInputStream == null) return null;
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

    public boolean savePreferiteSeries(Context context, Series series) {
        ArrayList<Series> preferiteSeries = getPreferiteSeries(context);
        if (preferiteSeries == null) return false;
        else {
            preferiteSeries.add(series);
            return createPreferiteFile(context, preferiteSeries);
        }
    }

    public boolean checkPreferiteSeries(Context context, Series series) {
        ArrayList<Series> preferiteSeries = getPreferiteSeries(context);
        if (preferiteSeries == null) return false;
        else {
            for (int i = 0; i < preferiteSeries.size(); i++) {
                if (preferiteSeries.get(i).getName().equals(series.getName())) return true;
            }
            return false;
        }
    }

    public boolean createPreferiteFile(Context context, ArrayList<Series> seriesList) {
        try {
            File preferiteLists = new File(context.getFilesDir(), "preferiteSeries.xml");
            FileOutputStream outputStream;
            outputStream = context.openFileOutput("preferiteSeries.xml", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            ArrayList<Series> seriesToSave  = new ArrayList<>();
            for(int i = 0 ; i < seriesList.size() ; i++) {
                seriesToSave.add(ApplicationVariables.getInstance().getSeriesFromData(context,seriesList.get(i)));
            }
            os.writeObject(seriesToSave);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkDataWarehouse(Context context) {
        try {
            FileInputStream fileInputStream = context.openFileInput("seriesList.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document original = db.parse(new InputSource(fileInputStream));

            printDocument(original, System.out);

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

    public void createDataWareHouse(Context context) {
        try {
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
            name.appendChild(doc.createTextNode(""));
            series.appendChild(name);

            // genre elements
            Element genre = doc.createElement("genre");
            genre.appendChild(doc.createTextNode(""));
            series.appendChild(genre);

            //description Element
            Element description = doc.createElement("description");
            description.appendChild(doc.createTextNode(""));
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

    public Series getSeriesFromData(Context context, Series series) {
        try {
            FileInputStream fileInputStream = context.openFileInput("seriesList.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document original = db.parse(new InputSource(fileInputStream));
            //check if already Exists
            XPath xPathObj = XPathFactory.newInstance().newXPath();
            Node node = (Node) xPathObj.compile("//series[name='" + series.getName() + "']").evaluate(original, XPathConstants.NODE);
            if (node != null) {
                System.out.println("Series Already Exists");
                Series s = new Series();
                for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                    if (node.getChildNodes().item(i).getNodeName().equals("tvdb_id"))
                        s.setId(node.getChildNodes().item(i).getTextContent());
                    if(node.getChildNodes().item(i).getNodeName().equals("imdb_id"))
                        s.setImdbID(node.getChildNodes().item(i).getTextContent());
                    if (node.getChildNodes().item(i).getNodeName().equals("name"))
                        s.setName(node.getChildNodes().item(i).getTextContent());
                    if (node.getChildNodes().item(i).getNodeName().equals("description"))
                        s.setDescription(node.getChildNodes().item(i).getTextContent());
                    if (node.getChildNodes().item(i).getNodeName().equals("genre"))
                        s.setGenere(node.getChildNodes().item(i).getTextContent());
                    if (node.getChildNodes().item(i).getNodeName().equals("image"))
                        s.setImageURL(node.getChildNodes().item(i).getTextContent());
                    if (node.getChildNodes().item(i).getNodeName().equals("rating"))
                        s.setRating(node.getChildNodes().item(i).getTextContent());
                    if (node.getChildNodes().item(i).getNodeName().equals("network"))
                        s.setProducer(node.getChildNodes().item(i).getTextContent());
                }
                return s;
            } else return null;
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Series updateDataWarehouse(Context context, Series series) {

        try{
            FileInputStream fileInputStream = context.openFileInput("seriesList.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document original = db.parse(new InputSource(fileInputStream));
            //check if already Exists
            Series x = getSeriesFromData(context, series);
            if(x != null) {
                System.out.println("Series Already Exists");
                return x;
            } else {
            // Series doesn't Exists, create it
            // series elements
                 Element seriesElement = original.createElement("series");


            //ID Element
                Element id = original.createElement("tvdb_id");
                if(series.getId() == null) series.setId("NONE");
                id.appendChild(original.createTextNode(series.getId()));
                seriesElement.appendChild(id);

                //IMDB_ID Element DA MODIFICARE
                Element imdb_id = original.createElement("imdb_id");
                if(series.getImdbID() == null) series.setImdbID("NONE");
                imdb_id.appendChild(original.createTextNode(series.getImdbID()));
                seriesElement.appendChild(imdb_id);

                // name elements
                Element name = original.createElement("name");
                name.appendChild(original.createTextNode(series.getName()));
                seriesElement.appendChild(name);

                //series Description
                Element descriptionElement = original.createElement("description");
                if(series.getDescription() == null) series.setDescription("");
                descriptionElement.appendChild(original.createTextNode(series.getDescription()));
                seriesElement.appendChild(descriptionElement);

                //series Genre
                Element genreElement = original.createElement("genre");
                if(series.getGenere() == null) series.setGenere("NONE");
                genreElement.appendChild(original.createTextNode(series.getGenere()));
                seriesElement.appendChild(genreElement);

                //Image Bytes
                Element imageElement = original.createElement("image");
                if(series.getImageURL() == null ) series.setImageURL("NONE");
                imageElement.appendChild(original.createTextNode(series.getImageURL()));
                seriesElement.appendChild(imageElement);

                //Producer ( Network )
                Element networkElement = original.createElement("network");
                if(series.getProducer() == null ) series.setProducer("NONE");
                networkElement.appendChild(original.createTextNode(series.getProducer()));
                seriesElement.appendChild(networkElement);

                Element rating = original.createElement("rating");
                if(series.getRating() == null) series.setRating("NONE");
                rating.appendChild(original.createTextNode(series.getRating()));
                seriesElement.appendChild(rating);


                original.getDocumentElement().appendChild(seriesElement);

                // write the content into xml file
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(original);


                File seriesLists = new File(context.getFilesDir(), "seriesList.xml");

                StreamResult result = new StreamResult(seriesLists);
                transformer.transform(source, result);

                System.out.println("File updated!");

            }

            printDocument(original,System.out);



        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (TransformerException e) {
            e.printStackTrace();
            return null;
        }
        return null;

    }

}
