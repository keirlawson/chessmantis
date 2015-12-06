import java.util.*;

/**
 * @author NH
 */

public class Macaque extends Sifaka implements Evaluator
{
	/* Using these to allow me to easily tweak the balancing of the functions. */
	private static final int CM = PV / 2;
	private static final int KAFA = PV / 10;
	private static final int KASA = PV / 20;
	private static final int CIC = PV / 10;
	private static final int COC = PV / 20;
	private static final int FM = 5;
	
	/** Checkmate Check, gives a bigger score for a checkmate to encourage the 
	engine to look for checkmates. */
	private int checkmate()
	{
		boolean colour = isPlayerTurn();
		int ret = 0;
		setIsPlayerTurn(BLACK);
		ret -= (isCheckmate()) ? CM : 0;
		setIsPlayerTurn(WHITE);
		ret += (isCheckmate()) ? CM : 0;
		setIsPlayerTurn(colour);
		return ret;
	}
	
	private static final int[] FIRSTAURA = {-1, -15, -16, -17, 1, 15, 16, 17};
	private static final int[] SECONDAURA = {-2, -14, -18, -30, -31, -32, -33, -34, 2, 14, 18, 30, 31, 32, 33, 34};
	/** kingAttack tries to force pieces to set up for an attack on the king, 
	which will sometimes encourage the engine to sacrifice pieces in order to 
	achieve openings on the king. */
	private int kingAttack(Integer[] whitemoves, Integer[] blackmoves)
	{
		int ret = 0;
		for(int pos : blackmoves)
		{
			for(int i : FIRSTAURA)
				if((pos == (blackKing - i)) || (pos == (blackKing + i)))
					ret += KAFA;
			for(int i : SECONDAURA)
			{
				if((pos == (blackKing - i)) || (pos == (blackKing + i)))
					ret += KASA;
			}
		}
		for(int pos : whitemoves)
		{
			for(int i : FIRSTAURA)
				if((pos == (whiteKing - i)) || (pos == (whiteKing + i)))
					ret -= KAFA;
			for(int i : SECONDAURA)
				if((pos == (whiteKing - i)) || (pos == (whiteKing + i)))
					ret -= KASA;
		}
		return ret;
	}
	
	private static final int[] INNERCENTER = {51, 52, 67, 68};
	private static final int[] OUTERCENTER = {34, 35, 36, 37, 50, 53, 66, 69, 82, 83, 84, 85};
	/** center calculates what center squares pieces on the board control. */
	private int center(Integer[] whitemoves, Integer[] blackmoves)
	{
		/* NH - Had to change this over to using moves and checking whether the 
		from or the to was in the desired range. Had to move this away from 
		Tam's implementation because his used whiteToPlay(), which I had forgot 
		to tell him we shouldn't use! */
		
		int total = 0;
		for(int pos : blackmoves)
		{
			for(int i : INNERCENTER)
				if(pos == i)
					total += CIC;
			for(int i : OUTERCENTER)
				if(pos == i)
					total += COC;
		}
		for(int pos : whitemoves)
		{
			for(int i : INNERCENTER)
				if(pos == i)
					total -= CIC;
			for(int i : OUTERCENTER)
				if(pos == i)
					total -= COC;
		}
		return total;
	}
	
	/** calculates how 'free' each side of the board is, and multiplies this 
	difference by a constant to give boards bonus's for having more freedom 
	with their pieces. */
	private int freedom(Integer[] whitemoves, Integer[] blackmoves)
	{
		return ((blackmoves.length - whitemoves.length) * FM);
	}
	
	/** positioning calculates where each of the pieces for either side can 
	move to, and then carries out the functions that rely on these moves. */
	private int positioning()
	{
		ArrayList<Integer> white = new ArrayList<Integer>();
		ArrayList<Integer> black = new ArrayList<Integer>();
		for(int i = 0; i < cb.length; i++)
		{
			if(cb[i] != null)
			{
				if(cb[i].isWhite() == WHITE)
					white.add(i);
				else
					black.add(i);
				
				List<Integer> deltas = cb[i].getDeltas();
				for(int delta : deltas)
				{
					int small = i - delta;
					if((0 <= small) && (small <= 127) && ((small & 8) != 8))
					{
						if(cb[i].isWhite() == WHITE)
							white.add(small);
						else
							black.add(small);
					}
					
					int large = i + delta;
					if((0 <= large) && (large <= 127) && ((large & 8) != 8))
					{
						if(cb[i].isWhite() == WHITE)
							white.add(large);
						else
							black.add(large);
					}
				}
			}
		}
		Integer[] whitemoves = white.toArray(new Integer[0]);
		Integer[] blackmoves = black.toArray(new Integer[0]);
		
		int ret = 0;
		ret += kingAttack(whitemoves, blackmoves);
		ret += center(whitemoves, blackmoves);
		ret += freedom(whitemoves, blackmoves);
		return ret;
	}
	
	public int evaluate()
	{
		int total = 0;
		total += super.evaluate();
		total += checkmate();
		total += positioning();
		return total;
	}
}
