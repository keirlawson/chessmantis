package uk.ac.gla.chessmantis;

import uk.ac.gla.chessmantis.piece.Piece;

/**
 * @author Tamerlan Tajaddinov
 * @version 1.1
 */

// DG - added castling support for hashing

public class Move implements Moveable
{
	private int from, to,capturePos;
	private Piece capturedFigure, promotionFigure;
	private boolean capture, promotion, castling;
	private int rookFrom, rookTo;
	
	/**
	 * Constructor
	 * @param from initial position on the board
	 * @param to final position on the board
	 */
	public Move(int from, int to)
	{
		this.from = from;
		this.to = to;
		capture = false;
		promotion = false;
		castling = false;
	}
	
	//PD overloaded this method to deal with the case when the capture
	// happened 'en- passant'. In such a case the captured piece does not
	// come from the square in the post position on the board.
	
	public boolean setCapturedFigure(Piece p, int capturePos)
	{
		this.capturedFigure = p;
		capture = true;
		this.capturePos=capturePos;
		return true;
	}
	
	public boolean setCapturedFigure(Piece p)
	{
		return setCapturedFigure(p,-1);
		/* this.capturedFigure = p;
		capture = true;*/
		
	}
	
	
	
	public boolean isCapture()
	{
		return capture;
	}
	
	// DG - castle support return info
	public boolean isCastling()
	{
		return castling;
	}
	
	// DG - castle support set
	public void setCastling(boolean castling, int rookFrom, int rookTo)
	{
		this.castling = castling;
		this.rookFrom = rookFrom; 
		this.rookTo = rookTo;
	}
	
	// DG - get rook initial position for castling
	public int getRookFrom ()
	{
		return this.rookFrom;
	}
	
	// DG - get rook post position for castling
	public int getRookTo ()
	{
		return this.rookTo;
	}
	
	public boolean isPromotion()
	{
		return promotion;
	}

	public boolean setPromotionFigure(Piece p)
	{
		this.promotionFigure = p;
		promotion = true;
		return true;
	}
	
	public Piece getPromotionFigure()
	{
		return promotion?promotionFigure:null;
	}
	
	public Piece getCapturedFigure()
	{
		return capture?capturedFigure:null;
	}
	
	public int getFromPosition()
	{
		return from;
	}
	
	public int getToPosition()
	{
		return to;
	}
	
	public int getCapturePos()
	{
		return capturePos;
	}
	
}