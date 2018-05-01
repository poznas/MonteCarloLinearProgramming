package monte.linear_programming;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.*;

public class StandardFormTest {

    private StandardForm form;
    private HashMap<String, Double> correctPoint;
    private HashMap<String, Double> incorrectPoint;

    @Before
    public void setUp() throws Exception {
        form = new StandardForm();
        form.objectiveFunction = "4x1 +6x2 +3x3 +12x4";
        form.decisionVariables = Arrays.asList("x1","x2","x3","x4");
        form.constraints = new ArrayList<>();
        form.constraints.add(new Constraint("x1 + x2 + x3","300",true));
        form.constraints.add(new Constraint("sin(x1)","0.95",true));

        correctPoint = new HashMap<String, Double>(){{
            put("x1",152.64444865810947);
            put("x2",1259.3254413047748);
            put("x3",1907.1878933005914);
            put("x4",212.0693899437365);
        }};
        incorrectPoint = new HashMap<String, Double>(){{
            put("x1",0.64444865810947);
            put("x2",1259.3254413047748);
            put("x3",1907.1878933005914);
            put("x4",212.0693899437365);
        }};
    }

    @Test
    public void correctPointPassesConstraints() {
        boolean result = form.meetsConstraints(correctPoint);

        assertTrue(result);
    }

    @Test
    public void incorrectPointFailsContraints() {
        boolean result = form.meetsConstraints(incorrectPoint);

        assertFalse(result);
    }

    @Test
    public void addNonnegativeVarConstraintsMethodAddsAsManyConstraintsAsFormHasVariables() {
        int previousSize = form.constraints.size();
        int variablesCount = form.decisionVariables.size();

        form.addNonnegativeVarConstraints();;
        int currentSize = form.constraints.size();

        assertEquals(variablesCount, currentSize - previousSize);
    }
}