package it.unisa.gad.seriestracker;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import it.unisa.gad.seriestracker.util.NewsArrayAdapter;
import it.unisa.gad.seriestracker.util.RSSItem;


public class FragNews extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<RSSItem> RSSItems = new ArrayList<RSSItem>();
    private ArrayAdapter<RSSItem> array_adapter = null;
    private ListView rssListView = null;
    private ParseHandler parseHandler = null;
    private XPath xPathObj;

    // TODO: Rename and change types of parameters
    public static FragNews newInstance(String param1, String param2) {
        FragNews fragment = new FragNews();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragNews() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // TODO: Change Adapter to display your content
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);


        rssListView = (ListView) view.findViewById(R.id.listViewNews);
        rssListView.setOnItemClickListener(this);

        rssListView.setAdapter(array_adapter);
        parseHandler = new ParseHandler();
        parseHandler.execute();

        return view;
    }


    private class ParseHandler extends
            AsyncTask<String, Void, ArrayList<RSSItem>> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getContext());
            dialog.setCancelable(true);
            dialog.setTitle("Loading...");
            dialog.setMessage("Loading News...");
            dialog.show();

        }

        @Override
        protected void onPostExecute(ArrayList<RSSItem> items) {
            dialog.dismiss();
            RSSItems.clear();
            RSSItems.addAll(items);
            array_adapter = new NewsArrayAdapter(getContext(), RSSItems);
            rssListView.setAdapter(array_adapter);
        }

        @Override
        protected ArrayList<RSSItem> doInBackground(String... feedUrl) {

            ArrayList<RSSItem> rssItems = new ArrayList<RSSItem>();
            URL url = null;

            try {
                url = new URL(URLConstant.TV_COM_NEWS_SECTION_URL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            HtmlPageParser p = new HtmlPageParser();
            p.setUrl(url);
            p.setXPath(XPathConstant.TV_COM_NEWS_GET_NEWS);

            p.perform();
            Document doc = p.getResultXmlDocument();

            Element element = doc.getDocumentElement();

            NodeList nodeList = element.getElementsByTagName("li");
            xPathObj = XPathFactory.newInstance().newXPath();
            try {
                for(int i = 1 ; i < doc.getElementsByTagName("li").getLength(); i ++ ) {

                    RSSItem item = new RSSItem();

                    Node img = (Node) xPathObj.compile("//li["+i+"]/div/div/a/img/@src").evaluate(doc, XPathConstants.NODE);
                    if(img == null) {
                        item.setImageURL("");
                    }else {
                        item.setImageURL(img.getTextContent());
                        System.out.println("IMG: "+item.getImageURL());
                    }

                    Object title = xPathObj.compile("//li["+i+"]/div[@class=\"info\"]/h3/a").evaluate(doc);

                    if(title == null){
                        item.setTitle("");
                    }else{
                        item.setTitle(title.toString());
                    }
                    System.out.println("TITLE: "+item.getTitle());

                    Node description = (Node) xPathObj.compile("//li["+i+"]/div[@class=\"info\"]/p[@class=\"body\"]/text()").evaluate(doc, XPathConstants.NODE);
                    if(description == null){
                        item.setDescription("");
                    }else{
                        item.setDescription(description.getTextContent());
                        System.out.println("DESCRIPTION: "+item.getDescription());
                    }
                    Node urlDescription = (Node) xPathObj.compile("//li["+i+"]/div[@class=\"info\"]/h3/a/@href").evaluate(doc, XPathConstants.NODE);
                    if(urlDescription == null){
                        item.setUrlDescription("");
                    }else{
                        item.setUrlDescription(urlDescription.getTextContent());
                        System.out.println("URLDESC: "+item.getUrlDescription());
                    }

                    rssItems.add(item);
                }

            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }

                    return rssItems;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String title = RSSItems.get(position).getTitle();
        String description = RSSItems.get(position).getDescription();
        FragNewsDetails fragment = FragNewsDetails.newInstance(title, description);

        if (title.contains("POLL")) {
            Toast.makeText(getContext(), "POLL", Toast.LENGTH_SHORT).show();

        } else {
            //Toast.makeText(getContext(), "not POLL", Toast.LENGTH_SHORT).show();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }


    }
}
