package ch.bbcag.youtubeextension;

/**
 * Created by bvioly on 02.05.2017.
 */

public class SearchResult {

    private String id;
    private String title;

    public SearchResult(String id, String title) {
        this.id = id;
        this.title = title;
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
}
