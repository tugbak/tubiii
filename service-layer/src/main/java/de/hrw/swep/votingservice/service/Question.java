package de.hrw.swep.votingservice.service;

import java.util.ArrayList;
import java.util.List;



/**
 * 
 * @author andriesc
 *
 */
public class Question {
	private int id;
	private String description;
	private QuestionStatusInterface status;

	// visibility is protected instead of private because of State pattern
	List<Integer> votes;

	/**
	 * Initialize a new question
	 * 
	 * @param id
	 *            the question's unique id
	 * @param open
	 *            <tt>true</tt> if open for voting, <tt>false</tt> otherwise
	 * @param votes
	 *            list of any existing votes for this question
	 * @param description
	 * 				is a description
	 * 
	 * 
	 */
	public Question(int id, String description, boolean open,
			List<Integer> votes) {
		this.id = id;
		this.description = description;

		if (votes == null) {
			this.votes = new ArrayList<Integer>();
		} else {
			this.votes = new ArrayList<Integer>(votes);
		}

		if (open) {
			setCurrentState(new QuestionOpen(this));
		} else {
			setCurrentState(new QuestionClosed(this));
		}
	}

	void setCurrentState(QuestionStatusInterface newStatus) {
		this.status = newStatus;
	}

	/**
	 * 
	 * @return the name of the current state class
	 */
	public String getCurrentState() {
		return status.getClass().getName();
	}

	/**
	 * Vote for question with numbers from 0 to 5 -- not less, not more. Method
	 * should be called only from a VotingServiceInterface.
	 * 
	 * @param result
	 *            Number between or equal to 0 and 5
	 */
	void vote(int result) {
		status.vote(result);
	}

	/**
	 * 
	 * @return the average for all votes for this question
	 */
	public float getAverageVote() {
		if (votes.size() == 0)
			throw new IllegalStateException("No votes.");
		else {
			int sum = 0;
			for (int i : votes) {
				sum += i;
			}
			return ((float) sum / votes.size());
		}
	}

	/**
	 * 
	 * @return the question's id
	 */
	public int getId() {
		return id;
	}

	/**
	 * 
	 * @return the question put into words
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @return list of any votes for this question
	 */
	public List<Integer> getVotes() {
		return votes;
	}

	/**
	 * 
	 * @return <tt>true</tt> if this question can be voted for, <tt>false</tt>
	 *         otherwise
	 */
	public boolean isOpenForVoting() {
		return status.isOpenForVoting();
	}

}
