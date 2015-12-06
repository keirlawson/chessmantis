public interface Writeable
{
	public void write(MoveEvent event);
	public void write(StatusEvent event);
	public void write(IllegalMoveEvent event);
	public void write(ErrorEvent event);
	public void rawWrite(String message);//Write a string to selected writer, this function is primarily for debugging purposes and should nto be used for other things
}
