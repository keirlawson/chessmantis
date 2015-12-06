import java.util.*;
import java.lang.Math;

/**
  * @author NH, PD & TT
  */

public class Mantis extends BoardArray implements Evaluator
{
	/**
	 * modifier
	 * @author TT
	 * @param degram sets the degree of randomness, 0 to deactivate
	 */
	public void setRandom (int degran)
	{
		this.degran = degran;
	}
	
	public Mantis ()
	{
		System.err.println("Evaluating with Mantis");
		degran = 0;
	}
	private static final int INF = 100000000;
	
	/* Using these to allow me to easily tweak the balancing of the functions. */
	private static final int PV = (new Pawn(WHITE)).getValue();
	private static final int PQEQ8 = PV / 10; // Pawn Quantity EQuals 8
	private static final int PQEQ7 = PV / 20; // Pawn Quantity EQuals 7
	private static final int CFEQ2 = PV / 2; // Crowded Files EQuals 2
	private static final int CFGT2 = PV; // Crowded Files Greater Than 2
	private static final int CFCP = PV / 4; // Crowded Files Crippled Pawn
	private static final float PPM = 0.25f; // Piece Pairs Multiplier
	private static final int KAFA = PV / 50; // King Attack First Aura
	private static final int KASA = PV / 100; // King Attack Second Aura
	private static final int CIC = PV / 20; // Center Inner Center
	private static final int COC = PV / 30; // Center Outer Center
	private static final int FM = 5; // Freedom Multiplier
	private static final int KC = 50; // King Center.
	private static final int KP = 8; // King Pawns;
	private static final int IP = 8; // King Pawns;
	
	private int degran; //sets the degree of randomness
	
	
	private static double multendgame = 1.0; // Used to slightly change values in the end game
	
	/** Checkmate Check, gives a bigger score for a checkmate to encourage the 
	engine to look for checkmates. */
	private int checkmate()
	{
		boolean colour = isPlayerTurn();
		int ret = 0;
		setIsPlayerTurn(BLACK);
		ret -= (isCheckmate()) ? INF : 0;
		setIsPlayerTurn(WHITE);
		ret += (isCheckmate()) ? INF : 0;
		setIsPlayerTurn(colour);
		return ret;
	}
	
	private static final int[] FIRSTAURA = {-1, -15, -16, -17, 1, 15, 16, 17};
	private static final int[] SECONDAURA = {-2, -14, -18, -30, -31, -32, -33, -34, 2, 14, 18, 30, 31, 32, 33, 34};
	private static final int[] INNERCENTER = {51, 52, 67, 68};
	private static final int[] OUTERCENTER = {34, 35, 36, 37, 50, 53, 66, 69, 82, 83, 84, 85};

	/** positioning calculates two things. Firstly if any pieces have a view on 
	the center of the board, and secondly if any pieces have a view on the king.
	Also calculates if the King is in the center of the board, which is bad at 
	the start of the game, but good at the end, and gives a bonus or penalty for
	this. */
	private int positioning(List<Integer> whitemoves, List<Integer> blackmoves)
	{
		int total = 0;
		for(int pos : blackmoves)
		{
			for(int i : INNERCENTER)
				if(pos == i)
					total += CIC;
			for(int i : OUTERCENTER)
				if(pos == i)
					total += COC;
			for(int i : FIRSTAURA)
				if((pos == (whiteKing - i)) || (pos == (whiteKing + i)))
					total += KAFA;
			for(int i : SECONDAURA)
				if((pos == (whiteKing - i)) || (pos == (whiteKing + i)))
					total += KASA;
		}
		for(int pos : whitemoves)
		{
			for(int i : INNERCENTER)
				if(pos == i)
					total -= CIC;
			for(int i : OUTERCENTER)
				if(pos == i)
					total -= COC;
			for(int i : FIRSTAURA)
				if((pos == (blackKing - i)) || (pos == (blackKing + i)))
					total -= KAFA;
			for(int i : SECONDAURA)
				if((pos == (blackKing - i)) || (pos == (blackKing + i)))
					total -= KASA;
		}
		
		boolean blackcenter = false;
		boolean whitecenter = false;
		for(int i : INNERCENTER)
		{
			if(i == blackKing)
				blackcenter = true;
			if(i == whiteKing)
				whitecenter = true;
		}
		
		for(int i : OUTERCENTER)
		{
			if(i == blackKing)
				blackcenter = true;
			if(i == whiteKing)
				whitecenter = true;
		}
			
		if(blackcenter)
			total += (0.5 - multendgame) * KC;
		if(whitecenter)
			total -= (0.5 - multendgame) * KC; 
		
		return total;
	}
	
