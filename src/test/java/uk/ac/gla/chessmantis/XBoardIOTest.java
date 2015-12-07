package uk.ac.gla.chessmantis;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import uk.ac.gla.chessmantis.event.MoveEvent;

import java.io.InputStream;
import java.io.PrintStream;

public class XBoardIOTest {

    XBoardIO testee;
    InputStream mockInputStream;
    PrintStream mockPrintStream;

    @Before
    public void setup() {
        mockInputStream = mock(InputStream.class);
        mockPrintStream = mock(PrintStream.class);
        testee = new XBoardIO(mockInputStream, mockPrintStream);
    }

    @Test
    public void shouldWriteMoveEventAsMoveCommandWithAlgebraicMoveNotation() {
        Move someMove = new Move(0, 63);

        testee.write(new MoveEvent(someMove));

        verify(mockPrintStream).append(contains("a1h8"));
        verify(mockPrintStream).append(contains("move"));
    }

}
