package org.us.senate.voting.main;

import gov.us.senate.voting.domain.ErrorMessages;
import gov.us.senate.voting.domain.MotionStatusEnum;
import gov.us.senate.voting.domain.ParliamentPositionEnum;
import gov.us.senate.voting.domain.Voter;
import gov.us.senate.voting.domain.VotingBallotEnum;
import gov.us.senate.voting.domain.VotingResult;
import gov.us.senate.voting.exception.IllegalVotingException;
import gov.us.senate.voting.exception.MaximumVotingCountException;
import gov.us.senate.voting.exception.MinimumVotingTimeException;
import gov.us.senate.voting.exception.TiedStateException;
import gov.us.senate.voting.main.VotingDelegate;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class VotingDelegate_UT {

	private VotingDelegate votingDelegate;

	private Integer DEFAULT_VOTER_ID = 1;
	private ParliamentPositionEnum DEFAULT_PARLIMENT_POSITION = ParliamentPositionEnum.MEMBER;
	private VotingBallotEnum DEFAULT_VOTE = VotingBallotEnum.YEAS;

	@Before
	public void init() throws Exception{
		votingDelegate = new VotingDelegate();
	}

	@Test
	public void testNoVotesCanBeCastedTillVotingBegan() {
		try {
			votingDelegate.castVote(new Voter(DEFAULT_VOTER_ID, DEFAULT_PARLIMENT_POSITION, DEFAULT_VOTE));
		} catch (IllegalVotingException ive) {
			assertEquals(ive.getMessage(), ErrorMessages.VOTING_NOT_OPENED_ERROR_MESSAGE);
		}catch(MaximumVotingCountException | MinimumVotingTimeException e){
			fail("This should not get called");
		}
	}

	@Test
	public void testMotionCannotBeClosedForVotingLessThan15Mins(){
		votingDelegate.openVoting();

		try {
			votingDelegate.endVoting(true);
		} catch (MinimumVotingTimeException e) {
			assertEquals(ErrorMessages.MIN_VOTING_TIME_ERROR_MESSAGE, e.getMessage());
		} catch (TiedStateException e) {
			fail("This should not get called");
		}
	}

	@Test
	public void testClosedVotingShouldReturnVotingInfo(){
		votingDelegate.openVoting();

		Integer votesFor = 21;
		Integer votesAgainst = 22;

		MotionStatusEnum motionStatus = MotionStatusEnum.FAIL;

		try{
			castDummyVotes(votesFor, votesAgainst);
			VotingResult votingResult = votingDelegate.endVoting(false);
			assertEquals(votesFor, votingResult.getVotesFor());
			assertEquals(votesAgainst, votingResult.getVotesAgainst());
			assertEquals(motionStatus, votingResult.getMotionStatus());
			assertTrue(votingResult.getVotingOpened() !=null);
			assertTrue(votingResult.getVotingClosed() !=null);

		} catch (IllegalVotingException | MaximumVotingCountException | MinimumVotingTimeException | TiedStateException e) {
			fail("This should not get called");
		}	
	}

	@Test
	public void testClosingVotingInTiedStateShouldPromptForVPMessage(){

		try{
			replicateTiedState(null, null);

		} catch (TiedStateException e) {
			assertEquals(e.getMessage(), ErrorMessages.TIED_STATE_ERROR_MESSAGE);

			try {
				votingDelegate.castVote(new Voter(DEFAULT_VOTER_ID, DEFAULT_PARLIMENT_POSITION, VotingBallotEnum.YEAS));
			} catch (IllegalVotingException e1) {
				assertEquals(e1.getMessage(), ErrorMessages.TIED_STATE_NON_VP_VOTING_ERROR_MESSAGE);
			} catch(MaximumVotingCountException| MinimumVotingTimeException e1){
				fail("This should not get called");
			}
		}		
	}

	@Test
	public void testCastingNonVPVoteInTiedStateShouldThrowError(){

		try{
			replicateTiedState(null, null);
		} catch (TiedStateException e) {
			try {
				votingDelegate.castVote(new Voter(DEFAULT_VOTER_ID, DEFAULT_PARLIMENT_POSITION, VotingBallotEnum.YEAS));
			} catch (IllegalVotingException e1) {
				assertEquals(e1.getMessage(), ErrorMessages.TIED_STATE_NON_VP_VOTING_ERROR_MESSAGE);
			} catch(MaximumVotingCountException| MinimumVotingTimeException e1){
				fail("This should not get called");
			}
		}	
	}

	@Test
	public void testCastingVPVoteInTiedStateShouldResolveTiedState(){
		Integer votesFor = 21;
		Integer votesAgainst = 21;

		MotionStatusEnum motionStatus = MotionStatusEnum.PASS;
		Integer resolvedVotesFor = 22;

		try{
			replicateTiedState(votesFor, votesAgainst);
		} catch (TiedStateException e) {
			try {
				votingDelegate.castVote(new Voter(DEFAULT_VOTER_ID, ParliamentPositionEnum.VICE_PRESIDENT , VotingBallotEnum.YEAS));
			} catch(MaximumVotingCountException| MinimumVotingTimeException | IllegalVotingException e1){
				fail("This should not get called");
			}

			VotingResult votingResult = votingDelegate.getCurrentState();
			assertEquals(resolvedVotesFor, votingResult.getVotesFor());
			assertEquals(votesAgainst, votingResult.getVotesAgainst());
			assertEquals(motionStatus, votingResult.getMotionStatus());
			assertTrue(votingResult.getVotingOpened() !=null);
			assertTrue(votingResult.getVotingClosed() !=null);
		}
	}

	@Test
	public void testForceClosingInTiedStateShouldCauseMotionToFail(){
		Integer votesFor = 21;
		Integer votesAgainst = 21;

		MotionStatusEnum motionStatus = MotionStatusEnum.FAIL;

		try{
			replicateTiedState(votesFor, votesAgainst);
		} catch (TiedStateException e) {
			try {

				VotingResult votingResult = votingDelegate.forceCloseTiedVoting(false);
				assertEquals(votesFor, votingResult.getVotesFor());
				assertEquals(votesAgainst, votingResult.getVotesAgainst());
				assertEquals(motionStatus, votingResult.getMotionStatus());
				assertTrue(votingResult.getVotingOpened() !=null);
				assertTrue(votingResult.getVotingClosed() !=null);

			} catch(MinimumVotingTimeException | IllegalVotingException e1){
				fail("This should not get called");
			}
		}
	}

	@Test
	public void testForceClosingWithoutTiedStateShouldThrowError(){
		try {
			votingDelegate.forceCloseTiedVoting(false);
		} catch (IllegalVotingException e2) {
			assertEquals(ErrorMessages.ILLEGAL_FORCE_CLOSING_ERROR_MESSAGE, e2.getMessage());
		} catch(MinimumVotingTimeException e){
			fail("This should not get called");
		}
	}

	private void replicateTiedState(Integer votesFor, Integer votesAgainst) throws TiedStateException{
		votingDelegate.openVoting();

		if(votesFor ==null || votesAgainst == null){
			votesFor = 21;
			votesAgainst = 21;
		}

		try {
			castDummyVotes(votesFor, votesAgainst);
			votingDelegate.endVoting(false);
		} catch (IllegalVotingException | MaximumVotingCountException | MinimumVotingTimeException e) {
			fail("This should not get called");
		}
	}
	@Test
	public void testDuplicateVotersCantCastVote(){
		votingDelegate.openVoting();
		Voter voter = new Voter(DEFAULT_VOTER_ID, DEFAULT_PARLIMENT_POSITION, DEFAULT_VOTE);
		try {
			votingDelegate.castVote(voter);
			votingDelegate.castVote(voter);
		} catch (IllegalVotingException ive) {
			assertEquals(ive.getMessage(), ErrorMessages.DUPLICATE_VOTING_ERROR_MESSAGE);
		}catch(MaximumVotingCountException | MinimumVotingTimeException e){
			fail("This should not get called");
		}
	}

	@Test
	public void testMaxVoterLimit(){
		votingDelegate.openVoting();
		try{
			for(int i = 0; i<103;i++){
				votingDelegate.castVote(new Voter(DEFAULT_VOTER_ID, DEFAULT_PARLIMENT_POSITION, DEFAULT_VOTE));
			}
		} catch (IllegalVotingException ive) {
			fail("This should not get called");

		}catch(MaximumVotingCountException mvce){
			assertEquals(mvce.getMessage(), ErrorMessages.MAX_VOTES_ERROR_MESSAGE);
		} catch (MinimumVotingTimeException e) {
			fail("This should not get called");
		}
	}

	@Test
	public void testSystemShouldSupportCurrentStateQuery(){
		votingDelegate.openVoting();

		Integer votesFor = 21;
		Integer votesAgainst = 22;

		MotionStatusEnum motionStatus = MotionStatusEnum.FAIL;

		try{
			castDummyVotes(votesFor, votesAgainst);

			VotingResult votingResult = votingDelegate.getCurrentState();
			assertEquals(votesFor, votingResult.getVotesFor());
			assertEquals(votesAgainst, votingResult.getVotesAgainst());
			assertEquals(motionStatus, votingResult.getMotionStatus());
			assertTrue(votingResult.getVotingOpened() !=null);
			assertTrue(votingResult.getVotingClosed() ==null);

		} catch (IllegalVotingException | MaximumVotingCountException | MinimumVotingTimeException e) {
			fail("This should not get called");
		}
	}

	private void castDummyVotes(Integer votesFor, Integer votesAgainst)
			throws IllegalVotingException, MaximumVotingCountException, MinimumVotingTimeException {
		for(int i = 0; i<votesFor;i++){
			votingDelegate.castVote(new Voter(DEFAULT_VOTER_ID, DEFAULT_PARLIMENT_POSITION, VotingBallotEnum.YEAS));
		}
		for(int i = 0; i<votesAgainst;i++){
			votingDelegate.castVote(new Voter(DEFAULT_VOTER_ID, DEFAULT_PARLIMENT_POSITION, VotingBallotEnum.NAYS));
		}
	}


	@Test
	public void testVPNotAllowedToVoteTillItsInTiedState(){
		votingDelegate.openVoting();
		try{
			votingDelegate.castVote(new Voter(DEFAULT_VOTER_ID, ParliamentPositionEnum.VICE_PRESIDENT, DEFAULT_VOTE));
		} catch (IllegalVotingException ive) {
			assertEquals(ive.getMessage(), ErrorMessages.VICE_PRESIDENT_VOTING_ERROR_MESSAGE);
		}catch(MaximumVotingCountException | MinimumVotingTimeException e){
			fail("This should not get called");
		}
	}

}
