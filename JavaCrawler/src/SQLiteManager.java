import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQLite database manager class
 * @author 
 */

public class SQLiteManager {

	private String databaseName = null;
	private String databaseURL  = null;
	
	private Connection connection = null;
	private Statement   statement = null;
	
	
	/**
	 * @throws ClassNotFoundException 
	 * 
	 */
	public SQLiteManager(String databaseName) throws ClassNotFoundException {
		this.databaseName = databaseName;
		this.databaseURL  = "jdbc:sqlite:" + databaseName;
		
		// load the sqlite-JDBC driver.
		Class.forName("org.sqlite.JDBC");
	}

	
	/**
	 * @throws SQLException 
	 * 
	 */
	public void openConnection() throws SQLException {
	    connection = DriverManager.getConnection(databaseURL);
	}
	
	
	/**
	 * @throws SQLException 
	 * 
	 */
	public void closeConnection() throws SQLException {
		if (connection != null)
			connection.close();
	}
	
	
	/**
	 * @throws SQLException 
	 * 
	 */
	public void verifyIfDatabaseExistsElseCreate() throws SQLException {
	    statement = connection.createStatement();
	    statement.setQueryTimeout(30);  // set timeout to 30 sec.
		statement.executeUpdate(
			"CREATE TABLE IF NOT EXISTS movie (id INTEGER PRIMARY KEY, title TEXT, url TEXT, poster_url TEXT, poster_blob BLOB)");
		//statement.close;
	}
	
	
	/**
	 * @throws SQLException 
	 * 
	 */
	public void insertMovieData(MovieObject movieObject) throws SQLException {
	    statement = connection.createStatement();
	    statement.setQueryTimeout(30);  // set timeout to 30 sec.
		statement.executeUpdate("INSERT OR IGNORE INTO movie VALUES ('" + 
				movieObject.getId()        + "', '" + 
				movieObject.getTitle()     + "', '" +
				movieObject.getURL()       + "', '" + 
				movieObject.getPosterURL() + "', null)");
		//statement.close;
	}
	
	
	/**
	 * @throws SQLException 
	 * @throws MalformedURLException
	 * @throws IOException 
	 * 
	 */
	public void insertMovieDataWithBlob(MovieObject movieObject) throws SQLException, MalformedURLException, IOException {
		String sql = "INSERT OR IGNORE INTO movie (id, title, url, poster_url, poster_blob) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement pstmt = connection.prepareStatement(sql);
		pstmt.setInt   (1, movieObject.getId());
		pstmt.setString(2, movieObject.getTitle());
		pstmt.setString(3, movieObject.getURL());
		pstmt.setString(4, movieObject.getPosterURL());
		pstmt.setBytes (5, movieObject.getPosterBlob());
		//pstmt.setBlob(5, new BufferedInputStream(new URL(movieObject.getPosterURL()).openStream()));
		//pstmt.setBinaryStream(5, new ByteArrayInputStream(movieObject.getPosterBlob()),movieObject.getPosterBlob().length);
		pstmt.execute();
		//pstmt.close();
	}

	
	/**
	 * @throws SQLException 
	 * 
	 */
	public void readTableAndPrint() throws SQLException  {
		ResultSet rs = statement.executeQuery("SELECT * FROM movie");
		while(rs.next()) {
			// read the result set.
			System.out.println("id = \t"          + rs.getString("id"));
			System.out.println("title = \t"       + rs.getString("title"));
			System.out.println("URL = \t"         + rs.getString("url"));
			System.out.println("poster_url = \t"  + rs.getString("poster_url"));
			System.out.println("poster_blob = \t" + rs.getByte("poster_blob"));
			System.out.println();
			//byte[] blob = rs.getBytes("poster_blob");
			//for(byte c : blob) {
			//    System.out.format("%d ", c);
			//}
			//System.out.println();
		}
	}
}
