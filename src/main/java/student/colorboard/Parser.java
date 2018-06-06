package student.colorboard;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {

    public static Board readBoard() throws IOException
    {
        return readBoard(new BufferedReader(new InputStreamReader(System.in)));
    }

    public static Board readBoard(String string) throws IOException
    {
        Reader inputString = new StringReader(string);
        return readBoard(new BufferedReader(inputString));
    }

    private static Board readBoard(BufferedReader reader) throws IOException {
        String[] sizeString = reader.readLine().split(",");
        int rows = Integer.parseInt(sizeString[0]);
        int columns = Integer.parseInt(sizeString[1]);

        ArrayList<BoardConstraint> rowConstraints = new ArrayList<>();
        for (int i = 0; i < rows; ++i) {
            rowConstraints.add(readConstraint(reader.readLine()));
        }

        ArrayList<BoardConstraint> coumnConstraints = new ArrayList<>();
        for (int i = 0; i < columns; ++i) {
            coumnConstraints.add(readConstraint(reader.readLine()));
        }

        return new Board(rows, columns, coumnConstraints, rowConstraints);
    }

    private static BoardConstraint readConstraint(String iline) {
        ArrayList<Block> blocks = new ArrayList<>();
        if (iline != null && iline.trim().length() > 0) {
            String[] line = iline.split(",");
            for (int i = 0; i < line.length; i += 2) {
                blocks.add(new Block(line[i].charAt(0), Integer.parseInt(line[i + 1]), i / 2));
            }
        }

        return new BoardConstraint(blocks);
    }

    public static char[][] readSolution(String string)  throws IOException{
        Reader inputString = new StringReader(string);
        return readSolution(new BufferedReader(inputString));
    }

    private static char[][] readSolution(BufferedReader reader)  throws IOException{
        List<char[]> res = reader.lines().map(x->x.toCharArray()).collect(Collectors.toList());

        int colls = res.get(0).length;
        char[][] result = new char[colls][res.size()];

        for(int row = 0; row < res.size(); ++row){
            for(int coll = 0; coll < colls; ++coll){
                result[coll][row] = res.get(row)[coll];
            }
        }

        return result;
    }
}
