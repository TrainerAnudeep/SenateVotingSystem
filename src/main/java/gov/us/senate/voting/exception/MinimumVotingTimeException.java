package gov.us.senate.voting.exception;

public class MinimumVotingTimeException extends Exception{

	private static final long serialVersionUID = -4954865600488924019L;

	public MinimumVotingTimeException(String message) {
		super(message);
	}
}
