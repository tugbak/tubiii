package de.hrw.swep.votingservice.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.Assertion;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.Test;

/**
 * @author andriesc
 *
 */
public class RealDatabaseTest {
	// server mode of HSQLDB:
	// private static final String connectionString =
	// "jdbc:hsqldb:hsql://localhost/";
	// in-process mode of HSQLDB:
	private static final String CONNECTION_STRING = "jdbc:hsqldb:file:../db-layer/database/votesdb";
	private static final String DB_USER = "sa";
	private static final String DB_PASSWORD = "";

	private RealDatabase db;
	private IDatabaseTester databaseTester;

	private final int[] questionIds = { 1, 2, 3, 4, 5 };

	// id of question in database is its position in this array
	private final String[] questionsDescriptions = { "",
			"Mögen Sie Schokoeis?",
			"Wie finden Sie Sommerwetter mit blauem Himmel und 37 Grad?",
			"Wie finden Sie Games of Thrones?",
			"Trinken Sie gerne Softdrinks?", "Mögen Sie Wassersport?" };

	private final int[] votesForID2 = { 5, 4, 5 };

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		db = new RealDatabase(true);

		databaseTester = new JdbcDatabaseTester("org.hsqldb.jdbcDriver",
				CONNECTION_STRING, DB_USER, DB_PASSWORD);
		databaseTester.setDataSet(new FlatXmlDataSetBuilder().build(new File(
				"full.xml")));
		databaseTester.onSetup();
	}

	@Test
	public void testGetAllQuestions() {
		List<Integer> allQuestions = db.getAllQuestions();
		assertEquals(5, allQuestions.size());
		for (int i = 0; i <= 4; i++) {
			assertEquals(questionIds[i], allQuestions.get(i).intValue());
		}
	}

	@Test
	public void testGetDescriptionOfQuestion() {
		List<Integer> allQuestions = db.getAllQuestions();
		assertEquals(5, allQuestions.size());
		for (int i = 0; i < 5; i++) {
			assertEquals(questionsDescriptions[allQuestions.get(i)],
					db.getDescriptionOfQuestion(allQuestions.get(i)));
		}
	}

	@Test
	public void testGetVotesForGericht() {
		List<Integer> votes = db.getVotesForQuestion(2);
		assertNotNull(votes);
		assertEquals(3, votes.size());
		for (int i = 0; i < 3; i++) {
			assertEquals(votesForID2[i], votes.get(i).intValue());
		}
	}

	@Test
	public void testAddVote() throws SQLException, Exception {
		assertEquals(3, db.getVotesForQuestion(2).size());
		db.addVote(2, 1);
		assertEquals(4, db.getVotesForQuestion(2).size());
		Assertion
				.assertEquals(new FlatXmlDataSetBuilder().build(new File(
						"oneAddedVote.xml")), databaseTester.getConnection()
						.createDataSet());
	}

	@Test
	public void testAddWrongVote() throws SQLException, Exception {
		try {
			db.addVote(1, 7);
		} catch (PersistenceException e) {
			IDataSet actual = databaseTester.getConnection().createDataSet();
			Assertion.assertEquals(
					new FlatXmlDataSetBuilder().build(new File("full.xml")),
					actual);
			return;
		}
		fail();
	}

	@Test
	public void testGetStatusOfQuestion() {
		assertTrue(db.getStatusOfQuestion(5));
		assertFalse(db.getStatusOfQuestion(1));
	}

	@Test
	public void testUpsertQuestion() {
		int id = db.getAllQuestions().iterator().next();
		boolean status = db.getStatusOfQuestion(id);
		List<Integer> bewertungen = db.getVotesForQuestion(id);
		db.upsertQuestion(id, "Neue Frage", status, bewertungen);

		// Update prüfen
		assertEquals("Neue Frage", db.getDescriptionOfQuestion(id));
		assertEquals(status, db.getStatusOfQuestion(id));

		// Insert prüfen
		bewertungen = new ArrayList<Integer>();
		bewertungen.add(5);
		bewertungen.add(3);
		db.upsertQuestion(-1, "Sollen die Briten die EU verlassen?", false,
				bewertungen);
	}

	@Test
	public void testDeleteVotes() {
		List<Integer> votes = db.getVotesForQuestion(2);
		assertEquals(3, votes.size());
		db.deleteVotes(2);
		votes = db.getVotesForQuestion(2);
		assertEquals(0, votes.size());
	}

	@Test
	public void testDeleteQuestion() {
		assertEquals("Mögen Sie Wassersport?", db.getDescriptionOfQuestion(5));
		db.deleteQuestion(5);
		assertNull(db.getDescriptionOfQuestion(5));
	}
}
