package ch.bbcag.youtubeextension;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.net.ssl.HttpsURLConnection;

import ch.bbcag.youtubeextension.view.SearchResultAdapter;

public class SearchResults extends AppCompatActivity {

    private static String TAG = "BadiInfo";
    private String badiId;
    private String name;
    private ProgressDialog mDialog;
    private AlertDialog aDialog;
    public Handler mHandler;
    public Context context;




    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Intent intent = getIntent();
        //Hier holen wir die Zusatzinformationen des Inents
        String searchQuery = intent.getStringExtra("searchQuery");
        TextView text = (TextView) findViewById(R.id.badiinfos);
        //und setzen setzen als Text den Namen der Badi
        text.setText(name);
        // Evtl. ist der Dialog nicht sichtbar, weil die Daten schnell geladen sind
        // aber hier ziegen wir dem Benutzer den Ladedialog an.
        mDialog = ProgressDialog.show(this, getString(R.string.loadinginfos), getString(R.string.pleasewait));
        // Danach wollen wir die Badidaten von der Webseite wiewarm.ch holen und verarbeiten:

        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();

        context = this;
        getBadiTemp("https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=50&q=" + searchQuery + "&type=channel&key=AIzaSyBfNM-tCGu4XYjgzNS8QSyCYjmAKtTPgws");
    }


    private void getBadiTemp(String url) {
        //Den ArrayAdapter wollen wir später verwenden um die Temperaturen zu speichern
        // angezeigt sollen sie im Format der simple_list_item_1 werden (einem Standard Android Element)
        final ArrayAdapter temps = new SearchResultAdapter(this, android.R.layout.simple_list_item_1);
        //Android verlangt, dass die Datenverarbeitung von den GUI Prozessen getrennt wird.
        // Darum starten wir hier einen asynchronen Task (quasi einen Hintergrundprozess).



        AsyncTask<String, String, String> execute = new AsyncTask<String, String, String>() {
            //Der AsyncTask verlangt die implementation der Methode doInBackground.
            // Nachdem doInBackground ausgeführt wurde, startet automatisch die Methode onPostExecute
            // mit den Daten die man in der Metohde doInBackground mit return zurückgegeben hat (hier msg).
            @Override
            protected String doInBackground(String[] badi) {
                //In der variable msg soll die Antwort der Seite wiewarm.ch gespeichert werden.
                String msg = "";
                try {
                    URL url = new URL(badi[0]);
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
                    final List<String> badiInfos = parseBadiTemp(result);
                    //Jetzt müssen wir nur noch alle Elemente der Liste badidetails hinzufügen.
                    // Dazu holen wir die ListView badidetails vom GUI
                    ListView badidetails = (ListView) findViewById(R.id.badidetails);
                    //und befüllen unser ArrayAdapter den wir am Anfang definiert haben (braucht es zum befüllen eines ListViews)
                    temps.addAll(badiInfos);
                    //Mit folgender Zeile fügen wir den befüllten ArrayAdapter der ListView hinzu:
                    badidetails.setAdapter(temps);
                    badidetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            badiInfos.get(position);
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

            private ArrayList<String> parseBadiTemp(String jonString) throws JSONException {
                {
                    //Wie bereits erwähnt können JSON Daten nicht direkt einem ListView übergeben werden.
                    // Darum parsen ("lesen") wir die JSON Daten und bauen eine ArrayListe, die kompatibel
                    // ,mit unserem ListView ist.
                    ArrayList<String> resultList = new ArrayList<String>();
                    JSONObject jsonObj = jsonObj = new JSONObject(jonString);
                    JSONArray items = jsonObj.getJSONArray("items");



                    for (int i = 0; i < items.length(); i++) {

                        JSONObject item = items.getJSONObject(i);
                        JSONObject snippet = item.getJSONObject("snippet");
                        resultList.add(snippet.getString("title"));

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


