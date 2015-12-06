/**
 * @author TT & DG
 *
*/

import java.util.*;


public class BoardArray implements Board
{
    private static final int MAXSQUARES = 128; 
    protected Piece[] cb;
    public static final boolean WHITE = true;
    public static final boolean BLACK = false;
    private boolean isPlayerTurn;
    protected int whiteKing, blackKing;
    private List<Moveable> currentMoves = new ArrayList<Moveable>();
    private int plyPlayed = 0; //TT private state corresponding to the number of plies playe
    
    private int err = 0; //TT private state for checking the number of calls to routines
    
    // Zobrist hashing related fields
	private long [][][] zPieces; // DG - piece-type, side to move, square
    
	private long zobristKey;
	private long zCastlingRights; // DG - random key indicating castling
	private long zEnPassant; // DG - random key indicating en passant
	private long zSide; // DG - random key indicating black move
	private long zPromotion; // DG - random key indicating promotion
	private long zCapture; // DG - random key indicating capture
	
	private final int PIECETYPE = 7;
	private final int SIDETOMOVE = 2;
	private final int EMPTY_SQUARE = 0;
	
	// Transposition table related fields
	
	static private int oldNode;
	
    /**
     * accessor
     * @author TT
     * @return returns the number of plies that have been played
     */
    public int getPlyPlayed()
    {
    	return plyPlayed;
    }
    
	/**
	 * constructor
	 * initializes the board to the normal starting state
	 * @author TT & DG
	 */
	public BoardArray()
	{
		cb  =   new Piece[MAXSQUARES];
		cb [0] = new Rook(WHITE);
		cb [1] = new Knight(WHITE);
		cb [2] = new Bishop(WHITE);
		cb [3] = new Queen(WHITE);
		cb [4] = new King(WHITE);
		cb [5] = new Bishop(WHITE);
		cb [6] = new Knight(WHITE);
		cb [7] = new Rook(WHITE);
		
		for (int i=16; i<24; i++)
			cb[i] = new Pawn(WHITE);
		
		cb [112] = new Rook(BLACK);
		cb [113] = new Knight(BLACK);
		cb [114] = new Bishop(BLACK);
		cb [115] = new Queen(BLACK);
		cb [116] = new King(BLACK);
		cb [117] = new Bishop(BLACK);
		cb [118] = new Knight(BLACK);
		cb [119] = new Rook(BLACK);
		
		for (int i=96; i<104; i++)
			cb[i] = new Pawn(BLACK);
		
		whiteKing = 4;
		blackKing = 116;
		
		
		isPlayerTurn = true;
		oldNode = -1;
		generateHashCodes();
	}
	
	/**
	 * constructor
	 * initializes the board to a custom state
	 * piece placement
	 * @author TT
	 */
	public BoardArray(Piece[] pieces)
	{
		cb  =   new Piece[MAXSQUARES];
		for (int i = 0; i < 64; i++)
		{
			cb[small2big(i)]=pieces[i];
		}
		oldNode = -1;
		generateHashCodes();
	}
	
