package gov.us.senate.voting.worker;

import gov.us.senate.voting.domain.ErrorMessages;
import gov.us.senate.voting.exception.MinimumVotingTimeException;

import java.util.Date;

import static java.util.concurrent.TimeUnit.*;

public class VotingTimer {

	private Date votingStarted;
	private Date votingEnded;
	private static final Long MIN_DURATION_IN_MINUTES = 15L;
	
	public void startVoting(){
		votingStarted = new Date();
	}
	
	public void endVoting(Boolean enableClosingTimeConstraint) throws MinimumVotingTimeException{
		votingEnded = new Date();
		if(enableClosingTimeConstraint) checkMinVotingConstraint();
	}
	
	public Long getCurrentVotingDurationInMinutes(){		
		long duration = new Date().getTime() - votingStarted.getTime();
		
		return MINUTES.convert(duration, MILLISECONDS);
	}
	
	public boolean didVotingStart(){
		return votingStarted != null; 
	}
	
	private void checkMinVotingConstraint() throws MinimumVotingTimeException{
		
		long duration = votingEnded.getTime() - votingStarted.getTime();
		
		if(duration < MILLISECONDS.convert(MIN_DURATION_IN_MINUTES, MINUTES)){
			throw new MinimumVotingTimeException(ErrorMessages.MIN_VOTING_TIME_ERROR_MESSAGE);
		}
	}

	public Date getVotingStarted() {
		return votingStarted;
	}

	public Date getVotingEnded() {
		return votingEnded;
	}
}
