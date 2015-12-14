package uk.ac.gla.chessmantis;

import uk.ac.gla.chessmantis.analyser.Analyser;
import uk.ac.gla.chessmantis.evaluator.Evaluator;
import uk.ac.gla.chessmantis.event.*;

import java.util.Date;
import java.util.concurrent.*;

public class Game
{
	private XBoardIO xBoardIO;
	private Evaluator evaluator;
	private Analyser analyser;

	private long currentMoveStartTime = 0;
	private long gameTimeRemaining = 0; //Time left (miliseconds) in which to make gameMovesRemaining, default to 5 minutes
	private int gameMovesRemaining = 0; //Number of moves to make within gameTimeRemaining, default to 40 moves
	private int timeinc = 0; //amount of time to increment gameTimeRemaining by after a move
	private int basemoves = 0;
	private long basetime = 0;
	private boolean makeMoves = true;
	
	private TimeControl timeControl;
	ExecutorService timeControlExecutor = Executors.newFixedThreadPool(1);
	CompletableFuture<Moveable> futureBestMove;

	public Game(Evaluator evaluator, Analyser analyser) {
		xBoardIO = new XBoardIO(System.in, System.out);
		this.evaluator = evaluator;
		this.analyser = analyser;
	}

	public void start() {
		Thread thread = new Thread(xBoardIO);
		thread.start();

		timeControl = new TimeControl(evaluator, analyser);

		for(;;)
		{
			try {
				// Avoids thrashing
				Thread.sleep(25);
			} catch (InterruptedException e) {;}

			handleEvent(xBoardIO.getNextEvent());
		}
	}

	private synchronized void handleResult(Moveable moveable) {
		if(moveable != null)
		{
			xBoardIO.write(new MoveEvent(moveable));

			long difference = (new Date()).getTime() - currentMoveStartTime;
			gameTimeRemaining = (gameTimeRemaining - difference) + timeinc;
			System.err.printf("that move took roughly %d seconds, we now have roughly %d seconds left\n",difference/1000, gameTimeRemaining /1000);

			evaluator.makeMove(moveable);

			if (--gameMovesRemaining == 0) {
				gameMovesRemaining = basemoves;
				gameTimeRemaining = basetime;
			}
		}
		else
		{
			xBoardIO.write(new StatusEvent(Status.Win));
		}
	}

	private synchronized void handleEvent(ChessEvent event) {
		if (event == null) {
			return;
		}

		// Should be some way to Dynamic Dispatch cast and get rid of the if
		if(event instanceof MoveEvent)
		{
			processEvent((MoveEvent)event);
		}
		else if(event instanceof StatusEvent)
		{
			processEvent((StatusEvent)event);
		}
		else if (event instanceof TimeEvent)
		{
			processEvent((TimeEvent)event);
		}
		else if (event instanceof MessageEvent)
		{
			processEvent((MessageEvent)event);
		}
		else if (event instanceof DepthEvent)
		{
			processEvent((DepthEvent)event);
		}
		else if(event instanceof ErrorEvent)
		{
			xBoardIO.write((ErrorEvent) event);
		}
	}

	private void computeMove() {
		// its the computers turn to make a move now
		// Start the timer
		currentMoveStartTime = (new Date()).getTime();
		System.err.println("Executing new time control thread");
		//Dont know why this works... but it does (seems to be a problem with the reference) - KL
		timeControl.setEvaluator(evaluator);
		timeControl.settimeformove(gameTimeRemaining / gameMovesRemaining);
		futureBestMove = CompletableFuture.supplyAsync(timeControl, timeControlExecutor);
		futureBestMove.thenAccept(this::handleResult);
	}

	/**
	 * Modifier
	 *
	 */
	private void Quit()
	{
		System.exit(0);
	}
	
	private void processEvent(MoveEvent me)
	{
		// Give move to uk.ac.gla.chessmantis.Board Class
		if (evaluator.makeMove(me.getMove()))
		{
			
			System.err.println("Game.java: Move event received");
			// Legal
			// 	Check the state of the new board - isCheckmate, isStalemate etc
			if(evaluator.isCheckmate())
			{
				
				// Create a status event - Win
				xBoardIO.write(new StatusEvent(Status.Win));
			}
			/* else if (g.evaluator.isStalemate())
			{
				System.err.println("uk.ac.gla.chessmantis.Game.java: Thinks stalemate has occured");
				// Create a status event - Draw
				g.xBoardIO.write(new uk.ac.gla.chessmantis.event.StatusEvent(uk.ac.gla.chessmantis.Status.Draw));
			} */
			else
			{
				if (makeMoves)
				{
					computeMove();
				}
			}
		}
		else
		{
			// Illegal move
			System.err.println("Game.java: The move was illegal");
			xBoardIO.write(new IllegalMoveEvent(me.getMove()));
		}
		
		// Computer move
		// g.analyser.getNextMove
		// Deal with move as above
	}
	
	private void processEvent(StatusEvent se)
	{
		switch (se.getStatus())
		{
			case Quit:
				Quit();
				break;
			case OfferedDraw:
				break;
			case Draw:
				System.err.println("thinks we drew");
				break;
			case Lose:
				break;
			case Win:
				break;
			case New:
				try {
					evaluator = evaluator.getClass().newInstance();
				} catch (Exception e) {
					//This should never really happen... unless say the class file was deleted during play
				}
				break;
			case Save:
				break;
			case Load:
				break;
			case Easy:
				break;
			case Hard:
				break;
			case Force:
				// Stop the engine thinking
				makeMoves = false;
				break;
			case Go:
				// Start the engine thinking
				makeMoves = true;
				computeMove();
				break;
			default:
				break;
		}
		xBoardIO.write(se);
	}
	
	private void processEvent(TimeEvent te)
	{
		basetime = gameTimeRemaining = te.getBase();
		basemoves = gameMovesRemaining = te.getMoves();
		timeinc = te.getIncrement();
	}
	
	private void processEvent(MessageEvent me)
	{
		// setboard has been called from xboard
		evaluator.setBoard(me.getMessage());
	}

	private void processEvent(DepthEvent de)
	{
		timeControl.setMaxDepth(de.getDepth());
	}

}
