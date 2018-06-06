package student;

import student.constraints.Constraint;

import java.util.*;

public class ConstraintEvaluator {
    private Map<String, List<Constraint>> constraintLookup;
    public ConstraintEvaluator(Collection<Constraint> constraints){
        this.constraintLookup = constructLookup(constraints);
    }

    private static Map<String,List<Constraint>> constructLookup(Collection<Constraint> constraints) {
        Map<String, List<Constraint>> lookup = new HashMap<>(constraints.size());
        for(Constraint constr : constraints) {
            for (String var : constr.getScope()) {
                List<Constraint> valueSet = lookup.computeIfAbsent(var, k -> new LinkedList<>());
                valueSet.add(constr);
            }
        }

        return lookup;
    }

    private boolean isValidForScope(Constraint constraint, String defaultVarName, Set<String> assignedVariables) {
        for(String dependency: constraint.getScope()){
            if(!dependency.equals(defaultVarName) && !assignedVariables.contains(dependency)){
                return false;
            }
        }

        return true;
    }

    public boolean isSatisfied(Assignment assignment, String lastVariable){
        Set<String> assignedVariables = assignment.getAssignedVariables();
        List<Constraint> constraints = constraintLookup.get(lastVariable);

        if(constraints == null){
            return true;
        }

        for(Constraint constraint: constraints){
            if(isValidForScope(constraint, lastVariable, assignedVariables)){
                if(!constraint.evaluate(assignment)){
                    return false;
                }
            }
        }

        return true;
    }
}
