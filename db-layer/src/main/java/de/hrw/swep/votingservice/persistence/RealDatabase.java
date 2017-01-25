package de.hrw.swep.votingservice.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author andriesc
 *
 */
public class RealDatabase implements DataStoreReadInterface,
		DataStoreWriteInterface {

	// server mode of HSQLDB:
	// private static final String connectionString =
	// "jdbc:hsqldb:hsql://localhost/";
	// in-process mode of HSQLDB:
	private static final String CONNECTION_STRING = "jdbc:hsqldb:file:../db-layer/database/votesdb";
	private static final String DB_USER = "sa";
	private static final String DB_PASSWORD = "";

	private Connection conn = null;

	/**
	 * 
	 * @param pooling
	 *            <tt>true</tt> if you want to pool connections
	 * @throws SQLException
	 *             in case of any SQL errors
	 * @throws ClassNotFoundException
	 *             in case JDBC driver cannot be found
	 */
	public RealDatabase(Boolean pooling) throws SQLException,
			ClassNotFoundException {

		// Load the HSQL Database Engine JDBC driver
		// hsqldb.jar should be in the class path or made part of the current
		// jar
		Class.forName("org.hsqldb.jdbcDriver");

		// connect to the database. This will load the db files and start the
		// database if it is not already running.
		conn = DriverManager.getConnection(CONNECTION_STRING, DB_USER,
				DB_PASSWORD);
		if (conn != null && !pooling)
			conn.close();
	}

	/**
	 * release resources when object is cleaned up
	 */
	public void finalize() {
		if (conn != null)
			try {
				close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	/**
	 * releases resources: closes database connection
	 * 
	 * @throws SQLException
	 *             in case of any database errors
	 * 
	 */
	public void close() throws SQLException {
		if (conn != null) {
			conn.close();
			conn = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hrw.swep.mensa.persistence.DataStoreWriteInterface#addBewertung(int,
	 * int)
	 */
	@Override
	public void addVote(int id, int vote) {
		try {
			if (vote < 0 || vote > 5)
				throw new PersistenceException("Invalid vote.");

			ResultSet rs = executeQuery("select max(id) from VOTES");
			int maxId = getInt(rs);

			if (maxId == -1)
				return;

			maxId++;
			int res = executeUpdate("insert into VOTES values (" + maxId + ","
					+ id + "," + vote + ")");
			if (res == 0)
				throw new PersistenceException("Vote could not be added.");
		} catch (SQLException e) {
			throw new PersistenceException("Error while adding vote.", e);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hrw.swep.mensa.persistence.DataStoreReadInterface#getBeschreibungOfGericht
	 * (int)
	 */
	@Override
	public String getDescriptionOfQuestion(int id) {
		List<String> results = getResultAsStrings("SELECT name FROM QUESTIONS WHERE id="
				+ id);
		if (results.size() == 1)
			return results.get(0);
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hrw.swep.mensa.persistence.DataStoreReadInterface#getBewertungenForGericht
	 * (int)
	 */
	@Override
	public List<Integer> getVotesForQuestion(int id) {
		return new ArrayList<Integer>(
				getResultAsInts("SELECT result FROM VOTES WHERE question ="
						+ id));
	}

	@Override
	public List<Integer> getAllQuestions() {
		return new ArrayList<Integer>(
				getResultAsInts("SELECT id FROM QUESTIONS"));
	}

	@Override
	public boolean getStatusOfQuestion(int id) {
		ResultSet rs;
		try {
			rs = executeQuery("select open from QUESTIONS where id=" + id);
			return getBoolean(rs);
		} catch (SQLException e) {
			throw new PersistenceException("Question id could not be read.", e);
		}
	}

	@Override
	public void upsertQuestion(int id, String description, boolean status,
			List<Integer> votes) {

		if ((id > -1)
				&& getResultAsInts("SELECT id FROM QUESTIONS WHERE id=" + id)
						.iterator().hasNext()) {
			// Perform update
			try {
				// First store question itself
				int res = executeUpdate("UPDATE QUESTIONS SET (name, open) = (\'"
						+ description + "\', " + status + ") WHERE id=" + id);
				if (res == 0)
					throw new PersistenceException(
							"Question could not be stored.");

				// then delete any prior votes
				deleteVotes(id);
				// store the new votes
				for (int i : votes) {
					addVote(id, i);
				}

			} catch (SQLException e) {
				throw new PersistenceException("Question could not be stored.",
						e);
			}

		} else {
			// did not find question for id given, thus we will ignore id and
			// retrieve a new one ourselves
			ResultSet rs;
			try {
				rs = executeQuery("select max(id) from QUESTIONS");
				int maxId = getInt(rs);
				if (maxId == -1)
					return;

				maxId++;

				int res = executeUpdate("INSERT INTO QUESTIONS (id, name, open) VALUES("
						+ maxId + ", \'" + description + "\', " + status + ")");

				if (res == 0)
					throw new PersistenceException(
							"Question could not be added.");

				// store votes
				for (int i : votes) {
					addVote(maxId, i);
				}

			} catch (SQLException e) {
				throw new PersistenceException("Question could not be added.",
						e);
			}
		}

	}

	@Override
	public void deleteVotes(int id) {
		List<Integer> bewertungen = getVotesForQuestion(id);

		// Are there any votes to delete?
		if (bewertungen.size() > 0) {
			try {
				int res = executeUpdate("DELETE FROM VOTES WHERE question="
						+ id);
				if (res == 0)
					throw new PersistenceException(
							"Votes could not be deleted.");
			} catch (SQLException e) {
				throw new PersistenceException("Votes could not be deleted.", e);

			}
		}

	}

	private ResultSet executeQuery(String sql) throws SQLException {
		Connection c = null;
		try {
			if (conn == null)
				c = DriverManager.getConnection(CONNECTION_STRING, DB_USER,
						DB_PASSWORD);
			else
				c = conn;
			ResultSet rs = c.createStatement().executeQuery(sql);
			c.commit();
			return rs;
		} finally {
			try {
				if (c != null && conn == null)
					c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private int executeUpdate(String sql) throws SQLException {
		Connection c = null;
		try {
			if (conn == null)
				c = DriverManager.getConnection(CONNECTION_STRING, DB_USER,
						DB_PASSWORD);
			else
				c = conn;
			int result = c.createStatement().executeUpdate(sql);
			c.commit();
			return result;
		} finally {
			try {
				if (c != null && conn == null)
					c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean getBoolean(ResultSet rs) throws SQLException {
		if (rs != null && rs.next())
			return rs.getBoolean(1);

		throw new PersistenceException("result set error");
	}

	private int getInt(ResultSet rs) throws SQLException {
		if (rs != null && rs.next())
			return rs.getInt(1);

		throw new PersistenceException("result set error");
	}

	private List<Integer> getResultAsInts(String sql) {
		List<Integer> list = new ArrayList<Integer>();
		try {
			ResultSet result = executeQuery(sql);
			while (result.next())
				list.add(result.getInt(1));
		} catch (SQLException e) {
			throw new PersistenceException("No integers could be read.", e);
		}
		return list;
	}

	private List<String> getResultAsStrings(String sql) {
		List<String> list = new ArrayList<String>();
		try {
			ResultSet result = executeQuery(sql);
			while (result.next())
				list.add(result.getString(1));
		} catch (SQLException e) {
			throw new PersistenceException("No strings could be read.", e);
		}
		return list;
	}

	@Override
	public void deleteQuestion(int id) {
		deleteVotes(id);

		try {
			int res = executeUpdate("DELETE FROM QUESTIONS WHERE id=" + id);
			if (res == 0)
				throw new PersistenceException("Question could not be deleted.");
		} catch (SQLException e) {
			throw new PersistenceException("Question could not be deleted.", e);

		}

	}
}
