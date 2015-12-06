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
