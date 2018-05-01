package monte.carlo;

import monte.linear_programming.Constraint;
import monte.linear_programming.StandardForm;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MonteCarloTest {

    private StandardForm form;
    private static final double ACCEPTABLE_ERROR = 0.1;

    @Before
    public void setUp() throws Exception {
        form = new StandardForm();
    }

    @Test
    public void returnsNegligiblyDifferentResultMax() {
        form.objectiveFunction = "4x1 +6x2 +3x3 +12x4";
        form.decisionVariables = Arrays.asList("x1","x2","x3","x4");
        form.addNonnegativeVarConstraints();
        form.constraints.add(new Constraint("x1+2x2+1.5x3+6x4","90000",false));
        form.constraints.add(new Constraint("2x1+2x2+1.5x3+4x4","120000",false));
        HashMap<String, Double> correctPoint = new HashMap<>();
        correctPoint.put("x1",30000.0);
        correctPoint.put("x2",30000.0);
        correctPoint.put("x3",0.0);
        correctPoint.put("x4",0.0);

        HashMap<String, Double> resultPoint = MonteCarlo.get(form,10000.0,true,null);
        assertNotNull(resultPoint);
        for( String dimension : form.decisionVariables){
            assertEquals(
                    correctPoint.get(dimension),
                    resultPoint.get(dimension),
                    ACCEPTABLE_ERROR);
        }
    }

    @Test
    public void returnsNegligiblyDifferentResultMin() {
        form.objectiveFunction = "20x1 + 30x2";
        form.decisionVariables = Arrays.asList("x1","x2");
        form.addNonnegativeVarConstraints();
        form.constraints.add(new Constraint("x1","3000",true));
        form.constraints.add(new Constraint("x1","7000",false));
        form.constraints.add(new Constraint("x2","4000",true));
        form.constraints.add(new Constraint("x2","8000",false));
        form.constraints.add(new Constraint("x1+x2","12000",false));
        form.constraints.add(new Constraint("-x1+2x2","2000",true));
        form.constraints.add(new Constraint("2x1+2x2","18000",true));
        form.constraints.add(new Constraint("-2x1+3x2","6000",false));
        HashMap<String, Double> correctPoint = new HashMap<>();
        correctPoint.put("x1",5000.0);
        correctPoint.put("x2",4000.0);

        HashMap<String, Double> resultPoint = MonteCarlo.get(form,10000.0,false,null);
        assertNotNull(resultPoint);
        for( String dimension : form.decisionVariables){
            assertEquals(
                    correctPoint.get(dimension),
                    resultPoint.get(dimension),
                    ACCEPTABLE_ERROR);
        }
    }
}