package student;

import student.algorithm.*;
import student.constraints.Constraint;

import java.util.*;

public class BacktrackingSolver implements Solver {
    private final CSP csp;
    private final Map<String, Set<Object>> domains;
    private final List<String> variables;
    private final Collection<Constraint> constraints;
    private final ConstraintEvaluator constraintEvaluator;
    private final AC3 ac3;
    private final BinaryConstraintLookup binaryConstraintLookup;
    private final ForwardChecking forwardChecking;
    private final VariableOrderHeuristic variableOrderHeuristic;
    private final ValueOrderHeuristic valueOrderHeuristic;

    public BacktrackingSolver(CSP csp, String valueHeuristic, String variableHeuristic) {
        this.csp = prepareCSP(csp);
        this.variables = csp.getVariables();
        this.domains = csp.getDomains();
        this.constraints = csp.getConstraints();
        this.constraintEvaluator = new ConstraintEvaluator(this.constraints);

        // For speed of the algorithm, we will only consider the binary constraints
        this.binaryConstraintLookup = new BinaryConstraintLookup(filterBinaryConstraints(this.constraints));
        this.ac3 = new AC3(this.variables, binaryConstraintLookup);
        this.forwardChecking = new ForwardChecking(this.binaryConstraintLookup);

        ValueOrderHeuristicFactory valueOrderHeuristicFactory = new ValueOrderHeuristicFactory(csp, this.forwardChecking);
        VariableOrderHeuristicFactory variableOrderHeuristicFactory = new VariableOrderHeuristicFactory(csp);
        this.valueOrderHeuristic = valueOrderHeuristicFactory.create(valueHeuristic);
        this.variableOrderHeuristic = variableOrderHeuristicFactory.create(variableHeuristic);
    }

    private List<Constraint> filterBinaryConstraints(Collection<Constraint> constraints){
        List<Constraint> binaryConstraints = new ArrayList<>();
        for(Constraint consr: constraints){
            if(consr.getScope().size() == 2){
                binaryConstraints.add(consr);
            }
        }

        return binaryConstraints;
    }

    private CSP prepareCSP(CSP csp) {
        return csp;
    }

    public List<Solution> solve() {
        return backtracking();
    }

    private List<Solution> backtracking()
    {
        List<Solution> solutions = new ArrayList<>();
        Assignment assignment = new StaticAssignment();
        Domain domain = new Domain(this.domains);
        recursiveBacktracking(solutions, assignment, domain);
        return solutions;
    }

    private boolean isEmptyDomain(Domain domain, Collection<String> unassignedVariables){
        for(String var : unassignedVariables){
            if(domain.get(var).size() == 0){
                return true;
            }
        }

        return false;
    }

    private List<String> getUnassignedVariables(Assignment assignment){
        Set<String> assignedVariables = assignment.getAssignedVariables();
        List<String> unassigned = new ArrayList<>();
        for(String var : variables){
            if(!assignedVariables.contains(var)){
                unassigned.add(var);
            }
        }

        return unassigned;
    }

    private void recursiveBacktracking(List<Solution> solutions, Assignment assignment, Domain domains)
    {
        if(assignment.size() == variables.size()){
            solutions.add(new Solution(assignment.getValues()));
            return;
        }

        String var = this.variableOrderHeuristic.selectVariable(assignment, domains);
        Set<Object> domain = domains.get(var);


        if(domain.size() > 0) {
            List<Object> domainOrdered = this.valueOrderHeuristic.orderValues(var, domains, assignment);
            Collection<String> unassignedVariables = null;

            for (Object value : domainOrdered) {
                assignment = assignment.set(var, value);
                unassignedVariables = (unassignedVariables == null)?
                        getUnassignedVariables(assignment) : unassignedVariables;

                Domain newDomains = domains.inherit();
                if(this.forwardChecking.execute(assignment, var, newDomains, unassignedVariables) < 0){
                    // Empty domain
                    assignment = assignment.reset(var);
                    continue;
                }

                ac3.filterDomain(assignment, newDomains);

                if(isEmptyDomain(newDomains, unassignedVariables)){
                    assignment = assignment.reset(var);
                    continue;
                }

                if (constraintEvaluator.isSatisfied(assignment, var)) {
                    recursiveBacktracking(solutions, assignment, newDomains);
                }

                assignment = assignment.reset(var);
            }
        }
    }
}
