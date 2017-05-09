package ch.bbcag.youtubeextension;

import java.text.DecimalFormat;

import ch.bbcag.youtubeextension.view.SearchResultAdapter;

/**
 * Created by bbrunm on 08.05.2017.
 */

public class ChannelDetails {
    public String title;
    public String description;
    public String viewCount;
    public String commentCount;
    public String videoCount;
    public String subscriberCount;
    public String viewsPerVideo;
    public String rawViewCount;
    public String rawCommentCount;
    public String rawVideoCount;
    public String rawSubscriberCount;
    public String rawViewsPerVideo;


    public void setViewsPerVideo() {
        double value = Double.parseDouble(this.rawViewCount) / Double.parseDouble(rawVideoCount);
      this.rawViewsPerVideo = Double.toString(value);
      DecimalFormat formatter = new DecimalFormat("#,##0");
        this.viewsPerVideo = formatter.format(value).toString();
    }


    public void setVideoCount(String videoCount) {
        this.rawVideoCount = videoCount;
        double value = Double.parseDouble(videoCount);
        DecimalFormat formatter = new DecimalFormat("#,##0");
        this.videoCount = formatter.format(value).toString();
    }

    public void setSubscriberCount(String subscriberCount) {
        this.rawSubscriberCount = subscriberCount;
        double value = Double.parseDouble(subscriberCount);
        DecimalFormat formatter = new DecimalFormat("#,##0");
        this.subscriberCount = formatter.format(value).toString();
    }


    public void setViewCount(String viewCount) {
        this.rawViewCount = viewCount;
        double value = Double.parseDouble(viewCount);
        DecimalFormat formatter = new DecimalFormat("#,##0");
        this.viewCount = formatter.format(value).toString();
    }

    public void setCommentCount(String commentCount) {
        this.rawCommentCount = commentCount;
        double value = Double.parseDouble(commentCount);
        DecimalFormat formatter = new DecimalFormat("#,##0");
        this.commentCount = formatter.format(value).toString();
    }



    public ChannelDetails() {
    }
}