	/** calculates how 'free' each side of the board is, and multiplies this 
	difference by a constant to give boards bonus's for having more freedom 
	with their pieces. */
	private int freedom(List<Integer> whitemoves, List<Integer> blackmoves)
	{
		return ((blackmoves.size() - whitemoves.size()) * FM);
	}
	
	/** The function deals with pawn quantity. The idea is that, in most 
	situations, it is a slight disadvantage to have too many pawns on board as 
	this restricts movement of other pieces. */
	private int pawnQuantity(List<Integer> wp, List<Integer> bp)
	{
		int total = 0;
		switch(bp.size())
		{
		case 8:
			total -= PQEQ8;
			break;
		case 7:
			total -= PQEQ7;
			break;
		default:
			break;
		}
		switch(wp.size())
		{
		case 8:
			total += PQEQ8;
			break;
		case 7:
			total += PQEQ7;
			break;
		default:
			break;
		}
		return total;
	}
	
	/** Emalgamation of 4 other pawn functions to try and gain extra efficieny. 
	Calculates;
	Crowded Files - Ensures pawns try to stay out of each others files if possible.
	Passed Pawns - Calculates whether any pawns are passed the opposing players pawns.
	Isolated Pawns = Calculates whether a pawn has any protection from neighbouring files. Should also satisfy Pawn Islands.
	King Pawns - Calculates whether the King has any pawns of its own colour protecting it.
	*/
	private int pawnFormation(List<Integer> wp, List<Integer> bp)
	{
		int blackFile[] = new int[8];
		int whiteFile[] = new int[8];
		Arrays.fill(blackFile, 0);
		Arrays.fill(whiteFile, 0);
		boolean passed;
		int modpos;
		int divpos;
		
		int total = 0;
		for(int pos : bp)
		{
			modpos = pos % 8;
			divpos = pos / 8;
			blackFile[modpos]++;
			
			for(int i : FIRSTAURA)
				if((pos == (blackKing - i)) || (pos == (blackKing + i)))
					total += KP;
			
			for(int i : bp)
				if(((i % 8) == (modpos - 1)) || ((i % 8) == (modpos + 1)))
					total += IP;
			
			passed = true;
			for(int wpos : wp)
				passed = (divpos < (wpos / 8));
			if(passed)
				total += divpos / multendgame;
		}
		for(int pos : wp)
		{
			modpos = pos % 8;
			divpos = pos / 8;
			whiteFile[modpos]++;
			
			for(int i : FIRSTAURA)
				if((pos == (whiteKing - i)) || (pos == (whiteKing + i)))
					total -= KP;
			
			for(int i : wp)
				if(((i % 8) == (modpos - 1)) || ((i % 8) == (modpos + 1)))
					total -= IP;
			
			passed = true;
			for(int bpos : bp)
				passed = (divpos > (bpos / 8));
			if(passed)
				total -= divpos / multendgame;
		}
		for(int i = 0; i < 8; i++)
		{
			if(blackFile[i] == 2)
				total -= CFEQ2;
			else if(blackFile[i] > 2)
				total -= CFGT2;
			if(whiteFile[i] == 2)
				total += CFEQ2;
			else if(whiteFile[i] > 2)
				total += CFGT2;
		}
		if(blackFile[0] > 0)
			total -= CFCP;
		if(blackFile[7] > 0)
			total -= CFCP;
		if(whiteFile[0] > 0)
			total += CFCP;
		if(whiteFile[7] > 0)
			total += CFCP;
		
		return total;
	}
	
