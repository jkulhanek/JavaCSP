package student.algorithm;

import student.Assignment;
import student.Domain;

import java.util.List;

public interface ValueOrderHeuristic {
    List<Object> orderValues(String variable, Domain domains, Assignment assignment);
}
