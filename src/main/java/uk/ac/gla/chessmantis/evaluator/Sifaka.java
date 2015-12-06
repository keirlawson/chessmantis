package uk.ac.gla.chessmantis.evaluator;

import uk.ac.gla.chessmantis.piece.Bishop;
import uk.ac.gla.chessmantis.piece.Knight;
import uk.ac.gla.chessmantis.piece.Pawn;
import uk.ac.gla.chessmantis.piece.Piece;

import java.util.*;

/**
 * @author NH && PD
 */
 
public class Sifaka extends Capuchin implements Evaluator
{
	/* Using these to allow me to easily tweak the balancing of the functions. */
	private static final int PQEQ8 = PV; //uk.ac.gla.chessmantis.piece.Pawn Quantity EQuals 8
	private static final int PQEQ7 = PV / 2; // uk.ac.gla.chessmantis.piece.Pawn Quantity EQuals 7
	private static final int CFEQ2 = PV; // Crowded Files EQuals 2
	private static final int CFGT2 = PV + (PV / 4) + (PV / 2); // Crowded Files Greater Than 2
	private static final int CFCP = PV / 2; // Crowded Files Crippled uk.ac.gla.chessmantis.piece.Pawn
	private static final int IP = PV / 2; // Isolated uk.ac.gla.chessmantis.piece.Pawn
	private static final int PP = PV; // Passed uk.ac.gla.chessmantis.piece.Pawn
	private static final int PAS = PV; // uk.ac.gla.chessmantis.piece.Pawn Advance static
	private static final float PAM = 0.3f; // uk.ac.gla.chessmantis.piece.Pawn Advance Multiplier
	private static final float PPM = 0.5f; // uk.ac.gla.chessmantis.piece.Piece Pairs Multiplier
	
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
	
	/** Evaluates pawn formation based on the idea that it is usually to a 
	players disadvantage to have 2 or more pawns in same file, as they hinder 
	each other's mobiltiy. Also checks if any of the Pawns are crippled pawns, 
	pawns that are on the outer files and thus only control 1 file each. */
	private int crowdedFiles(List<Integer> wp, List<Integer> bp)
	{
		int total = 0;
		final int FILES = 8;
		int blackFile[] = new int[FILES];
		int whiteFile[] = new int[FILES];
		
		Arrays.fill(blackFile, 0);
		Arrays.fill(whiteFile, 0);
		
		for(int pos : bp)
		{
			blackFile[(pos % 8)]++;
		}
		
		for(int pos : wp)
		{
			whiteFile[(pos % 8)]++;
		}
		
		for(int i = 0; i < FILES; i++)
		{
			if(blackFile[i] == 2)
			{
				total -= CFEQ2;
			}
			else if(blackFile[i] > 2)
			{
				total -= CFGT2;
			}
			
			if(whiteFile[i] == 2)
			{
				total += CFEQ2;
			}
			else if(whiteFile[i] > 2)
			{
				total += CFGT2;
			}
		}
		
		if(blackFile[0] > 0)
		{
			total -= CFCP;
		}
		
		if(blackFile[7] > 0)
		{
			total -= CFCP;
		}
		
		if(whiteFile[0] > 0)
		{
			total += CFCP;
		}
		
		if(whiteFile[7] > 0)
		{
			total += CFCP;
		}
		
		return total;
	}
	
