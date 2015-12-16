package it.unisa.gad.seriestracker.util;

import android.content.Context;
import android.opengl.GLException;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

import java.util.ArrayList;

import it.unisa.gad.seriestracker.R;


/**
 * Created by Rita on 14/11/2015.
 */
public class NewsArrayAdapter extends ArrayAdapter<RSSItem> {

    public NewsArrayAdapter(Context context, ArrayList<RSSItem> items) {
        super(context, R.layout.list_item, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final RSSItem item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_news, parent, false);
        }
        AQuery aq = new AQuery(convertView);

        // Lookup view for data population
        TextView textview = (TextView) convertView.findViewById(R.id.textViewTitle);
        TextView textviewDescription = (TextView) convertView.findViewById(R.id.textViewDescription);

        // Populate the data into the template view using the data object

        textview.setText(item.getTitle());
        textviewDescription.setText(item.getDescription());

        //we are loading a huge image from the network, but we only need the image to be bigger than 200 pixels wide
        //passing in the target width of 200 will down sample the image to conserve memory
        //aquery will only down sample with power of 2 (2,4,8...) for good image quality and efficiency
        //the resulting image width will be between 200 and 399 pixels

        aq.id(R.id.imageNews).image(item.getImageURL(), true, true, 200,0);

        // Return the completed view to render on screen
        return convertView;
    }

}