package student.colorboard;

import java.util.List;

public class BoardConstraint{
    private List<Block> _blocks;

    public BoardConstraint(List<Block> blocks){
        this._blocks = blocks;
    }

    public List<Block> getBlocks(){
        return this._blocks;
    }
}