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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_activity);

		tvTitle = (TextView) findViewById(R.id.titleTelefilm);
		tvDescription = (TextView) findViewById(R.id.textDetails);
		tvGenre = (TextView) findViewById(R.id.telefilmGenre);
		seriesBanner = (ImageView) findViewById(R.id.seriesDetBanner);
		tvNextEpisode = (TextView) findViewById(R.id.textNextEpisode);

		arg = getIntent().getExtras();
		nameTelefilm = arg.getString(Series.NAME_TELEFILM);
		idSerie= arg.getString(Series.ID_TELEFILM);

		Series s = new Series();
		s.setName(nameTelefilm);
		seriesToShow = ApplicationVariables.getInstance().getSeriesFromData(getApplicationContext(),s);



		//////////////////////////
		aq = new AQuery(this);
		if(arg.getString("imgUrl") != null ) {
			aq.id(R.id.seriesDetBanner).image(arg.getString("imgUrl"), true, true, 500,0);
		}
		/////////////////////////
		tvTitle.setText(nameTelefilm);


		Button follow = (Button) findViewById(R.id.buttonFollow);
		follow.setOnClickListener(new MyButtonClickListener());

		if( ApplicationVariables.getInstance().getPreferiteSeries(getApplicationContext()) == null) {
			ArrayList<Series> preferiteSeries = new ArrayList<Series>();
			ApplicationVariables.getInstance().createPreferiteFile(getApplicationContext(), preferiteSeries);
			//Toast.makeText(getApplicationContext(),"PREFERITE SERIES IS NULL",Toast.LENGTH_LONG).show();
		} else {
			//Toast.makeText(getApplicationContext(),"PREFERITE SERIES IS NOT NULL",Toast.LENGTH_LONG).show();
			Series temp = new Series();
			temp.setName(nameTelefilm);
			if(ApplicationVariables.getInstance().checkPreferiteSeries(getApplicationContext(), temp) == false) {
				follow.setText("Follow");
			} else follow.setText("Don't Follow");
		}
	}

	protected void onStart() {
		super.onStart();
		if(seriesToShow == null) {
			String seriesNameMod = nameTelefilm.replace(" ","_");
			String urlMod = "https://en.wikipedia.org/wiki/"+seriesNameMod+"_(TV_series)";
			String urlNextEp="";
			if(idSerie != null)
				urlNextEp = "http://www.tvshowsmanager.com/serie.php?id="+idSerie;

			Log.e("MyTAG",urlNextEp);
			try {
				String[] xPaths = new String[2];
				URL[] urls = new URL[2];

				xPaths[0]="//div[@id='bodyContent']";;
				xPaths[1]=XPathConstant.TV_SHOW_MANAGER_DAYS_FOR_NEXT_EP;

				urls[0]= new URL(urlMod);
				if(idSerie != null)
					urls[1]= new URL(urlNextEp);
				BackgroundTask backgroundTask = new BackgroundTask(xPaths,urls,this);
				backgroundTask.execute();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} else {
			tvDescription.setText(seriesToShow.getDescription());
			tvGenre.setText("GENRE: " + seriesToShow.getGenere());

		}
	}

	public class MyButtonClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
				
				Button button = (Button) v;
				if(button.getText().equals("Follow")){
					ArrayList<Series> preferiteList = ApplicationVariables.getInstance().getPreferiteSeries(getApplicationContext());
					if(preferiteList == null) return;
					Series temp = new Series();
					temp.setName(nameTelefilm);
					preferiteList.add(temp);
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
		private Context context;
		private String genreText;
		private Boolean flag;
		private String urlImage = null;
		private HttpURLConnection urlConnection;



		public BackgroundTask(String[] xPaths, URL[] urls, Context context) {
			this.xPath = xPaths[0];
			this.url = urls[0];
			this.xPath2 = xPaths[1];
			if(idSerie != null) {
				this.url2 = urls[1];
				try {
					urlConnection = (HttpURLConnection) this.url2.openConnection();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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

		@Override
		protected void onPostExecute(Void result) {

			//INSERIRE I VALORI NEI TEXFIELD
			Series temp = new Series();
			temp.setName(nameTelefilm);
			if(flag){
				tvDescription.setText(wikiDescription);
				tvGenre.setText("GENRE: "+genreText);
				temp.setDescription(wikiDescription);
				temp.setGenere(genreText);
			} else {
				tvGenre.setText("");
				temp.setGenere("");
				tvDescription.setText(arg.getString("description"));
				temp.setDescription(arg.getString("description"));
			}
//			if(arg.getString("imgUrl") != null ) {
//				if(!(arg.getString("imgUrl").equals("NONE"))) temp.setImageURL(arg.getString("imgUrl"));
//			} else {
////				if(urlImage != null) {
////					System.out.println("####### "+urlImage);
////					temp.setImageURL(urlImage);
////				}else temp.setImageURL("NONE");
//			}
			ApplicationVariables.getInstance().updateDataWarehouse(context, temp);
			dialog.dismiss();
		}

		@Override
		protected Void doInBackground(Void... params) {
			p.perform();
			doc = p.getResultXmlDocument();
			xPathObj = XPathFactory.newInstance().newXPath();
			wikiDescription = "";
			try {
				Node genre = (Node) xPathObj.compile("//table/tbody/tr/td[@class='category']").evaluate(doc, XPathConstants.NODE);
//				urlImage = (String) xPathObj.compile("//*[@id='mw-content-text']/table[1]/tbody/tr[2]/td/a/img/@src").evaluate(doc, XPathConstants.STRING);
//				System.out.println("URL IMAGE = "+urlImage);
				if(genre == null) {
					flag = false;
				} else {
				flag = true;
				genreText = genre.getTextContent();
				genreText = genreText.trim();
				genreText = genreText.replace("\n", " ");
				for(int i = 0; i < doc.getDocumentElement().getElementsByTagName("p").getLength() ; i++){
					if(doc.getDocumentElement().getElementsByTagName("p").item(i).getParentNode().getParentNode().getNodeName().equals("div")) {
						if(doc.getDocumentElement().getElementsByTagName("p").item(i).getTextContent().length() > 30) {
							if( (wikiDescription+doc.getDocumentElement().getElementsByTagName("p").item(i).getTextContent()).length() > 2000 ) {
								break;
							} else {
								wikiDescription = wikiDescription+"\n"+doc.getDocumentElement().getElementsByTagName("p").item(i).getTextContent();
							}
						}
					}
				}
				}
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}


			if(idSerie != null) {

				urlConnection.setDoOutput(true);
				urlConnection.setChunkedStreamingMode(0);
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = null;
				String daysUntil="";
				try {
					System.out.println("STO ENTRANDO QUA DENTRO");
					db = dbf.newDocumentBuilder();
					Document doc = db.parse(urlConnection.getInputStream());
					xPathObj = XPathFactory.newInstance().newXPath();
					ApplicationVariables.getInstance().printDocument(doc, System.out);
					Node days = (Node) xPathObj.compile(XPathConstant.TV_SHOW_MANAGER_DAYS_FOR_NEXT_EP).evaluate(doc, XPathConstants.NODE);
					daysUntil = days.getTextContent();
					System.out.println("STICAZZODIGIORNI_-----_>"+daysUntil);


				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XPathExpressionException e) {
					e.printStackTrace();
				} catch (TransformerException e) {
					e.printStackTrace();
				}




/*
				Log.e("MYTAG", "element ->"+element.getSchemaTypeInfo());
				String daysUntil = element.getFirstChild()
						.getNodeValue();
				Log.e("MYTAG", "I Giorni sono ->"+daysUntil);

				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(daysUntil));
				int days = calendar.get(Calendar.DAY_OF_MONTH);
				int month = calendar.get(Calendar.DAY_OF_WEEK);
				int years = calendar.get(Calendar.DAY_OF_YEAR);

				String dateNextEp = days + "/" + month + "/" + years;

				Toast.makeText(context, dateNextEp, Toast.LENGTH_SHORT).show();*/
			}
			return null;
		}
	}
}
