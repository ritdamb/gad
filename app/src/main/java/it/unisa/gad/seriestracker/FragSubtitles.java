package it.unisa.gad.seriestracker;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import it.unisa.gad.seriestracker.Constant.URLConstant;
import it.unisa.gad.seriestracker.util.RSSItem;
import it.unisa.gad.seriestracker.util.SubtitlesArrayAdapter;


public class FragSubtitles extends Fragment {
    String feedUrl = "";
    private ListView rssListView = null;
    private ArrayList<RSSItem> RSSItems = new ArrayList<RSSItem>();
    private ArrayAdapter<RSSItem> array_adapter = null;
    private View rootView;
    private RSSParseHandler rssparsehandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_subtitles, container, false);

        //di default al primo caricamento subs in francese
        if (feedUrl.equals("")) {
            feedUrl+=URLConstant.TV_SUBTITLES_RSS_XML+"rssfr.xml";;
        }

        rssparsehandler = new RSSParseHandler();
        rssparsehandler.execute(feedUrl);
        rssListView = (ListView) rootView.findViewById(R.id.listViewSubtitles);

        array_adapter = new SubtitlesArrayAdapter(rootView.getContext(), RSSItems);
        rssListView.setAdapter(array_adapter);
        return rootView;

    }

    @Override
    public void onPause() {
        rssparsehandler.dialog.dismiss();
        super.onPause();
    }

    private class RSSParseHandler extends
            AsyncTask<String, Void, ArrayList<RSSItem>> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getContext());
            dialog.setCancelable(true);
            dialog.setTitle("Caricamento in corso..");
            dialog.show();

        }

        @Override
        protected void onPostExecute(ArrayList<RSSItem> items) {
            dialog.dismiss();
            RSSItems.clear();
            RSSItems.addAll(items);
            array_adapter = new SubtitlesArrayAdapter(getContext(), RSSItems);
            rssListView.setAdapter(array_adapter);
        }

        @Override
        protected ArrayList<RSSItem> doInBackground(String... feedUrl) {
            ArrayList<RSSItem> rssItems = new ArrayList<RSSItem>();

            try {

                URL url = new URL(feedUrl[0]);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();

                    DocumentBuilderFactory dbf = DocumentBuilderFactory
                            .newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();

                    Document document = db.parse(is);
                    Element element = document.getDocumentElement();

                    NodeList nodeList = element.getElementsByTagName("item");

                    if (nodeList.getLength() > 0) {
                        for (int i = 0; i < nodeList.getLength(); i++) {

                            Element entry = (Element) nodeList.item(i);
                            Element _titleE = (Element) entry
                                    .getElementsByTagName("title").item(0);
                            Element _descriptionE = (Element) entry
                                    .getElementsByTagName("description").item(0);

                            String _title = _titleE.getFirstChild()
                                    .getNodeValue();

                            String _description = _descriptionE.getFirstChild()
                                    .getNodeValue();


                            // inizio parsing html description
                            //va poi sostituita con la classe HtmlPageParser

                            HtmlCleaner cleaner = new HtmlCleaner();
                            CleanerProperties props = cleaner.getProperties();
                            props.setAllowHtmlInsideAttributes(true);
                            props.setAllowMultiWordAttributes(true);
                            props.setRecognizeUnicodeChars(true);
                            props.setOmitComments(true);

                            String _img="";

                            try {
                                TagNode rootNode= cleaner.clean(_description);
                                String pattern = "//img[@class ='headerimage']/@src";
                                Object tag[] = rootNode.evaluateXPath(pattern);

                               //se la xpath ha fallito,
                               // prendo la prima immagine disponibile
                                if(tag.length == 0){
                                    pattern="//img[1]/@src";
                                    tag = rootNode.evaluateXPath(pattern);
                                    _img=tag[0].toString();
                                }
                                else
                                    _img = tag[0].toString();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //fine parsing html descriprion

                            RSSItem rssItem = new RSSItem(_title, _description,_img);

                            rssItems.add(rssItem);

                        }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();

            }

            return rssItems;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menusubtitles, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // in base ai subs scelti dal menu refresh del fragment
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.france:
                feedUrl=feedUrl.substring(0,feedUrl.length()-9);
                feedUrl +="rssfr.xml";
                refreshFragment();
                break;
            case R.id.spain:
                feedUrl=feedUrl.substring(0,feedUrl.length()-9);
                feedUrl +="rsses.xml";
                refreshFragment();
                break;
            case R.id.italian:
                feedUrl=feedUrl.substring(0,feedUrl.length()-9);
                feedUrl +="rssit.xml";
                refreshFragment();
                break;

        }
        return true;
    }
    //metodo invocato dopo aver selezionato una scelta dal menu per ricaricare il fragment
    private void refreshFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}