package student;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StaticAssignment implements Assignment {
    private Map<String, Object> values = new HashMap<>();
    private Set<String> cachedAssignedVariables = new HashSet<>();
    private Integer assignedCount = 0;

    @Override
    public <TValue> TValue getValue(String name) {
        return (TValue)this.getValues().get(name);
    }

    @Override
    public Assignment reset(String name){
        if(this.cachedAssignedVariables.remove(name)){
            this.values.remove(name);
            this.assignedCount--;
        }
        else{
            throw new InvalidParameterException("Name is invalid");
        }

        return this;
    }

    @Override
    public int size(){
        return assignedCount;
    }

    @Override
    public Map<String, Object> getValues(){
        return this.values;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder("[SimpleVariableAssignment {");
        for(Map.Entry<String, Object> entr: this.getValues().entrySet())
            builder.append(entr.getKey() + "=" + entr.getValue() + ",");
        return builder.toString();
    }

    @Override
    public Set<String> getAssignedVariables(){
        return this.cachedAssignedVariables;
    }

    @Override
    public Assignment set(String name, Object value){
        this.cachedAssignedVariables.add(name);
        this.values.put(name, value);
        this.assignedCount++;
        return this;
    }
}
