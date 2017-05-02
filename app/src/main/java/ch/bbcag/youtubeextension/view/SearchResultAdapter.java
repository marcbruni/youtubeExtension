package ch.bbcag.youtubeextension.view;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import ch.bbcag.youtubeextension.SearchResult;

/**
 * Created by bvioly on 02.05.2017.
 */

public class SearchResultAdapter extends ArrayAdapter<SearchResult> {

    private Context context;
    private int viewResourceId;
    private SearchResult[] data;

    public SearchResultAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    public SearchResultAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull SearchResult[] objects) {
        super(context, resource, objects);

        this.context = context;
        this.viewResourceId = resource;
        this.data = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }


}
