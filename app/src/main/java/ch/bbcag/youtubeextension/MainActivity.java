package ch.bbcag.youtubeextension;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity {

    private TextView editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final EditText editText = (EditText) findViewById(R.id.editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    sendMessage(editText.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });

    }

    public  void sendMessage(String SearchQuery){
        Intent intent = new Intent(getApplicationContext(), SearchResults.class);

                intent.putExtra("searchQuery", SearchQuery);

        startActivity(intent);
    }

}

