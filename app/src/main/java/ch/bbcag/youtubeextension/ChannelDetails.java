package ch.bbcag.youtubeextension;

import java.text.DecimalFormat;

/**
 * Created by bbrunm on 08.05.2017.
 */

public class ChannelDetails {
    public String title;
    public String description;
    public String viewCount;
    public String commentCount;

    public void setSubscriberCount(String subscriberCount) {
        double value = Double.parseDouble(subscriberCount);
        DecimalFormat formatter = new DecimalFormat("#,###");
        this.subscriberCount = formatter.format(value).toString();
    }

    public String subscriberCount;

    public void setViewCount(String viewCount) {
        double value = Double.parseDouble(viewCount);
        DecimalFormat formatter = new DecimalFormat("#,###");
        this.viewCount = formatter.format(value).toString();
    }

    public void setCommentCount(String commentCount) {
        double value = Double.parseDouble(commentCount);
        DecimalFormat formatter = new DecimalFormat("#,###");
        this.commentCount = formatter.format(value).toString();
    }



    public ChannelDetails() {
    }
}
