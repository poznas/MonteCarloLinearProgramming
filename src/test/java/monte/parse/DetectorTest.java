package monte.parse;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DetectorTest {

    private Detector detector;

    @Before
    public void setUp() throws Exception {
        detector = new Detector();
    }

    @Test
    public void detectsAllVariablesProperlyFromString() {
        List<String> expectedVariables = Arrays.asList("x1","x2","x3");
        String testInput = "x2x1sin(2x2^0.5x3)/[x1*x2]-0.1x1";

        List<String> output = detector.variables(testInput);

        assertEquals(new HashSet<>(output), new HashSet<>(expectedVariables));
    }
}