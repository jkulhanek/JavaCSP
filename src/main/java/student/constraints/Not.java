package student.constraints;

import student.Assignment;

import java.util.Collection;

public final class Not extends ConstraintBase {

    private final ConstraintBase constraint;

    public Not(ConstraintBase c){
        this.constraint = c;
    }

    @Override
    public boolean evaluate(Assignment assignment) {
        return !constraint.evaluate(assignment);
    }

    @Override
    protected void getScope(Collection<String> variables) {
        constraint.getScope(variables);
    }

    @Override
    public String toString() {
        return "!" + this.constraint.toString();
    }
}
