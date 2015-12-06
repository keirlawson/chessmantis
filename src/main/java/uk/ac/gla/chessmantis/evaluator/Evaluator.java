package uk.ac.gla.chessmantis.evaluator;

import uk.ac.gla.chessmantis.Board;

public interface Evaluator extends Board
{
	public int evaluate();
	public void setBoard(String setup);
	public void setRandom(int degran);
}
