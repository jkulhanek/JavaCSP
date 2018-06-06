package student.colorboard;

import student.BacktrackingSolver;
import student.CSP;
import student.Solution;
import student.Solver;
import student.constraints.Constraint;
import student.constraints.ConstraintBase;
import student.constraints.c;

import java.util.*;
import java.util.stream.Collectors;

/***
 * This class represents the CSP problem of finding the right board coloring
 * All constraints are binary
 *
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
 * The default solver uses LeastConstrainingValueHeuristic and MostConstrainingVariable heuristics
 * It was tried empirically, that this finds the solution fastest.
 */
public class ColorBoardCSPBinary implements CSP {

    private static final Character BLANK = '_';
    private final Board board;
    private final int colls;
    private final int rows;
    private final List<String> variables;
    private final Map<String, Set<Object>> domains;
    private final Collection<Constraint> constraints;

    private final List<char[]>[] horizontalVariableSpace;
    private final List<char[]>[] verticalVariableSpace;

    public ColorBoardCSPBinary(Board board){
        this.board = board;
        this.rows = board.getRows();
        this.colls = board.getColls();
        this.variables = new ArrayList<>();
        this.domains = new HashMap<>();
        this.constraints = new ArrayList<>();

        createBoardVariables(variables, domains, board);
        verticalVariableSpace = createConstraintVariables(variables, domains, "v", board.getVerticalConstraints(), board.getRows());
        horizontalVariableSpace = createConstraintVariables(variables, domains, "h", board.getHorizontalConstraints(), board.getColls());
        addBoardConstraints(constraints, board);
    }

    public List<char[][]> solve(){
        Solver solver = new BacktrackingSolver(this,
                "LeastConstrainingValue",
                "MostConstrainingVariable");
        List<Solution> solutions = solver.solve();
        return exportSolutions(solutions);
    }

    private static void createBoardVariables(List<String> variables, Map<String, Set<Object>> domains, Board board){
        // get all colors
        HashSet<Object> allColors = new HashSet<>();
        for (BoardConstraint cons : board.getVerticalConstraints()) {
            for (Block block : cons.getBlocks())
                allColors.add(block.getColor());
        }
        allColors.add(BLANK);

        int colls = board.getColls();
        int rows = board.getRows();

        // Create variables and domains
        String scopeName = "b";
        for (int col = 0; col < colls; col++) {
            for (int row = 0; row < rows; row++) {
                String variableName = scopeName + col + ":" + row;
                variables.add(variableName);
                domains.put(variableName, new HashSet<>(allColors));
            }
        }

        // Restrict domains
        // Filter domain values
        List<BoardConstraint> constraints = board.getVerticalConstraints();
        for(int coll = 0; coll < colls; ++coll){
            BoardConstraint cons = constraints.get(coll);
            HashSet<Character> colors = new HashSet<>();
            for (Block block : cons.getBlocks())
                colors.add(block.getColor());
            colors.add(BLANK);

            for(int row = 0; row < rows; ++row){
                String variableName = scopeName + coll + ":" + row;
                domains.get(variableName).retainAll(colors);
            }
        }
        constraints = board.getHorizontalConstraints();
        for(int row = 0; row < rows; ++row){
            BoardConstraint cons = constraints.get(row);
            HashSet<Character> colors = new HashSet<>();
            for (Block block : cons.getBlocks())
                colors.add(block.getColor());
            colors.add(BLANK);

            for(int coll = 0; coll < colls; ++coll){
                String variableName = scopeName + coll + ":" + row;
                domains.get(variableName).retainAll(colors);
            }
        }
    }

