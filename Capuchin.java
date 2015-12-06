/**
 * @author NH
 */

public class Capuchin extends Mantis implements Evaluator
{
	protected static final int PV = (new Pawn(WHITE)).getValue();
	
	private int materia()
	{
		int total = 0;
		for(int i = 0; i < cb.length; i++)
			if(cb[i] != null)
				total += (cb[i].isWhite()) ? -cb[i].getValue() : cb[i].getValue();
		return total;
	}
	
	public int evaluate()
	{
		int total = materia();
		return total;
	}
}
