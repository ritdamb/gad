package it.unisa.gad.seriestracker.Domain;

import android.graphics.Bitmap;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ludimar on 09/11/15.
 */
public class Series {


    private String name;
    private String episodeTitle;
    private String producer;
    private String description;
    private String genere;
    private String rating;
    private int startYear;
    private Bitmap imageSmall;
    private Bitmap imageBig;
    private int endYear; //0 or null if the series is still running
    private boolean isTodaySeries;
    private String startH;
    private String imageURL;
    private ArrayList<Person> cast;
    private String firstAired;

    public static final String NAME_TELEFILM="name_Telefilm";


    public String getFirstAired() {
        return firstAired;
    }

    public void setFirstAired(String firstAired) {
        this.firstAired = firstAired;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public Bitmap getImageSmall() {
        return imageSmall;
    }

    public void setImageSmall(Bitmap imageSmall) {
        this.imageSmall = imageSmall;
    }

    public Bitmap getImageBig() {
        return imageBig;
    }

    public void setImageBig(Bitmap imageBig) {
        this.imageBig = imageBig;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public ArrayList<Person> getCast() {
        return cast;
    }

    public void setCast(ArrayList<Person> cast) {
        this.cast = cast;
    }

    public String getStartH() {
        return startH;
    }

    public void setStartH(String startH) {
        this.startH = startH;
    }

    public boolean isTodaySeries() {
        return isTodaySeries;
    }

    public void setIsTodaySeries(boolean isTodaySeries) {
        this.isTodaySeries = isTodaySeries;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getEpisodeTitle() {
        return episodeTitle;
    }

    public void setEpisodeTitle(String episodeTitle) {
        this.episodeTitle = episodeTitle;
    }
}
