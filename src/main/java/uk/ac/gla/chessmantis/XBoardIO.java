package uk.ac.gla.chessmantis;

import uk.ac.gla.chessmantis.event.*;

import java.lang.*;
import java.util.*;
import java.io.*;

/**
 * @author Keir Lawson and Neil Henning
 */

public class XBoardIO implements Readable, Writeable, Runnable
{
	private Scanner scan;
	private Formatter form;

	//Should probably read these from somewhere...
	final String enginename = "Chess uk.ac.gla.chessmantis.Mantis";
	final String[] featurenames = {"name","ics","usermove","ping","analyze","colors","pause","setboard","time","sigterm","sigint"};//,"time"};
	final boolean[] usefeatures = {false,false,true,true,false,false,false,true,false,false,false};
	final String[] ignorablecommands = {"accepted","xboard","computer","variant","random"};	

	Deque<ChessEvent> eventqueue = new LinkedList<ChessEvent>();

	/* NH - Commands being ignored right now are as follows;
	 * resign
	 * tells of all kinds
	 * askuser
	 */
	 
	
	public XBoardIO(InputStream in, OutputStream out)
	{
		scan = new Scanner(in);
		form = new Formatter(out);
		scan.useDelimiter("\n");
	}
	
	public XBoardIO()
	{
		// This line doesn't work, oh dear.
		//this(System.in, System.out);
		scan = new Scanner(System.in);
		form = new Formatter(System.out);
		scan.useDelimiter("\n");
	}
	
	/** Writes a uk.ac.gla.chessmantis.event.MoveEvent to the XBoard interface. */
	public void write(MoveEvent event)
	{
		//System.err.format("\nmove %s\n", uk.ac.gla.chessmantis.XBUtils.moveToString((uk.ac.gla.chessmantis.Move) event.getMove()));
		form.format("move %s\n", XBUtils.moveToString((Move) event.getMove()));
	}
	
	/** Writes a uk.ac.gla.chessmantis.event.StatusEvent to the XBoard interface. */
	public void write(StatusEvent event)
	{
		//Assuming Win means white win.
		if((event.getStatus()).equals(Status.Win))
		{
			form.format("1-0 {YERMAW}\n");
		}
		else if((event.getStatus()).equals(Status.Draw))
		{
			form.format("1/2-1/2 {YERMAW}\n");
		}
		else if((event.getStatus()).equals(Status.Lose))
		{
			form.format("0-1 {YERMAW}\n");
		}
		else if((event.getStatus()).equals(Status.OfferedDraw))
		{
			form.format("offer draw\n");
		}
	}
	
	/** Writes an uk.ac.gla.chessmantis.event.IllegalMoveEvent to the XBoard interface. */
	public void write(IllegalMoveEvent event)
	{
		//TT changed from event.getIllegalMove().getMove() to make it compile
		//doesn't necessarily make much sense though;)
		form.format("Illegal move: %s\n", XBUtils.moveToString(event.getIllegalMove()));
	}
	
	/** Writes an uk.ac.gla.chessmantis.event.ErrorEvent to the XBoard interface. */
	public void write(ErrorEvent event)
	{
		form.format("Error (%s): %s\n", event.getError(), event.getCommand());
	}

	public void rawWrite(String message)
	{
		form.format("%s\n",message);
	}

