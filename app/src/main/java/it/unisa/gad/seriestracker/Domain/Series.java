package it.unisa.gad.seriestracker.Domain;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by ludimar on 09/11/15.
 */
public class Series {

    private String name;
    private String producer;
    private String description;
    private String genere;
    private double rating;
    private int startYear;
    private Bitmap imageSmall;
    private Bitmap imageBig;
    private int endYear; //0 or null if the series is still running
    private ArrayList<Person> cast;

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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
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
}
