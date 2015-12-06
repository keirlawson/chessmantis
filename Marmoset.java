import java.util.Random;

public class Marmoset extends Mantis implements Evaluator
{
	private Random rand;
	private static final int MAX = 100;
	
	public Marmoset()
	{
		super();
		rand = new Random();
	}
	
	/*
	public Marmoset(String setup)
	{
		super(setup);
		rand = new Random();
	}
	*/
	
	public int evaluate()
	{
		return rand.nextInt(MAX);
	}
}
