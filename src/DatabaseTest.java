import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTest {
	

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		try {
			Files.deleteIfExists(Paths.get("test.db"));
		} catch (IOException e) {
			System.err.println("Something is wrong with the filesystem.");
		}
		
		Class.forName("org.sqlite.JDBC");
		Connection connection = null;
		connection = DriverManager.getConnection("jdbc:sqlite:test.db");
		Statement stat = connection.createStatement();
		
		// Create basic table
		stat.executeUpdate("CREATE TABLE users(\n" + 
				"Nickname TEXT PRIMARY KEY,\n" + 
				"EMail TEXT,\n" + 
				"Password INTEGER\n" + 
				");");
		stat.executeUpdate("CREATE TABLE status(\n" + 
				"  ID INTEGER NOT NULL,\n" + 
				"  Message TExt,\n" + 
				"  user_nick text,\n" + 
				"  Primary key(ID),\n" + 
				"  foreign key(user_nick) references users(Nickname)\n" + 
				"  ON DELETE CASCADE ON UPDATE NO ACTION\n" + 
				");");
		stat.executeUpdate("CREATE table friends(\n" + 
				"  userA text not null,\n" + 
				"  userB text not NULL,\n" + 
				"  Primary key (userA, userB),\n" + 
				"  Foreign key(userA) references users(nickname)\n" + 
				"  ON DELETE CASCADE ON UPDATE NO ACTION\n" + 
				"  foreign key(userB) references users(nickname)\n" + 
				"  ON DELETE CASCADE ON UPDATE NO ACTION\n" + 
				");");
		connection.setAutoCommit(false);
		
		// Put data into the table
		PreparedStatement prep = connection.prepareStatement(
				"INSERT INTO users VALUES (?,?,?);");
				prep.setString(1, "madave");
				prep.setString(2, "david.tschirschwitz@online.de");
				prep.setInt(3, 12345678);
				prep.addBatch();
				prep.setString(1, "moleop");
				prep.setString(2, "moritz.preuß@aol.de");
				prep.setInt(3, 0000000);
				prep.addBatch();
				prep.setString(1, "tornupto100");
				prep.setString(2, "thomas.strezeletz@dingbums.de");
				prep.setInt(3, 100805);
				prep.addBatch();
				prep.setString(1, "hocklebock");
				prep.setString(2, "jan.froehlich@gmail.de");
				prep.setInt(3, 371402147);
				prep.addBatch();
				prep.setString(1, "pikachu");
				prep.setString(2, "pokemaster@alabasta.de");
				prep.setInt(3, 150);
				prep.addBatch();
				prep.executeBatch();
				connection.commit();
		
		// print all nicknames of table users		
		ResultSet rs = stat.executeQuery("SELECT nickname FROM users;");
		while(rs.next()){
			System.out.println("nickname = " + rs.getString("nickname"));
		}
		rs.close();
		
		// add status data
		prep = connection.prepareStatement(
				"INSERT INTO status VALUES(?,?,?);");
				prep.setInt(1, 1);
				prep.setString(2, "One does not simply walk into mordor");
				prep.setString(3, "hocklebock");
				prep.addBatch();
				prep.setInt(1, 2);
				prep.setString(2, "What is love?");
				prep.setString(3, "hocklebock");
				prep.addBatch();
				prep.setInt(1, 3);
				prep.setString(2, "I like SQL.");
				prep.setString(3, "hocklebock");
				prep.addBatch();
				prep.setInt(1, 4);
				prep.setString(2, "my 4th status");
				prep.setString(3, "hocklebock");
				prep.addBatch();
				prep.setInt(1, 10);
				prep.setString(2, "the last status i will send");
				prep.setString(3, "hocklebock");
				prep.addBatch();
				prep.setInt(1, 5);
				prep.setString(2, "i made this database");
				prep.setString(3, "madave");
				prep.addBatch();
				prep.setInt(1, 6);
				prep.setString(2, "biking is fun");
				prep.setString(3, "madave");
				prep.addBatch();
				prep.setInt(1, 7);
				prep.setString(2, "hee, fliendo, how does this work");
				prep.setString(3, "tornupto100");
				prep.addBatch();
				prep.setInt(1, 8);
				prep.setString(2, "oh no, did i break it?");
				prep.setString(3, "tornupto100");
				prep.addBatch();
				prep.setInt(1, 9);
				prep.setString(2, "hallo, ich bau langsam datenbanken, auf deutsch.");
				prep.setString(3, "moleop");
				prep.addBatch();
				prep.setInt(1, 11);
				prep.setString(2, "Bergsteigeralgorithmus");
				prep.setString(3, "moleop");
				prep.addBatch();
				prep.setInt(1, 12);
				prep.setString(2, "pika, pika");
				prep.setString(3, "pikachu");
				prep.addBatch();
				prep.executeBatch();
				connection.commit();
				
		// print status of user hocklebock
		rs = stat.executeQuery("SELECT * FROM status where user_nick == 'hocklebock';");
		while(rs.next()){
			System.out.println("Message = " + rs.getString("Message"));
		}
		rs.close();
				
		prep = connection.prepareStatement(
				"INSERT into friends VALUES(?,?)");
				prep.setString(1, "madave");
				prep.setString(2, "moleop");
				prep.addBatch();
				prep.setString(1, "madave");
				prep.setString(2, "tornupto100");
				prep.addBatch();
				prep.setString(1, "madave");
				prep.setString(2, "hocklebock");
				prep.addBatch();
				prep.setString(1, "tornupto100");
				prep.setString(2, "hocklebock");
				prep.addBatch();
				prep.setString(1, "tornupto100");
				prep.setString(2, "moleop");
				prep.executeBatch();
				connection.commit();
				
		rs = stat.executeQuery("SELECT message from status where user_nick in ("
				+ "select userB from friends where userA = 'madave')"
				+ "ORDER BY ID DESC limit 3");
		while(rs.next()){
			System.out.println("Message = " + rs.getString("Message"));
		}
		rs.close();
		
		connection.close();
	}
	
	static void addUser (Connection con, String nickname, String email, int password) {
		try {
			PreparedStatement prep = con.prepareStatement("INSERT INTO users VALUES (?,?,?);");
			prep.setString(1, nickname);
			prep.setString(2, email);
			prep.setInt(3, password);
			con.commit();
		} catch (SQLException e) {
		}
		
	}

}
