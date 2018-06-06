package student;

import student.constraints.Constraint;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CSP{
    List<String> getVariables();
    Map<String, Set<Object>> getDomains();
    Collection<Constraint> getConstraints();
}