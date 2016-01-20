package it.unisa.gad.seriestracker;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class FragmentYoutube extends Fragment {

    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;
    private YouTubePlayer YPlayer;
    YouTubePlayerSupportFragment youTubePlayerFragment;
    public static final String YOUTUBE_API_KEY = "AIzaSyD-CnjmWIz5j4MUPavjKdW522dmUKNj15c";

    public static FragmentYoutube newInstance(String param1) {
        FragmentYoutube fragment = new FragmentYoutube();
        Bundle args = new Bundle();
        args.putString("query", param1);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentYoutube(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag_youtube, container, false);


        youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();


        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();
        Bundle arg = getArguments();
        BackgroundTask b = new BackgroundTask(arg.getString("query"));
        b.execute();

        return rootView;
    }




    private class BackgroundTask extends AsyncTask<Void, Void, Void> {


        private ProgressDialog dialog;
        private String query;
        private String resultID;

        public BackgroundTask(String q) {
            query=q+" trailer";
        }

        @Override
        protected void onPreExecute() {

            dialog = new ProgressDialog(getContext());
            dialog.setCancelable(true);
            dialog.setTitle("Loading...");
            dialog.setMessage("Loading Trailer...");
            dialog.show();

        }

        @Override
        protected void onPostExecute(Void result) {

            dialog.dismiss();
            youTubePlayerFragment.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    if (!b) {
                        YPlayer = youTubePlayer;
                        YPlayer.setFullscreen(true);

                        YPlayer.loadVideo(resultID);
                        YPlayer.play();
                    }
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            });


        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                    public void initialize(HttpRequest request) throws IOException {
                    }
                }).setApplicationName("it.unisa.gad.videotube").build();


                YouTube.Search.List search = youtube.search().list("id,snippet");


                String apiKey = "AIzaSyAtRsV3b_60mT9NlZ-P4XkxzE0B2eWILEI";
                search.setKey(apiKey);
                search.setQ(query);


                search.setType("video");


                search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
                search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);


                SearchListResponse searchResponse = search.execute();
                List<SearchResult> searchResultList = searchResponse.getItems();
                Iterator<SearchResult> iterator=searchResultList.iterator();

                SearchResult singleVideo = iterator.next();
                ResourceId rId = singleVideo.getId();
                resultID=rId.getVideoId();

            } catch (GoogleJsonResponseException e) {
                System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                        + e.getDetails().getMessage());
            } catch (IOException e) {
                System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
            } catch (Throwable t) {
                t.printStackTrace();
            }

            return null;
        }
    }
}