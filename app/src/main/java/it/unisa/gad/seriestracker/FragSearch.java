package it.unisa.gad.seriestracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import it.unisa.gad.seriestracker.Constant.URLConstant;
import it.unisa.gad.seriestracker.Constant.XPathConstant;
import it.unisa.gad.seriestracker.Domain.Series;
import it.unisa.gad.seriestracker.util.ApplicationVariables;
import it.unisa.gad.seriestracker.util.FoundSeriesAdapter;
import it.unisa.gad.seriestracker.util.HtmlPageParser;

/**
 * Created by ludimar on 02/12/15.
 */
public class FragSearch extends android.support.v4.app.Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private int mParam1;
    private OnFragmentInteractionListener mListener;
    private Context context;
    private Button btnSearch;
    private EditText seriesNameValue;
    private ListView resultList;

    public static FragSearch newInstance(int param1) {
        FragSearch fragment = new FragSearch();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public FragSearch() {
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
        View rootView = inflater.inflate(R.layout.frag_search, container, false);

        btnSearch = (Button) rootView.findViewById(R.id.btnSearch);
        seriesNameValue = (EditText) rootView.findViewById(R.id.seriesNameValue);
        resultList = (ListView) rootView.findViewById(R.id.resultListView);


        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Series serie = (Series) parent.getItemAtPosition(position);
                String nameTele = serie.getName();
                Intent intent = new Intent(getView().getContext(), DetailsActivity.class);
                intent.putExtra(Series.NAME_TELEFILM, nameTele);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                Bitmap b = serie.getImageBig();
                if(b == null ) {
                    Toast.makeText(getActivity().getApplicationContext(),"NULL BITCH "+serie.getImageURL(),Toast.LENGTH_LONG).show();
                    return;
                }
                b.compress(Bitmap.CompressFormat.PNG,50,bs);
                intent.putExtra("img", bs.toByteArray());
                Log.e("MYTAG", serie.getImageURL());
                intent.putExtra("imgUrl",serie.getImageURL());
                intent.putExtra("description",serie.getDescription());
                startActivity(intent);
            }
        });

        init();
        return rootView;
    }

    private void init(){

        btnSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        try {
                            String seriesName = ""+seriesNameValue.getText();
                            seriesName = seriesName.replace(" ","%20");
                            ///CERCARE PRIMA NELL IPOTETICO DATAWAREHOUSE , SE NON ESISTE , ESEGUIRE CODICE BELOW
                            String link = "http://thetvdb.com/api/GetSeries.php?seriesname="+seriesName;
                            URL url = new URL(link);
                            BackgroundTask b = new BackgroundTask(getActivity().getApplicationContext(),url,resultList);
                            b.execute();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return true;
            }

        });

    }

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {

        private URL url;

        private XPath xPathObj;
        private ProgressDialog dialog;
        private HttpURLConnection urlConnection;
        private ArrayList<Series> seriesFound;
        private FoundSeriesAdapter arrayAdapter;
        private Context context;
        private ListView resultList;

        public BackgroundTask(Context context , URL url, ListView list) {

            this.url = url;
            seriesFound = new ArrayList<>();
            arrayAdapter = new FoundSeriesAdapter(context,seriesFound);
            this.context = context;
            this.resultList = list;

            try {
                urlConnection = (HttpURLConnection) this.url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {

            dialog = new ProgressDialog(getContext());
            dialog.setCancelable(true);
            dialog.setTitle("Searching...");
            dialog.setMessage("Looking For Series...");
            dialog.show();

        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            if(arrayAdapter.getCount() == 0 ) {
                Toast.makeText(context,"NO RESULT FOUND",Toast.LENGTH_LONG).show();
            } else {
                resultList.setAdapter(arrayAdapter);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(urlConnection.getInputStream());
                ApplicationVariables.getInstance().printDocument(doc,System.out);
                NodeList nodes = doc.getElementsByTagName("Series");
                for(int i = 0 ; i < nodes.getLength() ; i++) {
                    nodes.item(i).getChildNodes();
                    Series temp = new Series();
                    for(int x = 0 ; x < nodes.item(i).getChildNodes().getLength(); x++) {

                        if(nodes.item(i).getChildNodes().item(x).getNodeName().equals("SeriesName")) {
                            temp.setName(nodes.item(i).getChildNodes().item(x).getTextContent());
                        }
                        if(nodes.item(i).getChildNodes().item(x).getNodeName().equals("banner")) {
                            temp.setImageURL("http://thetvdb.com/banners/" + nodes.item(i).getChildNodes().item(x).getTextContent());
                            System.out.println("###Searched Img Url = "+temp.getImageURL());
                        }
                        if(nodes.item(i).getChildNodes().item(x).getNodeName().equals("FirstAired")) {
                            temp.setFirstAired(nodes.item(i).getChildNodes().item(x).getTextContent());
                        }
                        if(nodes.item(i).getChildNodes().item(x).getNodeName().equals("Network")) {
                            temp.setProducer(nodes.item(i).getChildNodes().item(x).getTextContent());
                        }
                        if(nodes.item(i).getChildNodes().item(x).getNodeName().equals("Overview")) {
                            temp.setDescription(nodes.item(i).getChildNodes().item(x).getTextContent());
                        }
                    }
                    seriesFound.add(temp);
                }

                System.out.println(seriesFound.size() + " series found");

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }

            return null;
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

}
