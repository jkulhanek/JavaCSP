package student;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface Assignment {
    <TValue> TValue getValue(String name);

    int size();

    Map<String, Object> getValues();

    Set<String> getAssignedVariables();

    Assignment reset(String name);

    Assignment set(String name, Object value);
}