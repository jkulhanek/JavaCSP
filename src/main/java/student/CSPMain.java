package student;

import student.colorboard.*;

import java.io.IOException;
import java.util.Collection;

/**
 * Variables:
 * The variable set is composed of those three sets:
 * {"bx:y" for each x in columns and each y in each row, indexing starts at 0} union
 * {"hx" for each row}
 * {"vx" for each column}
 *
 * Domains:
 * For each variable "bx:y", the domain is all possible colors or "_" that is additionally filtered
 * by unary constraints such that if there is no "BlockConstraint" in specific row or in specific column with a specific color,
 * the pixel cannot have that color. Those unary constraints are not given explicitly, but are reduced to
 * the domain for speed efficiency.
 *
 * For each variable "hx" or "vx" the domain is the set of all possible configurations of blocks in the given
 * row or column.
 *
 * Constraints.
 * Constraints is a set of all such tuples, that links each pixel with its column or row variable.
 * Formally Constraints = {("bx:y", "hx" for each x for each y if the color on y-th position of hx configuration is equal to bx:y}
 * union {("bx:y", "vx" for each x for each y if the color on y-th position of vx configuration is equal to bx:y}
 *
 * For more information, please take look at ColorBoardCSPBinary.java
 */
public class CSPMain {
    public static void main(String[] args) {
        try {
            Board b = Parser.readBoard();
            ColorBoardCSPBinary csp = new ColorBoardCSPBinary(b);
            Collection<char[][]> solutions = csp.solve();
            if(solutions.size() == 0){
                System.out.println("null");
            }
            else{
                boolean isFirst = true;
                for(char[][] solution : solutions){
                    if(isFirst) isFirst = false;
                    else System.out.println(); // Splits solutions by an empty line

                    printSolution(solution);
                }
            }
        } catch (IOException error) {
            System.err.println("IO error occured");
        }
    }

    private static void printSolution(char[][] solution){
        for(int i = 0;i<solution.length;++i){
            System.out.println(solution[i]);
        }
    }
}
