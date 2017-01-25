package de.hrw.swep.votingservice.persistence;

import java.sql.SQLException;
import java.util.List;

/**
 * @author andriesc
 *
 */
public interface DataStoreReadInterface {

	/**
	 * 
	 * @return all questions stored
	 */
	List<Integer> getAllQuestions();

	/**
	 * 
	 * @param id
	 *            the question's <tt>id</tt>
	 * @return description of the question having ID <tt>id</tt>
	 */
	String getDescriptionOfQuestion(int id);

	/**
	 * 
	 * @param id
	 *            the question's <tt>id</tt>
	 * @return all votes for question having ID <tt>id</tt>
	 */
	List<Integer> getVotesForQuestion(int id);

	/**
	 * 
	 * @param id
	 *            the question's <tt>id</tt>
	 * @return <tt>true</tt>, if question is open for votes and <tt>false</tt>,
	 *         if not
	 */
	boolean getStatusOfQuestion(int id);

	/**
	 * Must be called to release resources after database is not being used
	 * anymore.
	 * 
	 * @throws SQLException
	 *             in case of any errors during release of database resources
	 */
	void close() throws SQLException;
}
