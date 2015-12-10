package uk.ac.gla.chessmantis;

import uk.ac.gla.chessmantis.analyser.Analyser;
import uk.ac.gla.chessmantis.analyser.MiniMaxAnalyser;
import uk.ac.gla.chessmantis.evaluator.Evaluator;
import uk.ac.gla.chessmantis.event.*;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Game
{
	private XBoardIO xBoardIO;
	private Evaluator evaluator;
	private Analyser analyser;

	private long starttime = 0;
	private long timeleft = 0; //Time left (miliseconds) in which to make movesleft, default to 5 minutes
	private int movesleft = 0; //Number of moves to make within timeleft, default to 40 moves
	private int timeinc = 0; //amount of time to increment timeleft by after a move
	private int basemoves = 0;
	private long basetime = 0;
	private boolean think = true;
	
	private TimeControl timeControl;
	ExecutorService timeControlExecutor = Executors.newFixedThreadPool(1);

	public Game(Evaluator evaluator, Analyser analyser) {
		xBoardIO = new XBoardIO(System.in, System.out);
		this.evaluator = evaluator;
		this.analyser = analyser;
	}

	public void start() {
		Thread thread = new Thread(xBoardIO);
		thread.start();

		timeControl = new TimeControl(evaluator, analyser);
		timeControlExecutor.submit(timeControl);

		for(;;)
		{
			try {
				// Avoids thrashing
				Thread.sleep(25);
			} catch (InterruptedException e) {;}
			if (timeControl.isDone()){
				handleResult(timeControl.getResult());
			}
			handleEvent(xBoardIO.getNextEvent());
		}
	}

	private void handleResult(Moveable moveable) {
		if(moveable != null)
		{
			xBoardIO.write(new MoveEvent(moveable));

			long difference = (new Date()).getTime() - starttime;
			timeleft = (timeleft - difference) + timeinc;
			System.err.printf("that move took roughly %d seconds, we now have roughly %d seconds left\n",difference/1000,timeleft/1000);

			evaluator.makeMove(moveable);

			timeControl.reset();

			if (--movesleft == 0) {
				movesleft = basemoves;
				timeleft = basetime;
			}
		}
		else
		{
			xBoardIO.write(new StatusEvent(Status.Win));
			timeControl.reset();
		}
	}

	private void handleEvent(ChessEvent event) {
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
				if (think)
				{
					// its the computers turn to make a move now
					// Start the timer
					starttime = (new Date()).getTime();
					System.err.println("Executing new time control thread");
					//Dont know why this works... but it does (seems to be a problem with the reference) - KL
					timeControl.setEvaluator(evaluator);
					timeControl.settimeformove(timeleft/movesleft);
					timeControlExecutor.submit(timeControl);
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
				think = false;
				break;
			case Go:
				// Start the engine thinking
				think = true;
				// its the computers turn to make a move now
				// Start the timer
				starttime = (new Date()).getTime();
				System.err.println("Executing new time control thread");
				//Dont know why this works... but it does (seems to be a problem with the reference) - KL
				timeControl.setEvaluator(evaluator);
				timeControl.settimeformove(timeleft/movesleft);
				timeControlExecutor.submit(timeControl);
				break;
			default:
				break;
		}
		xBoardIO.write(se);
	}
	
	private void processEvent(TimeEvent te)
	{
		basetime = timeleft = te.getBase();
		basemoves = movesleft = te.getMoves();
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
