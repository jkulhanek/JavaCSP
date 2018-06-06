package student.constraints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public final class c {
    public static ConstraintBase and(ConstraintBase c1, ConstraintBase c2){
        return new AndConstraint(Arrays.asList(c1, c2));
    }

    public static ConstraintBase and(Collection<ConstraintBase> c){
        return new AndConstraint(c);
    }

    public static ConstraintBase or(ConstraintBase c1, ConstraintBase c2){
        return new OrConstraint(Arrays.asList(c1, c2));
    }

    public static ConstraintBase or(Collection<ConstraintBase> c){
        return new OrConstraint(c);
    }

    public static <TValue> ConstraintBase eq(Atomic<TValue> a1, Atomic<TValue> a2) {
        return new EqualConstraint(a1, a2);
    }

    public static ConstraintBase lt(Atomic<Integer> a1, Atomic<Integer> a2) {
        return new LessThanConstraint(a1, a2);
    }

    public static ConstraintBase gt(Atomic<Integer> a1, Atomic<Integer> a2) {
        return new GreaterThanConstraint(a1, a2);
    }

    public static ConstraintBase le(Atomic<Integer> a1, Atomic<Integer> a2) {
        return new LessThanEqualConstraint(a1, a2);
    }

    public static ConstraintBase ge(Atomic<Integer> a1, Atomic<Integer> a2) {
        return new GreaterThanEqualConstraint(a1, a2);
    }

    public static <TValue> Atomic<TValue> c(TValue value){
        return new Constant<>(value);
    }

    public static <TValue> Atomic<TValue> v(String name){
        return new VariableAccess<TValue>(name);
    }

    public static Atomic<Integer> plus(Atomic<Integer> c1, Atomic<Integer> c2){
        return new Plus(Arrays.asList(c1, c2));
    }

    public static Atomic<Integer> plus(Collection<Atomic<Integer>> c){
        return new Plus(c);
    }

    public static ConstraintBase not(ConstraintBase c){
        return new Not(c);
    }
}
