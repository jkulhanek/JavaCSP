package student.algorithm;
import student.Assignment;
import student.CSP;
import student.Domain;
import student.constraints.Constraint;

import java.util.*;

public final class MostConstrainingVariableHeuristic extends Heuristic implements VariableOrderHeuristic {

    @Override
    public String selectVariable(Assignment assignment, Domain domains) {
        // Find maximum
        Integer maximum = null;
        String selectedVar = null;
        for(String var : this.getUnassignedVariables(assignment)){
            Integer priority = this.variables.get(var);
            if(maximum == null || maximum < priority){
                maximum = priority;
                selectedVar = var;
            }
        }

        return selectedVar;
    }

    private class ValueWithPriority implements Comparable{
        public ValueWithPriority(String variable, Integer priority){
            this.priority = priority;
            this.variable = variable;
        }

        public String variable;
        public Integer priority;

        @Override
        public int compareTo(Object o) {
            return -this.priority.compareTo(((ValueWithPriority)o).priority);
        }
    }

    private Map<String, Integer> variables;

    public MostConstrainingVariableHeuristic(CSP csp) {
        super(csp);
        variables = new HashMap<>();
        for(String var : csp.getVariables()){
            variables.put(var, 0);
        }

        for(Constraint constr: csp.getConstraints()){
            for(String var : constr.getScope()){
                variables.compute(var, (str, old) -> old + 1);
            }
        }
    }
}
