package ch.bbcag.youtubeextension.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
        final SearchResultHolder holder;


        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(viewResourceId, parent, false);

            holder = new SearchResultHolder();
            holder.txtSearchResultTitle = (TextView) row.findViewById(R.id.txtSearchResultTitle);
            holder.imgSearchResultThumb = (ImageView) row.findViewById(R.id.imgSearchResultThumb);

            row.setTag(holder);
        }
        else
        {
            holder = (SearchResultHolder) row.getTag();
        }


        final SearchResult result = data[position];
        holder.txtSearchResultTitle.setText(result.getTitle());

        Thread getImage = new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap thumbnail = LoadImageFromWebOperations(result.getThumbUrl());
                new ImageDownloaderTask(holder.imgSearchResultThumb).execute(result.getThumbUrl());
                //holder.setThumbnail(thumbnail);

            }
        });
        getImage.start();

        return row;
    }

    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;

        public ImageDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadBitmap(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        Drawable placeholder = null;
                        imageView.setImageDrawable(placeholder);
                    }
                }
            }
        }

        private Bitmap downloadBitmap(String url) {
            HttpURLConnection urlConnection = null;
            try {
                URL uri = new URL(url);
                urlConnection = (HttpURLConnection) uri.openConnection();

                final int responseCode = urlConnection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            } catch (Exception e) {
                urlConnection.disconnect();
                Log.w("ImageDownloader", "Errore durante il download da " + url);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }


    public static Bitmap LoadImageFromWebOperations(String url) {

        URL photoUrl = null;
        try {
            photoUrl = new URL(url);
            InputStream is = (InputStream) photoUrl.getContent();
            Bitmap d = BitmapFactory.decodeStream(is);
            return d;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    static class SearchResultHolder
    {
        TextView txtSearchResultTitle;
        ImageView imgSearchResultThumb;

        public void setThumbnail(Bitmap thumbnail) {
            imgSearchResultThumb.setImageBitmap(thumbnail);
        }
    }
}
