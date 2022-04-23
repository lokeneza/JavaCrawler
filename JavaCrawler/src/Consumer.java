import java.io.IOException;
import java.sql.SQLException;
import java.util.Queue;

/**
 * Consumer thread class to save movie objects into a database from a queue.
 * @author 
 *
 */
class Consumer extends Thread {
    private Queue<MovieObject> queue;
    private String databaseName;
    private SQLiteManager sqlite = null;
    
    
    public Consumer(Queue<MovieObject> queue, String name, String databaseName) {
        super(name);
        this.queue = queue;
        this.databaseName = databaseName;
    }
    
    
    @Override
    public void run() {
    	
		// Prepare the database.
		try {
			sqlite = new SQLiteManager(databaseName);
			sqlite.openConnection();
			sqlite.verifyIfDatabaseExistsElseCreate();
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	// Get movie objects from queue and insert them into database.
        while (true) {
            synchronized (queue) {
                while (queue.isEmpty()) {                    
                    try {
                    	// Queue is empty, so wait for the producer thread to put some items into the queue.
                        queue.wait();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }

              
                MovieObject movieObject = queue.remove();
                if (movieObject == null) break;					// check FOR END-OF-QUEUE message / POISON
                //System.out.println("Consuming value : " + movieObject);
                try {
					sqlite.insertMovieDataWithBlob(movieObject);
				} catch (SQLException | IOException e) {
					e.printStackTrace();
				}
                System.out.printf("%s id: %d \t %s \t %s %n", 
                		"[DONE]", movieObject.getId(), movieObject.getPosterURL(), movieObject.getTitle());	
                
                queue.notifyAll();
            }
        }
        
        // Close the database.
        try {
			//sqlite.readTableAndPrint();
			sqlite.closeConnection();
		} 
        catch (SQLException e) {
			e.printStackTrace();
		}
        finally {
        	System.out.println("THE END");
        }
    }
}