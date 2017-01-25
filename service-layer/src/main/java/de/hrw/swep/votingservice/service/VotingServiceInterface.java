package de.hrw.swep.votingservice.service;

import java.sql.SQLException;
import java.util.List;

/**
 * 
 * @author andriesc
 *
 */
public interface VotingServiceInterface {

	/**
	 * 
	 * Returns all questions
	 * 
	 * @return List of all stored questions
	 */
	 List<Question> getAllQuestions();

	/**
	 * 
	 * @param question
	 *            question to vote for
	 * @param vote
	 *            a value not less than 0 and not higher than 5
	 */
	void voteFor(Question question, int vote);

	/**
	 * 
	 * Store question in database including its votes. If a question having ID
	 * <tt>id</tt> already does exist in the database, it will be updated with
	 * the new data given. Otherwise, it will inserted.
	 * 
	 * @param question
	 *            The question to be stored
	 */
	void storeQuestion(Question question);

	/**
	 * Deletes the question given from the database including its votes.
	 * 
	 * @param question
	 *            The question to be deleted
	 */
	void deleteQuestion(Question question);

	/**
	 * Must be called when the voting service won't be used any longer; it will
	 * release resources.
	 * 
	 * @throws SQLException
	 *             in case of any errors during release of database resources
	 */
	void close() throws SQLException;
}
