package student.algorithm;

import student.Assignment;
import student.BinaryConstraintLookup;
import student.Domain;
import student.constraints.Constraint;

import java.util.*;

public class AC3 {
    private final Collection<String> variables;
    private final BinaryConstraintLookup constraintLookup;

    public AC3(Collection<String> variables, BinaryConstraintLookup constraintLookup){
        this.variables = variables;
        this.constraintLookup = constraintLookup;
    }

    private boolean evaluateConstraints(Assignment assignment, Collection<Constraint> constraints){
        for(Constraint constraint : constraints) {
            if (!constraint.evaluate(assignment)) {
                return false;
            }
        }

        return true;
    }

    public void filterDomain(Assignment assignment, Domain domain){
        List<String> allVariables = new LinkedList<>(this.variables);
        allVariables.removeAll(assignment.getAssignedVariables());
        Queue<String> variables = new LinkedList<String>(allVariables);


        while(!variables.isEmpty()){
            String x = variables.poll();
            for(String y:allVariables){
                if(removeValues(assignment, domain, x,y)){
                    if(domain.get(y).size() == 0){
                        return;
                    }

                    variables.add(y);
                }
            }
        }

        return;
    }

    private boolean removeValues(Assignment assignment, Domain domain, String var1, String var2){
        boolean removed = false;
        Collection<Constraint> constraints = constraintLookup.getConstraints(var1, var2);
        if(constraints == null || constraints.isEmpty())
            return removed;

        List<Object> valuesToRestrict = new LinkedList<>();
        for(Object val2 : domain.get(var2)){
            assignment = assignment.set(var2, val2);
            boolean isAnySatisfied = false;
            for(Object val1 : domain.get(var1)){
                assignment = assignment.set(var1, val1);
                if(evaluateConstraints(assignment, constraints)){
                    isAnySatisfied = true;
                    assignment = assignment.reset(var1);
                    break;
                }
                assignment = assignment.reset(var1);
            }

            if(!isAnySatisfied){
                valuesToRestrict.add(val2);
                removed = true;
            }

            assignment = assignment.reset(var2);
        }

        if(removed){
            domain.restrict(var2, valuesToRestrict);
        }

        return removed;
    }
}
