import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Queue;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Producer thread class that downloads movie details from the TMDB website.
 * @author 
 *
 */
class Producer extends Thread {
    private Queue<MovieObject> queue;
    private int maxQueueSize;
    private String baseURL;
    private int minMovieIndex;
    private int maxMovieIndex;
    private int threadSleepTime;
    private int threadSleepInterval;
    
    
    
    public Producer(Queue<MovieObject> queue, String name, int maxQueueSize, String baseURL, 
                    int minMovieIndex, int maxMovieIndex, 
                    int threadSleepTime, int threadSleepInterval){
        super(name);
        this.queue = queue;
        this.maxQueueSize = maxQueueSize;
        this.baseURL = baseURL;
        this.minMovieIndex = minMovieIndex;
        this.maxMovieIndex = maxMovieIndex;
        this.threadSleepTime = threadSleepTime;
        this.threadSleepInterval = threadSleepInterval;
    }
    
    
    @Override
    public void run() {
        
        System.out.println("Crawling TMDB (" + baseURL + "/movie/" + 
                           ") movies from " + minMovieIndex + " to " + maxMovieIndex + " ...");
        
        for (int i=minMovieIndex; i<=maxMovieIndex; i++) {
            synchronized (queue) {
                while (queue.size() == maxQueueSize) {
                    try {
                        // Queue is full, so wait for consumer thread to take some queue items out.
                        queue.wait();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                
                
                try {
                    
                    // Let the thread sleep after every interval.
                    if (i % this.threadSleepInterval == 0) { 
                        //System.out.println("Thread sleeping.");
                        sleep(this.threadSleepTime);
                    }
                    
                    
                    // Set the movie url.
                    String movieURL = baseURL + "/movie/" + i; 
                    
                    // Get the whole HTML.
                    Document doc = (Document) Jsoup.connect(movieURL).timeout(5000).get();  
                    //System.out.print(doc.toString());
                    
                    // Extract title and image url from meta tags.
                    String movieTitle     = doc.select("meta[property=og:title]").attr("content");
                    String moviePosterURL = doc.select("meta[property=og:image]").attr("content");
                    if (moviePosterURL != null && moviePosterURL != "")
                        moviePosterURL   = baseURL + moviePosterURL;
                    //System.out.println(movieTitle + "\t " + moviePosterURL);
                    
                    // Get the image content from url and save it into a byte[] array (BLOB).
                    byte[] moviePosterBlob = getMoviePosterFromURLasBytes(moviePosterURL);
                    
                    // Create a movie object with obtained movie details.
                    MovieObject movieObject = new MovieObject(i, movieTitle, movieURL, moviePosterURL, moviePosterBlob);    

                    //System.out.println("Producing value : " + movieObject);
                    queue.add(movieObject);
                    queue.notifyAll();
                }
                catch (HttpStatusException e) {    
                    //System.out.print(e.toString());
                    //e.printStackTrace();
                    if (e.getStatusCode() == 404)
                        System.out.printf("%s id: %d \t %s %n", "[FAIL]", i, "(404) Webpage not found.");
                    else
                        System.out.printf("%s id: %d \t (%d) %s %n", "[FAIL]", i, e.getStatusCode(), e.getMessage());
                } 
                catch (MalformedURLException e) {
                    //e.printStackTrace();
                    System.out.printf("%s id: %d \t %s %n", "[FAIL]", i, e.getMessage());
                }
                catch (UnknownHostException e) {
                    //e.printStackTrace();
                    System.out.printf("%s id: %d \t %s %n", "[FAIL]", i, "Connection problem. Website or internet is down.");
                    //stop the program when no connection.
                    //break    
                }
                catch (SocketTimeoutException e) {
                  //e.printStackTrace();
                    System.out.printf("%s id: %d \t %s %n", "[FAIL]", i, "Connection timeout.");
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                } 
                

            }
        }
        // Insert END-OF-QUEUE message / POISON to indicate that the producer finished its work.
        queue.add(null);        
    }


    private byte[] getMoviePosterFromURLasBytes(String moviePosterURL) throws IOException {
        if (moviePosterURL != null && moviePosterURL != "") {
            URL url                   = new URL(moviePosterURL);
            InputStream in            = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer             = new byte[1024];
            int n                     = 0;
            while (-1!=(n=in.read(buffer)))
               out.write(buffer, 0, n);
            out.close();
            in.close();
            return out.toByteArray(); 
            //System.out.println("\n\r moviePosterBlob: " + Arrays.toString(out.toByteArray()));
        }
        return null;
    }
}
