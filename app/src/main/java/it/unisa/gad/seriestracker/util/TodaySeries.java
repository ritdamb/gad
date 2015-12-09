package it.unisa.gad.seriestracker.util;

import android.os.AsyncTask;

import java.net.URL;

/**
 * Created by ludimar on 16/11/15.
 */
public class TodaySeries extends AsyncTask<Void, Void, Void> {

    private URL url;
    private String xPath;
    private HtmlPageParser p;

    public TodaySeries( URL url) {
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
        p.printElements();
    }

    @Override
    protected Void doInBackground(Void... params) {
        p.perform();
        return null;
    }
}

