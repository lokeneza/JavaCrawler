import java.util.LinkedList;
import java.util.Queue;

/**
 * Main launcher and configuration class
 * @author 
 *
 */
public class Main {
    
    private static final String BASE_URL           = "https://www.themoviedb.org";
    private static final int MIN_MOVIE_INDEX       = 1;     // min is 1
    private static final int MAX_MOVIE_INDEX       = 50;    // max is 999'999
    private static final String DATABASE_NAME      = "JavaCrawlerSQLite.db";
    private static final int MAX_QUEUE_SIZE        = 10;    // queue used by producer/consumer threads
    private static final int THREAD_SLEEP_TIME     = 5000;  // in milliseconds
    private static final int THREAD_SLEEP_INTERVAL = 100;   // wait after N movies


    public static void main(String[] args) {

        System.out.println("JavaCrawler is launched."); 
        Queue<MovieObject> queue = new LinkedList<MovieObject>();  
        Thread producer = new Producer(queue, "PRODUCER", MAX_QUEUE_SIZE, BASE_URL, 
                                       MIN_MOVIE_INDEX, MAX_MOVIE_INDEX, 
                                       THREAD_SLEEP_TIME, THREAD_SLEEP_INTERVAL); 
        Thread consumer = new Consumer(queue, "CONSUMER", DATABASE_NAME); 
        producer.start(); 
        consumer.start();

    }

}