	/**
	 * constructor
	 * @author - AM
	 * @param setup Marks out pieces' position on board denoting each piece from a8 along rank then file including empty space.  Black pieces are lowercase, white are uppercase.
	 * Initial board would look like this: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
	 * 
	 * AM - Currently not working - It works, I've just to get the numbers right
	 */
	public void setBoard(String setup)
	{
		cb = new Piece[MAXSQUARES];
		setup = setup.replaceAll("/", "");
		// Get the last character and cut it off the string
		Character p = setup.charAt(0);
		setup = setup.substring(1, setup.length());
		// System.err.println("Dealing with character: " + p);
		// System.err.println("setup string looks like this: " + setup);
		System.err.println("Got Here");
		// For each square on the board
		for (int i=112; i > -1; i=i-16)
		{
			for (int j=i; j < i+8; j++)
			{
				// If the character is a number denoting an empty space
				if ((p >= '0') && (p <= '9'))
				{
					// Step over this space
					if (p == '1')
					{
						p = setup.charAt(0);
						setup = setup.substring(1, setup.length());
					}
					else
					{
						// Reduce the number by 1
						p = ((Integer) (Integer.parseInt(p.toString()) - 1)).toString().charAt(0);
					}
				}
				else if (p == ' ')
				{
					return;
				}
				else
				{
					switch (p)
					{
						// rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
						case 'r':
							cb[j] = new Rook(BLACK);
							System.err.println(j + " is Black Rook");
							break;
						case 'n':
							cb[j] = new Knight(BLACK);
							System.err.println(j + " is Black Knight");
							break;
						case 'b':
							cb[j] = new Bishop(BLACK);
							System.err.println(j + " is Black Bishop");
							break;
						case 'q':
							cb[j] = new Queen(BLACK);
							System.err.println(j + " is Black Queen");
							break;
						case 'k':
							cb[j] = new King(BLACK);
							System.err.println(j + " is Black King");
							break;
						case 'p':
							cb[j] = new Pawn(BLACK);
							System.err.println(j + " is Black Pawn");
							break;
						case 'R':
							cb[j] = new Rook(WHITE);
							System.err.println(j + " is White Rook");
							break;
						case 'N':
							cb[j] = new Knight(WHITE);
							System.err.println(j + " is White Knight");
							break;
						case 'B':
							cb[j] = new Bishop(WHITE);
							System.err.println(j + " is White Bishop");
							break;
						case 'Q':
							cb[j] = new Queen(WHITE);
							System.err.println(j + " is White Queen");
							break;
						case 'K':
							cb[j] = new King(WHITE);
							System.err.println(j + " is White King");
							break;
						case 'P':
							cb[j] = new Pawn(WHITE);
							System.err.println(j + " is White Pawn");
							break;
					}
					// Get the next character
					if (setup.length() > 0)
					{
						System.out.println(setup.length());
						System.out.println("String: " + setup);
						p = setup.charAt(0);
						setup = setup.substring(1, setup.length());
					}
				}
			}
		}

		
		whiteKing = 4;
		blackKing = 116;
		
		isPlayerTurn = true;
		oldNode = -1;
		generateHashCodes();
	}
	
	/**
	 * @author TT & DG
	 * @param Moveable m - move, that assumes a 64 square representation
	 * @return true if the move seems to be legal
	 * @see Board#IsLegalMove(Moveable)
	 * NB this doesn't check if this leaves the side checked
	 */
	private boolean isLegalMove(Moveable m)
	{
		int posf = small2big(m.getFromPosition());
		int post = small2big(m.getToPosition());
		
		// The piece is a king, and it's moving 2 left or right
		
		if ((post & 8) == 8) // TT binary check for out of boundaries positions
			return false;

		if (post == posf)	// DG if the piece is moved to the same field
			return false;

		if (!(cb[posf].isWhite()==isPlayerTurn)) // DG if the piece of the wrong colour moved
			return false;

		if ((cb[post]!=null) && (cb[post].isWhite()==cb[posf].isWhite()))
			return false; // TT return false if trying to capture own figure

		int delta128 = post - posf;
		
		if (!cb[posf].isLegalMove(delta128)) // DG if incorrect movement pattern for the piece, return false
			return false;
		if (cb[posf] instanceof Pawn) // DG Check if pawn is trying to "capture" empty field or capture figure by moving forward
		{
			int adelta = Math.abs(delta128);
			if (cb[post]==null && (adelta == 15 || adelta == 17)) // DG Check on "capturing" empty field

				return false;
			if (cb[post]!=null && ((adelta == 16) || (adelta == 32))) // DG Check on "capturing" piece by moving forward
				return false;
		}

		if (!(cb[posf] instanceof Knight)) 		// DG Check for blocked way (does not concern knights)
			return (!isPathBlocked(posf, post));
		
		// Check for king trying to move into check
		/*
		if (isInCheck(isPlayerTurn))
				return false;
		*/
		
		return true;
	}

	/* (non-Javadoc)
	 * @see Board#isCheckmate()
	 */
	public boolean isCheckmate()
	{
		// TODO Auto-generated method stub
	    if(currentMoves.isEmpty() && isInCheck(isPlayerTurn))
	    {
	    	// System.err.println("Checkmate: ");
	    	// printBoard();
			return true;
	    }
	    
	    return false;
	       
	}
	
	/**
	 * Accessor
	 * @author TT
	 * @param colour - true if checking if white is in check, false otherwise
	 * @see Board#isInCheck(boolean)
	 * @return true if the side is in check, false otherwise
	 * Works in the following way:
	 * 1) pretend it's the other side's turn to play
	 * 2) try to capture own king
	 * 3) if successful then in check, false otherwise
	 */
	public boolean isInCheck(boolean colour)
	{
		// System.err.println("isInCheck1");
		return isInCheck(colour, -1, -1);
	}
	
