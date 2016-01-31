package gov.us.senate.voting.exception;

public class TiedStateException extends Exception {

	private static final long serialVersionUID = -2962115115983370264L;
	
	public TiedStateException(String message) {
		super(message);
	}
}
