package student.algorithm;

import student.Assignment;
import student.CSP;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class Heuristic {
    protected final CSP csp;
    protected final List<String> variables;

    public Heuristic(CSP csp){
        this.csp = csp;
        this.variables = csp.getVariables();
    }

    protected List<String> getUnassignedVariables(Assignment assignment){
        Set<String> assignedVariables = assignment.getAssignedVariables();
        List<String> unassigned = new ArrayList<>();
        for(String var : variables){
            if(!assignedVariables.contains(var)){
                unassigned.add(var);
            }
        }

        return unassigned;
    }
}
