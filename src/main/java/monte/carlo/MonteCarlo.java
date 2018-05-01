package monte.carlo;

import monte.linear_programming.StandardForm;
import monte.viewer.Chart2D;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MonteCarlo implements Callable<HashMap<String, Double>> {

    private static final Logger logger = Logger.getLogger(MonteCarlo.class.getSimpleName());
    private Chart2D chart;

    private StandardForm standardForm;
    private boolean searchForMax;
    private double radius;
    public static final double INIT_SIZE = 10000;
    private static final double MIN_SEARCH_PROGRESS = 0.01;
    private double progress;

    private MonteCarlo(double initSize, StandardForm standardForm, boolean searchForMax, Chart2D chart) {
        radius = initSize / 2;
        this.standardForm = standardForm;
        this.searchForMax = searchForMax;
        this.chart = chart;
    }

    @Override
    public HashMap<String, Double> call() throws Exception {

        HashMap<String, Double> previousPoint, currentPoint;
        previousPoint = MultiPoint.getMiddle(standardForm, (long) (radius*2));

        logger.log(Level.INFO,
                "Calling Monte Carlo, initPoint: "
                + previousPoint + ", radius: " + radius);
        do{
            currentPoint = searchForCurrentPoint(previousPoint);
            if(chart != null)chart.addCurrentPoint(currentPoint);
            radius = MultiPoint.distance(currentPoint, previousPoint);

            logger.log(Level.INFO, "Search results: previous: " + previousPoint
                    + "\ncurrent: " + currentPoint
                    + "\nradius: " + radius);
            logger.log(Level.INFO, standardForm.result(currentPoint));

            previousPoint = currentPoint;

        }while (radius > MIN_SEARCH_PROGRESS);
        return currentPoint;
    }

    private HashMap<String, Double> searchForCurrentPoint(HashMap<String, Double> previousPoint) {

        ForkJoinPool pool=new ForkJoinPool();
        RandomPointGenerator generator = new RandomPointGenerator(
                standardForm.decisionVariables, radius, previousPoint);
        pool.execute(generator);
        generator.join();

        logger.log(Level.INFO, "Random points generated, size: " + generator.getResultPoints().size() );

        if( chart != null ) chart.add(generator.getResultPoints());
        pool.shutdown();

        Optional<HashMap<String, Double>> result = generator.getResultPoints()
                .parallelStream()
                .filter(point -> standardForm.meetsConstraints(point))
                .reduce( (A,B) -> {
                    if((standardForm.calculateFunction(A)
                            > standardForm.calculateFunction(B)) == searchForMax){
                        return A;
                    }else{
                        return B;
                    }
                });
        return result.orElse(previousPoint);
    }

    public static HashMap<String, Double> get(StandardForm standardForm, double initSize, boolean searchForMax, Chart2D chart){

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        CompletionService<HashMap<String, Double>> completionService = new ExecutorCompletionService<>(executor);
        completionService.submit(new MonteCarlo(initSize, standardForm,searchForMax, chart));

        try {
            Future<HashMap<String, Double>> result = completionService.take();
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }finally {
            executor.shutdown();
        }
    }
}