    private static List<char[]>[] createConstraintVariables(List<String> variables, Map<String, Set<Object>> domains, String scopeName, List<BoardConstraint> constraints, int length) {
        int[] sizes = new int[constraints.size()];
        for (int i = 0; i < sizes.length; ++i) {
            sizes[i] = constraints.get(i).getBlocks().size();
        }

        class ConstraintCreationScope{
            public int prepareConstraintRecursive(int[][] linspace, List<Block> blocks, int coll, int minpos, int i, int size) {
                if (i >= size) {
                    return length;
                }

                Block block = blocks.get(i);
                int newMinPos = minpos + block.getLength()
                        + ((i + 1 < size && block.getColor() == blocks.get(i + 1).getColor()) ? 1 : 0);

                int maxpos = prepareConstraintRecursive(linspace, blocks, coll, newMinPos, i + 1, size);
                int newMaxPos = maxpos - block.getLength()
                        - ((i + 1 < size && block.getColor() == blocks.get(i + 1).getColor()) ? 1 : 0);

                // Add domains in valid range
                linspace[i][0] = minpos;
                linspace[i][1] = newMaxPos;
                return newMaxPos;
            }

            private char[] renderBlocks(List<Block> blocks, int[] positions){
                char[] place = new char[length];
                for(int i = 0; i < place.length;++i){
                    place[i] = BLANK;
                }

                for(int i = 0; i < positions.length;++i){
                    Block block = blocks.get(i);
                    Character color = block.getColor();
                    for(int k = positions[i]; k < positions[i] + block.getLength(); ++k){
                        place[k] = color;
                    }
                }
                return place;
            }

            public int createValuesRecursive(List<Block> blocks, int[][] linspace, int index, int[] positions,
                                             List<char[]> valueSpace,int offset){
                if(index < linspace.length){
                    int start = linspace[index][0];
                    if(index > 0){
                        // Remove used positions
                        start = positions[index - 1] + linspace[index][0] - linspace[index - 1][0];
                    }

                    for(int i = start; i <= linspace[index][1]; ++i){
                        positions[index] = i;
                        offset = createValuesRecursive(blocks, linspace, index + 1, positions, valueSpace, offset);
                    }
                }
                else{
                    // Export solution
                    valueSpace.add(renderBlocks(blocks, positions));
                    offset = offset + 1;
                }

                return offset;
            }
        }

        ConstraintCreationScope creationScope = new ConstraintCreationScope();
        List<char[]>[] variableSpace = new List[constraints.size()];
        for(int i = 0; i < constraints.size(); ++i) {
            String variableName = scopeName + (i + 1);
            variables.add(variableName);
            Set<Object> d = new HashSet<>();
            domains.put(variableName, d);

            List<Block> blocks = constraints.get(i).getBlocks();
            int[][] linspace = new int[blocks.size()][2];
            creationScope.prepareConstraintRecursive(linspace, blocks, i, 0, 0, blocks.size());

            int[] positions = new int[blocks.size()];
            List<char[]> valueSpace = new ArrayList<>();
            creationScope.createValuesRecursive(blocks, linspace,0, positions, valueSpace, 0);

            for(int j = 0; j < valueSpace.size(); ++j){
                d.add(j);
            }

            variableSpace[i] = valueSpace;
        }

        return variableSpace;
    }

    private void addBoardConstraints(Collection<Constraint> constraints, Board board) {
        for(int row = 0; row < board.getRows(); ++row){
            for(int coll = 0; coll < board.getColls(); ++coll){
                constraints.add(new BlockLineConstraint(
                        horizontalVariableSpace[row],
                        "h"+(1 + row), "h", row, coll));

                constraints.add(new BlockLineConstraint(
                        verticalVariableSpace[coll],
                        "v"+(1 + coll), "v", row, coll));

            }
        }
    }


    private List<char[][]> exportSolutions(Collection<Solution> solutions){
        List<char[][]> solutionsParsed = new ArrayList<>();
        for(Solution sol : solutions){
            solutionsParsed.add(exportSolution(sol));
        }
        return solutionsParsed;
    }

    public char[][] exportSolution(Solution solution){
        String prefix = "b";
        char[][] board = new char[rows][colls];
        for(int i=0;i<rows;++i){
            for(int j=0;j<colls;++j){
                board[i][j] = (Character)solution.get(prefix + j + ":" + i);
            }
        }


        return board;
    }

    @Override
    public List<String> getVariables() {
        return this.variables;
    }

    @Override
    public Map<String, Set<Object>> getDomains() {
        return this.domains;
    }

    @Override
    public Collection<Constraint> getConstraints() {
        return this.constraints;
    }
}
