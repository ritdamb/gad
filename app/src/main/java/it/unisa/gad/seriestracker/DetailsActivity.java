package it.unisa.gad.seriestracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import com.androidquery.AQuery;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

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


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_activity);

		tvTitle = (TextView) findViewById(R.id.titleTelefilm);
		tvDescription = (TextView) findViewById(R.id.textDetails);
		tvGenre = (TextView) findViewById(R.id.telefilmGenre);
		seriesBanner = (ImageView) findViewById(R.id.seriesDetBanner);

		arg = getIntent().getExtras();
		nameTelefilm = arg.getString(Series.NAME_TELEFILM);
		Bitmap b = BitmapFactory.decodeByteArray(arg.getByteArray("img"),0,arg.getByteArray("img").length);

		seriesBanner.setImageBitmap(b);
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
			if(ApplicationVariables.getInstance().checkPreferiteSeries(getApplicationContext(),temp) == false) {
				follow.setText("Follow");
			} else follow.setText("Don't Follow");


		}
	}

	protected void onStart() {
		super.onStart();

		String seriesNameMod = nameTelefilm.replace(" ","_");
		String urlMod = "https://en.wikipedia.org/wiki/"+seriesNameMod+"_(TV_series)";
		try {
			URL url = new URL(urlMod);
			BackgroundTask backgroundTask = new BackgroundTask("//div[@id='bodyContent']",url,this);
			backgroundTask.execute();
		} catch (MalformedURLException e) {
			e.printStackTrace();
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
							nameTelefilm + "added to your preferite list", Toast.LENGTH_SHORT).show();
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
		private String xPath;
		private HtmlPageParser p;
		private XPath xPathObj;
		private ProgressDialog dialog;
		private String wikiDescription;
		private Context context;
		private String genreText;
		private Boolean flag;


		public BackgroundTask(String xPath, URL url, Context context) {
			this.xPath = xPath;
			this.url = url;
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
			if(flag){
				tvDescription.setText(wikiDescription);
				tvGenre.setText("GENRE: "+genreText);
			} else {
				tvGenre.setText("");
				tvDescription.setText(arg.getString("description"));
			}
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
			return null;
		}
	}



}
