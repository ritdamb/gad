package it.unisa.gad.seriestracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import it.unisa.gad.seriestracker.Domain.Series;
import it.unisa.gad.seriestracker.util.ApplicationVariables;

public class DetailsActivity extends Activity {

	private String nameTelefilm;
	private File file ;
	private ArrayList<String> arrayTelefilm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_activity);

		TextView tvTitle = (TextView) findViewById(R.id.titleTelefilm);
		TextView tvDescription = (TextView) findViewById(R.id.textDetails);

		Bundle arg = getIntent().getExtras();
		nameTelefilm = arg.getString(Series.NAME_TELEFILM);
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
	

}
