package student;

import java.util.*;

public class Domain{
    private Map<String, Set<Object>> domainRepresentation;
    private Map<String, Set<Object>> originalDomain;

    public Domain(Map<String, Set<Object>> values){
        domainRepresentation = new HashMap<>(values);
        originalDomain = values;
    }

    public void restrict(String variable, Collection<Object> values){
        if(values.size() == 0)
            return;

        Set<Object> valueSet;
        if(originalDomain.get(variable) == (valueSet = domainRepresentation.get(variable))){
            domainRepresentation.put(variable, (valueSet = new HashSet<Object>(valueSet)));
        }

        valueSet.removeAll(values);
    }

    public Set<Object> get(String variable){
        return domainRepresentation.get(variable);
    }

    public Domain inherit(){
        return new Domain(this.domainRepresentation);
    }
}