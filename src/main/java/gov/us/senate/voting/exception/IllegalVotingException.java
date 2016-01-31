package gov.us.senate.voting.exception;

public class IllegalVotingException extends Exception {

	private static final long serialVersionUID = 2058773578649740891L;

	public IllegalVotingException(String message) {
		super(message);
	}
}
