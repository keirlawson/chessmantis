package uk.ac.gla.chessmantis.analyser;

import uk.ac.gla.chessmantis.Moveable;
import uk.ac.gla.chessmantis.evaluator.Evaluator;

public interface Analyser extends Runnable
{
	public Moveable getNextMove(Evaluator evaluator, int ply);

	public void setDepth(int depth);

	public void setEvaluator(Evaluator eval);

	public void setCancel(boolean c);

	public boolean isDone();

	public void reset();

	public Moveable get();
}
