package student.constraints;

import student.Assignment;

import java.util.Collection;
import java.util.stream.Collectors;

public final class AndConstraint extends LogicalConstraint {

    AndConstraint(Collection<ConstraintBase> constraints){
        super(constraints);
    }

    @Override
    public boolean evaluate(Assignment assignment) {
        for(ConstraintBase constraint : this.children){
            if(!constraint.evaluate(assignment))
                return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "(" + this.children.stream().map(x->x.toString()).collect(Collectors.joining(" && ")) + ")";
    }
}
