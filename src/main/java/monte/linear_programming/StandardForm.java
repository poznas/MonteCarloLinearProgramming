package monte.linear_programming;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import monte.Main;
import monte.viewer.Viewer;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class StandardForm {

    public String objectiveFunction;
    public List<String> decisionVariables;
    public List<Constraint> constraints;


    public StandardForm() {
    }

    public static void loadFromJson() {
        try {
            JsonReader reader = new JsonReader(new FileReader(Main.currentFile));
            Main.standardForm = new Gson().fromJson(reader, StandardForm.class);
            Viewer.show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void saveToJson() {
        if (Main.currentFile == null) {
            return;
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (Writer writer = new FileWriter(Main.currentFile)) {
            gson.toJson(Main.standardForm, StandardForm.class, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNonnegativeVarConstraints() {
        if( constraints == null ){
            constraints = new ArrayList<>();
        }
        decisionVariables.forEach( var ->
                constraints.add(new Constraint(var,"0",true)));
    }

    public boolean meetsConstraints(HashMap<String, Double> point){
        for( Constraint constraint : constraints){
            double leftSide = evaluate(constraint.leftSide, point);
            double rightSide = evaluate(constraint.rightSide, point);
            if( (leftSide > rightSide) != constraint.gt ){
                return false;
            }
        }
        return true;
    }

    public double calculateFunction(HashMap<String, Double> point){
        return evaluate(objectiveFunction, point);
    }

    private double evaluate(String equation, HashMap<String, Double> point){
        Expression expression = new ExpressionBuilder(equation)
                .variables(new HashSet<>(decisionVariables)).build();

        point.keySet().forEach(var -> expression.setVariable(var, point.get(var)));
        return expression.evaluate();
    }

    public String result(HashMap<String,Double> point) {
        StringBuilder result = new StringBuilder(objectiveFunction + " = "
                + evaluate(objectiveFunction, point) + "\n");

        constraints.forEach( constraint -> {
            result.append(constraint.leftSide).append(" = ")
                    .append(evaluate(constraint.leftSide, point));
            if ((constraint.gt)) {
                result.append(" >= ");
            } else {
                result.append(" =< ");
            }
            result.append(constraint.rightSide).append(" = ")
                    .append(evaluate(constraint.rightSide, point)).append("\n");
        });
        return result.toString();
    }

    public double progress(HashMap<String,Double> previousPoint, HashMap<String,Double> currentPoint) {
        return Math.abs(calculateFunction(previousPoint) - calculateFunction(currentPoint));
    }
}
