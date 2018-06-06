package student.colorboard;

public class Block {
    private Character _color;
    private int _length;
    private int _index;

    public Block(Character color, int length, int index){
        this._color = color;
        this._length = length;
        this._index = index;
    }

    public Character getColor() { return _color; }

    public int getLength() { return _length; }

    public int getIndex(){
        return _index;
    }
}
