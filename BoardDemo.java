/**
 * 
 */

/**
 * @author icelook
 *
 */

import java.util.*;

public class BoardDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Board cb = new BoardArray();
		List<Moveable> moves;
		Scanner scanner = new Scanner(System.in);
		int from, to;
		while (true)
		{
			moves = cb.generateLegalMoves();
			System.out.println("Generated "+ moves.size() + " moves");
			for (Moveable move : moves)
			{
				System.out.printf("Move: %d %d\n", move.getFromPosition(),
						move.getToPosition());
			}
			System.out.print("Enter your move: ");
			from = scanner.nextInt();
			to = scanner.nextInt();
			cb.makeMove(new Move(from, to));
		}
		
	}

}
