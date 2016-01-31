package gov.us.senate.voting.domain;

import java.util.Date;

import gov.us.senate.voting.worker.VotesCounter;
import gov.us.senate.voting.worker.VotingTimer;

public class VotingResult {

	private Date votingOpened;
	private Date votingClosed;
	private Integer votesFor;
	private Integer votesAgainst;
	private MotionStatusEnum motionStatus;

	public VotingResult(VotesCounter votesCounter, VotingTimer votingTimer){
		this.votingOpened = votingTimer.getVotingStarted();
		this.votingClosed = votingTimer.getVotingEnded();
		this.votesFor = votesCounter.getNumberOfYayys();
		this.votesAgainst = votesCounter.getNumberOfNayys();
		this.motionStatus = votesCounter.getCurrentMotionStatus();
	}

	public Integer getVotesFor() {
		return votesFor;
	}

	public Integer getVotesAgainst() {
		return votesAgainst;
	}

	public Date getVotingOpened() {
		return votingOpened;
	}

	public Date getVotingClosed() {
		return votingClosed;
	}

	public MotionStatusEnum getMotionStatus() {
		return motionStatus;
	}

	public void forceCloseVotingResult(){
		this.motionStatus = MotionStatusEnum.FAIL;
	}

	public boolean isMotionInTiedState(){
		return motionStatus == MotionStatusEnum.TIE;
	}

	@Override
	public String toString() {
		return "VotingResult [votingOpened=" + votingOpened + ", votingClosed="
				+ votingClosed + ", votesFor=" + votesFor + ", votesAgainst="
				+ votesAgainst + ", motionStatus=" + motionStatus + "]";
	}

}
