package de.hrw.swep.votingservice.service;

/**
 * 
 * @author andriesc
 *
 */
public interface QuestionStatusInterface {

	/**
	 * try to vote for the question.
	 * 
	 * @param result
	 *            the vote to be recorded for this question
	 */
	void vote(int result);

	/**
	 * 
	 * @return <tt>true</tt> if the question is open for voting, <tt>false</tt>
	 *         otherwise
	 */
	boolean isOpenForVoting();

	/**
	 * open the question for voting
	 */
	void openForVoting();

	/**
	 * close the question for voting
	 */
	void closeForVoting();
}
