package uk.ac.gla.chessmantis.event;

import uk.ac.gla.chessmantis.event.ChessEvent;

public class DepthEvent implements ChessEvent {
	private int depth;
	public DepthEvent(int depth) {
		this.depth = depth;
	}
	public int getDepth() {
		return depth;
	}
}
