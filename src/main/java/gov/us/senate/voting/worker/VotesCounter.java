package gov.us.senate.voting.worker;

import java.util.LinkedHashSet;
import java.util.Set;

import gov.us.senate.voting.domain.ErrorMessages;
import gov.us.senate.voting.domain.MotionStatusEnum;
import gov.us.senate.voting.domain.ParliamentPositionEnum;
import gov.us.senate.voting.domain.Voter;
import gov.us.senate.voting.domain.VotingBallotEnum;
import gov.us.senate.voting.exception.MaximumVotingCountException;
import gov.us.senate.voting.exception.IllegalVotingException;

public class VotesCounter {

	private int numberOfYayys = 0;
	private int numberOfNayys = 0;

	private static final Integer MAX_VOTES_LIMIT = 101;

	private Set<Voter> votersSet = new LinkedHashSet<Voter>(MAX_VOTES_LIMIT);

	public MotionStatusEnum getCurrentMotionStatus() {
		MotionStatusEnum result;

		if(numberOfYayys>numberOfNayys){
			result = MotionStatusEnum.PASS;
		}else if(numberOfYayys == numberOfNayys){
			result = MotionStatusEnum.TIE;
		}else{
			result = MotionStatusEnum.FAIL;
		}
		return result;
	}

	public void castVote(Voter voter) throws IllegalVotingException, MaximumVotingCountException{
		countThisVote(voter);
		try{
			votersSet.add(voter);
		}catch(IllegalArgumentException e){
			e.printStackTrace();
			throw new IllegalVotingException(ErrorMessages.DUPLICATE_VOTING_ERROR_MESSAGE);
		}
	}

	public Integer getNumberOfNayys() {
		return numberOfNayys;
	}
	
	public Integer getNumberOfYayys() {
		return numberOfYayys;
	}
	
	private void countThisVote(Voter voter) throws MaximumVotingCountException, IllegalVotingException{
		checkForVicePresident(voter.getPosition());
		if(voter.getVote() == VotingBallotEnum.YEAS){
			incrementYaysByOne();
		}else if(voter.getVote() == VotingBallotEnum.NAYS){
			incrementNayysByOne();
		}
	}

	private void checkForVicePresident(ParliamentPositionEnum position) throws IllegalVotingException{
		if(position == ParliamentPositionEnum.VICE_PRESIDENT 
				&& getCurrentMotionStatus() != MotionStatusEnum.TIE){
			throw new IllegalVotingException(ErrorMessages.VICE_PRESIDENT_VOTING_ERROR_MESSAGE);
		}
	}

	private void incrementYaysByOne() throws MaximumVotingCountException{
		numberOfYayys++;
		checkTotalVotesCount();
	}

	private void incrementNayysByOne() throws MaximumVotingCountException{
		numberOfNayys++;
		checkTotalVotesCount();
	}

	private Integer getTotalVotesCount(){
		return numberOfYayys+numberOfNayys;
	}

	private void checkTotalVotesCount() throws MaximumVotingCountException {
		if(getTotalVotesCount() >MAX_VOTES_LIMIT){
			throw new MaximumVotingCountException(ErrorMessages.MAX_VOTES_ERROR_MESSAGE);
		}
	}

	@Override
	public String toString() {
		return "VotingCounter [numberOfYayys=" + numberOfYayys
				+ ", numberOfNayys=" + numberOfNayys + "]";
	}
}
