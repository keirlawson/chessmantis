package uk.ac.gla.chessmantis;

import uk.ac.gla.chessmantis.analyser.Analyser;
import uk.ac.gla.chessmantis.analyser.MiniMaxAnalyser;
import uk.ac.gla.chessmantis.evaluator.Evaluator;
import uk.ac.gla.chessmantis.event.*;

import java.util.Date;

/**
 * 
 */

/**
 * @author macdonal (n00b)
 *
 */
public class Game
{
	XBoardIO x;
	Evaluator b;
	Analyser a;

	private long starttime = 0;
	private long timeleft = 0; //Time left (miliseconds) in which to make movesleft, default to 5 minutes
	private int movesleft = 0; //Number of moves to make within timeleft, default to 40 moves
	private int timeinc = 0; //amount of time to increment timeleft by after a move
	private int basemoves = 0;
	private long basetime = 0;
	private boolean think = true;
	private int maxdepth = 0;//The maximum depth of game tree that we will analyse to, 0 == infinite
	
	private TimeControl t;
	private Thread t1;
	
	/**
	 * Constructor
	 * 
	 */
	public Game(String evaluatorstring, String analyserstring)
	{
		x = new XBoardIO();
		//dont really understand why we need the below two lines...
		//a = new uk.ac.gla.chessmantis.analyser.AlphaBetaAnalyser();
		//b = new uk.ac.gla.chessmantis.evaluator.Capuchin();
		Class c;
		try {
			c = Class.forName(evaluatorstring);
			b = (Evaluator) c.newInstance();
		} catch (Exception e) {
			//That evaluator didn't work... default to marmoset
			//FIXME should report an error here... somehow
			b = new Mantis();
		}
		try {
			c = Class.forName(analyserstring);
			a = (Analyser) c.newInstance();
		} catch (Exception e) {
			//That analyser didn't work... default to minimax
			//FIXME should report an error here... somehow
			a = new MiniMaxAnalyser();
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
	
	private void ProcessEvent(MoveEvent me)
	{
		// Give move to uk.ac.gla.chessmantis.Board Class
		if (b.makeMove(me.getMove()))
		{
			
			System.err.println("uk.ac.gla.chessmantis.Game.java: uk.ac.gla.chessmantis.Move event received");
			// Legal
			// 	Check the state of the new board - isCheckmate, isStalemate etc
			if(b.isCheckmate())
			{
				
				// Create a status event - Win
				x.write(new StatusEvent(Status.Win));
			}
			/* else if (g.b.isStalemate())
			{
				System.err.println("uk.ac.gla.chessmantis.Game.java: Thinks stalemate has occured");
				// Create a status event - Draw
				g.x.write(new uk.ac.gla.chessmantis.event.StatusEvent(uk.ac.gla.chessmantis.Status.Draw));
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
					t.setEvaluator(b);
					t.settimeformove(timeleft/movesleft);
					t1 = new Thread(t);
					t1.start();
				}
			}
		}
		else
		{
			// Illegal move
			System.err.println("uk.ac.gla.chessmantis.Game.java: The move was illegal");
			x.write(new IllegalMoveEvent(me.getMove()));
		}
		
		// Computer move
		// g.a.getNextMove
		// Deal with move as above
	}
	
	private void ProcessEvent(StatusEvent se)
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
					b = (Evaluator) b.getClass().newInstance();
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
				t.setEvaluator(b);
				t.settimeformove(timeleft/movesleft);
				t1 = new Thread(t);
				t1.start();
				break;
			default:
				break;
		}
		x.write(se);
	}
	
	private void ProcessEvent(TimeEvent te)
	{
		basetime = timeleft = te.getBase();
		basemoves = movesleft = te.getMoves();
		timeinc = te.getIncrement();
		System.err.printf("");
	}
	
	private void ProcessEvent(MessageEvent me)
	{
		// setboard has been called from xboard
		b.setBoard(me.getMessage());
	}

	private void ProcessEvent(DepthEvent de)
	{
		t.setMaxDepth(de.getDepth());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		//The below code isnt very flexible or well designed... could be redone - KL
		//Not sure if it would be worth redoing though, as it does what is needed - KL
		String evaluator = "uk.ac.gla.chessmantis.Mantis";
		String analyser = "AlphaBetaAnalyserTT";
		if (args.length > 1) //need atleast two arguments to make sense
		{
			for (int i = 0; i < args.length; i++)
			{
				if (args[i].startsWith("-")) //If it is a flag...
				{
					char flag = args[i].charAt(1);
					if (flag == 'e') //If the evaluator is specified
					{
						evaluator = args[++i];
					}
					else if (flag == 'a') //If the analyser is specified
					{
						analyser = args[++i];
					}
				}
			}
		}
		Game g = new Game(evaluator, analyser);
		Moveable m;
		
		Thread thread = new Thread(g.x);	
		thread.start();
		g.t = new TimeControl(g.b,g.a);
		g.t1 = new Thread(g.t);

		for(;;)
		{
			try {
				// Avoids thrashing
				Thread.sleep(25);
			} catch (InterruptedException e) {;}
			if (g.t.isDone()){
				m = g.t.getResult();
				if(m != null)
				{
					g.x.write(new MoveEvent(m));
					long difference = (new Date()).getTime() - g.starttime;
					g.timeleft = (g.timeleft - difference) + g.timeinc;
					System.err.printf("that move took roughly %d seconds, we now have roughly %d seconds left\n",difference/1000,g.timeleft/1000);
					g.b.makeMove(m);
					g.t.reset();
					if (--g.movesleft == 0) {
						g.movesleft = g.basemoves;
						g.timeleft = g.basetime;
					}
				}
				else
				{
					g.x.write(new StatusEvent(Status.Win));
					g.t.reset();
				}
			}
			ChessEvent c = g.x.getNextEvent();
			if (c != null)
			{
				// Should be some way to Dynamic Dispatch cast and get rid of the if
				if(c instanceof MoveEvent)
				{
					g.ProcessEvent((MoveEvent)c);
				}
				else if(c instanceof StatusEvent)
				{
					g.ProcessEvent((StatusEvent)c);
				}
				else if (c instanceof TimeEvent)
				{
					g.ProcessEvent((TimeEvent)c);
				}
				else if (c instanceof MessageEvent)
				{
					g.ProcessEvent((MessageEvent)c);
				}
				else if (c instanceof DepthEvent)
				{
					g.ProcessEvent((DepthEvent)c);
				}
				else if(c instanceof ErrorEvent)
				{
					g.x.write((ErrorEvent) c);
				}
			}
		}
	}
}
