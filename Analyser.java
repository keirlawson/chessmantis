import java.util.concurrent.*;
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
