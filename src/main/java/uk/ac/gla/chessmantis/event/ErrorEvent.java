package uk.ac.gla.chessmantis.event;

import uk.ac.gla.chessmantis.event.ChessEvent;

public class ErrorEvent implements ChessEvent
{
	private String error;
	private String command;
	
	public ErrorEvent(String error, String command)
	{
		this.error = error;
		this.command = command;
	}
	
	public String getError()
	{
		return this.error;
	}
	
	public String getCommand()
	{
		return this.command;
	}
}
