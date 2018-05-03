package monte.carlo;

import monte.linear_programming.StandardForm;

import java.util.HashMap;
import java.util.stream.Collectors;

public class MultiPoint {

    private static final double acceptableError = 0.01;

    public static HashMap<String,Double> nextGuide(HashMap<String, Double> currentIdPoint,
                                                   HashMap<String, Double> globalBorder,
                                                   double gridUnit, double size) {
        HashMap<String,Double> guide = new HashMap<>();
        for( String dimension : currentIdPoint.keySet()){
            double newValue = currentIdPoint.get(dimension) + size*gridUnit;
            if( newValue - globalBorder.get(dimension) > acceptableError){
                return null;
            }
            guide.put(dimension, newValue);
        }
        return guide;
    }

    public static HashMap<String,Double> getDecrement(String decrementedDimension,
                                                      HashMap<String,Double> currentIdPoint,
                                                      double gridUnit, double size) {

        HashMap<String,Double> point = new HashMap<>();
        for( String dimension : currentIdPoint.keySet()){
            double value = currentIdPoint.get(dimension);
            if( dimension.equals(decrementedDimension)){
                value -= gridUnit*size;
                if( value <= 0 ){
                    return null;
                }
            }
            point.put(dimension, value);
        }
        //check if not guide unit
        Double previousValue = null;
        for( String dimension : currentIdPoint.keySet()){
            if( previousValue == null ){
                previousValue = point.get(dimension);
            }else{
                if( Math.abs(point.get(dimension) - previousValue) > acceptableError ){
                    return point;
                }
            }
        }
        return null;
    }

    public static String hash(HashMap<String, Double> decrementBranch, double size, double gridUnit) {
        double baseUnit = size*gridUnit;

        return decrementBranch.entrySet().stream().map(stringDoubleEntry -> {
            double value = stringDoubleEntry.getValue();
            double intRatio = Math.floor(value / baseUnit);
            double leftBorder = intRatio * baseUnit;
            double rightBorder = (intRatio+1) * baseUnit;

            return ( value - leftBorder) > (rightBorder - value) ?
                    String.valueOf(rightBorder) :
                    String.valueOf(leftBorder);

        }).collect(Collectors.joining("|"));
    }

    public static HashMap<String,Double> getMiddle(StandardForm standardForm, long initSize) {
        HashMap<String, Double> middlePoint = new HashMap<>();
        standardForm.decisionVariables.forEach(
                var -> middlePoint.put(var, (0.5*initSize)));

        return middlePoint;
    }

    public static double distance(HashMap<String, Double> previousPoint, HashMap<String, Double> currentPoint) {
        double quadraticSum = previousPoint.keySet().stream()
                .mapToDouble(var -> Math.pow(
                        Math.abs(previousPoint.get(var) - currentPoint.get(var)), 2)
                )
                .sum();

        return Math.sqrt(quadraticSum);
    }
}
