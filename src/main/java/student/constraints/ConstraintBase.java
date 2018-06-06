package student.constraints;

import student.Assignment;
import java.util.HashSet;
import java.util.Set;

public abstract class ConstraintBase extends Atomic<Boolean> implements Constraint {
    private Set<String> cachedScope;

    @Override
    public final Set<String> getScope(){
        if(cachedScope == null) {
            cachedScope = new HashSet<>();
            this.getScope(cachedScope);
        }
        return cachedScope;
    }

    @Override
    public abstract boolean evaluate(Assignment assignment);

    @Override
    protected Boolean getValue(Assignment assignment) {
        return evaluate(assignment);
    }
}
