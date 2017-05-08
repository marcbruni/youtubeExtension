package ch.bbcag.youtubeextension;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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
import java.util.logging.Handler;

import javax.net.ssl.HttpsURLConnection;

import ch.bbcag.youtubeextension.view.SearchResultAdapter;
import ch.bbcag.youtubeextension.ChannelDetails;

/**
 * Created by bvioly on 02.05.2017.
 */

public class SingleChannel extends Activity {


    private static String TAG = "ChannelInfo";
    private ProgressDialog mDialog;

    private SearchResultAdapter searchResultAdapter;

    public Context context;

    public ChannelDetails channel = new ChannelDetails();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String id = getIntent().getStringExtra("id");

        setContentView(R.layout.single_channel);




        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();





            mDialog = ProgressDialog.show(this, getString(R.string.loadinginfos), getString(R.string.pleasewait));


            ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();

            context = this;
            getChannels("https://www.googleapis.com/youtube/v3/channels?part=snippet%2C+statistics&id="+id+"&key=AIzaSyBfNM-tCGu4XYjgzNS8QSyCYjmAKtTPgws");
        }


    private void getChannels(String url) {

        final SingleChannel activity = this;


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
                    channel  = parseSearchResults(result);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            setContentView(R.layout.single_channel);


                            final TextView title = (TextView) findViewById(R.id.title);
                            final TextView description = (TextView) findViewById(R.id.description);
                            final TextView viewCount = (TextView) findViewById(R.id.viewCount);
                            final TextView commentCount = (TextView) findViewById(R.id.commentCount);
                            final TextView subscriberCount = (TextView) findViewById(R.id.subscriberCount);
                            title.setText(channel.title);
                            description.setText(channel.description);
                            viewCount.setText(channel.viewCount);
                            commentCount.setText(channel.subscriberCount);
                            subscriberCount.setText(channel.subscriberCount);



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

            private ChannelDetails parseSearchResults(String jonString) throws JSONException {
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
                        JSONObject statistics = item.getJSONObject("statistics");


                        try {
                            String title = snippet.getString("title");
                            String description = snippet.getString("description");
                            String viewCount = statistics.getString("viewCount");
                            String commentCount = statistics.getString("commentCount");
                            String subscriberCount = statistics.getString("subscriberCount");


                            channel.title = title;
                            channel.description = description;
                            channel.setViewCount(viewCount);
                            channel.setCommentCount(commentCount);
                            channel.setSubscriberCount(subscriberCount);



                        }
                        catch (JSONException e) {
                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if(channel.title == null || channel.description == null){

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

                    return channel;
                }
            }

        }.execute(url);
    }

    public void onCancel(){

    }
}



