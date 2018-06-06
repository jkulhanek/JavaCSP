package student.algorithm;

import student.Assignment;
import student.Domain;

public interface VariableOrderHeuristic {
    String selectVariable(Assignment assignment, Domain domains);
}
