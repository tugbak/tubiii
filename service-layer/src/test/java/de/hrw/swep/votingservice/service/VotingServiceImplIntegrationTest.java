/**
 * 
 */
package de.hrw.swep.votingservice.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.hrw.swep.votingservice.persistence.RealDatabase;

/**
 * @author andriesc
 *
 */
public class VotingServiceImplIntegrationTest {
	// server mode of HSQLDB:
	// private static final String connectionString =
	// "jdbc:hsqldb:hsql://localhost/";
	// in-process mode of HSQLDB:
	private static final String CONNECTION_STRING = "jdbc:hsqldb:file:../db-layer/database/votesdb";
	private static final String DB_USER = "sa";
	private static final String DB_PASSWORD = "";

	VotingServiceInterface votingService;

	private final boolean[] questionsStates = { false, false, false, true, true };
	private final double[] questionsTotalResults = { 2.33333325, 4.66666650,
			4.66666650, 3.33333325, 4.666667 };

	// id of question in database is its position in this array
	private final String[] questionsDescriptions = { "",
			"Mögen Sie Schokoeis?",
			"Wie finden Sie Sommerwetter mit blauem Himmel und 37 Grad?",
			"Wie finden Sie Games of Thrones?",
			"Trinken Sie gerne Softdrinks?", "Mögen Sie Wassersport?" };

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		IDatabaseTester databaseTester = new JdbcDatabaseTester(
				"org.hsqldb.jdbcDriver", CONNECTION_STRING, DB_USER,
				DB_PASSWORD);
		databaseTester.setDataSet(new FlatXmlDataSetBuilder().build(new File(
				"..\\db-layer\\full.xml")));
		databaseTester.onSetup();

		RealDatabase db = new RealDatabase(true);
		votingService = new VotingServiceImpl(db, db);
	}

	@After
	public void tearDown() throws Exception {
		votingService.close();
	}

	boolean checkVotes(List<Integer> votes) {
		int votesWith5 = 1;
		int votesWith3 = 1;
		int votesWith2 = 1;
		int votesWrong = 0;
		for (int i : votes) {
			if (i == 5) {
				votesWith5--;
			} else if (i == 3) {
				votesWith3--;
			} else if (i == 2) {
				votesWith2--;
			} else {
				votesWrong++;
			}
		}
		return (votesWith5 == 0 && votesWith3 == 0 && votesWith2 == 0 && votesWrong == 0);
	}

	@Test
	public void testGetAllQuestions() {
		List<Question> questions = votingService.getAllQuestions();
		assertEquals(5, questions.size());

		boolean found = false;
		for (Question question : questions) {
			assertEquals(questionsDescriptions[question.getId()],
					question.getDescription());
			assertEquals(questionsStates[question.getId() - 1],
					question.isOpenForVoting());
			if (question.getId() == 5) {
				try {
					@SuppressWarnings("unused")
					float f = question.getAverageVote();
					fail();
				} catch (IllegalStateException e) {
					assertEquals("No votes.", e.getMessage());
				}
			} else {
				assertEquals(questionsTotalResults[question.getId() - 1],
						question.getAverageVote(), 0.0000002);
				if (question.getId() == 4) {
					found = true;
					assertTrue(checkVotes(question.getVotes()));
				}
			}
		}

		assertTrue(found);
	}

	@Test
	public void testStoreQuestion() {
		List<Question> questions = votingService.getAllQuestions();

		boolean found = false;
		Question newQuestion = null;
		for (Question question : questions) {
			if (question.getId() == 4) {
				found = true;
				newQuestion = new Question(question.getId(), "new question",
						question.isOpenForVoting(), question.getVotes());
			}
		}
		assertTrue(found);

		votingService.storeQuestion(newQuestion);

		questions = votingService.getAllQuestions();
		found = false;
		for (Question question : questions) {
			if (question.getId() == 4) {
				found = true;
				assertEquals("new question", question.getDescription());
				assertTrue(question.isOpenForVoting());

				// check votes en detail
				assertEquals(questionsTotalResults[4 - 1],
						question.getAverageVote(), 0.0000002);
				List<Integer> votes = question.getVotes();
				assertEquals(3, votes.size());
				checkVotes(votes);
			}
		}

		assertTrue(found);
	}

	@Test
	public void testDeleteQuestion() {
		List<Question> questions = votingService.getAllQuestions();
		Question question = questions.iterator().next();
		assertTrue(questions.contains(question));

		// Ich weiß leider nicht , wie man den Test korrekt  implemebtiert
	}

	@Test
	public void testDeleteNullQuestion() {
		try {
			votingService.deleteQuestion(null);
		} catch (IllegalStateException e) {
			assertEquals("Null is not a question that could be deleted.",
					e.getMessage());
			return;
		}
		fail();
	}

}
