package monte.carlo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ThreadLocalRandom;

public class RandomPointGenerator extends CountedCompleter<List<HashMap<String,Double>>> {

    private static final double GRID_UNIT = 0.2;
    private static final int POINTS_PER_UNIT = 10;
    private double size;
    private HashMap<String,Double> shift;
    private HashMap<String,Double> globalBorder;
    private HashMap<String,Double> currentIdPoint;
    private CopyOnWriteArrayList<String> handledSpaces;

    private List<RandomPointGenerator> childTasks;
    private List<HashMap<String,Double>> resultPoints;


    public RandomPointGenerator(List<String> variables, double radius, HashMap<String, Double> middlePoint){
        globalBorder = new HashMap<>();
        currentIdPoint = new HashMap<>();
        this.size = radius*2.0;

        for( String variable : variables ){
            globalBorder.put(variable, size);
            currentIdPoint.put(variable, size*GRID_UNIT);
        }
        handledSpaces = new CopyOnWriteArrayList<>();

        shift = new HashMap<>();
        for( String dimension : middlePoint.keySet()){
            shift.put(dimension, middlePoint.get(dimension) - radius);
        }
    }

    protected RandomPointGenerator(CountedCompleter<?> completer,
                                   HashMap<String, Double> vector,
                                   HashMap<String, Double> shift,
                                   CopyOnWriteArrayList<String> handledSpaces,
                                HashMap<String, Double> globalBorder, double size) {
        super(completer);
        this.currentIdPoint = vector;
        this.shift = shift;
        this.globalBorder = globalBorder;
        this.handledSpaces = handledSpaces;
        this.size = size;
    }

    public List<HashMap<String, Double>> getResultPoints() {
        return resultPoints;
    }

    @Override
    public void compute() {

        resultPoints = new ArrayList<>();
        childTasks = new ArrayList<>();

        if( globalBorder != null ){
            spawnNextGuideBranch();
        }
        spawnDecrementBranches();
        generateRandomPoints();
        tryComplete();
    }

    @Override
    public void onCompletion(CountedCompleter<?> caller) {
        for( RandomPointGenerator generator : childTasks){
            resultPoints.addAll(generator.getResultPoints());
        }
    }

    private void spawnNextGuideBranch() {

        HashMap<String, Double> nextGuidePoint = NPoint.nextGuide(
                currentIdPoint, globalBorder, GRID_UNIT, size);
        if( nextGuidePoint == null ){
            return;
        }
        RandomPointGenerator nextGuide = new RandomPointGenerator(
                this, nextGuidePoint, shift, handledSpaces, globalBorder, size);
        nextGuide.fork();
        addToPendingCount(1);
        childTasks.add(nextGuide);
    }

    private void spawnDecrementBranches() {

        for( String dimension : currentIdPoint.keySet() ){
            HashMap<String, Double> decrementPoint = NPoint.getDecrement(
                    dimension, currentIdPoint, GRID_UNIT, size);

            if( decrementPoint != null ){
                String id = NPoint.hash(decrementPoint, size, GRID_UNIT);
                if( !handledSpaces.contains(id) ){
                    handledSpaces.add(id);

                    RandomPointGenerator newBranch = new RandomPointGenerator(
                            this, decrementPoint, shift, handledSpaces, null, size);

                    newBranch.fork();
                    addToPendingCount(1);
                    childTasks.add(newBranch);
                }
            }
        }
    }

    private void generateRandomPoints() {
        double max, min;

        for( int i=0; i<POINTS_PER_UNIT; i++ ){
            HashMap<String, Double> point = new HashMap<>();
            boolean nonNegativeValues = true;
            for( String dimension : currentIdPoint.keySet()){
                max = currentIdPoint.get(dimension);
                min = max - GRID_UNIT*size;

                double randomValue = ThreadLocalRandom.current().nextDouble(min, max) + shift.get(dimension);
                if( randomValue < 0 ){
                    nonNegativeValues = false;
                    break;
                }
                point.put(dimension, randomValue);
            }
            if( nonNegativeValues ){
                resultPoints.add(point);
            }
        }
    }
}
