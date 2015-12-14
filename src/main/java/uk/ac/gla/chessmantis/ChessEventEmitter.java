package uk.ac.gla.chessmantis;

import uk.ac.gla.chessmantis.event.ChessEvent;

import java.util.function.Consumer;

public interface ChessEventEmitter {
    void handleChessEvent(Consumer<ChessEvent> eventHandler);
}
