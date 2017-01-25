package de.hrw.swep.votingservice.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.hrw.swep.votingservice.persistence.DataStoreReadInterface;
import de.hrw.swep.votingservice.persistence.DataStoreWriteInterface;

public class VotingServiceImplMockTest {
	
	VotingServiceInterface votingService;
	DataStoreReadInterface dbReadMock;
	DataStoreWriteInterface dbWriteMock;
	private final List<Integer> votes = new ArrayList<Integer>(Arrays.asList(5,
			4, 5));

	@Before
	public void setUp() throws Exception {
		dbReadMock = Mockito.mock(DataStoreReadInterface.class);
		dbWriteMock = Mockito.mock(DataStoreWriteInterface.class);

		List<Integer> ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
		ids.add(3);
		when(dbReadMock.getAllQuestions()).thenReturn(ids);
		when(dbReadMock.getDescriptionOfQuestion(1)).thenReturn(
				"Mögen Sie Schokoeis?");
		when(dbReadMock.getVotesForQuestion(1)).thenReturn(votes);
		when(dbReadMock.getStatusOfQuestion(1)).thenReturn(false);

		when(dbReadMock.getDescriptionOfQuestion(2)).thenReturn(
				"Wie finden Sie Sommerwetter mit blauem Himmel und 37 Grad?");
		when(dbReadMock.getVotesForQuestion(2)).thenReturn(votes);
		when(dbReadMock.getStatusOfQuestion(2)).thenReturn(false);


		votingService = new VotingServiceImpl(dbReadMock, dbWriteMock);
	}

	@Test
	public void testGetAlleGerichte() {
		//  Finde leider die richtige Stelle nicht 

		List<Question> questions = votingService.getAllQuestions();
		assertEquals(3, questions.size());

		boolean found = false;
		for (Question question : questions) {
			if (question.getId() == 3) {
				assertEquals("Wie finden Sie Games of Thrones?",
						question.getDescription());
				assertTrue(question.isOpenForVoting());

				// check votes en detail
				assertEquals(4.6666667, question.getAverageVote(), 0.0000002);
				List<Integer> votesForQuestion3 = question.getVotes();
				assertEquals(3, votesForQuestion3.size());
				int votesWith5 = 2;
				int votesWith4 = 1;
				int votesWrong = 0;
				for (int i : votesForQuestion3) {
					if (i == 5) {
						votesWith5--;
					} else if (i == 4) {
						votesWith4--;
					} else {
						votesWrong++;
					}
				}
				assertEquals(0, votesWith5);
				assertEquals(0, votesWith4);
				assertEquals(0, votesWrong);

				found = true;
			}
		}

		assertTrue(found);
	}

}
