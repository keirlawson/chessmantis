package uk.ac.gla.chessmantis; /**
 * uk.ac.gla.chessmantis.Board interface
 * @author TT and DG
 */

import java.util.*;

public interface Board
{
	boolean makeMove(Moveable m);
	boolean reverseMove(Moveable m);
	boolean isStalemate();
	boolean isCheckmate();
	boolean isInCheck(boolean colour);
	boolean isPlayerTurn();
	List<Moveable> generateLegalMoves();
}