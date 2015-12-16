package it.unisa.gad.seriestracker.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.androidquery.AQuery;

import java.util.ArrayList;

import it.unisa.gad.seriestracker.Domain.Series;
import it.unisa.gad.seriestracker.R;

/**
 * Created by ludimar on 02/12/15.
 */

public class FoundSeriesAdapter extends ArrayAdapter<Series> {

    public FoundSeriesAdapter (Context context, ArrayList<Series> items) {
        super(context, R.layout.found_list_item_banner, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Series item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.found_list_item_banner, parent, false);
        }
        AQuery aq = new AQuery(convertView);

        // Lookup view for data population
        TextView textview = (TextView) convertView.findViewById(R.id.seriesFoundTitle);
        TextView textviewNetwork = (TextView) convertView.findViewById(R.id.seriesFoundNetwork);
        TextView textviewAired = (TextView) convertView.findViewById(R.id.seriesFoundFirstAired);
        // Populate the data into the template view using the data object

        textview.setText(item.getName());

        if(item.getProducer() == null ) {
            textviewNetwork.setText("");
        } else {
            textviewNetwork.setText(item.getProducer());
        }

        if(item.getFirstAired() == null) {
            textviewAired.setText("");
        } else {
            textviewAired.setText(item.getFirstAired());
        }

        //we are loading a huge image from the network, but we only need the image to be bigger than 200 pixels wide
        //passing in the target width of 200 will down sample the image to conserve memory
        //aquery will only down sample with power of 2 (2,4,8...) for good image quality and efficiency
        //the resulting image width will be between 200 and 399 pixels
        if(item.getImageURL() == null || item.getImageURL().length() < 1 ) {
            aq.id(R.id.seriesFoundBanner).image(R.drawable.nobanner);
        }else {
            aq.id(R.id.seriesFoundBanner).image(item.getImageURL(), true, true, 500,0);
            item.setImageBig(aq.getCachedImage(item.getImageURL()));
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
