package student.colorboard;

import java.util.List;

public class Board {
    public Board(int rows, int columns, List<BoardConstraint> verticalConstraints, List<BoardConstraint> horizontalConstraints){
        this.verticalConstraints = verticalConstraints;
        this.horizontalConstraints = horizontalConstraints;
        this._rows = rows;
        this._columns = columns;
    }

    private List<BoardConstraint> verticalConstraints;
    private List<BoardConstraint> horizontalConstraints;
    private int _rows;
    private int _columns;

    public List<BoardConstraint> getVerticalConstraints(){
        return verticalConstraints;
    }

    public List<BoardConstraint> getHorizontalConstraints(){
        return horizontalConstraints;
    }

    public int getColls(){
        return this._columns;
    }

    public int getRows(){
        return this._rows;
    }

}