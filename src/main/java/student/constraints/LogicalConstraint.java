package student.constraints;

import java.util.Collection;

public abstract class LogicalConstraint extends ConstraintBase {
    protected Collection<ConstraintBase> children;

    public LogicalConstraint(Collection<ConstraintBase> constraints){
        this.children = constraints;
    }

    @Override
    protected void getScope(Collection<String> variables) {
        if (children != null) {
            for (Atomic child : children) {
                child.getScope(variables);
            }
        }
    }
}
