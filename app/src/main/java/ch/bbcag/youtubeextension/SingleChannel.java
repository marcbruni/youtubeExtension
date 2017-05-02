package ch.bbcag.youtubeextension;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by bvioly on 02.05.2017.
 */

public class SingleChannel extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String id = getIntent().getStringExtra("id");

        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }
}