	/** Takes into account of isolated pawns. There are 2 special cases to 
	consider: Pawns positioned on right and leftmost files. */
	private int isolatedPawns(List<Integer> wp, List<Integer> bp)
	{
		int total = 0;
		for(int pos : bp)
		{
			int modpos = pos % 8;
			if((modpos != 0) && (modpos != 7))//this ensures only general cases considered
			{
				if(!(((cb[(pos + 1)] instanceof Pawn) && !(cb[(pos + 1)].isWhite())) || ((cb[(pos - 1)] instanceof Pawn)&& !(cb[(pos - 1)].isWhite()))))
				{
					//this means pawn's isolated
					total -= IP;
				}
			}
			//now consider case when pawn is located on rightmost file
			if(modpos == 7)
			{
				if(!(cb[(pos - 1)] instanceof Pawn))
				{
					total -= IP;
				}
			}
			//now consider pawn on leftmost file
			if(modpos == 0)
			{
				if(!(cb[(pos + 1)] instanceof Pawn))
				{
					total -= IP;
				}
			}
		}
		
		//same as above but for white pawns
		for(int pos : wp)
		{
			int modpos = pos % 8;
			if((modpos != 0) && (modpos != 7))//this ensures only general cases considered
			{
				if(!(((cb[(pos + 1)] instanceof Pawn) && cb[(pos + 1)].isWhite())||((cb[(pos - 1)] instanceof Pawn)&& cb[(pos - 1)].isWhite())))
				{
					//this means pawn's isolated
					total += IP;
				}
			}
			//now consider case when pawn is located on rightmost file
			if(modpos == 7)
			{
				if(!(cb[(pos - 1)] instanceof Pawn))
				{
					total += IP;
				}
			}
			//now consider pawn on leftmost file
			if(modpos == 0)
			{
			if(!(cb[(pos + 1)] instanceof Pawn))
				{
					total += IP;
				}
			}
		}
		return total;
	}
	
	/** The following function determines whether or not there are any passed 
	pawns i.e a pawn which has progressed up the board to a point where it has 
	passed all other opposition pawns. */
	private int passedPawns(List<Integer> wp, List<Integer> bp)
	{
		int total = 0;
		//consider black pawns first
		for(int bpos : bp)
		{
			boolean passed = true;
			for(int wpos : wp)
			{
				passed = ((bpos / 8) < (wpos / 8));
			}
			if(passed)
			{
				total += PP;
			}
		}
		
		//now check for passed white pawns
		for(int wpos : wp)
		{
			boolean passed = true;
			for(int bpos : bp)
			{
				passed = ((wpos / 8) > (bpos / 8));
			}
			if(passed)
			{
				total -= PP;
			}
		}
		
		return total;
	}
	
	/** pawnAdvance tries to force the pawns to leave their starting positions
	to allow pieces behind them to open up. */
	private int pawnAdvance(List<Integer> wp, List<Integer> bp)
	{
		int total = 0;
		for(int i : wp)
			if((i / 8) == 2)
				total -= PAS;
		for(int i : bp)
			if((i / 8) == 12)
				total += PAS;
		total *= PAM;
		return total;
	}
	
	private int pawnFormation()
	{
		List<Integer> blackpawns = new ArrayList<Integer>();
		List<Integer> whitepawns = new ArrayList<Integer>();
		int total = 0;
		for(int i = 0; i < cb.length; i++)
		{
			if(cb[i] != null)
			{
				if(cb[i] instanceof Pawn)
				{
					if(cb[i].isWhite())
					{
						whitepawns.add(i);
					}
					else
					{
						blackpawns.add(i);
					}
				}
			}
		}
		
		total += pawnQuantity(whitepawns, blackpawns);
		total += crowdedFiles(whitepawns, blackpawns);
		total += isolatedPawns(whitepawns, blackpawns);
		total += passedPawns(whitepawns, blackpawns);
		total += pawnAdvance(whitepawns, blackpawns);
		return total;
	}
	
	private int piecePairs()
	{
		int wk = 0, vwk = 0, wb = 0, vwb = 0;
		int bk = 0, vbk = 0, bb = 0, vbb = 0;
		
		for(int i = 0; i < cb.length; i++)
		{
			Piece p = cb[i];
			if(p != null)
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
		}
		
		if(wk == 2)
		{
			vwk *= PPM;
		}
		
		if(bk == 2)
		{
			vbk *= PPM;
		}
		
		if(wb == 2)
		{
			vwb *= PPM;
		}
		
		if(bb == 2)
		{
			vbb *= PPM;
		}
		return ((vbb + vbk) - (vwb + vwk));
	}
	
	public int evaluate()
	{
		int total = 0;
		total += super.evaluate();
		total += pawnFormation();
		total += piecePairs();
		return total;
	}
}
