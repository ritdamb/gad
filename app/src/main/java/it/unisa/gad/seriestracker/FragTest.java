package it.unisa.gad.seriestracker;

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

import org.w3c.dom.Document;

import java.net.MalformedURLException;
import java.net.URL;

import it.unisa.gad.seriestracker.Constant.URLConstant;
import it.unisa.gad.seriestracker.Constant.XPathConstant;
import it.unisa.gad.seriestracker.util.HtmlPageParser;


public class FragTest extends android.support.v4.app.Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private int mParam1;
    private OnFragmentInteractionListener mListener;
    private Context context;
    private Document doc;

    public static FragTest newInstance(int param1) {
        FragTest fragment = new FragTest();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public FragTest() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        context = getContext();
        downloadTrendingTonightSeries();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragtest, container, false);
        return rootView;
    }
    public void downloadTrendingTonightSeries(){
        try {
//            URL u = new URL(URLConstant.TV_COM_GET_NEWS_URL);
//            BackgroundTask b = new BackgroundTask(XPathConstant.TV_COM_GET_LINK_SHOW, u);
//            b.execute();
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

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {

        private URL url;
        private String xPath;
        private HtmlPageParser p;

        public BackgroundTask(String xPath, URL url) {
            this.xPath = xPath;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            p = new HtmlPageParser();
            p.setUrl(url);
            p.setXPath(xPath);

        }

        @Override
        protected void onPostExecute(Void result) {

            doc = p.getDomDocument();
            for(int i = 0 ; i < doc.getElementsByTagName("h3").getLength() ; i++ ) {
                System.out.println("###NODE VALUE "+doc.getElementsByTagName("h3").item(i).getTextContent());
            }
            System.out.println("**NodeFirstChild" + doc.getFirstChild().getNodeValue());
        }

        @Override
        protected Void doInBackground(Void... params) {
            p.perform();
            return null;
        }
    }
}


