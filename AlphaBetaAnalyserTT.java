import java.util.*;

/**
 * @author Keir Lawson & Dominik Gotojuch
 * @version 1.0
 */

public class AlphaBetaAnalyserTT implements StableAnalyser
{
	private int cutoffdepth;

	private Evaluator eval;

	private final int infinity = 10000000;
	
	// Aspiration window 
	private final int WINDOWSIZE = 10;
	
	// Transposition tables
	private final int HASHFEXACT = 0;
	private final int HASHFALPHA = 1;
	private final int HASHFBETA = 2;
	
	//TranspositionTable tt;
	
	private boolean done = false;

	private Moveable finalmove = null;

	private int min(int x, int y)
	{
		return (x < y) ? x : y;
	}

	public Moveable get(){
		return finalmove;
	}

	private boolean canceled = false;

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

	public void run() {
		finalmove = getNextMove(eval, cutoffdepth);
		done = true;
	}

	public void setCancel(boolean c) {
		canceled = c;
	}

	public boolean isDone() {
		return done;
	}

	public void reset() {
		done = false;
	}

     
	int alphaBeta (Evaluator evaluator, int depth, int alpha, int beta) throws InterruptedException
	{
		if (canceled) 
		{
			System.err.println("Got a cancel, let's get out of here!");
			throw (new InterruptedException()); //Break out here...
		}
		
		int val;
		
		int hashf = hashfALPHA;

		if ((val = ProbeHash(depth, alpha, beta)) != UNKNOWN)
			return val;
	   
		List<Moveable> legalmoves = evaluator.generateLegalMoves();
		depth++;
		
		if (legalmoves.size() == 0 || (depth >= cutoffdepth))
		{
			val = evaluator.evaluate();
			long zobristKey = BoardArray.zobristKey;
			tt.recordTT(zobristKey, depth, HASHFEXACT, val, m);
			return evaluator.evaluate();
		}
		
		for (Moveable m : legalmoves)
		{
			evaluator.makeMove(m);
			try 
			{
				val = -alphaBeta(evaluator, depth, -beta, -alpha);
			} 
			catch (InterruptedException e)
			{
				evaluator.reverseMove(m);
				throw (new InterruptedException());
			}
			//long zobristKey = BoardArray.zobristKey;
			evaluator.reverseMove(m);
			
			if (beta <= val)
			{
				//tt.recordTT(zobristKey, depth, HASHFALPHA, alpha, m);
				return beta;
			}
	        if (val > alpha) 
	        {
	           // hashf = hashfEXACT;
	            alpha = val;
	        }	
		}
	    //RecordHash(depth, alpha, hashf);
	    return alpha;
	}
		
	int miniMax(Evaluator evaluator, int depth, int alpha, int beta) throws InterruptedException
	{
		if (canceled) 
		{
			System.err.println("Got a cancel, let's get out of here!");
			throw (new InterruptedException()); //Break out here...
		}
				
		List<Moveable> legalmoves = evaluator.generateLegalMoves();
		depth++;
		
		if (legalmoves.size() == 0 || (depth >= cutoffdepth))
		{
			//long zobristKey = BoardArray.zobristKey;
			//tt.recordTT(zobristKey, depth, HASHFEXACT, alpha, m);
			return evaluator.evaluate();
		}
		
		if (evaluator.isPlayerTurn())
		{
			for (Moveable m : legalmoves)
			{
				evaluator.makeMove(m);
				try 
				{
					beta = min(beta, miniMax(evaluator, depth, alpha, beta));
				} 
				catch (InterruptedException e) 
				{
					evaluator.reverseMove(m);
					throw (new InterruptedException());
				}
				//long zobristKey = BoardArray.zobristKey;
				evaluator.reverseMove(m);
				if (beta <= alpha)
				{
					//tt.recordTT(zobristKey, depth, HASHFALPHA, alpha, m);
					return alpha;
				}
			}
			//tt.recordTT(zobristKey, depth, HASHFBETA, beta, m);
			return beta;
		}
		else //We are to play at this node
		{
			for (Moveable m : legalmoves) 
			{
				evaluator.makeMove(m);
				try 
				{
					alpha = max(alpha, miniMax(evaluator, depth-1, alpha, beta));
				} 
				catch (InterruptedException e) 
				{
					evaluator.reverseMove(m);
					throw (new InterruptedException());
				}
				//long zobristKey = BoardArray.zobristKey;
				evaluator.reverseMove(m);
				if (alpha > beta)
				{
					//tt.recordTT(zobristKey, depth, HASHFBETA, beta, m);
					return beta;
				}
			}
			//tt.recordTT(zobristKey, depth, HASHFALPHA, alpha, m);
			return alpha;
		}
	}

	/** Returns a moveable of the next move that this Analyser thinks the 
	evaluateable board should do. */
	public Moveable getNextMove(Evaluator evaluator, int ply)
	{
		/* NH - Couldn't think of a better way to store the ply of MiniMax 
		without using a private variable. */
		this.cutoffdepth = ply;
		int alpha = 0-infinity;
		int beta = infinity;
		List<Moveable> moves = evaluator.generateLegalMoves();
		boolean humansturn = evaluator.isPlayerTurn();
		int best = (humansturn) ? infinity : -infinity;
		Moveable bestmove = null;
		int minimax = 0;
		for(Moveable move : moves)
		{
			if (canceled) 
			{
				break;
			}
			
			if(evaluator.makeMove(move))
			{
				try 
				{
					minimax = alphaBeta(evaluator, 1, alpha, beta); //If we throw an error, minimax isnt updated, problems for History Heuristic/Transposition Tables?
					
					// DG - Aspiration window
					if (minimax <= alpha || minimax >= beta)
					{
						minimax = alphaBeta(evaluator, 1, 0-infinity, infinity);
					}
					
					alpha = minimax + WINDOWSIZE;
					beta = minimax - WINDOWSIZE;
				} 
				catch (InterruptedException e) 
				{
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
		Analyser epsilon = new AlphaBetaAnalyser();
		Writeable writer = new DebugWriter("Marmoset!");
		
		for(int i = 0; i < 2; i++)
		{
			Moveable move = epsilon.getNextMove(omega, 5);
			if(omega.makeMove(move))
				writer.write(new MoveEvent(move));
		}
	}
}
