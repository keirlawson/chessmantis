import java.util.*;

/**
 * @author TT & NH
 * Yet another evaluator, that builds up on what we've already got workin
 * @version 1.0
 * @see Evaluator
 */
public class Tamarin extends Macaque implements Evaluator
{
	/* NH - removed FreedomMultiplier as it added an entry to the stack which 
	wasn't at all needed. */
	
	public int evaluate()
	{
		int total = 0;
		total += super.evaluate();
		return total;
	}
}
