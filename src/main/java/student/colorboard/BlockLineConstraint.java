package student.colorboard;

import student.Assignment;
import student.constraints.DelegateConstraint;

import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;

public class BlockLineConstraint extends DelegateConstraint {
    private final String blockLineVariable;
    private final String pixelVariable;
    private final Integer pointMaskOffset;
    private final List<char[]> valueSpace;

    public BlockLineConstraint(List<char[]> valueSpace,
                               String blockLineVariable,
                               String orientation,
                               int row,
                               int coll){

        super(getDependencies(blockLineVariable, row, coll));
        this.blockLineVariable = blockLineVariable;
        this.pixelVariable = "b"+ coll + ":" + row;
        this.valueSpace = valueSpace;

        switch(orientation){
            case "v": pointMaskOffset = row; break;
            case "h": pointMaskOffset = coll; break;
            default: throw new InvalidParameterException("orientation");
        }
    }

    private static List<String> getDependencies(String blockLineVariable, int row, int coll){
        List<String> dependencies = new LinkedList<>();
        dependencies.add(blockLineVariable);
        dependencies.add("b"+ coll + ":" + row);
        return dependencies;
    }

    @Override
    public boolean evaluate(Assignment assignment) {
        Character pixel = assignment.getValue(pixelVariable);
        Integer valueLine = assignment.getValue(blockLineVariable);

        Character expected = this.valueSpace.get(valueLine)[pointMaskOffset];
        return pixel.equals(expected);
    }
}
