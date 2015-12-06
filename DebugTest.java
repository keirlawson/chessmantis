public class DebugTest
{
	public static void main(String[] args)
	{
		DebugWriter debug = new DebugWriter();
		for(int i = 0; i < 8; i++)
		{
			debug.write(new MoveEvent(new Move(1, 1)));
			debug.write(new StatusEvent(Status.New));
			debug.write(new IllegalMoveEvent(new Move(1, 1)));
			debug.write(new ErrorEvent("MONO", "BAW"));
		}
		for(;;);
	}
}
