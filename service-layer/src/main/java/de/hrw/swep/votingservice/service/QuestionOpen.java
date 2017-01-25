package de.hrw.swep.votingservice.service;

/**
 * 
 * @author andriesc
 *
 */
public class QuestionOpen implements QuestionStatusInterface {
	private Question question;

	/**
	 * 
	 * @param question the question this object is a state of
	 */
	public QuestionOpen(Question question) {
		this.question = question;
	}

	@Override
	public void vote(int result) {
		if (result >= 0 || result <= 5) {
			question.votes.add(result);
		} else
			throw new IllegalStateException("Invalid vote.");
	}

	@Override
	public void openForVoting() {
		// do nothing
	}

	@Override
	public void closeForVoting() {
		question.setCurrentState(new QuestionClosed(question));
	}

	@Override
	public boolean isOpenForVoting() {
		return true;
	}

}
