package gov.us.senate.voting.domain;

public class ErrorMessages {
	public static final String VOTING_NOT_OPENED_ERROR_MESSAGE = "Votes cannot be casted till voting has been opened";
	public static final String MAX_VOTES_ERROR_MESSAGE = "The maximum votes limit of 101 has been reached, this vote cannot be cast.";
	public static final String VICE_PRESIDENT_VOTING_ERROR_MESSAGE = "Vice President is only allowed to cast vote when motion is a tie";
	public static final String DUPLICATE_VOTING_ERROR_MESSAGE = "Same voter is not allowed to vote twice";
	public static final String MIN_VOTING_TIME_ERROR_MESSAGE = "Voting needs to be open for minimum of 15 minutes.";
	public static final String TIED_STATE_ERROR_MESSAGE = "Voting cannot be closed as it entered a tied state, please ask Vice President to Vote.";
	public static final String TIED_STATE_NON_VP_VOTING_ERROR_MESSAGE = "Only Vice President is allowed to vote in a tied state";
	public static final String ILLEGAL_FORCE_CLOSING_ERROR_MESSAGE = "Force closing is only allowed in a tied state";

}
