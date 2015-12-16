package uk.ac.gla.chessmantis.analyser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.gla.chessmantis.DebugWriter;
import uk.ac.gla.chessmantis.Moveable;
import uk.ac.gla.chessmantis.ChessEventWriter;
import uk.ac.gla.chessmantis.evaluator.Capuchin;
import uk.ac.gla.chessmantis.evaluator.Evaluator;
import uk.ac.gla.chessmantis.event.MoveEvent;

import java.util.*;

/**
 * @author Neil Henning
 * @version 1.0
 */

public class MiniMaxAnalyser implements Analyser
{

	public static final Logger logger = LogManager.getLogger("MiniMaxAnalyser");

	private int cutoffdepth;

	private Moveable finalmove;

	private Evaluator eval;

	private boolean canceled = false;

	private boolean done = false;

	private final int infinity = 10000000;

	public Moveable get() {
		return finalmove;
	}

	public boolean isDone() {
		return done;
	}

	public void reset() {
		canceled = false;
		done = false;
	}

	private int min(int x, int y)
	{
		return (x < y) ? x : y;
	}

	private int max(int x, int y)
	{
		return (x > y) ? x : y;
	}

	public void setDepth(int depth) {
		cutoffdepth = depth;
	}

	public void setEvaluator(Evaluator eval) {
		this.eval = eval;
	}

	public void setCancel(boolean c) {
		canceled = c;
	}

	public void run() {
		finalmove = getNextMove(eval, cutoffdepth);
		done = true;
	}
	
	/** Private function that returns the relevant integer value from a minimax
	evaluation. */
	private int doMiniMax(Evaluator evaluator, int depth)
	{
		depth++;
		List<Moveable> moves = evaluator.generateLegalMoves();
		if(depth >= this.cutoffdepth || moves.size() == 0)
			return evaluator.evaluate();
		int result = (evaluator.isPlayerTurn()) ? infinity : -infinity;
		for(Moveable move : moves)
		{
			boolean humansturn = evaluator.isPlayerTurn();
			if(evaluator.makeMove(move))
			{
				result = (humansturn) ? min(result, doMiniMax(evaluator, depth)) : max(result, doMiniMax(evaluator, depth));
				evaluator.reverseMove(move);
			}
		}
		depth--;
		return result;
	}
	
        //Based on wikipedia psuedocode
	int miniMax(Evaluator evaluator, int depth) throws InterruptedException {
		if (canceled) {
			logger.debug("Got a cancel, let's get out of here!");
			throw (new InterruptedException());//Break out here...
		}
		int alpha, beta;
		List<Moveable> legalmoves = evaluator.generateLegalMoves();
		if (legalmoves.size() == 0 || (depth == cutoffdepth))
		{
			return evaluator.evaluate();
		}
		if (evaluator.isPlayerTurn())
		{
			beta = infinity;
			for (Moveable m : legalmoves)
			{
				evaluator.makeMove(m);
				try {
					beta = min(beta, miniMax(evaluator, depth + 1));
				} catch (InterruptedException e) {
					evaluator.reverseMove(m);
					throw (new InterruptedException());
				}
				evaluator.reverseMove(m);
			}
			return beta;
		}
		else //We are to play at this node
		{
			alpha = 0 - infinity;
			for (Moveable m : legalmoves) 
			{
				evaluator.makeMove(m);
				try {
				alpha = max(alpha, miniMax(evaluator, depth + 1));
				} catch (InterruptedException e) {
					evaluator.reverseMove(m);
					throw (new InterruptedException());
				}
				evaluator.reverseMove(m);
			}
			return alpha;
		}
	}

	/** Returns a moveable of the next move that this uk.ac.gla.chessmantis.analyser.Analyser thinks the
	evaluateable board should do. */
	public Moveable getNextMove(Evaluator evaluator, int ply)
	{
		/* NH - Couldn't think of a better way to store the ply of MiniMax 
		without using a private variable. */
		this.cutoffdepth = ply;
		List<Moveable> moves = evaluator.generateLegalMoves();
		boolean humansturn = evaluator.isPlayerTurn();
		int best = (humansturn) ? infinity : 0 - infinity;
		Moveable bestmove = null;
		int minimax = 0;
		for(Moveable move : moves)
		{
			if (canceled) {
				break;
			}
			if(evaluator.makeMove(move))
			{
				try {
					minimax = miniMax(evaluator, 1);
				} catch (InterruptedException e) {
					;
				}
				/* NH - Tricky bit here. What we want is when minormax is true, and 
				when the best is less than minimax, we want to change the bestmove. 
				But we also want to change the bestmove when minormax is false, and 
				when best is less than minimax is also false. So this works out as 
				XOR of the two boolean expressions. */
				if(humansturn ^ (best < minimax))
				{
					bestmove = move;
					best = minimax;
				}
				evaluator.reverseMove(move);
			}
		}
		return bestmove;
	}
	
	/** Using this for testing purposes at the moment, to ensure we are 
	generating moves from this minimax. */
	public static void main(String[] args)
	{
		Evaluator omega = new Capuchin();
		MiniMaxAnalyser epsilon = new MiniMaxAnalyser();
		ChessEventWriter writer = new DebugWriter("uk.ac.gla.chessmantis.evaluator.Marmoset!");
		
		for(int i = 0; i < 2; i++)
		{
			Moveable move = epsilon.getNextMove(omega, 2);
			if(omega.makeMove(move))
				writer.write(new MoveEvent(move));
		}
	}
}
