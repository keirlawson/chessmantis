package uk.ac.gla.chessmantis.event; /**
 * 
 */

import uk.ac.gla.chessmantis.event.ChessEvent;

/**
 * @author macdonal
 * 
 * Purpose - Initially to get setboard from xboard to the board implimentation
 * 				But can be used just to pass messages about?
 */
public class MessageEvent implements ChessEvent
{
	private String msg;
	
	public MessageEvent(String msg)
	{
		this.msg = msg;
	}
	
	public String getMessage()
	{
		return this.msg;
	}
}
