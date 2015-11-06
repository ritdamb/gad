package it.unisa.gad.seriestracker;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import it.unisa.gad.seriestracker.util.RSSItem;


public class News extends Fragment {
    private String feedUrl = "";
    private ListView rssListView = null;
    private ArrayList<RSSItem> RSSItems = new ArrayList<RSSItem>();
    private ArrayAdapter<RSSItem> array_adapter = null;
    private View rootView;
    private RSSParseHandler rssparsehandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_news, container, false);

        feedUrl = "http://www.spoilertv.com/feeds/posts/default?alt=rss";

        rssListView = (ListView) rootView.findViewById(R.id.listViewNews);

        array_adapter = new ArrayAdapter<RSSItem>(rootView.getContext(),
                R.layout.list_item, RSSItems);
        rssListView.setAdapter(array_adapter);
        rssparsehandler = new RSSParseHandler();
        rssparsehandler.execute(feedUrl);

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
            array_adapter = new ArrayAdapter<RSSItem>(getContext(),
                    R.layout.list_item, RSSItems);
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

                            RSSItem rssItem = new RSSItem(_title, _description);

                            rssItems.add(rssItem);

                        }
                    }

                }

            } catch (Exception e) {
                Toast.makeText(getContext(),
                        "Errore nel reperire le news", Toast.LENGTH_LONG)
                        .show();

            }

            return rssItems;
        }
    }

}
