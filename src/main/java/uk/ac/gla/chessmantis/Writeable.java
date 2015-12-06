package uk.ac.gla.chessmantis;

import uk.ac.gla.chessmantis.event.ErrorEvent;
import uk.ac.gla.chessmantis.event.IllegalMoveEvent;
import uk.ac.gla.chessmantis.event.MoveEvent;
import uk.ac.gla.chessmantis.event.StatusEvent;

public interface Writeable
{
	public void write(MoveEvent event);
	public void write(StatusEvent event);
	public void write(IllegalMoveEvent event);
	public void write(ErrorEvent event);
	public void rawWrite(String message);//Write a string to selected writer, this function is primarily for debugging purposes and should nto be used for other things
}
