package uk.ac.gla.chessmantis;

import uk.ac.gla.chessmantis.event.ChessEvent;

public interface Readable
{
	public ChessEvent getNextEvent();
}
