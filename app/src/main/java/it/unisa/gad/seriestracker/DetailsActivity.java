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

		File fileDir = getDocumentStorageDir("telefilm"); // prendo la directory
		file = new File(fileDir, "telefilm.txt");
		
		if (!file.exists()) { // creo il file se non esiste
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//controllo se il telefilm è già seguito
		
		arrayTelefilm = loadFileInArray(file);
		Boolean flag = false;
		
		for (String name : arrayTelefilm)
			if(name.equals(nameTelefilm)) 
				flag=true;
	
		if(flag)
			follow.setText("Non seguire");
	}

	public class MyButtonClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			
			if (isExternalStorageWritable()) { 
				
				Button button = (Button) v;
				if(button.getText().equals("Segui")){
					writeOnFile(file,nameTelefilm,true);
					Toast.makeText(DetailsActivity.this,
							nameTelefilm + " aggiunto", Toast.LENGTH_SHORT).show();
					button.setText("Non seguire più");
				}
				else{
					deleteOnFile(file,nameTelefilm);
					Toast.makeText(DetailsActivity.this,
							nameTelefilm + " rimosso", Toast.LENGTH_SHORT).show();
					button.setText("Segui");
				}
			} else {
				Toast.makeText(DetailsActivity.this,
						"Impossibile leggere/scrivere su la sdcard",
						Toast.LENGTH_LONG).show();
			}

		}

	}
	
	private void writeOnFile(File file, String buff, Boolean append) {
		try {
			FileWriter writer = new FileWriter(file.getAbsoluteFile(),append);
			BufferedWriter bw = new BufferedWriter(writer);
			
			if(buff.length() != 0) 
				bw.append(buff+"\n");
			else// cancello il file
				bw.write("");
			
			bw.close();
		} catch (IOException e) {
			Toast.makeText(DetailsActivity.this,
					"Impossibile leggere/scrivere sul file",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		
	}

	public void deleteOnFile(File file, String nameTelefilm) {
		String buff="";
		for (String name: arrayTelefilm) 
			if(!(name.equals(nameTelefilm)))
				buff=buff+name+"\n";
		
		if(buff.length() != 0)
			buff=buff.substring(0,buff.length()-1);
		
		Log.v("DA",buff);
		writeOnFile(file, buff, false);
	}

	private ArrayList<String> loadFileInArray(File file) {
		FileReader filereader=null;
		try {
			filereader = new FileReader(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader buff = new BufferedReader(filereader);
		String line = "";
		ArrayList<String> arr = new ArrayList<String>();
		try {
			while ((line = buff.readLine()) != null) {
				arr.add(line);
			}
			buff.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return arr;
	}


	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public File getDocumentStorageDir(String documentName) {
		// Get the directory for the user's public pictures directory.
		File file = new File(Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_DOWNLOADS),documentName);
		file.mkdirs();
		return file;
	}
}
