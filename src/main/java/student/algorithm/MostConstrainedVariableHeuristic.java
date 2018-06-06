package student.algorithm;

import student.Assignment;
import student.CSP;
import student.Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MostConstrainedVariableHeuristic implements VariableOrderHeuristic {
    private final List<String> variables;

    public MostConstrainedVariableHeuristic(CSP csp) {
        this.variables = csp.getVariables();
    }

    public String selectVariable(Assignment assignment, Domain domains) {
        // Most constrained value
        Set<String> assignedVariables = assignment.getAssignedVariables();
        List<String> allVariables = new ArrayList<>(this.variables);
        allVariables.removeAll(assignedVariables);

        String selectedVar = null;
        Integer minimum = null;
        for (String variable : allVariables) {
            int domainSize = domains.get(variable).size();

            if (minimum == null || minimum > domainSize) {
                minimum = domainSize;
                selectedVar = variable;
            }
        }

        return selectedVar;
    }
}
