package gov.us.senate.voting.exception;

public class MaximumVotingCountException extends Exception {

	private static final long serialVersionUID = -3873850131462446711L;
	
	public MaximumVotingCountException(String message) {
		super(message);
	}
}
