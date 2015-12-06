class TimeEvent implements ChessEvent {

	private int moves;

	private int base;

	private int increment;

	public TimeEvent(int moves, int base, int increment) {
		this.moves = moves;
		this.base = base;
		this.increment = increment;
	}

	public int getMoves()
	{
		return moves;
	}

	public int getBase()
	{
		return base;
	}

	public int getIncrement()
	{
		return increment;
	}
}