	/**
	 * @author AM PD
	 * @param colour
	 * @param post
	 * @return
	 * Say whether the board would be in check for the king moving to a square
	 */
	public boolean isInCheck(boolean colour, int post)
	{
		//System.err.println("isInCheck2");
		return isInCheck(colour, post, -1);
	}
	/**
	 * Accessor
	 * @author AM, PD
	 * @param colour - true if checking if white is in check, false otherwise
	 * @param bigpos - Big board integer position (-1 for current colour king)
	 * @see BoardArray#isInCheck(boolean)
	 * @return true if the side is in check, false otherwise
	 * Allows InCheck checking of an arbitrary square
	 * Can do 3 things:
	 * 		Say whether the board is currently in check - colour, -1, false
	 * 		Say whether the board would be in check for the king moving to a square - colour, kingToPosition, true
	 * 		Say whether the board would be in check for a piece moving to a square - colour, pieceToPosition, false
	 */
	public boolean isInCheck(boolean colour, int post, int posf)
	{
		// System.err.println("post: " + post + ", posf: " + posf);
		isPlayerTurn = !colour;
		Moveable m;
		int t;
		int king;
		boolean pieceMoved = false; // Something other than the king has been shifted
		/* TT Position of the King corresponding to
		 * the concerned side in the 64 square notation */
		if (post > 0)
		{
			// System.err.println("isInCheck: Got here 1");
			if (posf > 0)
			{
				// System.err.println("isInCheck: Got here 2");
				// Move the piece in posf to post
				System.err.println("post: " + post + " posf: " + posf);
				cb[post] = cb[posf];
				cb[posf] = null;
				
				pieceMoved = true;
			}
			
			king = big2small(post);
		}
		else
			// Find the king
			king = big2small(colour?whiteKing:blackKing);
		
		// AM - For each square on the big (128) board
		for (int pos = 0; pos < MAXSQUARES; pos++)
		{
			// AM - Don't worry about empty spaces or same colour pieces
			if (cb[pos]==null || cb[pos].isWhite()!=isPlayerTurn)
				continue;
			t = big2small(pos);
			m = new Move(t,king);
			if (isLegalMove(m))
			{
				isPlayerTurn = colour;
				if (pieceMoved)
				{
					// Stick the piece back
					cb[posf] = cb[post];
					cb[post] = null;
				}
				return true;
			}
		}
		isPlayerTurn = colour;
		if (pieceMoved)
		{
			// Stick the piece back
			cb[posf] = cb[post];
			cb[post] = null;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see Board#isStalemate()
	 */
	public boolean isStalemate()
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * accessor
	 * @author TT & DG
	 * @param a position on a 64 square board
	 * @return position on a 128 square board
	 */
	private int small2big(int a)
	{
		return (((a >> 3) << 4)+ (a & 7)); // DG (a/8)*16+(a%8)
	}
	
	/**
	 * accessor
	 * @author DG & TT
	 * @param position on a 128 square board 
	 * @return position on a 64 square board 
	 */
	private int big2small(int b)
	{
		return (((b >> 4) << 3) + (b & 15)); // DG (b/16)*8+(b%16)
	}
	
	/**
	 * accessor
	 * @author DG
	 * @param difference between target and current position
	 * @return true if piece's path is blocked, false otherwise
	 */
	protected boolean isPathBlocked (int posf, int post)
	{
		int delta = post - posf;
		if ((delta & 15) == 0) // DG Check on vertical path
		{
			int k = (delta > 0) ? 1 : -1;
			for (int i = posf+(k*16); i!=post; i+=(k*16))
			{
				if (cb[i]!=null)
					return true;
			}
		}
		else if (Math.abs(delta) < 8 ) // DG Check on horizontal path
		{
			int k = (delta > 0)? 1 : -1;
			for (int i = posf+k; i!=post; i+=k)
			{
				if(cb[i]!=null)
					return true;
			}
		}
		else if (delta % 15 == 0) // DG Check on left diagonal path
		{
			int k = (delta > 0) ? 1 : -1;
			int step=k*15;
			for (int i = posf+(k*15); i!=post; i+=step)
			{
				if (cb[i]!=null)
					return true;						
			}
		}
		else if (delta % 17 == 0) // DG Check on right diagonal path
		{
			int k = (delta > 0) ? 1 : -1;
			for (int i = posf+(k*17); i!=post; i+=(k*17))
			{
				if (cb[i]!=null)
					return true;
			}
		}
		return false;	// DG Path is not blocked
	}
	
	/**
	 * Accessor
	 * @author AM PD
	 * @param m  - move, that assumes a 64 square representation
	 * @return Integer position of the rook to castle with, big representation
	 * When castling, find out which rook to play with
	 */
	private int getRook(Moveable m)
	{
		int posf = small2big(m.getFromPosition());
		int post = small2big(m.getToPosition());
		
		if (isPlayerTurn)
		{
			// White
			if ((post - posf) > 0) // It's a kingside attempt
			{
				return 7;
			}
			else
			{
				// Queenside attempt
				return 0;
			}
		}
		else
		{
			// Black
			if ((post - posf) > 0) // It's a kingside attempt
			{
				return 119;
			}
			else
			{
				// Queenside attempt
				return 112;
			}
		}
	}
	
	/**
	 * Accessor
	 * @author AM PD
	 * @param m  - move, that assumes a 64 square representation
	 * @return true if it looks like the move is an attempt at castling
	 */
	private boolean isCastling (Moveable m)
	{
		int posf = small2big(m.getFromPosition());
		int post = small2big(m.getToPosition());
		
		// If it's a king and it's moving 3 left or 2 right
		if (cb[posf] instanceof King)
		{
			if (((post - posf) == -2 ) || ((post - posf) == 2))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
			return false;
	}
	
	private boolean wasCastling(Moveable m)
	{
		int posf = small2big(m.getFromPosition());
		int post = small2big(m.getToPosition());
		int delta = post - posf;
		
		if (delta > 0)
		{
			// Kingside
			if (delta == 2 && cb[post] instanceof King)
				return true;
		}
		else
		{
			// Queenside
			if (delta == -2 && cb[post] instanceof King)
				return true;
		}
		
		return false;
	}
	
	/**
	 * Accessor
	 * @author AM PD
	 * @param m  - move, that assumes a 64 square representation
	 * @return true if legally attempting to castle, otherwise false
	 */
	protected boolean canCastle (Moveable m)
	{
		int posf = small2big(m.getFromPosition());
		int post = small2big(m.getToPosition());
		int rook = getRook(m);
		
		// If it's not a king
		if (!(cb[posf] instanceof King))
			return false;
		
		// If the king hasn't moved
		if (cb[posf].hasMoved())
			return false;

		// If the rook hasn't moved
		if (!(cb[rook] instanceof Rook) || cb[rook].hasMoved())
			return false;
		
		// If nothing's in the way
		if (isPathBlocked(posf, post))
			return false;
		
		// If the square going to isn't occupied
		if (cb[post] != null)
			return false;

		// If the king's not currently in check
		if (isInCheck(cb[posf].isWhite()))
				return false;

		// If the king is safe where it's moving to
		if (isInCheck(cb[posf].isWhite(), post))
			return false;

		// If the king wont pass through any squares under attack
		if (post - posf > 0)
		{
			// Kingside
			for (int i=0; i < 3; i++)
			{
				if (isInCheck(isPlayerTurn, posf+i, -1))
					return false;
			}
		}
		else
		{
			// Queenside
			for (int i=0; i < 4; i++)
			{
				if (isInCheck(isPlayerTurn, posf-i, -1))
					return false;
			}
		}

		// If the rook and the king are on the same rank
		if ((posf / 8) != (rook / 8))
			return false;

		return true;
	}
	
	/**
	 * modifier
	 * @author TT & DG
	 * @param m Moveable m. absolute move on a 64 square board
	 * @return true if move has been made successfully, false if illegal and incomplete
	 * @see Board#makeMove(Moveable)
	 * TODO: Recognise castling and handle it
	 */
	public boolean makeMove(Moveable m) // DG Might be and is necessary to shift checks for kings' positions to inside isLegalmove, will do that on friday
	{
		if  (m == null)		// DG Check if legal form of move passed
			return false;
		
		int posf = small2big(m.getFromPosition()); // TT conversion from small representation to big
		int post = small2big(m.getToPosition()); // DG Moved up for testing purposes, will be below isLegalMove!
		
		if (cb[posf] == null) // DG Test if fields valid for move
		{
			System.err.println("makeMove: cb[posf] empty! " + posf);
			System.exit(1);
		}
		
		if (!isLegalMove(m)) // DG if illegal move
			return false;

		// Check for castling
		if (isCastling(m))
		{
			if (canCastle(m))
			{
				int rookPosf = getRook(m);
				cb[rookPosf].setMoved(); //TT rook has moved as a result of castling
				cb[post]=cb[posf];
				cb[posf]=null;
				
				// Move the rook
				int rookPost; // DG - final rook position recorded for hashing
				if (post - posf > 0)
				{
					cb[post-1]=cb[rookPosf];
					rookPost = post-1;
				}
				else
				{
					cb[post+1]=cb[rookPosf];
					rookPost = post+1;
				}
				cb[rookPosf]=null;
				m.setCastling(true, rookPosf, rookPost); // DG - castling data recorded within move for hashing
			}
			else
			{
				// Tried to castle and failed - bad move
				return false;
			}
		}
		else
		{ 
			if (cb[post]!=null)
			{
				m.setCapturedFigure(cb[post]);
				cb[post] = null;
			}
			cb[post]=cb[posf];
			cb[posf]=null;
		}
		// if (p)
		//	System.err.println("makeMove: Tail posf: " + posf + " post: " + post);
		
		if (cb[post] instanceof King) //TT track positions of Kings on the board
		{
			if (cb[post].isWhite())
				whiteKing = post;
			else
				blackKing = post;
		}
		
		if (m.isPromotion()) //Promotion handler
		{
			m.getPromotionFigure().setColour(isPlayerTurn);
			cb[post]=m.getPromotionFigure();
		}
		else
		{
			if ((cb[post] instanceof Pawn)&&(post>>3==(isPlayerTurn?7:0)))
			{
				cb[post]=new Queen(isPlayerTurn);
				m.setPromotionFigure(cb[post]);
			}
		}
		
		cb[post].setMoved();	// DG Piece has moved, so increment move counter

		// DG isInCheck causes incorrect output, doesn't allow legal moves
		if (isInCheck(isPlayerTurn)) // TT if leaves us in check, the move isn't legal
		{
			isPlayerTurn=!isPlayerTurn;
			reverseMove(m);
			return false;
		}
		
		generateZobristKey(m);
		
		isPlayerTurn=!isPlayerTurn; //TT forces isPlayerTurn() to operate correctly
		oldNode *= -1;
		plyPlayed++;
		err++;
		// if (p)
		// 	printBoard();
		return true;
	}

	/**
	 * modifier
	 * @param Moveable m
	 * @see Board#reverseMove(Moveable)
	 */
	public boolean reverseMove(Moveable m)
	{
		if  (m == null)		// DG Check if legal form of move passed
			return false;
		
		isPlayerTurn=!isPlayerTurn;
		
		generateZobristKey(m);
		
		int posf = small2big(m.getFromPosition());
		int post = small2big(m.getToPosition());
		// if (p)
		//	System.err.println("reverseMove: Head posf: " + posf + " post: " + post);
		
		// Check for castling
		// If was castling
		if (wasCastling(m))
		{
			// Find the rook
			if (cb[post+1] instanceof Rook)
			{
				// Found the rook
				// Shift him back
				cb[post-2] = cb[post+1];
				cb[post+1] = null;
				cb[post-2].unsetMoved();
			}
			else
			{
				if (cb[post-1] instanceof Rook)
				{
				// Rook's on the left
				// Shift him back
				cb[post+1] = cb[post-1];
				cb[post-1] = null;
				cb[post+1].unsetMoved();
				}
			}
		}
		
		if (m.isPromotion())
		{
			cb[posf]=new Pawn(cb[post].isWhite());
			for (int i = 0; i<6; i++)
				cb[posf].setMoved();
		}
		else
		{
			if (cb[post]==null) // DG Test block added to reveal null errors
			{
				System.err.println("ReverseMove: cb[post] empty! " + post);
				if (cb[posf]==null)
					System.err.println("ReverseMove: cb[posf] empty! " + posf);
				else
					System.err.println("ReverseMove: cb[posf]: " + cb[posf].toString());
			}					// DG End Test block
			
			cb[posf] = cb[post];
		}
		if (m.isCapture())
		{
			cb[post]=m.getCapturedFigure();
		}
		else
		{
			cb[post]=null;
		}
		if (cb[posf] == null){
			System.err.printf("reverse move! err: %d from: %d (%d) to: %d (%d) capture: %b promotion: %b\n" 
				+ (m.isPromotion()?"Promotion figure: "+m.getPromotionFigure().toString():"")
				+ (m.isCapture()?"Capture figure: "+m.getCapturedFigure().toString():""),
				err, m.getFromPosition(), posf, m.getToPosition(), post, m.isCapture(), m.isPromotion());
			System.err.println("");
			printBoard();
		}
		
		cb[posf].unsetMoved();//Null pointer exception caused by this line at times, presumably because cb[posf] doesnt exist
		//Sample crashing input: 27115 <first : reverse move! from: 40 (80) to: 24 (48) capture: false promotion: false
		
		// DG: possibly caused by giving the reverseMove a move with a null TO field (thus reversing the move makes posf null
		// and produces an error, when setMoved is called from that field) or passing a legal move, but it is applied to a 
		// corrupt board (null field where we should have a piece)
		
		if ((m.getFromPosition()>>3==1)&&(cb[posf] instanceof Pawn)&&isPlayerTurn)
			cb[posf].unsetMoved(); //work around for promoted pawns on plies > 5
		
		if ((m.getFromPosition()>>3==6)&&(cb[posf] instanceof Pawn)&&!isPlayerTurn)
			cb[posf].unsetMoved(); //work around for promoted pawns on plies > 5

		if (cb[posf] instanceof King) //TT track positions of Kings on the board
		{
			if (cb[posf].isWhite())
				whiteKing = posf;
			else
				blackKing = posf;
		}
		
		plyPlayed--;
		err--;
		return true;
	}

	/**
	 * accessor
	 * @author TT
	 * @return returns a List of all Legal moves for the current side
	 */
	public List<Moveable> generateLegalMoves()
	{
		// AM - List of viable moves
		List<Moveable> legalMoves = new ArrayList<Moveable>();
		currentMoves.clear();
		// AM - For every square on the big board (128)
		for (int pos=0; pos < MAXSQUARES; pos++)
		{
			if ((cb[pos]!=null)&&(cb[pos].isWhite()==isPlayerTurn))
				// check moves only for the correct side that is
				// isWhite returns true for white, false for black
				// AM - if not an empty square and colour of piece is equal to currently playing colour
			{
				// AM - Find all available integer values the piece can move
				List<Integer> deltas = cb[pos].getDeltas();
				
				int t = big2small(pos);
				// AM - Change to small board representation
				Moveable m;
				for (Integer delta : deltas) // check if a move is legal
				{
					int tm = big2small(pos - delta);
					int tp = big2small(pos + delta);
					// AM - If the delta of the piece doesn't take us off the board (top end)
					if (tp <= 63)
					{
						m = new Move(t, tp);
						if (makeMove(m))
						{
							reverseMove(m);
							currentMoves.add(m);
							legalMoves.add(m);
							/*
							System.err.println(m.getFromPosition() + " " + m.getToPosition());
							printBoard();
							System.err.println("");
							*/
						}
					}
					// AM - If the delta of the piece doesn't take us off the board (bottom end)
					if (tm >= 0)
					{
						m = new Move(t, tm);
						if (makeMove(m))
						{
							reverseMove(m);
							currentMoves.add(m);
							legalMoves.add(m);
							/*
							System.err.println(m.getFromPosition() + " " + m.getToPosition());
							printBoard();
							System.err.println("");
							*/
						}
					}
				}
			}
		}
		//printMoves(lm);
		return legalMoves;
	}
	
	/**
	 * accessor
	 * @author TT & DG
	 * @return boolean
	 * function returns true if it's White's turn to play
	 * black otherwise
	 * @see Board#isPlayerTurn()
	 */
	public boolean isPlayerTurn()
	{
		return isPlayerTurn;
	}
	
	//
	// DG - Zobrist hashing handling block

	/**
	 * modifier
	 * @author DG
	 * @return void
	 * method generates the Zobrist key for the current board situation
	 * it is error prone, since relies on key from previous move
	 */
	
	public void generateZobristKey(Moveable m)
	{
		int posf = small2big(m.getFromPosition());
		int post = small2big(m.getToPosition());
		int colour = 0;
		
		if (!this.isPlayerTurn())
		{
			this.zobristKey ^= this.zSide;
			colour = 1;
		}
		
		if (this.isCastling(m))
		{
			this.zobristKey ^= this.zCastlingRights;
			this.zobristKey ^= zPieces[3][colour][m.getRookFrom()];
			this.zobristKey ^= zPieces[3][colour][m.getRookTo()];
		}
		
		// capture
		if (m.isCapture())
		{
			Piece p = m.getCapturedFigure();
			int capcol;
			
			if (colour == 1)
				capcol = 0;
			else
				capcol = 1;
			
			this.zobristKey ^= this.zCapture;
			this.zobristKey ^= zPieces[p.getID()][capcol][post];
		}
		
		// DG - en Passant TODO when implemented
		//if ()
		//{
		//	this.zobristKey ^= this.enPassant;
		//}
		
		// DG - promotion or normal move
		if (m.isPromotion())
		{
			this.zobristKey ^= this.zPromotion;
			this.zobristKey ^= zPieces[6][colour][posf];
			this.zobristKey ^= zPieces[cb[post].getID()][colour][post];
		}
		else
		{

		this.zobristKey ^= zPieces[cb[post].getID()][colour][post];
		this.zobristKey ^= zPieces[cb[post].getID()][colour][posf];
		}
	}
	
	/**
	 * modifier
	 * @author DG
	 * @return void
	 * generates random keys for all board squares and situations, as well, as the
	 * initial Zobrist key
	 */
	
	public void generateHashCodes()
	{
		this.zobristKey = 0;
		
		Random rnd = new Random();
		this.zSide = Math.abs(rnd.nextLong());
		this.zCastlingRights = Math.abs(rnd.nextLong());
		this.zEnPassant = Math.abs(rnd.nextLong());
		this.zCapture = Math.abs(rnd.nextLong());
		this.zPromotion = Math.abs(rnd.nextLong());
		
		this.zPieces = new long[this.PIECETYPE][this.SIDETOMOVE][this.MAXSQUARES]; // piece, side to move, square
		for(int square = 0; square < this.MAXSQUARES; square++)
		{
			if((square & 0x88) == 0)
			{		
				for(int p = 0; p < this.PIECETYPE; p++)
				{
					zPieces[p][0][square] = Math.abs(rnd.nextLong());
					zPieces[p][1][square] = Math.abs(rnd.nextLong());
				}
				// DG - establishing initial hash value for the board
				if (this.cb[square] != null)
				{
					int piece = this.cb[square].getID(); 
					int colour = this.cb[square].isWhite() ? 0 : 1;
					zobristKey ^= zPieces[piece][colour][square];
				}
			}
			else square +=7;
		}
	}
	
	// DG - End of Zobrist hashing block
	//
	
	public static void printMoves(List<Moveable> lm)
	{
		for (Moveable m : lm)
		{
			System.err.printf("Legal Move: %d %d\n",
					m.getFromPosition(), m.getToPosition(), XBUtils.moveToString(m));
		}
	}
	
	/**
	 * mutator
	 * @author TT
	 * @param c sets white side to play if true, false otherwise
	 */
	public void setIsPlayerTurn(boolean c)
	{
		this.isPlayerTurn = c;
	}
	
	/**
	 * accessor
	 * @author tajaddin
	 * Prints board in ASCII
	 */
	public void printBoard()
	{
		Piece p;
		for(int i = 112; i >= 0; i-=16)
		{
			for (int j = 0; j < 16; j++)
			{
				if (j == 8)
					System.err.print("||");
				p = cb[i+j];
				if (p == null)
				{
					System.err.print("XX");
					continue;
				}
				else if (p instanceof King)
					System.err.print("K");
				else if (p instanceof Queen)
					System.err.print("Q");
				else if (p instanceof Knight)
					System.err.print("N");
				else if (p instanceof Bishop)
					System.err.print("B");
				else if (p instanceof Rook)
					System.err.print("R");
				else if (p instanceof Pawn)
					System.err.print("P");
				System.err.print(p.isWhite()?"w":"b");
			}
			System.err.println();
		}
	}
	
}
