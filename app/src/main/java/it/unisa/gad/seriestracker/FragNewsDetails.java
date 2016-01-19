package it.unisa.gad.seriestracker;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.XMLReader;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import it.unisa.gad.seriestracker.Constant.URLConstant;
import it.unisa.gad.seriestracker.Constant.XPathConstant;
import it.unisa.gad.seriestracker.util.HtmlPageParser;
import it.unisa.gad.seriestracker.util.NewsArrayAdapter;
import it.unisa.gad.seriestracker.util.RSSItem;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link FragNewsDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragNewsDetails extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String title;
    private String urldescription;
    private View rootView;
    private TextView tvDescription,tvTitle;
    private XPath xPathObj;
    private boolean imageChecker;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragNewsDetails.
     */
    // TODO: Rename and change types and number of parameters
    public static FragNewsDetails newInstance(String param1, String param2) {
        FragNewsDetails fragment = new FragNewsDetails();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragNewsDetails() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_PARAM1);
            urldescription = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_news_details, container, false);

        imageChecker=false;

        tvTitle = (TextView) rootView.findViewById(R.id.textViewDetailsTitle);
        tvDescription = (TextView) rootView.findViewById(R.id.textViewDetailsDescription);


        ParseHandler parseHandler = new ParseHandler();
        parseHandler.execute();



        return rootView;


    }


    private class ParseHandler extends
            AsyncTask<String, Void, String> {

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
        protected void onPostExecute(String result) {
            dialog.dismiss();
            result = result.replaceAll("&amp;quot;","''");
            Spanned span = Html.fromHtml(result, new ImageGetter(), null);
            tvDescription.setText(span);
            tvDescription.setMovementMethod(new ScrollingMovementMethod());
            tvTitle.setText(title);

        }

        @Override
        protected String doInBackground(String... feedUrl) {

            URL url = null;
            String pattern="";

            if(urldescription.contains("http://www.tv.com/news/"))
                pattern=XPathConstant.TV_COM_GET_NEWS_DETAILS_NEWS;
            else
                pattern=XPathConstant.TV_COM_GET_NEWS_DETAILS_SHOWS;



            try {
                url = new URL(urldescription);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            HtmlPageParser p = new HtmlPageParser();
            p.setUrl(url);
            p.setXPath(pattern);
            p.perform();
            Document doc = p.getResultXmlDocument();


            String resulting ="";
            DocumentBuilderFactory domFact = DocumentBuilderFactory.newInstance();
            try {
                DOMSource domSource = new DOMSource(doc);
                StringWriter writer = new StringWriter();
                StreamResult result = new StreamResult(writer);
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer transformer = tf.newTransformer();
                transformer.transform(domSource, result);
                resulting = writer.toString();
            }  catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }





            return resulting;
        }
    }


    private class ImageGetter implements Html.ImageGetter {

        public Drawable getDrawable(String source) {

            Drawable d = new ColorDrawable(Color.TRANSPARENT);
            AQuery aq = new AQuery(rootView);
            if(imageChecker) return d;
            aq.id(R.id.imageView).image(source, true, true);
            imageChecker=true;
            Log.e("MYTAG", source);
            return d;
        }
    };

}

