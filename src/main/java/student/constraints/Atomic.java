package student.constraints;
import student.Assignment;

import java.util.Collection;

abstract class Atomic<TValue> {
    protected abstract void getScope(Collection<String> variables);

    protected abstract TValue getValue(Assignment assignment);
}
