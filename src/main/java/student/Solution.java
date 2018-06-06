package student;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    private final Map<String, Object> assignment;

    public Solution(Map<String, Object> assignment){
        this.assignment = new HashMap<>(assignment);
    }

    public Object get(String variable){
        return this.assignment.get(variable);
    }

    @Override
    public boolean equals(Object obj) {
        Solution other = (Solution)obj;
        if(other == null){
            return false;
        }

        if(other.assignment.size() != this.assignment.size())
            return false;

        for(String key: this.assignment.keySet()){
            if(!this.assignment.get(key).equals(other.assignment.get(key)))
                return false;
        }

        return true;
    }
}