	public void run() {
		for (;;) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {;}
			//Need to think about concurrency here... should really lock
			ChessEvent c = parseCommand(scan.next());
			synchronized(eventqueue) {
				eventqueue.add(c);
			}
		}
	}
	
	/** Returns the next uk.ac.gla.chessmantis.event.ChessEvent, or null if no event found. */
	public ChessEvent getNextEvent()
	{
		synchronized(eventqueue) {
			return eventqueue.pollLast();
		}
	}

	/**
 	 * returns an uk.ac.gla.chessmantis.event.ChessEvent or null if it needs to recieve the next command before returning a uk.ac.gla.chessmantis.event.ChessEvent
 	 *
 	 * @param	command	A string containing the command and any arguments it carried
 	 * @return		A uk.ac.gla.chessmantis.ChessEvent or null
 	 * @see            ChessEvent
 	 */
	private ChessEvent parseCommand(String commandline)
	{
		
		StringTokenizer commandtokenizer = new StringTokenizer(commandline);
		
		String command = commandtokenizer.nextToken();
		for (String s : ignorablecommands) {
			if (command.equals(s)) return null;
		}
		if(command.equals("protover"))
		{
			String featurestring = "feature done=0 myname=\"" + enginename + "\" ";
			for (int i = 0; i < featurenames.length; i++)
				featurestring += featurenames[i] + "=" + ((usefeatures[i]) ? 1 : 0) + " ";
			featurestring += "done=1\n";
			form.format(featurestring);
			return null;
		}
		else if(command.equals("rejected"))
		{
			return (new ErrorEvent("The client rejected the following feature: ",commandtokenizer.nextToken()));
		}
		else if(command.equals("ping"))
		{
			form.format("pong %s\n", commandtokenizer.nextToken());
			return null;
		}
		else if(command.equals("level"))
		{
			int moves = Integer.parseInt(commandtokenizer.nextToken());
			String[] minsandsecs = commandtokenizer.nextToken().split(":");
			int base = (Integer.valueOf(minsandsecs[0]) * 60000);
			if (minsandsecs.length > 1) base += (Integer.valueOf(minsandsecs[1]) * 1000);//Only handle minutes jsut now, need to implement seconds
			int increment = Integer.parseInt(commandtokenizer.nextToken());
			return (new TimeEvent(moves,base,increment));
		}
		//The below code doesnt work properly... could be fixed quite easily, but not a priority now
		/*else if(command.equals("st"))
		{
			int time = Integer.parseInt(commandtokenizer.nextToken());
			return (new uk.ac.gla.chessmantis.event.TimeEvent(1,time,0));
		}*/
		else if(command.equals("force"))
		{
			// Stop the engine thinking
			return (new StatusEvent(Status.Force));
		}
		else if(command.equals("go"))
		{
			// Need to make the engine start thinking again
			// (After force, which should stop the engine thinking)
			return (new StatusEvent(Status.Go));
		}
		else if(command.equals("new"))
		{
			return (new StatusEvent(Status.New));
		}
		else if(command.equals("quit"))
		{
			return (new StatusEvent(Status.Quit));
		}
		else if(command.equals("easy"))
		{
			return (new StatusEvent(Status.Easy));
		}
		else if(command.equals("hard"))
		{
			return (new StatusEvent(Status.Hard));
		}
		else if(command.equals("draw"))
		{
			return (new StatusEvent(Status.OfferedDraw));
		}
		else if(command.equals("usermove"))
		{
			return (new MoveEvent(XBUtils.stringToMove(commandtokenizer.nextToken())));
		}
		else if(command.equals("sd"))
		{
			int maxdepth = Integer.parseInt(commandtokenizer.nextToken());
			return (new DepthEvent(maxdepth));
		}
		else if(command.equals("result"))
		{
			String token = commandtokenizer.nextToken();
			if(token.equals("1-0"))
			{
				System.err.println("uk.ac.gla.chessmantis.XBoardIO Win");
				return (new StatusEvent(Status.Win));
			}
			else if(token.equals("1/2-1/2"))
				return (new StatusEvent(Status.Draw));
			else if(token.equals("0-1"))
				return (new StatusEvent(Status.Lose));
			else
				return null;
		}
		else if(command.equals("setboard"))
		{
			return (new MessageEvent(commandtokenizer.nextToken()));
		}
		else if(command.equals("undo"))
		{
			return (new StatusEvent(Status.Undo));
		}
		else
		{
			return (new ErrorEvent("Couldn't Recognise Command", command));
		}
	}
}
