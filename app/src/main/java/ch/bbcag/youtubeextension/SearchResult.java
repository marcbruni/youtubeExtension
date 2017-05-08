package ch.bbcag.youtubeextension;

/**
 * Created by bvioly on 02.05.2017.
 */

public class SearchResult {

    private String id;
    private String title;
    private String thumbUrl;

    public SearchResult(String id, String title, String thumbUrl) {
        this.id = id;
        this.title = title;
        this.thumbUrl = thumbUrl;
    }


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }
}
