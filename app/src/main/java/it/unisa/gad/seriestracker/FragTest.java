package it.unisa.gad.seriestracker;

/**
 * Created by ludimar on 27/10/15.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
        try {
            URL u = new URL(URLConstant.tvComGetNewsUrl);
            BackgroundTask b = new BackgroundTask(XPathConstant.tvComGetLinkShow, u);
            b.execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragtest, container, false);
        return rootView;
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

        }

        @Override
        protected Void doInBackground(Void... params) {
            p.perform();
            p.printElements();
            return null;
        }
    }
}


