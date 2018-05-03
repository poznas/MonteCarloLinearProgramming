package monte.parse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Detector {

    private Collection<String> buildInFunctions;
    private Collection<String> buildInOperators;
    private Collection<String> braces;

    public Detector() {
        readProperties();
    }

    private void readProperties() {
        File file = new File(getClass().getClassLoader()
                .getResource("exp4j/exp4j.xml").getFile());
        FileInputStream fileInput;
        try {
            fileInput = new FileInputStream(file);
            Properties properties = new Properties();
            properties.loadFromXML(fileInput);
            fileInput.close();

            loadBuildIns(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBuildIns(Properties properties) {
        buildInFunctions = new DefaultListDelimiterHandler('\n')
                .split(properties.getProperty("functions"), true);
        buildInOperators = new DefaultListDelimiterHandler('\n')
                .split(properties.getProperty("operators"), true);
        braces = new DefaultListDelimiterHandler('\n')
                .split(properties.getProperty("braces"), true);
    }

    public List<String> variables(String expression){

        String cleared = whitespaceBuildIns(expression);
        Stream<String> variables = removeNumbers(cleared.split("\\s+"));

        return variables.distinct().collect(Collectors.toList());
    }

    private Stream<String> removeNumbers(String[] split) {
        return Arrays.stream(split).parallel()
                .filter(candidate -> !candidate.matches("\\d+"))
                .map(this::separateVariables)
                .flatMap(Collection::stream);
    }

    private Collection<? extends String> separateVariables(String text) {
        List<String> variables = new ArrayList<>();
        String current = "";
        boolean buildVariable = false;
        char c;
        for( int i=0; i<text.length(); i++ ){
            c = text.charAt(i);
            if (!Character.isDigit(c)) {
                if(!current.equals("") && !buildVariable){
                    variables.add(current);
                    current = "";
                }
                buildVariable = true;
                current += String.valueOf(c);
            }else{
                buildVariable = false;
                if(!current.equals("")){
                    current += c;
                }
            }
        }
        if(!current.equals("")){
            variables.add(current);
        }
        return variables;
    }

    private String whitespaceBuildIns(String expression) {

        Iterable<String> buildIns = CollectionUtils.union(
                CollectionUtils.union(buildInFunctions, buildInOperators), braces);

        for( String buildIn : buildIns){
            expression = expression.replaceAll(buildIn, " ");
        }
        return expression;
    }
}
