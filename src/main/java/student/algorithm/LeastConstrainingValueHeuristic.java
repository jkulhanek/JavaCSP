package student.algorithm;

import student.Assignment;
import student.CSP;
import student.Domain;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class LeastConstrainingValueHeuristic extends Heuristic implements ValueOrderHeuristic {
    private ForwardChecking forwardChecking;

    public LeastConstrainingValueHeuristic(CSP csp, ForwardChecking forwardChecking){
        super(csp);
        this.forwardChecking = forwardChecking;
    }

    @Override
    public List<Object> orderValues(String variable, Domain domains, Assignment assignment) {
        class PrioritizedValue implements Comparable{
            public PrioritizedValue(int priority, Object value){
                this.priority = priority;
                this.value = value;
            }
            public Integer priority;
            public Object value;

            @Override
            public int compareTo(Object o) {
                return this.priority.compareTo(((PrioritizedValue)o).priority);
            }
        }


        Set<Object> domain = domains.get(variable);
        if(domain.size() == 1){
            return new LinkedList<>(domain);
        }

        PriorityQueue<PrioritizedValue> priorityQueue = new PriorityQueue<>();
        List<String> unassigned = getUnassignedVariables(assignment);
        unassigned.remove(variable);

        // Most constrained value
        for(Object value : domain){
            assignment = assignment.set(variable, value);

            Domain newDomains = domains.inherit();
            Integer res = forwardChecking.execute(assignment, variable, newDomains, unassigned);
            if(res >= 0){
                priorityQueue.add(new PrioritizedValue(res, value));
            }

            assignment = assignment.reset(variable);
        }

        List<Object> orderedValues = new LinkedList<>();
        while(!priorityQueue.isEmpty()){
            orderedValues.add(priorityQueue.poll().value);
        }
        return orderedValues;
    }
}
