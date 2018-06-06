package student.colorboard;

import student.*;
import student.constraints.Constraint;
import student.constraints.ConstraintBase;
import student.constraints.c;

import java.util.*;
import java.util.stream.Collectors;

/***
 * This class represents the CSP problem of finding the right board coloring
 *
 * The the set of variables is such a set Var, that
 * Var = {b(i):(j) for each column i and each row j of the board} union
 *      {v(i+1):(j+1) for each column i and each block constraint j in that column} union
 *      {h(i+1):(j+1) for each row i and each block constraint j in that row}
 *
 * The b.. variable represents the coloring of the (x,y) point of the board.
 * With the domain of all posible values for each board point (constraint by possible color values
 * in the column and row given by "constraints")
 * The h.. and v.. variable represents the offset of each block from the top left origin.
 * The domain for each variable is all possible offsets of each block.
 *
 * Constraints:
 * The set of constraints is composed of those constraints:
 * For each board pixel and each block in the same row or column, there is a constraint, that
 * if any point of the block lies on the given pixel, that pixel should have the same color.
 *
 * Next, there is a set of constraints, for each pair of block in each row / column, such that they
 * do not overlap and follow the rules of having one empty space between blocks of the same color.
 *
 * Next, there is a set of n-ary constraints connecting each pixel with all row /column constraint blocks
 * of the same color saying, that if there is no block laying on the given pixel, the pixel should not
 * have that color
 *
 * Finally there is a set of n-ary constraints connecting each pixel with all row / column constraint blocks
 * saying that if there is no block at all laying on the given pixel, that pixel should be empty.

 */
public class ColorBoardCSP implements CSP {

    private static final Character BLANK = '_';
    private final Board board;
    private final int colls;
    private final int rows;
    private final List<String> variables;
    private final Map<String, Set<Object>> domains;
    private final Collection<Constraint> constraints;

    public ColorBoardCSP(Board board){
        this.board = board;
        this.rows = board.getRows();
        this.colls = board.getColls();
        this.variables = new ArrayList<>();
        this.domains = new HashMap<>();
        this.constraints = new ArrayList<>();

        createBoardVariables(variables, domains, board);
        createConstraintVariables(variables, domains, "v", board.getVerticalConstraints(), board.getRows());
        createConstraintVariables(variables, domains, "h", board.getHorizontalConstraints(), board.getColls());
        addBoardConstraints(constraints, board);
    }

