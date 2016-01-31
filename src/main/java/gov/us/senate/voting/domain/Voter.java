package gov.us.senate.voting.domain;

public class Voter {

	private Integer voterId;

	private ParliamentPositionEnum position;

	private VotingBallotEnum vote;
	
	public Voter(Integer voterId, ParliamentPositionEnum position,
			VotingBallotEnum vote) {
		super();
		this.voterId = voterId;
		this.position = position;
		this.vote = vote;
	}
	
	public Integer getVoterId() {
		return voterId;
	}

	public ParliamentPositionEnum getPosition() {
		return position;
	}
	
	public VotingBallotEnum getVote() {
		return vote;
	}

	@Override
	public String toString() {
		return "Voter [voterId=" + voterId + ", position=" + position
				+ ", vote=" + vote + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		result = prime * result + ((vote == null) ? 0 : vote.hashCode());
		result = prime * result + ((voterId == null) ? 0 : voterId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Voter other = (Voter) obj;
		if (position != other.position)
			return false;
		if (vote != other.vote)
			return false;
		if (voterId == null) {
			if (other.voterId != null)
				return false;
		} else if (!voterId.equals(other.voterId))
			return false;
		return true;
	}

}
