package uk.ac.gla.chessmantis;

import uk.ac.gla.chessmantis.event.ErrorEvent;
import uk.ac.gla.chessmantis.event.IllegalMoveEvent;
import uk.ac.gla.chessmantis.event.MoveEvent;
import uk.ac.gla.chessmantis.event.StatusEvent;

public interface ChessEventWriter
{
	void write(MoveEvent event);
	void write(StatusEvent event);
	void write(IllegalMoveEvent event);
	void write(ErrorEvent event);
}
