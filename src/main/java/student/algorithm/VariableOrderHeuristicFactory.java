package student.algorithm;

import student.CSP;

import java.security.InvalidParameterException;

public class VariableOrderHeuristicFactory {
    private final CSP csp;

    public VariableOrderHeuristicFactory(CSP csp){
        this.csp = csp;
    }

    public VariableOrderHeuristic create(String name){
        switch(name){
            case "MostConstrainingVariable":
                return new MostConstrainingVariableHeuristic(this.csp);
            case "MostConstrainedVariable":
                return new MostConstrainedVariableHeuristic(this.csp);
                default:
                    throw new InvalidParameterException();
        }
    }
}
