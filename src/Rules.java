
public class Rules {
	
	boolean canKeepMultipleGoalsPerThrow;
	


	boolean canRethrowKnownDice;
	boolean canUseKnownDiceInPattern;
	
	public Rules() {
		super();
	}
	
	public Rules(boolean canKeepMultiplePatternsPerThrow, boolean canRethrowKnownDice, boolean canUseKnownDiceInPattern) {
//		Rules();
		this.canKeepMultipleGoalsPerThrow = canKeepMultiplePatternsPerThrow;
		this.canRethrowKnownDice = canRethrowKnownDice;
		this.canUseKnownDiceInPattern = canUseKnownDiceInPattern;
	}

	public boolean isCanKeepMultipleGoalsPerThrow() {
		return canKeepMultipleGoalsPerThrow;
	}
	
	public void setCanKeepMultipleGoalsPerThrow(boolean canKeepMultipleGoalsPerThrow) {
		this.canKeepMultipleGoalsPerThrow = canKeepMultipleGoalsPerThrow;
	}


	
	
}
