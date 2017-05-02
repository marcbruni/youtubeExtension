package ch.bbcag.youtubeextension.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ch.bbcag.youtubeextension.R;
import ch.bbcag.youtubeextension.SearchResult;

/**
 * Created by bvioly on 02.05.2017.
 */

public class SearchResultAdapter extends ArrayAdapter<SearchResult> {

    private Context context;
    private int viewResourceId;
    private SearchResult[] data;



    public SearchResultAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull SearchResult[] objects) {
        super(context, resource, objects);

        this.context = context;
        this.viewResourceId = resource;
        this.data = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        SearchResultHolder holder = null;


        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(viewResourceId, parent, false);

            holder = new SearchResultHolder();
            holder.txtSearchResultTitle = (TextView) row.findViewById(R.id.txtSearchResultTitle);

            row.setTag(holder);
        }
        else
        {
            holder = (SearchResultHolder) row.getTag();
        }


        SearchResult result = data[position];
        holder.txtSearchResultTitle.setText(result.getTitle());

        return row;
    }


    static class SearchResultHolder
    {
        TextView txtSearchResultTitle;
    }
}
