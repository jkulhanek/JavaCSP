package student;

import student.constraints.Constraint;

import java.util.*;

public class BinaryConstraintLookup {
    private final Map<String, List<Constraint>> constraintLookup;

    public BinaryConstraintLookup(Collection<Constraint> binaryConstraints){
        this.constraintLookup = constructLookup(binaryConstraints);
    }

    private static String getLookupKey(String var1, String var2){
        if(var1.compareTo(var2) > 0){
            String tmp = var1;
            var1 = var2;
            var2 = tmp;
        }

        return var1 + "~" + var2;
    }

    private static Map<String,List<Constraint>> constructLookup(Collection<Constraint> binaryConstraints) {
        Map<String, List<Constraint>> lookup = new HashMap<>(binaryConstraints.size());
        for(Constraint constr : binaryConstraints){
            String[] scope = new String[2];
            constr.getScope().toArray(scope);
            String key = getLookupKey(scope[0], scope[1]);

            List<Constraint> valueSet = lookup.computeIfAbsent(key, k -> new LinkedList<Constraint>());
            valueSet.add(constr);
        }
        return lookup;
    }

    public Collection<Constraint> getConstraints(String var1, String var2){
        return constraintLookup.get(getLookupKey(var1, var2));
    }
}
