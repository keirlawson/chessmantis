package uk.ac.gla.chessmantis.event;

import uk.ac.gla.chessmantis.Status;
import uk.ac.gla.chessmantis.event.ChessEvent;

public class StatusEvent implements ChessEvent
{
	private Status status;
	
	public StatusEvent(Status status)
	{
		this.status = status;
	}
	
	public Status getStatus()
	{
		return this.status;
	}
}
