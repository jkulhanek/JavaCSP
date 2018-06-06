package student.colorboard;

import junit.framework.TestCase;
import student.constraints.Constraint;
import student.constraints.c;

import java.util.*;

public class ColorBoardCSPTest extends TestCase {

    public void testGetDomains() {
        List<Block> verticalBlocks = new ArrayList<>();
        verticalBlocks.add(new Block('^', 1, 0));
        verticalBlocks.add(new Block('^', 2, 1));
        verticalBlocks.add(new Block('*', 1, 2));

        List<Block> h1b = new ArrayList<>();
        h1b.add(new Block('^', 1, 0));
        BoardConstraint h1c = new BoardConstraint(h1b);

        List<Block> hbb = new ArrayList<>();
        hbb.add(new Block('*', 1, 0));
        BoardConstraint hbc = new BoardConstraint(h1b);

        List<Block> heb = new ArrayList<>();
        heb.add(new Block('-', 1, 0));
        BoardConstraint hec = new BoardConstraint(h1b);



        BoardConstraint verticalConstraint = new BoardConstraint(verticalBlocks);
        List<BoardConstraint> verticalConstraints = new ArrayList<>();
        verticalConstraints.add(verticalConstraint);
        verticalConstraints.add(new BoardConstraint(new ArrayList<Block>()));

        List<BoardConstraint> horizontalConstraints = new ArrayList<>();
        horizontalConstraints.add(h1c);
        horizontalConstraints.add(hec);
        horizontalConstraints.add(h1c);
        horizontalConstraints.add(h1c);
        horizontalConstraints.add(hbc);

        Board b= new Board(5,2, verticalConstraints, horizontalConstraints);
        ColorBoardCSP csp = new ColorBoardCSP(b);

        Collection<Constraint> constraints = csp.getConstraints();

        assertConstraint(constraints, c.ge(c.v("v1:2"), c.plus(c.c(2), c.v("v1:1"))));
    }

    private void assertConstraint(Collection<Constraint> constraints, Constraint constraint){
        for(Constraint c : constraints){
            if(c.toString() == constraint.toString()){
                return;
            }

            Set<String> scope = new HashSet<>(c.getScope());
            Set<String> scopeCst = constraint.getScope();
            if(scope.size() == scopeCst.size()){
                scope.removeAll(scopeCst);
                if(scope.size() == 0){
                    // Should validate constraints on all values
                }
            }
        }

        fail("Constraint " + constraint.toString() + " is not present");
    }

    public void testGetConstraints() {
    }
}