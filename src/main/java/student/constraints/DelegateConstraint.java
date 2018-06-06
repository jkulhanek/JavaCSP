package student.constraints;

import student.Assignment;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class DelegateConstraint implements Constraint {
    private final HashSet<String> scope;

    public DelegateConstraint(Collection<String> dependencies){
        this.scope = new HashSet<>(dependencies);
    }

    @Override
    public Set<String> getScope() {
        return scope;
    }

    @Override
    public abstract boolean evaluate(Assignment assignment);
}
