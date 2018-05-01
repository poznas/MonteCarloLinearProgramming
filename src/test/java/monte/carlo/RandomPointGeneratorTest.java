package monte.carlo;

import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ForkJoinPool;

import static monte.carlo.RandomPointGenerator.GRID_UNIT;
import static monte.carlo.RandomPointGenerator.POINTS_PER_UNIT;
import static org.junit.Assert.assertEquals;

public class RandomPointGeneratorTest {

    ForkJoinPool pool;
    RandomPointGenerator generator;

    @Before
    public void setUp() throws Exception {
        pool=new ForkJoinPool();
    }

    @Test
    public void generatesExpectedNumberOfPointsForMultiDimensionalSpaces() {
        List<String> variables;
        HashMap<String, Double> middlePoint;
        int expectedSize, actualSize;
        variables = new ArrayList<String>(){{add("x1");}};
        middlePoint = new HashMap<String, Double>(){{ put("x1",1000.0); }};

        for( int n=2; n<6; n++ ){
            variables.add("x"+n);
            middlePoint.put("x"+n,1000.0);
            expectedSize = getExpectedSize(n);
            actualSize = runGenerator(variables, middlePoint);
            assertEquals(expectedSize,actualSize);
        }
    }

    private int getExpectedSize(int n) {
        return (int) Math.round(Math.pow(GRID_UNIT,-n) * POINTS_PER_UNIT);
    }

    private int runGenerator(List<String> variables, HashMap<String, Double> middlePoint) {
        RandomPointGenerator generator = new RandomPointGenerator(variables, 1000, middlePoint);
        pool.execute(generator);
        generator.join();

        return generator.getResultPoints().size();
    }
}