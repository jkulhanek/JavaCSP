package student.constraints;

import student.Assignment;

import java.util.Collection;

public abstract class Numeric<TValue> extends Atomic<TValue> {
    protected final Collection<Atomic<TValue>> children;

    public Numeric(Collection<Atomic<TValue>> children){
        this.children = children;
    }

    @Override
    protected void getScope(Collection<String> variables){
        for (Atomic child : children) {
            child.getScope(variables);
        }
    }
}
