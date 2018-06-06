package student.algorithm;
import student.Assignment;
import student.CSP;
import student.Domain;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class ValueOrderHeuristicFactory {
    private final CSP csp;
    private ForwardChecking forwardChecking;

    public ValueOrderHeuristicFactory(CSP csp, ForwardChecking forwardChecking){
        this.csp = csp;
        this.forwardChecking = forwardChecking;
    }

    public ValueOrderHeuristic create(String name){
        if(name == null) return new NullValueOrderHeuristic();

        switch(name){
            case "LeastConstrainingValue":
                return new LeastConstrainingValueHeuristic(this.csp, forwardChecking);
            default:
                throw new InvalidParameterException();
        }
    }

    private class NullValueOrderHeuristic implements ValueOrderHeuristic {
        @Override
        public List<Object> orderValues(String variable, Domain domains, Assignment assignment) {
            return new ArrayList<>(domains.get(variable));
        }
    }
}
