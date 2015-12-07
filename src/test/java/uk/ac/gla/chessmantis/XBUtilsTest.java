package uk.ac.gla.chessmantis;

import org.junit.Test;
import uk.ac.gla.chessmantis.Moveable;
import uk.ac.gla.chessmantis.XBUtils;

import static org.junit.Assert.*;

public class XBUtilsTest {

    @Test
    public void stringToMoveShouldConvertFromPositionTo64SquareRepresentation() {
        String bottomLeftToTopRight = "a1h8";

        Moveable result = XBUtils.stringToMove(bottomLeftToTopRight);
        assertTrue(result.getFromPosition() == 0);
    }

    @Test
    public void stringToMoveShouldConvertToPositionTo64SquareRepresentation() {
        String bottomLeftToTopRight = "a1h8";

        Moveable result = XBUtils.stringToMove(bottomLeftToTopRight);
        assertTrue(result.getToPosition() == 63);
    }
}
