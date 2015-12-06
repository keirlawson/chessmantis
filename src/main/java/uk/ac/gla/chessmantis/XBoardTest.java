package uk.ac.gla.chessmantis;

import uk.ac.gla.chessmantis.event.*;

class XBoardTest
{
	public static void main(String[] args)
	{
		XBoardIO x = new XBoardIO();
		DebugWriter w = new DebugWriter("uk.ac.gla.chessmantis.DebugWriter");
		for(;;)
		{
			ChessEvent c = x.getNextEvent();
			if(c instanceof MoveEvent)
			{
				x.write((MoveEvent) c);
				w.write((MoveEvent) c);
			}
			else if(c instanceof StatusEvent)
			{
				x.write((StatusEvent) c);
				w.write((StatusEvent) c);
			}
			else if(c instanceof IllegalMoveEvent)
			{
				x.write((IllegalMoveEvent) c);
				w.write((IllegalMoveEvent) c);
			}
			else if(c instanceof ErrorEvent)
			{
				x.write((ErrorEvent) c);
				w.write((ErrorEvent) c);
			}
		}
	}
}
