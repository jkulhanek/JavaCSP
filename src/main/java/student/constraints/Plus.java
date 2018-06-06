package student.constraints;

import student.Assignment;

import java.util.Collection;
import java.util.stream.Collectors;

public final class Plus extends Numeric<Integer> {
    public Plus(Collection<Atomic<Integer>> children) {
        super(children);
    }

    @Override
    protected Integer getValue(Assignment assignment) {
        int sum = 0;
        for(Atomic child: this.children){
            sum += (Integer)child.getValue(assignment);
        }

        return sum;
    }

    @Override
    public String toString() {
        return "(" + this.children.stream().map(x->x.toString()).collect(Collectors.joining(" + ")) + ")";
    }
}
