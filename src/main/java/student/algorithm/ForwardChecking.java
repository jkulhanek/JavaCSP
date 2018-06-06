package student.algorithm;

import student.Assignment;
import student.BinaryConstraintLookup;
import student.Domain;
import student.constraints.Constraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ForwardChecking {
    private final BinaryConstraintLookup binaryConstraintLookup;

    public ForwardChecking(BinaryConstraintLookup binaryConstraintLookup){
        this.binaryConstraintLookup = binaryConstraintLookup;
    }

    public int execute(Assignment assignment,
                                String assignedVariable,
                                Domain domains,
                                Collection<String> unassignedVariables){
        // For speed of the algorithm, we will only consider the binary constraints
        int removedValues = 0;
        for(String var : unassignedVariables){
            Collection<Constraint> constraints = binaryConstraintLookup.getConstraints(var, assignedVariable);
            boolean isAnyValid = false;
            List<Object> invalidValues = new ArrayList<>();
            for(Object val: domains.get(var)) {
                assignment = assignment.set(var, val);
                boolean isValid = true;
                if (constraints != null) {
                    for (Constraint constr : constraints) {
                        if (!constr.evaluate(assignment)) {
                            isValid = false;
                            break;
                        }
                    }
                }

                if(isValid){
                    isAnyValid = true;
                }
                else{
                    invalidValues.add(val);
                    removedValues++;
                }
                assignment = assignment.reset(var);
            }

            domains.restrict(var, invalidValues);

            if(!isAnyValid){
                // Empty domain
                return -1;
            }
        }

        return removedValues;
    }
}
