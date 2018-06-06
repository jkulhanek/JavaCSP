package student;

import java.io.IOException;
import java.util.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import student.colorboard.*;
import student.constraints.Constraint;

/**
 * Unit test for simple App.
 */
public class CSPTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CSPTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( CSPTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCSP()
    {
        ArrayList<BoardConstraint> verticalConstraints = new ArrayList<>();
        ArrayList<Block> blockLine = new ArrayList<>();
        blockLine.add(new Block('^', 2, 0));

        verticalConstraints.add(new BoardConstraint(new ArrayList<>()));
        verticalConstraints.add(new BoardConstraint(blockLine));
        verticalConstraints.add(new BoardConstraint(blockLine));
        verticalConstraints.add(new BoardConstraint(new ArrayList<>()));

        Board board = new Board(4,4, verticalConstraints, verticalConstraints);

        ColorBoardCSP csp = new ColorBoardCSP(board);
        List<char[][]> solutions = csp.solve();


        char[][] realSolution = new char[][]{ { '-', '-', '-', '-'},
            { '-', '^', '^', '-'},
            { '-', '^', '^', '-'},
            { '-', '-', '-', '-'}};

        assertEquals(1, solutions.size());

        char[][] actualSolution = solutions.get(0);

        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 4; ++j){
                assertEquals(realSolution[i][j], actualSolution[i][j]);
            }
        }
    }

    public void testCSPAssignment1() throws IOException{
        String boardStr = "11,11\n" +
                "G,1\n" +
                "G,2\n" +
                "R,4,G,1,R,4\n" +
                "R,8,Y,1,R,2\n" +
                "R,3,Y,1,R,7\n" +
                "R,6,Y,1,R,2\n" +
                "R,9\n" +
                "R,2,Y,1,R,4\n" +
                "R,7\n" +
                "R,2,Y,1,R,2\n" +
                "R,3\n" +
                "R,2\n" +
                "R,5\n" +
                "R,7\n" +
                "R,2,Y,1,R,5\n" +
                "R,5,Y,1,R,3\n" +
                "G,2,R,6,Y,1,R,1\n" +
                "G,2,R,9\n" +
                "R,3,Y,1,R,4\n" +
                "R,1,Y,1,R,5\n" +
                "R,5\n" +
                "R,2";
        String filledBoard = "______G____\n" +
                "_____GG____\n" +
                "_RRRRGRRRR_\n" +
                "RRRRRRRRYRR\n" +
                "RRRYRRRRRRR\n" +
                "_RRRRRRYRR_\n" +
                "_RRRRRRRRR_\n" +
                "__RRYRRRR__\n" +
                "__RRRRRRR__\n" +
                "___RRYRR___\n" +
                "____RRR____";

        Board board = Parser.readBoard(boardStr);
        ColorBoardCSP csp = new ColorBoardCSP(board);
        Assignment ass  = fromBoard(board, Parser.readSolution(filledBoard));

        assertEquals(csp.getVariables().size(), ass.size()); // Can reach end?
        assertAssignmentConsistent(ass, csp.getConstraints());
        List<char[][]> solutions = csp.solve();
        assertEquals(1, solutions.size());
    }

    public void testCSPAssignment2() throws IOException{
        String boardStr = "8,7\n" +
                "#,2\n" +
                "#,1,#,1\n" +
                "#,1,#,1\n" +
                "#,2\n" +
                "#,2,#,1\n" +
                "#,1,#,2,#,2\n" +
                "#,4,#,1\n" +
                "#,3\n" +
                "#,2\n" +
                "#,1,#,1\n" +
                "#,2\n" +
                "#,2,#,4\n" +
                "#,1,#,1,#,2\n" +
                "#,1,#,1,#,1,#,1\n" +
                "#,2,#,2";
        String filledBoard1 = "___##__\n" +
                "___#_#_\n" +
                "____#_#\n" +
                "_____##\n" +
                "##_#___\n" +
                "#_##_##\n" +
                "_####_#\n" +
                "___###_\n";
        String filledBoard2 =
                "____##_\n" +
                "___#__#\n" +
                "___#__#\n" +
                "____##_\n" +
                "##_#___\n" +
                "#_##_##\n" +
                "_####_#\n" +
                "___###_";

        Board board = Parser.readBoard(boardStr);
        ColorBoardCSP csp = new ColorBoardCSP(board);

        Assignment ass1  = fromBoard(board, Parser.readSolution(filledBoard1));
        assertEquals(csp.getVariables().size(), ass1.size()); // Can reach end?
        assertAssignmentConsistent(ass1, csp.getConstraints());

        Assignment ass2  = fromBoard(board, Parser.readSolution(filledBoard2));
        assertEquals(csp.getVariables().size(), ass2.size()); // Can reach end?
        assertAssignmentConsistent(ass2, csp.getConstraints());

        List<Solution> correctSolutions = new ArrayList<>();
        correctSolutions.add(new Solution(ass1.getValues()));
        correctSolutions.add(new Solution(ass2.getValues()));

        Solver solver = new BacktrackingSolver(csp, "LeastConstrainingValue",
                "MostConstrainedVariable");
        List<Solution> solutions = solver.solve();

        assertEquals(2, solutions.size());
    }

    private void assertAllSolutionsCorrect(List<Solution> solutions, Collection<Solution> correctSolutions) {
        for(Solution sol : solutions){
            assertSolutionCorrect(sol, correctSolutions);
        }
    }

    private void assertSolutionCorrect(Solution solution, Collection<Solution> correctSolutions){
        boolean any = false;
        for(Solution sol:correctSolutions){
            any |= sol.equals(solution);
            if(any)
                break;
        }

        if(!any){
            fail("Solution is not correct");
        }
    }

    private static Assignment fromBoard(Board board, char[][] solution){
        Assignment ass = new StaticAssignment();
        List<BoardConstraint> constraints = board.getVerticalConstraints();
        for(int i = 0; i < board.getColls(); ++i){
            for(int j = 0; j < board.getRows(); ++j) {
                ass.set("b" + i + ":" + j, solution[i][j]);
            }

            int lastOffset = -1;
            for(Block block : constraints.get(i).getBlocks()){
                while(solution[i][++lastOffset] != block.getColor());
                ass.set("v" + (i + 1) + ":" + (block.getIndex() + 1), lastOffset);
                lastOffset += block.getLength() - 1;
            }
        }
        constraints = board.getHorizontalConstraints();
        for(int j = 0; j < board.getRows(); ++j){
            int lastOffset = -1;
            for(Block block : constraints.get(j).getBlocks()){
                while(solution[++lastOffset][j] != block.getColor());
                ass.set("h" + (j + 1) + ":" + (block.getIndex() + 1), lastOffset);
                lastOffset += block.getLength() - 1;
            }
        }

        return ass;
    }

    public void testCSPTwoBlocksTwoColors() throws IOException
    {
        ArrayList<BoardConstraint> verticalConstraints = new ArrayList<>();
        ArrayList<Block> blockLine1 = new ArrayList<>();
        blockLine1.add(new Block('^', 2, 0));

        ArrayList<Block> blockLine2 = new ArrayList<>();
        blockLine2.add(new Block('^', 2, 0));

        verticalConstraints.add(new BoardConstraint(new ArrayList<>()));
        verticalConstraints.add(new BoardConstraint(blockLine1));
        verticalConstraints.add(new BoardConstraint(blockLine1));
        verticalConstraints.add(new BoardConstraint(new ArrayList<>()));
        verticalConstraints.add(new BoardConstraint(blockLine2));
        verticalConstraints.add(new BoardConstraint(blockLine2));
        verticalConstraints.add(new BoardConstraint(new ArrayList<>()));

        Board board = new Board(7,7, verticalConstraints, verticalConstraints);

        ColorBoardCSP csp = new ColorBoardCSP(board);
        Solver solver = new BacktrackingSolver(csp,"LeastConstrainingValue",
                "MostConstrainingVariable");
        List<Solution> solutions = solver.solve();

        String realSolution1Str = "-------\n-^^----\n-^^----\n-------\n----^^-\n----^^-\n-------";
        Assignment realSolution1 = fromBoard(board, Parser.readSolution(realSolution1Str));
        assertAssignmentConsistent(realSolution1, csp.getConstraints());
        String realSolution2Str = "-------\n----^^-\n----^^-\n-------\n-^^----\n-^^----\n-------";
        Assignment realSolution2 = fromBoard(board, Parser.readSolution(realSolution2Str));
        assertAssignmentConsistent(realSolution2, csp.getConstraints());


        assertEquals(2, solutions.size());

        List<Solution> realSolutions = new ArrayList<>();
        realSolutions.add(new Solution(realSolution1.getValues()));
        realSolutions.add(new Solution(realSolution2.getValues()));

        assertAllSolutionsCorrect(solutions, realSolutions);
    }


    public void testCSPTrivial()
    {
        ArrayList<BoardConstraint> verticalConstraints = new ArrayList<>();
        ArrayList<Block> blockLine = new ArrayList<>();
        blockLine.add(new Block('^', 2, 0));

        verticalConstraints.add(new BoardConstraint(blockLine));

        ArrayList<BoardConstraint> horizontalConstraints = new ArrayList<>();
        ArrayList<Block> blockLine2 = new ArrayList<>();
        blockLine2.add(new Block('^', 1, 0));
        horizontalConstraints.add(new BoardConstraint(blockLine2));
        horizontalConstraints.add(new BoardConstraint(blockLine2));
        horizontalConstraints.add(new BoardConstraint(new ArrayList<>()));

        Board board = new Board(3,1, verticalConstraints, horizontalConstraints);

        ColorBoardCSP csp = new ColorBoardCSP(board);

        Map<String, Set<Object>> domain = csp.getDomains();
        assertEquals(2, domain.get("v1:1").size());

        List<char[][]> solutions = csp.solve();


        char[][] realSolution = new char[][]{ { '^'},
                { '^' },
                { '-'}};

        assertEquals(1, solutions.size());

        char[][] actualSolution = solutions.get(0);

        for(int i = 0; i < 3; ++i){
            for(int j = 0; j < 1; ++j){
                assertEquals(realSolution[i][j], actualSolution[i][j]);
            }
        }
    }

    public void testMultipleColors() throws IOException{
        String assignmentStr = "5,2\n^,1\n\n^,1\n^,1\n*,1\n^,1,^,2,*,1\n";
        String assigned = "^-\n--\n^-\n^-\n*-";
        Board b = Parser.readBoard(assignmentStr);
        Assignment ass = fromBoard(b, Parser.readSolution(assigned));
        ColorBoardCSP csp = new ColorBoardCSP(b);
        Collection<?> solutions = csp.solve();

        assertAssignmentConsistent(ass, csp.getConstraints());
        assertEquals(1, solutions.size());
    }

    private void assertAssignmentConsistent(Assignment assignment, Collection<Constraint> constraints){
        for(Constraint constr : constraints){
            try {
                if (!constr.evaluate(assignment)) {
                    fail("Invalid constraint - " + constr.toString());
                }
            }
            catch(NullPointerException ex)
            {
                fail("Invalid constraint - " + constr.toString());
            }
        }

    }


    public void testDino() throws IOException{
        String board = "20,30\n" +
                "#,6,#,1\n" +
                "#,3,#,4,#,1\n" +
                "#,9,#,2\n" +
                "#,2,#,2,#,3,#,2\n" +
                "#,1,#,2,#,3,#,3\n" +
                "#,2,#,5,#,4\n" +
                "#,10,#,5\n" +
                "#,15\n" +
                "#,13\n" +
                "#,10\n" +
                "#,8\n" +
                "#,2,#,7\n" +
                "#,2,#,2,#,5\n" +
                "#,2,#,5\n" +
                "#,2,#,2\n" +
                "#,2,#,2\n" +
                "#,2,#,2\n" +
                "#,2,#,2\n" +
                "#,2,#,2\n" +
                "#,2,#,2\n" +
                "#,2\n" +
                "#,2\n" +
                "#,2,#,1\n" +
                "#,3,#,2\n" +
                "#,5\n" +
                "#,1,#,2\n" +
                "#,3\n" +
                "#,3\n" +
                "#,4\n" +
                "#,5\n" +
                "#,5,#,1\n" +
                "#,5,#,2\n" +
                "#,7,#,1\n" +
                "#,6,#,2\n" +
                "#,7\n" +
                "#,6,#,1\n" +
                "#,8,#,2\n" +
                "#,9,#,2\n" +
                "#,12,#,1\n" +
                "#,8,#,2,#,2\n" +
                "#,3,#,4,#,2\n" +
                "#,4,#,4\n" +
                "#,3,#,2\n" +
                "#,4\n" +
                "#,3\n" +
                "#,3\n" +
                "#,2\n" +
                "#,2\n" +
                "#,2\n" +
                "#,3";

        String assignmentR = "___######____________________#\n" +
                "__###_####___________________#\n" +
                "_#########__________________##\n" +
                "##__##__###________________##_\n" +
                "#__##____###_____________###__\n" +
                "__##_____#####_________####___\n" +
                "__________##########_#####____\n" +
                "__________###############_____\n" +
                "___________#############______\n" +
                "____________##########________\n" +
                "____________########__________\n" +
                "___________##_#######_________\n" +
                "__________##_##_#####_________\n" +
                "____________##__#####_________\n" +
                "_________________##_##________\n" +
                "__________________##_##_______\n" +
                "__________________##_##_______\n" +
                "_________________##_##________\n" +
                "________________##_##_________\n" +
                "_______________##_##__________";


        Board b = Parser.readBoard(board);
        ColorBoardCSP csp = new ColorBoardCSP(b);

        Assignment ass  = fromBoard(b, Parser.readSolution(assignmentR));
        assertEquals(csp.getVariables().size(), ass.size()); // Can reach end?
        assertAssignmentConsistent(ass, csp.getConstraints());
    }
}
