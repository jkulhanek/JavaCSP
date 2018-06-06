package student.constraints;

import student.Assignment;

import java.util.Collection;

public final class Constant<TValue> extends Atomic<TValue>{
    private final TValue value;

    public Constant(TValue value){
        this.value = value;
    }

    @Override
    protected void getScope(Collection<String> variables) {

    }

    @Override
    protected TValue getValue(Assignment assignment) {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
