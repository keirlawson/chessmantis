package uk.ac.gla.chessmantis;

import uk.ac.gla.chessmantis.piece.Piece;

/**
 * @author Tamerlan Tajaddin
 * @version 1.1
 * uk.ac.gla.chessmantis.Moveable interfaces
 */

public interface Moveable
{
	boolean setCapturedFigure(Piece p);
	boolean setCapturedFigure(Piece p,int capturePos);
	boolean isCapture();
	boolean isPromotion();
	boolean setPromotionFigure(Piece p);
	Piece getPromotionFigure();
	Piece getCapturedFigure();
	public boolean isCastling();
	void setCastling(boolean castling, int rookFrom, int rookTo);
	int getRookFrom ();	
	int getRookTo ();
	int getFromPosition();
	int getToPosition();
	int getCapturePos();
}