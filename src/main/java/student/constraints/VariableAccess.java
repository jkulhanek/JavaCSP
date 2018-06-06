package student.constraints;

import student.Assignment;
import student.Variable;

import java.util.Collection;

public class VariableAccess<TValue> extends Atomic<TValue> {

    private String var;

    public VariableAccess(String variableName){
        this.var = variableName;
    }

    @Override
    protected void getScope(Collection<String> variables) {
        variables.add(var);
    }

    @Override
    protected TValue getValue(Assignment assignment) {
        return assignment.getValue(this.var);
    }

    @Override
    public String toString() {
        return "$" + this.var;
    }
}
