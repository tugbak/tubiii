/**
 * 
 */
package de.hrw.swep.votingservice.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.hrw.swep.votingservice.persistence.DataStoreReadInterface;
import de.hrw.swep.votingservice.persistence.DataStoreWriteInterface;

/**
 * @author andriesc
 *
 */
public class VotingServiceImpl implements VotingServiceInterface {
	private DataStoreReadInterface dbRead;
	private DataStoreWriteInterface dbWrite;

	/**
	 * initialize a new VotingServiceImpl
	 * 
	 * @param dbRead
	 *            we need an object implementing DataStoreReadInterface
	 * @param dbWrite
	 *            we need an object implementing DataStoreWriteInterface
	 */
	public VotingServiceImpl(DataStoreReadInterface dbRead,
			DataStoreWriteInterface dbWrite) {
		this.dbRead = dbRead;
		this.dbWrite = dbWrite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hrw.swep.mensa.service.MensaServiceInterface#getAlleGerichte()
	 */
	@Override
	public List<Question> getAllQuestions() {
		List<Question> questions = new ArrayList<Question>();
		List<Integer> ids = dbRead.getAllQuestions();
		for (int i : ids) {
			Question question = new Question(i,
					dbRead.getDescriptionOfQuestion(i),
					dbRead.getStatusOfQuestion(i),
					dbRead.getVotesForQuestion(i));
			questions.add(question);
		}

		return questions;
	}

	@Override
	public void storeQuestion(Question question) {
		dbWrite.upsertQuestion(question.getId(), question.getDescription(),
				question.isOpenForVoting(), question.getVotes());

	}

	@Override
	public void close() throws SQLException {
		dbRead.close();
	}

	@Override
	public void deleteQuestion(Question question) {
		if (question != null) {
			dbWrite.deleteQuestion(question.getId());
		} else
			throw new IllegalStateException(
					"Null is not a question that could be deleted.");
	}

	@Override
	public void voteFor(Question question, int vote) {
		question.vote(vote);
	}
}
