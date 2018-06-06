package student.constraints;

import student.Assignment;

import java.util.Set;

public interface Constraint {
    Set<String> getScope();
    boolean evaluate(Assignment assignment);
}
