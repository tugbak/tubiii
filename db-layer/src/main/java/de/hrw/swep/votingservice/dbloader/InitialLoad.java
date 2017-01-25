package de.hrw.swep.votingservice.dbloader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fill database with initial values
 * 
 * @author andriesc
 *
 */
public class InitialLoad {
	// server mode of HSQLDB:
	//	private static final String connectionString = "jdbc:hsqldb:hsql://localhost/";
	// in-process mode of HSQLDB:
	private static final String CONNECTION_STRING = "jdbc:hsqldb:file:../db-layer/database/votesdb";
	
	private static final String DB_USER = "sa";
	private static final String DB_PASSWORD = "";

	public static void main(String[] args) throws SQLException {
		Connection c = DriverManager.getConnection(CONNECTION_STRING, DB_USER,
				DB_PASSWORD);
		c.setAutoCommit(false);
		System.out.println("Autocommit " + (c.getAutoCommit() ? "on" : "off"));

		c.createStatement().executeQuery("DROP TABLE VOTES IF EXISTS");
		c.createStatement().executeQuery("DROP TABLE QUESTIONS IF EXISTS");

		c.createStatement()
				.executeQuery(
						"CREATE TABLE QUESTIONS (id INTEGER PRIMARY KEY, name varchar(255), open BOOLEAN)");
		c.createStatement()
				.executeQuery(
						"CREATE TABLE VOTES (id INTEGER PRIMARY KEY, question INTEGER, result INTEGER, "
								+ "constraint FK_QUESTIONS FOREIGN KEY (question) REFERENCES QUESTIONS(id))");

		c.createStatement()
				.executeQuery(
						"INSERT INTO QUESTIONS VALUES (1, 'Mögen Sie Schokoeis?',FALSE)");
		c.createStatement()
				.executeQuery(
						"INSERT INTO QUESTIONS VALUES (2, 'Wie finden Sie Sommerwetter mit blauem Himmel und 37 Grad?', FALSE)");
		c.createStatement()
				.executeQuery(
						"INSERT INTO QUESTIONS VALUES (3, 'Wie finden Sie Games of Thrones?', FALSE)");
		c.createStatement()
				.executeQuery(
						"INSERT INTO QUESTIONS VALUES (4, 'Trinken Sie gerne Softdrinks?', TRUE)");
		c.createStatement()
				.executeQuery(
						"INSERT INTO QUESTIONS VALUES (5, 'Mögen Sie Wassersport?', TRUE)");

		c.createStatement().executeQuery(
				"INSERT INTO VOTES VALUES (1, 1, 2)");
		c.createStatement().executeQuery(
				"INSERT INTO VOTES VALUES (2, 1, 2)");
		c.createStatement().executeQuery(
				"INSERT INTO VOTES VALUES (3, 1, 3)");
		c.createStatement().executeQuery(
				"INSERT INTO VOTES VALUES (4, 2, 5)");
		c.createStatement().executeQuery(
				"INSERT INTO VOTES VALUES (5, 2, 4)");
		c.createStatement().executeQuery(
				"INSERT INTO VOTES VALUES (6, 2, 5)");
		c.createStatement().executeQuery(
				"INSERT INTO VOTES VALUES (7, 3, 5)");
		c.createStatement().executeQuery(
				"INSERT INTO VOTES VALUES (8, 3, 4)");
		c.createStatement().executeQuery(
				"INSERT INTO VOTES VALUES (9, 3, 5)");
		c.createStatement().executeQuery(
				"INSERT INTO VOTES VALUES (10, 4, 5)");
		c.createStatement().executeQuery(
				"INSERT INTO VOTES VALUES (11, 4, 2)");
		c.createStatement().executeQuery(
				"INSERT INTO VOTES VALUES (12, 4, 3)");

		c.commit();
		c.close();
		System.out.println("ready");
	}
}