    public List<char[][]> solve(){
        Solver solver = new BacktrackingSolver(this,
                "LeastConstrainingValue",
                "MostConstrainedVariable");
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

    private static void createConstraintVariables(List<String> variables, Map<String, Set<Object>> domains, String scopeName, List<BoardConstraint> constraints, int length) {
        int[] sizes = new int[constraints.size()];
        for (int i = 0; i < sizes.length; ++i) {
            sizes[i] = constraints.get(i).getBlocks().size();
        }

        // Create raw variables
        for(int i = 0; i < sizes.length; ++i){
            int size = sizes[i];
            for(int j=0;j<size;++j){
                String variableName = scopeName + ((Integer)(i+1)).toString()
                        + ":" + ((Integer)(j+1)).toString();
                variables.add(variableName);
                domains.put(variableName, new HashSet<>());
            }
        }

        // Create domains
        class ConstraintCreationScope{
            public int prepareConstraintRecursive(List<Block> blocks, int coll, int minpos, int i, int size) {
                if (i >= size) {
                    return length;
                }

                Block block = blocks.get(i);
                int newMinPos = minpos + block.getLength()
                        + ((i + 1 < size && block.getColor() == blocks.get(i + 1).getColor()) ? 1 : 0);

                int maxpos = prepareConstraintRecursive(blocks, coll, newMinPos, i + 1, size);
                int newMaxPos = maxpos - block.getLength()
                        - ((i + 1 < size && block.getColor() == blocks.get(i + 1).getColor()) ? 1 : 0);

                // Add domains in valid range
                String variableName = scopeName + (coll + 1) + ":" + (i+1);
                Set<Object> domain = domains.get(variableName);
                for (int j = minpos; j <= newMaxPos; ++j) {
                    domain.add(j);
                }


                return newMaxPos;
            }
        }

        ConstraintCreationScope creationScope = new ConstraintCreationScope();
        for (int j = 0; j < sizes.length; ++j) {
            creationScope.prepareConstraintRecursive(constraints.get(j).getBlocks(), j, 0, 0, sizes[j]);
        }
    }

    private void addBoardConstraints(Collection<Constraint> constraints, Board board) {
        class boardConstraintConstructor {
            private void addConstraint(Constraint constraint){
                constraints.add(constraint);
            }

            private void addBlockConstraints(String orientation, List<BoardConstraint> constraints, int length) {
                for (int coll = 0; coll < constraints.size(); ++coll) {
                    // Add non overlaping constraint

                    // Up pyramid
                    final Integer local_coll = coll;
                    List<Block> blocks = constraints.get(coll).getBlocks();
                    for (int constraint_id = 0; constraint_id < blocks.size() - 1; ++constraint_id) {
                        final Integer cid = constraint_id;

                        int offset = 0;
                        for (int sub_constraint_id = constraint_id + 1; sub_constraint_id < blocks.size(); ++sub_constraint_id) {
                            final Integer sub_cid = sub_constraint_id;

                            offset += blocks.get(sub_constraint_id - 1).getLength()
                                    + ((blocks.get(sub_constraint_id - 1).getColor() == blocks.get(sub_constraint_id).getColor())? 1 : 0);

                            final int internal_offset = offset;

                            String subVar = orientation + (1 + local_coll) + ":" + (1 + sub_cid);
                            String var = orientation + (1 + local_coll) + ":" + (1 + cid);
                            this.addConstraint(c.le(c.plus(c.v(var), c.c(internal_offset)), c.v(subVar)));
                        }

                        offset = 0;
                        for (int sub_constraint_id = constraint_id - 1; sub_constraint_id >= 0; --sub_constraint_id) {
                            final Integer sub_cid = sub_constraint_id;

                            offset += blocks.get(sub_constraint_id).getLength()
                                    + ((blocks.get(sub_constraint_id + 1).getColor() == blocks.get(sub_constraint_id).getColor())? 1 : 0);

                            final int internal_offset = offset;

                            String subVar = orientation + (1 + local_coll) + ":" + (1 + sub_cid);
                            String var = orientation + (1 + local_coll) + ":" + (1 + cid);

                            this.addConstraint(c.ge(c.v(var), c.plus(c.v(subVar), c.c(internal_offset))));
                        }
                    }
                }
            }

            private void addDependencyConstraintsVertical(List<BoardConstraint> constraints, int length){
                String orientation = "v";
                for(int coll = 0; coll < board.getColls(); ++coll){
                    List<Block> blocks = constraints.get(coll).getBlocks();
                    final Integer fcoll = coll;
                    for(int block_id = 0; block_id < blocks.size();++block_id){
                        final Integer bid = block_id;
                        Block block = blocks.get(block_id);
                        final Integer block_length = block.getLength();
                        for(int j= 0; j < length; ++j){
                            final Integer jfin = j;
                            String pointVariable = "b" + fcoll +  ":" + jfin;// point variable
                            String blockPosition = orientation +(1 + fcoll) +  ":" + (1 + bid); // block variable
                            ConstraintBase isIn = c.and(c.ge(c.c(jfin), c.v(blockPosition)), c.lt(c.c(jfin), c.plus(c.v(blockPosition), c.c(block_length))));
                            this.addConstraint(c.or(c.not(isIn), c.eq(c.v(pointVariable), c.c(block.getColor()))));
                        }
                    }

                    // Add no color constraint
                    // Add no color constraint
                    // Blocks by color
                    Map<Character, List<Block>> colorBlocks = blocks.stream().collect(Collectors.groupingBy(Block::getColor));
                    for(Map.Entry<Character, List<Block>> item : colorBlocks.entrySet()){
                        for(int j= 0; j < length; ++j){
                            final Integer jfin = j;
                            String pointVariable = "b" + fcoll +  ":" + jfin;
                            ArrayList<ConstraintBase> inBlockBases = new ArrayList<>();

                            for(Block block : item.getValue()){
                                String positionVar = orientation + (1 + fcoll) +  ":" + (1 + block.getIndex());
                                inBlockBases.add(c.and(c.le(c.v(positionVar), c.c(jfin)), c.lt(c.c(jfin), c.plus(c.v(positionVar), c.c(block.getLength())))));
                            }

                            ConstraintBase inAny = c.or(inBlockBases);
                            this.addConstraint(c.eq(c.eq(c.v(pointVariable), c.c(item.getKey())), inAny));
                        }
                    }
                }
            }


            private void addDependencyConstraintsHorizontal(List<BoardConstraint> constraints, int length){
                String orientation = "h";
                for(int row = 0; row < board.getRows(); ++row){
                    List<Block> blocks = constraints.get(row).getBlocks();
                    final Integer frow = row;
                    for(int block_id = 0; block_id < blocks.size();++block_id){
                        final Integer bid = block_id;
                        Block block = blocks.get(block_id);
                        final Integer block_length = block.getLength();
                        for(int j= 0; j < length; ++j){
                            final Integer jfin = j;
                            String pointVariable = "b" + jfin +  ":" + frow;// point variable
                            String blockPosition = orientation +(1 + frow) +  ":" + (1 + bid); // block variable

                            // jfin >= blockPosition && jfin < blockPosition + block_length
                            ConstraintBase isIn = c.and(c.ge(c.c(jfin), c.v(blockPosition)), c.lt(c.c(jfin), c.plus(c.v(blockPosition), c.c(block_length))));

                            // !isIn || pointVariable = block.getColor()
                            this.addConstraint(c.or(c.not(isIn), c.eq(c.v(pointVariable), c.c(block.getColor()))));
                        }
                    }

                    // Add no color constraint
                    // Add no color constraint
                    // Blocks by color
                    Map<Character, List<Block>> colorBlocks = blocks.stream().collect(Collectors.groupingBy(Block::getColor));
                    for(Map.Entry<Character, List<Block>> item : colorBlocks.entrySet()){
                        for(int j= 0; j < length; ++j){
                            final Integer jfin = j;
                            String pointVariable = "b" + jfin +  ":" + frow;
                            ArrayList<ConstraintBase> inBlockBases = new ArrayList<>();

                            for(Block block : item.getValue()){
                                String positionVar = orientation + (1 + frow) +  ":" + (1 + block.getIndex());
                                inBlockBases.add(c.and(c.le(c.v(positionVar), c.c(jfin)), c.lt(c.c(jfin), c.plus(c.v(positionVar), c.c(block.getLength())))));
                            }

                            ConstraintBase inAny = c.or(inBlockBases);
                            addConstraint(c.eq(c.eq(c.v(pointVariable), c.c(item.getKey())), inAny));
                        }
                    }
                }
            }

            public void addConstraints(Board board) {
                addBlockConstraints("h", board.getHorizontalConstraints(), board.getColls());
                addBlockConstraints("v", board.getVerticalConstraints(), board.getRows());

                addDependencyConstraintsHorizontal(board.getHorizontalConstraints(), board.getColls());
                addDependencyConstraintsVertical(board.getVerticalConstraints(), board.getRows());
            }
        }

        new boardConstraintConstructor().addConstraints(board);
    }


    private List<char[][]> exportSolutions(Collection<Solution> solutions){
        List<char[][]> solutionsParsed = new ArrayList<>();
        for(Solution sol : solutions){
            solutionsParsed.add(exportSolution(sol));
        }
        return solutionsParsed;
    }

    public char[][] exportSolution(Solution solution){
        /*String prefix = "b";
        char[][] board = new char[rows][colls];
        for(int i=0;i<rows;++i){
            for(int j=0;j<colls;++j){
                board[i][j] = (Character)solution.get(prefix + j + ":" + i);
            }
        }*/

        String prefix = "h";
        char[][] board = new char[rows][colls];
        List<BoardConstraint> horizontalConstraints = this.board.getHorizontalConstraints();
        for(int i=0;i<rows;++i){
            for(int j=0;j<colls;++j){
                board[i][j] = BLANK;
            }

            for(Block block : horizontalConstraints.get(i).getBlocks()){
                String varName = prefix + (i+1) + ":" + (block.getIndex() +1);
                Integer offset = (Integer)solution.get(varName);
                for(int k = offset; k < offset + block.getLength(); ++k){
                    board[i][k] = block.getColor();
                    board[i][k] = block.getColor();
                }
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
