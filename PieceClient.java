/**
 * 
 */

/**
 * @author icelook
 *
 */
public class PieceClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Piece p = new Pawn(BoardArray.WHITE);
		for (Integer i : p.getDeltas())
			System.out.println("Pawn delta: " + i);
		p = new Rook(true);
		for (Integer i : p.getDeltas())
			System.out.println("Rook delta: " + i);
		p = new Bishop(true);
		for (Integer i : p.getDeltas())
			System.out.println("Bishop delta: " + i);
		p = new Knight(true);
		for (Integer i : p.getDeltas())
			System.out.println("Knight delta: " + i);
		p = new King(true);
		for (Integer i : p.getDeltas())
			System.out.println("King delta: " + i);
		p = new Queen(true);
		for (Integer i : p.getDeltas())
			System.out.println("Queen delta: " + i);
		

	}

}
