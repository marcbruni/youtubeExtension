package ch.bbcag.youtubeextension;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.net.ssl.HttpsURLConnection;

import ch.bbcag.youtubeextension.view.SearchResultAdapter;

public class SearchResults extends AppCompatActivity {

    private static String TAG = "ChannelInfo";
    private ProgressDialog mDialog;
    private AlertDialog aDialog;

    private SearchResultAdapter searchResultAdapter;

    public Handler mHandler;
    public Context context;




    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Intent intent = getIntent();

        String searchQuery = intent.getStringExtra("searchQuery");
        TextView text = (TextView) findViewById(R.id.channelinfos);
        text.setText(searchQuery);

        mDialog = ProgressDialog.show(this, getString(R.string.loadinginfos), getString(R.string.pleasewait));


        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();

        context = this;
        getChannels("https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=50&q=" + searchQuery + "&type=channel&key=AIzaSyBfNM-tCGu4XYjgzNS8QSyCYjmAKtTPgws");
    }


    private void getChannels(final String url) {

        final AppCompatActivity activity = this;


        AsyncTask<String, String, String> execute = new AsyncTask<String, String, String>() {
            //Der AsyncTask verlangt die implementation der Methode doInBackground.
            // Nachdem doInBackground ausgeführt wurde, startet automatisch die Methode onPostExecute
            // mit den Daten die man in der Metohde doInBackground mit return zurückgegeben hat (hier msg).
            @Override
            protected String doInBackground(String[] channel) {
                //In der variable msg soll die Antwort der Seite wiewarm.ch gespeichert werden.
                String msg = "";
                try {
                    URL url = new URL(channel[0]);
                    //Hier bauen wir die Verbindung auf:
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    // Lesen des Antwortcodes der Webseite:
                    int code = conn.getResponseCode();
                    // Nun können wir den Lade Dialog wieder ausblenden (die Daten sind ja gelesen)
                    mDialog.dismiss();
                    //Hier lesen wir die Nachricht der Webseite wiewarm.ch für Badi XY:
                    msg = IOUtils.toString(conn.getInputStream());
                    //und Loggen den Statuscode in der Konsole:
                    Log.i(TAG, Integer.toString(code));
                } catch (Exception e) {
                    Log.v(TAG, e.toString());
                    //mDialog.setTitle(getString(R.string.noconn));
                    mDialog.dismiss();
                    // Show the AlertDialog.
                    //AlertDialog alertDialog = alertDialogBuilder.show();
                }
                return msg;
            }

            public void onPostExecute(String result) {




                //In result werden zurückgelieferten Daten der Methode doInBackground (return msg;) übergeben.
                // Hier ist also unser Resultat der Seite z.B. http://www.wiewarm.ch/api/v1/bad.json/55
                // In einem Browser IE, Chrome usw. sieht man schön das Resulat als JSON formatiert.
                // JSON Daten können wir aber nicht direkt ausgeben, also müssen wir sie umformatieren.
                try { //Zum Verarbeiten bauen wir die Methode parseBadiTemp und speichern das Resulat in einer Liste.
                    final List<SearchResult> searchResults = parseSearchResults(result);
                    //Jetzt müssen wir nur noch alle Elemente der Liste badidetails hinzufügen.
                    // Dazu holen wir die ListView badidetails vom GUI
                    ListView channeldetails = (ListView) findViewById(R.id.channeldetails);

                    SearchResult[] data = searchResults.toArray(new SearchResult[0]);

                    searchResultAdapter = new SearchResultAdapter(activity, R.layout.item_search_result, data);

                    channeldetails.setAdapter(searchResultAdapter);
                    channeldetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                    //Wie bereits erwähnt können JSON Daten nicht direkt einem ListView übergeben werden.
                    // Darum parsen ("lesen") wir die JSON Daten und bauen eine ArrayListe, die kompatibel
                    // ,mit unserem ListView ist.
                    ArrayList<SearchResult> resultList = new ArrayList<SearchResult>();
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
                       /* mHandler.post(new Runnable() {
                            public void run(){
                                //Be sure to pass your Activity class, not the Thread
                                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
                                //... setup dialog and show
                            }
                        });
                    }*/

                    return resultList;
                }
            }

        }.execute(url);
    }

    public void onCancel(){

    }

}


