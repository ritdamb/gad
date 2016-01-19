package it.unisa.gad.seriestracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.androidquery.AQuery;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SyncStatusObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import it.unisa.gad.seriestracker.Constant.XPathConstant;
import it.unisa.gad.seriestracker.Domain.Series;
import it.unisa.gad.seriestracker.util.ApplicationVariables;
import it.unisa.gad.seriestracker.util.HtmlPageParser;

public class DetailsActivity extends Activity {

	private String nameTelefilm;
	private File file ;
	private ArrayList<String> arrayTelefilm;

	private Document doc;
	private TextView tvDescription;
	private TextView tvTitle;
	private TextView tvGenre;
	private ImageView seriesBanner;
	private Bundle arg;
	private AQuery aq;
	private Series seriesToShow;
	private TextView tvNextEpisode;
	private String idSerie;
	private Series s;
	private WebView webView;
	private Button btnPictures;
	private TextView tvRating;
	private TextView tvNetwork;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_activity);

		tvTitle = (TextView) findViewById(R.id.titleTelefilm);
		tvDescription = (TextView) findViewById(R.id.textDetails);
		tvGenre = (TextView) findViewById(R.id.telefilmGenre);
		seriesBanner = (ImageView) findViewById(R.id.seriesDetBanner);
		tvNextEpisode = (TextView) findViewById(R.id.textNextEpisode);
		webView = (WebView) findViewById(R.id.googleImg);
		tvRating = (TextView) findViewById(R.id.ratingText);
		btnPictures = (Button) findViewById(R.id.buttonPicture);
		arg = getIntent().getExtras();
		nameTelefilm = arg.getString(Series.NAME_TELEFILM);
		tvNetwork  = (TextView) findViewById(R.id.network);

		btnPictures.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				webView.loadUrl("https://www.google.com/search?tbm=isch&q="+nameTelefilm);
			}
		});

		idSerie= arg.getString(Series.ID_TELEFILM);

		s = new Series();
		s.setName(nameTelefilm);
		seriesToShow = ApplicationVariables.getInstance().getSeriesFromData(getApplicationContext(),s);

		if(seriesToShow == null) {
			s.setImageURL(arg.getString("imgUrl"));
			s.setDescription(arg.getString("description"));
			s.setId(arg.getString(Series.ID_TELEFILM));
			s.setImdbID(arg.getString("imdbId"));
			s.setProducer(arg.getString("network"));
		}


		//////////////////////////
		aq = new AQuery(this);
		if(arg.getString("imgUrl") != null ) {
			aq.id(R.id.seriesDetBanner).image(arg.getString("imgUrl"), true, true, 500,0);
		}else {
			if(seriesToShow != null) {
				if(seriesToShow.getImageURL() != null) aq.id(R.id.seriesDetBanner).image(seriesToShow.getImageURL(), true, true, 500,0);
			}
		}
		/////////////////////////
		tvTitle.setText(s.getName());


		Button follow = (Button) findViewById(R.id.buttonFollow);
		follow.setOnClickListener(new MyButtonClickListener());

		if( ApplicationVariables.getInstance().getPreferiteSeries(getApplicationContext()) == null) {
			ArrayList<Series> preferiteSeries = new ArrayList<Series>();
			ApplicationVariables.getInstance().createPreferiteFile(getApplicationContext(), preferiteSeries);
			//Toast.makeText(getApplicationContext(),"PREFERITE SERIES IS NULL",Toast.LENGTH_LONG).show();
		} else {
			//Toast.makeText(getApplicationContext(),"PREFERITE SERIES IS NOT NULL",Toast.LENGTH_LONG).show();
			Series temp = new Series();
			temp.setName(s.getName());
			if(ApplicationVariables.getInstance().checkPreferiteSeries(getApplicationContext(), temp) == false) {
				follow.setText("Follow");
			} else follow.setText("Don't Follow");
		}
	}

	protected void onStart() {
		super.onStart();
		if(seriesToShow == null) {
			//If it's null , it will look in Wikipedia
			String urlMod;
			String urlNextEp="http://next-episode.net/"+nameTelefilm.replaceAll(" ","-");
			Boolean isWiki = true;
			if(s.getProducer() != null) tvNetwork.setText("Network : "+s.getProducer());
			else tvNetwork.setText("Network : Not Available");
			if(s.getImdbID() != null && !(s.getImdbID().equals("NONE"))) {
				urlMod = "http://www.imdb.com/title/"+s.getImdbID();
				isWiki = false;
			} else {
				String seriesNameMod = s.getName().replace(" ", "_");
				urlMod = "https://en.wikipedia.org/wiki/"+seriesNameMod+"_(TV_series)";
			}
			try {
				String[] xPaths = new String[2];
				URL[] urls = new URL[2];

				if(isWiki) xPaths[0]="//div[@id='bodyContent']"; //XPath di Wikipedia
				else xPaths[0]="//div[@id='content-2-wide']"; //Xpath di IMDB

				xPaths[1]="//div[@id=\"next_episode\"]";
				urls[0]= new URL(urlMod);
				urls[1]= new URL(urlNextEp);

				BackgroundTask backgroundTask = new BackgroundTask(xPaths,urls,this,isWiki,false);
				backgroundTask.execute();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} else {
			String[] xPaths = new String[2];
			URL[] urls = new URL[2];
			xPaths[0]="//div[@id='content-2-wide']"; //Xpath di IMDB
			xPaths[1]="//div[@id=\"next_episode\"]";
			try {
				urls[0]= new URL("http://www.imdb.com/title/"+s.getImdbID());
				urls[1]= new URL("http://next-episode.net/"+seriesToShow.getName().replaceAll(" ", "-"));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			BackgroundTask backgroundTask = new BackgroundTask(xPaths,urls,this,false,true);
			backgroundTask.execute();
			if(seriesToShow.getRating().equals("NONE")) tvRating.setText("Rating: Not available");
			else tvRating.setText("Rating: "+seriesToShow.getRating()+"/10");
			tvDescription.setText(seriesToShow.getDescription());
			if(!(seriesToShow.getGenere().equals("NONE")))
				tvGenre.setText("Genre: " + seriesToShow.getGenere());
			else
				tvGenre.setText("Genre: Not Available");
			if(seriesToShow.getProducer() != null) tvNetwork.setText("Network : "+seriesToShow.getProducer());
			else tvNetwork.setText("Network : Not Available");
		}

	}

	public class MyButtonClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
				
				Button button = (Button) v;
				if(button.getText().equals("Follow")){
					ArrayList<Series> preferiteList = ApplicationVariables.getInstance().getPreferiteSeries(getApplicationContext());
					if(preferiteList == null) return;
					if(seriesToShow == null) preferiteList.add(s);
					else preferiteList.add(seriesToShow);
					ApplicationVariables.getInstance().createPreferiteFile(getApplicationContext(), preferiteList);

					Toast.makeText(DetailsActivity.this,
							nameTelefilm + " added to your preferite list", Toast.LENGTH_SHORT).show();
					button.setText("Don't Follow");
				}
				else{
					ArrayList<Series> preferiteList = ApplicationVariables.getInstance().getPreferiteSeries(getApplicationContext());
					for(int i = 0 ; i < preferiteList.size(); i++) {
						if(preferiteList.get(i).getName().equals(nameTelefilm)) {
							preferiteList.remove(i);
							Toast.makeText(DetailsActivity.this,nameTelefilm + " removed from preferite list", Toast.LENGTH_SHORT).show();
							button.setText("Follow");
							ApplicationVariables.getInstance().createPreferiteFile(getApplicationContext(), preferiteList);
							return;
						}
					}
				}
			}

	}


	private class BackgroundTask extends AsyncTask<Void, Void, Void> {

		private URL url;
		private URL url2;
		private String xPath;
		private String xPath2;
		private HtmlPageParser p;
		private XPath xPathObj;
		private ProgressDialog dialog;
		private String wikiDescription;
		private String imdbDescription;
		private String imdbRating;
		private Context context;
		private String genreText;
		private Boolean flag;
		private String urlImage = null;
		private HttpURLConnection urlConnection;
		private Boolean isWiki;
		private String genreValues = "";
		private String data;
		private Boolean justDate;



		public BackgroundTask(String[] xPaths, URL[] urls, Context context,Boolean isWiki,Boolean justDate) {
			this.isWiki = isWiki;
			this.xPath = xPaths[0];
			this.url = urls[0];
			this.xPath2=xPaths[1];
			this.url2=urls[1];
			this.justDate = justDate;
			this.context = context;
			flag = false;


		}

		@Override
		protected void onPreExecute() {
			p = new HtmlPageParser();
			p.setUrl(url);
			p.setXPath(xPath);
			dialog = new ProgressDialog(context);
			dialog.setCancelable(true);
			dialog.setTitle("Loading...");
			dialog.setMessage("Loading The Selected Series...");
			dialog.show();
		}




		protected void onPostExecute(Void result) {
			if(!justDate) {
				if(imdbDescription != null && s.getDescription() != null) {
					if(imdbDescription.length() < s.getDescription().length()) tvDescription.setText(s.getDescription());
					else tvDescription.setText(imdbDescription);
				} else tvDescription.setText(s.getDescription());

				if(genreValues.equals("")) genreValues="Not available";
				tvGenre.setText("Genre: "+genreValues);
				if(data == null) tvNextEpisode.setText("Next Episode: not available");
				else tvNextEpisode.setText("Next Episode :"+data);
				if(imdbRating != null) {
					if(!(imdbRating.equals(""))) {
						tvRating.setText("Rating: "+imdbRating+"/10");
						s.setRating(imdbRating);
					} else tvRating.setText("Rating: Not available");
				} else tvRating.setText("Rating: Not available");

				dialog.dismiss();
				//Aggiungere al DataWarehouse
				s.setDescription(tvDescription.getText().toString());
				s.setGenere(genreValues);
				ApplicationVariables.getInstance().updateDataWarehouse(getApplicationContext(),s);

			}else {
				dialog.dismiss();
				if(data == null) tvNextEpisode.setText("Next Episode: not available");
				else tvNextEpisode.setText("Next Episode :"+data);
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			p.perform();
			doc = p.getResultXmlDocument();
			xPathObj = XPathFactory.newInstance().newXPath();
			if(isWiki) {

			}else {
				try{
					imdbDescription = (String) xPathObj.compile("//div[@itemprop='description']").evaluate(doc,XPathConstants.STRING);
					imdbRating = (String) xPathObj.compile("//span[@itemprop='ratingValue']").evaluate(doc,XPathConstants.STRING);
					NodeList genreList = (NodeList) xPathObj.compile("//span[@itemprop='genre']").evaluate(doc,XPathConstants.NODESET);
					genreValues = "";
					for(int i = 0; i < genreList.getLength() ; i++) {
						genreValues = genreValues+" "+genreList.item(i).getTextContent();
					}


				}catch (XPathExpressionException e) {
					e.printStackTrace();
			}
			}

			p = new HtmlPageParser();
			p.setUrl(url2);
			p.setXPath(xPath2);
			p.perform();
			doc = p.getResultXmlDocument();
			xPathObj = XPathFactory.newInstance().newXPath();
			try {
				Node nodeData = (Node) xPathObj.compile("//div[@id=\"next_episode\"]//div[@class=\"subheadline\" and h3/text()=\"Date:\"]/following::text()[1]").evaluate(doc, XPathConstants.NODE);
				if(nodeData != null ) data = nodeData.getTextContent();
				else data = null;
			}catch (XPathExpressionException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
