
/**
 * Movie detail class
 * @author 
 *
 */
public class MovieObject {
    private int    id;
    private String title;
    private String URL;
    private String posterURL;
    private byte[] posterBlob;
    
    
    public MovieObject(int id, String title, String URL, String posterURL, byte[] posterBlob) {
        this.id         = id;
        this.title      = title;
        this.URL        = URL;
        this.posterURL  = posterURL;
        this.posterBlob = posterBlob;
    }

    
    public String toString() {
        return "Id: " + id + ", Title: " + title + ", URL: " + URL + ", Poster URL: " + posterURL;
    }


    public int getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }


    public String getURL() {
        return URL;
    }


    public String getPosterURL() {
        return posterURL;
    }


    public byte[] getPosterBlob() {
        return posterBlob;
    }


    public void setId(int id) {
        this.id = id;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public void setURL(String uRL) {
        URL = uRL;
    }


    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }


    public void setPosterBlob(byte[] posterBlob) {
        this.posterBlob = posterBlob;
    }

}
