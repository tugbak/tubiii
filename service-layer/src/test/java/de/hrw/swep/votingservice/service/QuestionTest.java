/**
 * 
 */
package de.hrw.swep.votingservice.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author andriesc
 *
 */
public class QuestionTest {
	private Question question;
	private List<Integer> votes = Arrays.asList(5, 5, 5, 5, 3, 2, 1);
	private String description = "Mögen Sie Möhrenbrei?";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		question = new Question(0, description, true, votes);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		question = null;
	}

	@Test
	public void testCreateQuestion() {
		assertEquals(0, question.getId());
		assertEquals(description, question.getDescription());
		assertEquals(votes, question.getVotes());
		assertEquals(3.71, question.getAverageVote(), 0.01);
	}

	@Test
	public void testChangeQuestionStatus() {
		assertEquals(QuestionOpen.class.getName(), question.getCurrentState());
		question.setCurrentState(new QuestionOpen(question));
		assertEquals(QuestionOpen.class.getName(), question.getCurrentState());

		question.setCurrentState(new QuestionClosed(question));
		assertEquals(QuestionClosed.class.getName(), question.getCurrentState());
		assertEquals(0, question.getId());
		assertEquals(description, question.getDescription());
		assertEquals(votes, question.getVotes());
		assertEquals(3.71, question.getAverageVote(), 0.01);

		question.setCurrentState(new QuestionOpen(question));
		assertEquals(QuestionOpen.class.getName(), question.getCurrentState());

		assertEquals(0, question.getId());
		assertEquals(description, question.getDescription());
		assertEquals(votes, question.getVotes());
		assertEquals(3.71, question.getAverageVote(), 0.01);
	}

	@Test
	public void testVoteForOpenQuestion() {
		question.setCurrentState(new QuestionOpen(question));
		assertEquals(QuestionOpen.class.getName(), question.getCurrentState());
		assertEquals(votes, question.getVotes());
		assertEquals(3.71, question.getAverageVote(), 0.01);
		question.vote(5);
		question.vote(4);
		question.vote(1);
		assertEquals(3.59, question.getAverageVote(), 0.01);
	}

	@Test
	public void testVoteForClosedQuestion() {
		question.setCurrentState(new QuestionClosed(question));
		assertEquals(QuestionClosed.class.getName(), question.getCurrentState());
		assertEquals(votes, question.getVotes());
		assertEquals(3.71, question.getAverageVote(), 0.01);
		try {
			question.vote(5);
		} catch (IllegalStateException e) {
			assertEquals("Invalid vote.", e.getMessage());
			assertEquals(votes, question.getVotes());
			assertEquals(3.71, question.getAverageVote(), 0.01);
			return;
		}
		fail("Vote was accepted, this should not have happened.");
	}

	@Test
	public void testOpenQuestionGetVotes() {
		question.setCurrentState(new QuestionOpen(question));
		assertEquals(QuestionOpen.class.getName(), question.getCurrentState());
		assertEquals(votes, question.getVotes());
		assertEquals(3.71, question.getAverageVote(), 0.01);
	}

	@Test
	public void testClosedQuestionGetVotes() {
		question.setCurrentState(new QuestionClosed(question));
		assertEquals(QuestionClosed.class.getName(), question.getCurrentState());
		assertEquals(votes, question.getVotes());
		assertEquals(3.71, question.getAverageVote(), 0.01);
	}
}
