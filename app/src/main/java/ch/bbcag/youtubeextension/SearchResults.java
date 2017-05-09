package ch.bbcag.youtubeextension;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;

import ch.bbcag.youtubeextension.view.SearchResultAdapter;

public class SearchResults extends AppCompatActivity {

    private static String TAG = "ChannelInfo";
    private ProgressDialog mDialog;

    private SearchResultAdapter searchResultAdapter;

    public Context context;




    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Intent intent = getIntent();

        String searchQuery = intent.getStringExtra("searchQuery");
        TextView text = (TextView) findViewById(R.id.channelinfos);
        text.setText(searchQuery);

        mDialog = ProgressDialog.show(this, getString(R.string.loadinginfos), getString(R.string.pleasewait));

        context = this;
        getChannels("https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=50&q=" + searchQuery + "&type=channel&key=AIzaSyBfNM-tCGu4XYjgzNS8QSyCYjmAKtTPgws");
    }


    private void getChannels(final String url) {

        final AppCompatActivity activity = this;

        //AsyncTask für ausführung im Hintergrund
        AsyncTask<String, String, String> execute = new AsyncTask<String, String, String>() {
            // doInBackGround für AsyncTask definieren
            // Nachdem doInBackground ausgeführt wurde, startet automatisch die Methode onPostExecute
            // mit den Daten die man in der Metohde doInBackground mit return zurückgegeben hat (hier msg).
            @Override
            protected String doInBackground(String[] channel) {
                //In der variable apiReply soll die Antwort der Seite wiewarm.ch gespeichert werden.
                String apiReply = "";
                try {
                    URL url = new URL(channel[0]);
                    // Verbindung aufbauen
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    // Antwortcode auslesen
                    int code = conn.getResponseCode();
                    // Ladedialog ausblenden
                    mDialog.dismiss();
                    //Antwort von API lesen
                    apiReply = IOUtils.toString(conn.getInputStream());
                } catch (Exception e) {
                    Log.v(TAG, e.toString());

                    mDialog.dismiss();
                }
                return apiReply;
            }

            public void onPostExecute(String result) {
                // JSON verarbeiten
                try {
                    final List<SearchResult> searchResults = parseSearchResults(result);
                    ListView channelDetails = (ListView) findViewById(R.id.channeldetails);

                    SearchResult[] data = searchResults.toArray(new SearchResult[0]);

                    searchResultAdapter = new SearchResultAdapter(activity, R.layout.item_search_result, data);

                    channelDetails.setAdapter(searchResultAdapter);
                    channelDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            SearchResult sresult = searchResults.get(position);
                            Intent i = new Intent(getApplicationContext(), SingleChannel.class);
                            i.putExtra("id", sresult.getId());
                            startActivity(i);
                        }
                    });
                } catch (JSONException e) {
                    Log.v(TAG, e.toString());
                    runOnUiThread(new Runnable() {
                        public void run() {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setTitle(getString(R.string.noconn));
                            alertDialogBuilder.setMessage(getString(R.string.checkconn));
                            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                                    startActivity(intent);
                                }
                            });

                            AlertDialog alert = alertDialogBuilder.create();
                            alert.show();

                        }
                    });
                }
            }

            private ArrayList<SearchResult> parseSearchResults(String jonString) throws JSONException {
                {
                    ArrayList<SearchResult> resultList = new ArrayList<>();
                    JSONObject jsonObj = jsonObj = new JSONObject(jonString);
                    JSONArray items = jsonObj.getJSONArray("items");



                    for (int i = 0; i < items.length(); i++) {

                        JSONObject item = items.getJSONObject(i);
                        JSONObject snippet = item.getJSONObject("snippet");

                        try {

                            String title = snippet.getString("title");
                            String id = snippet.getString("channelId");
                            JSONObject deafultthumb = snippet.getJSONObject("thumbnails");
                            JSONObject thumb = deafultthumb.getJSONObject("default");
                            String channelthumb = thumb.getString("url");

                            resultList.add(new SearchResult(id, title, channelthumb));


                        }
                        catch (JSONException e) {
                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    if(resultList.isEmpty()){

                        runOnUiThread(new Runnable() {
                            public void run() {

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                alertDialogBuilder.setTitle(getString(R.string.nores));
                                alertDialogBuilder.setMessage(getString(R.string.changequery));
                                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                                        startActivity(intent);
                                    }
                                });

                                AlertDialog alert = alertDialogBuilder.create();
                                alert.show();

                            }
                        });
                    }

                    return resultList;
                }
            }

        }.execute(url);
    }

}


