package gov.us.senate.voting.main;

import gov.us.senate.voting.domain.ErrorMessages;
import gov.us.senate.voting.domain.ParliamentPositionEnum;
import gov.us.senate.voting.domain.Voter;
import gov.us.senate.voting.domain.VotingResult;
import gov.us.senate.voting.exception.IllegalVotingException;
import gov.us.senate.voting.exception.MaximumVotingCountException;
import gov.us.senate.voting.exception.MinimumVotingTimeException;
import gov.us.senate.voting.exception.TiedStateException;
import gov.us.senate.voting.worker.VotesCounter;
import gov.us.senate.voting.worker.VotingTimer;

public class VotingDelegate {

	private VotesCounter votesCounter;
	private VotingTimer votingTimer;
	private boolean motionInTiedState = false;

	public VotingDelegate(){
		votesCounter = new VotesCounter();
		votingTimer = new VotingTimer();
	}

	public void openVoting(){
		votingTimer.startVoting();
	}

	/*
	 * Takes enableClosingTimeConstraint boolean for writing UT's for result scenario
	 */
	public VotingResult endVoting(Boolean enableClosingTimeConstraint) throws MinimumVotingTimeException, TiedStateException{
		votingTimer.endVoting(enableClosingTimeConstraint);
		VotingResult votingResult = getCurrentState();
		checkForTiedState(votingResult);
		return votingResult;
	}

	public VotingResult forceCloseTiedVoting(Boolean enableClosingTimeConstraint) throws IllegalVotingException, MinimumVotingTimeException{
		if (!motionInTiedState){
			throw new IllegalVotingException(ErrorMessages.ILLEGAL_FORCE_CLOSING_ERROR_MESSAGE);
		}else{
			votingTimer.endVoting(enableClosingTimeConstraint);
			VotingResult votingResult = getCurrentState();
			votingResult.forceCloseVotingResult();
			return votingResult;
		}
	}

	private void checkForTiedState(VotingResult votingResult) throws TiedStateException{
		if(votingResult.isMotionInTiedState()){
			motionInTiedState = true;
			throw new TiedStateException(ErrorMessages.TIED_STATE_ERROR_MESSAGE);
		}
	}

	public void castVote(Voter voter) throws IllegalVotingException, MaximumVotingCountException, MinimumVotingTimeException{
		checkIfVotingBegan();

		if (!motionInTiedState)
			votesCounter.castVote(voter);
		else
			handleTiedState(voter);
	}

	private void checkIfVotingBegan() throws IllegalVotingException{
		if(!votingTimer.didVotingStart()){
			throw new IllegalVotingException(ErrorMessages.VOTING_NOT_OPENED_ERROR_MESSAGE);
		}
	}

	private void handleTiedState(Voter voter) throws MinimumVotingTimeException, IllegalVotingException, MaximumVotingCountException{
		if(voter.getPosition() != ParliamentPositionEnum.VICE_PRESIDENT){
			throw new IllegalVotingException(ErrorMessages.TIED_STATE_NON_VP_VOTING_ERROR_MESSAGE);
		}else{
			votesCounter.castVote(voter);
			votingTimer.endVoting(false);
		}
	}

	public VotingResult getCurrentState(){
		VotingResult votingResult = new VotingResult(votesCounter, votingTimer);
		return votingResult;
	}	
}
