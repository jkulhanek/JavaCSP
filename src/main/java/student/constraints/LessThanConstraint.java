package student.constraints;

import student.Assignment;

import java.util.Collection;

public final class LessThanConstraint extends ConstraintBase {

    private final Atomic<Integer> second;
    private final Atomic<Integer> first;

    public LessThanConstraint(Atomic<Integer> first, Atomic<Integer> second){
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean evaluate(Assignment assignment) {
        return first.getValue(assignment) < second.getValue(assignment);
    }

    @Override
    protected void getScope(Collection<String> variables) {
        first.getScope(variables);
        second.getScope(variables);
    }

    @Override
    public String toString() {
        return "(" + first.toString() + " < " + second.toString() + ")";
    }
}
