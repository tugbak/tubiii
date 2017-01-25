package de.hrw.swep.votingservice.service;

/**
 * 
 * @author andriesc
 *
 */
public class QuestionClosed implements QuestionStatusInterface {
	private Question question;

	/**
	 * 
	 * @param question
	 *            the question this object is a state of
	 */
	public QuestionClosed(Question question) {
		this.question = question;
	}

	@Override
	public void vote(int result) {
		throw new IllegalStateException("Invalid vote.");
	}

	@Override
	public void openForVoting() {
		question.setCurrentState(new QuestionOpen(question));
	}

	@Override
	public void closeForVoting() {
		// do nothing
	}

	@Override
	public boolean isOpenForVoting() {
		return false;
	}

}
