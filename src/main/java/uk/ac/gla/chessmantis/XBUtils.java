package uk.ac.gla.chessmantis;

import uk.ac.gla.chessmantis.piece.*;

public class XBUtils
{
	/*The below conversion functions do not take into account castling or promotions*/
	private static int getNumber(char letter)
	{
		return (letter - 'a');
	}
	
	private static char getCharacter(int number)
	{
		return (char) ('a' + number);
	}

	private static boolean isLegalMove(final char[] moveArray) {
		boolean firstLegal = moveArray[0] >= 'a' && moveArray[0] <= 'h';
		boolean secondLegal = moveArray[1] >= '1' && moveArray[1] <= '8';
		boolean thirdLegal = moveArray[2] >= 'a' && moveArray[2] <= 'h';
		boolean fourthLegal = moveArray[3] >= '1' && moveArray[3] <= '8';

		return firstLegal && secondLegal && thirdLegal && fourthLegal;
	}

	/** Static method to convert a string representation of a uk.ac.gla.chessmantis.Move into an
	actual move object. */
	public static Moveable stringToMove(final String st)
	{
		char[] s = st.toCharArray();

		if (!isLegalMove(s)) {
			//FIXME Should throw an IlegalMoveException here, havent coded it yet though
			return null;
		}

		int from = getNumber(s[0]) + ((Integer.parseInt("" + s[1]) - 1) * 8);
		int to = getNumber(s[2]) + ((Integer.parseInt("" + s[3]) - 1) * 8);
		Moveable m = new Move(from, to);
		if (s.length > 4) {//If it looks like a promotion
			switch(s[4]) {
				case 'q':
					m.setPromotionFigure(new Queen());
					break;
				case 'r':
					m.setPromotionFigure(new Rook());
					break;
				case 'n':
					m.setPromotionFigure(new Knight());
					break;
				case 'b':
					m.setPromotionFigure(new Bishop());
					break;
				default:
					break;
			}
		}
		return m;
	}
	
	/** Static method to convert a uk.ac.gla.chessmantis.Move object back into its Chess String
	representation. */
	static String moveToString(Moveable m)
	{
		// getFromPosition 8=a2, 18=c3
		char one = getCharacter(m.getFromPosition() % 8);
		int two = ((m.getFromPosition() / 8) + 1);
		char three = getCharacter(m.getToPosition() % 8);
		int four = ((m.getToPosition() / 8) + 1);
		String movestring;
		if (m.isPromotion()) 
		{
			char five;
			Piece promo = m.getPromotionFigure();
			if (promo instanceof Knight)
			{
				five = 'n';
			}
			else if (promo instanceof Queen)
			{
				five = 'q';
			}
			else if (promo instanceof Bishop)
			{
				five = 'b';
			}
			else
			{
				five = 'r';
			}
			movestring = "" + one + two + three + four + five;
		}
		else
		{
			movestring = "" + one + two + three + four;
		}
		return movestring;
	}
}
