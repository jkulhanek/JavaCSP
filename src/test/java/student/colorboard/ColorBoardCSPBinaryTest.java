package student.colorboard;

import junit.framework.TestCase;
import student.Assignment;
import student.StaticAssignment;
import student.constraints.Constraint;

import java.io.IOException;
import java.util.*;

public class ColorBoardCSPBinaryTest extends TestCase {

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

        ColorBoardCSPBinary csp = new ColorBoardCSPBinary(board);

        Map<String, Set<Object>> domain = csp.getDomains();
        assertEquals(2, domain.get("v1").size());

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

    public void testMultipleColors() throws IOException {
        String assignmentStr = "5,2\n^,1\n\n^,1\n^,1\n*,1\n^,1,^,2,*,1\n";
        String assigned = "^-\n--\n^-\n^-\n*-";
        Board b = Parser.readBoard(assignmentStr);
        Assignment ass = fromBoard(b, Parser.readSolution(assigned));
        ColorBoardCSPBinary csp = new ColorBoardCSPBinary(b);
        Collection<?> solutions = csp.solve();
        assertEquals(1, solutions.size());
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
        ColorBoardCSPBinary csp = new ColorBoardCSPBinary(board);
        List<char[][]> solutions = csp.solve();
        assertEquals(1, solutions.size());
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


}