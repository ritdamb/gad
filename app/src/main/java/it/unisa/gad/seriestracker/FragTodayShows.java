package it.unisa.gad.seriestracker;

/**
 * Created by ludimar on 25/11/15.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.w3c.dom.Document;

import java.net.MalformedURLException;
import java.net.URL;

import it.unisa.gad.seriestracker.Constant.URLConstant;
import it.unisa.gad.seriestracker.Constant.XPathConstant;
import it.unisa.gad.seriestracker.Domain.Series;
import it.unisa.gad.seriestracker.util.ApplicationVariables;
import it.unisa.gad.seriestracker.util.HtmlPageParser;


/**
 * Created by ludimar on 27/10/15.
 */

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import it.unisa.gad.seriestracker.Constant.URLConstant;
import it.unisa.gad.seriestracker.Constant.XPathConstant;
import it.unisa.gad.seriestracker.util.HtmlPageParser;
import it.unisa.gad.seriestracker.util.SeriesArrayAdapter;


public class FragTodayShows extends android.support.v4.app.Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private int mParam1;
    private OnFragmentInteractionListener mListener;
    private Context context;
    private Document doc;
    private ListView list;
    private SeriesArrayAdapter arrayAdapter;
    private ArrayList<Series> seriesList;

    public static FragTodayShows newInstance(int param1) {
        FragTodayShows fragment = new FragTodayShows();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public FragTodayShows() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        context = getContext();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_today_shows, container, false);
        seriesList = new ArrayList<Series>();
        arrayAdapter = new SeriesArrayAdapter(getContext(),seriesList);
        list = (ListView) rootView.findViewById(R.id.listViewTonight);
        downloadTrendingTonightSeries();
        init();
        return rootView;
    }
    public void downloadTrendingTonightSeries(){
        try {

            URL u = new URL(URLConstant.TV_GUIDE_TRENDING_TONIGHT);
            BackgroundTask b = new BackgroundTask(XPathConstant.TV_GUIDE_TRENDING_TONIGHT, u);
            b.execute();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void init() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Series s = (Series) parent.getItemAtPosition(position);
                Intent intent = new Intent(getView().getContext(), DetailsActivity.class);
                intent.putExtra(Series.NAME_TELEFILM, s.getName());
                startActivity(intent);

            }
        });
    }

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {

        private URL url;
        private String xPath;
        private HtmlPageParser p;
        private XPath xPathObj;
        private ProgressDialog dialog;

        public BackgroundTask(String xPath, URL url) {
            this.xPath = xPath;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            p = new HtmlPageParser();
            p.setUrl(url);
            p.setXPath(xPath);
            dialog = new ProgressDialog(getContext());
            dialog.setCancelable(true);
            dialog.setTitle("Loading...");
            dialog.setMessage("Loading The Tonight Series...");
            dialog.show();

        }

        @Override
        protected void onPostExecute(Void result) {

            list.setAdapter(arrayAdapter);
            dialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            p.perform();
            doc = p.getResultXmlDocument();
            xPathObj = XPathFactory.newInstance().newXPath();
            try {
                for(int i = 1 ; i < doc.getElementsByTagName("li").getLength(); i ++ ) {
                    Series temp = new Series();
                    Node  name = (Node) xPathObj.compile("//li["+i+"]/div[3]/h3").evaluate(doc, XPathConstants.NODE);
                    if(name == null) {
                        temp.setName("");
                    }else {
                        temp.setName(name.getTextContent());
                    }
                    Node  title = (Node) xPathObj.compile("//li["+i+"]/div[3]/h4").evaluate(doc, XPathConstants.NODE);
                    if(title == null) {
                        temp.setEpisodeTitle("");
                    }else {
                        temp.setEpisodeTitle((title.getTextContent().trim()).replace("\n", "").replace("\r", ""));
                    }
                    Node  start = (Node) xPathObj.compile("//li["+i+"]/div[3]/p[@class='listings-program-airing-info']").evaluate(doc, XPathConstants.NODE);
                    if(start == null) {
                        temp.setStartH("");
                    }else {
                        temp.setStartH(start.getTextContent());
                    }
                    String url = (String) xPathObj.compile("//li["+i+"]/div[1]/a/img/@src").evaluate(doc, XPathConstants.STRING);
                    if(url == null ) {
                        temp.setImageURL("");
                    } else {
                        temp.setImageURL(url);
                    }
                    seriesList.add(temp);
                }

            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}