	private int piecePairs(List<Piece> pieces)
	{
		int wk = 0, vwk = 0, wb = 0, vwb = 0;
		int bk = 0, vbk = 0, bb = 0, vbb = 0;
		
		for(Piece p : pieces)
		{
			if(p instanceof Knight)
			{
				if(p.isWhite())
				{
					wk++;
					vwk += p.getValue();
				}
				else
				{
					bk++;
					vbk += p.getValue();
				}
			}
			else if(p instanceof Bishop)
			{
				if(p.isWhite())
				{
					wb++;
					vwb += p.getValue();
				}
				else
				{
					bb++;
					vbb += p.getValue();
				}
			}
		}
		if(wk == 2)
			vwk *= PPM;
		if(bk == 2)
			vbk *= PPM;
		if(wb == 2)
			vwb *= PPM;
		if(bb == 2)
			vbb *= PPM;
		return ((vbb + vbk) - (vwb + vwk));
	}
	
	public int evaluate()
	{
		List<Piece> knightbishop = new ArrayList<Piece>();
		List<Integer> blackpawns = new ArrayList<Integer>();
		List<Integer> whitepawns = new ArrayList<Integer>();
		List<Integer> whitemoves = new ArrayList<Integer>();
		List<Integer> blackmoves = new ArrayList<Integer>();
		
		int white = 0;
		int black = 0;
		int val;
		for(int i = 0; i < cb.length; i++)
		{
			if(cb[i] != null)
			{
				if(cb[i] instanceof Knight || cb[i] instanceof Bishop)
					knightbishop.add(cb[i]);
				List<Integer> deltas = cb[i].getDeltas();
				if(cb[i].isWhite())
				{
					white += cb[i].getValue();
					if(cb[i] instanceof Pawn)
						whitepawns.add(i);
					for(int delta : deltas)
					{
						val = i - delta;
						if((val >= 0) && ((val & 8) != 8) && ((cb[val]==null) || (cb[val].isWhite()!=cb[i].isWhite())) && ((cb[i] instanceof Knight) || (!isPathBlocked(i, val))))
							whitemoves.add(val);
						val = i + delta;
						if((val <= 127) && ((val & 8) != 8) && ((cb[val]==null) || (cb[val].isWhite()!=cb[i].isWhite())) && ((cb[i] instanceof Knight) || (!isPathBlocked(i, val))))
							whitemoves.add(val);
					}
				}
				else
				{
					black += cb[i].getValue();
					if(cb[i] instanceof Pawn)
						blackpawns.add(i);
					for(int delta : deltas)
					{
						val = i - delta;
						if((val >= 0) && ((val & 8) != 8) && ((cb[val]==null) || (cb[val].isWhite()!=cb[i].isWhite())) && ((cb[i] instanceof Knight) || (!isPathBlocked(i, val))))
							blackmoves.add(val);
						val = i + delta;
						if((val <= 127) && ((val & 8) != 8) && ((cb[val]==null) || (cb[val].isWhite()!=cb[i].isWhite())) && ((cb[i] instanceof Knight) || (!isPathBlocked(i, val))))
							blackmoves.add(val);
					}
				}
			}
		}
		
		multendgame = ((double) (black + white)) / 10000.0;
		
		int total = checkmate();
		
		if(total != 0)
			return total;
		
		total += black - white;
		total += pawnQuantity(whitepawns, blackpawns);
		total += pawnFormation(whitepawns, blackpawns);
		total += piecePairs(knightbishop);
		total += positioning(whitemoves, blackmoves);
		total += freedom(whitemoves, blackmoves);
		total += (Math.random() * degran);
		return total;
	}
}
