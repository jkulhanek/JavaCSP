package student.constraints;

import student.Assignment;

import java.util.Collection;

public final class EqualConstraint<TValue> extends ConstraintBase {

    private final Atomic<TValue> second;
    private final Atomic<TValue> first;

    public EqualConstraint(Atomic<TValue> first, Atomic<TValue> second){
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean evaluate(Assignment assignment) {
        return first.getValue(assignment).equals(second.getValue(assignment));
    }

    @Override
    protected void getScope(Collection<String> variables) {
        first.getScope(variables);
        second.getScope(variables);
    }

    @Override
    public String toString() {
        return "(" + first.toString() + " == " + second.toString() + ")";
    }
}
