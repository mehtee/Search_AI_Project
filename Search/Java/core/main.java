package core;

import AI.*;
import model.ACTION_TYPE;
import model.Board;
import model.Cell;
import model.Node;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Scanner;

public class main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String mn = sc.nextLine();
        int rows = Integer.parseInt(mn.split(" ")[0]);
        int columns = Integer.parseInt(mn.split(" ")[1]);
        String[][] board = new String[rows][columns];
        String[] lines = new String[rows];
        for (int i = 0; i < rows; i++) {
            lines[i] = sc.nextLine();
            String[] line = lines[i].split(" ");
            if (columns >= 0) System.arraycopy(line, 0, board[i], 0, columns);
        }
        Mapper mapper = new Mapper();
        Cell[][] cells = mapper.createCells(board, rows, columns);
        Board gameBoard = mapper.createBoard(cells, rows, columns);
        Hashtable<String, Boolean> initHash = new Hashtable<>();
        initHash.put(Cell.getStart().toString(), true);
        Node start = new Node(Cell.getStart(), Cell.getStart().getValue(), Cell.getGoal().getValue(), gameBoard, null, initHash, ACTION_TYPE.RIGHT);

//         goal or destination
        initHash.put(Cell.getGoal().toString(), true);
        Node goal = new Node(Cell.getGoal(), Cell.getGoal().getValue(), Cell.getStart().getValue(), gameBoard, null, initHash, ACTION_TYPE.DOWN);

        // BFS
//        BFS bfs = new BFS();
//        bfs.search(start);

        // IDS
//        IDS ids = new IDS();
//        ids.search(start);

        // BDS
        BDS bds = new BDS();
        LinkedList<Node> paths = bds.findPathBDS(start, goal);

        // A*
//        AStar a = new AStar();
//        System.out.println(a.search(start, goal));

        // IDA*
//        IDAStar ida = new IDAStar();
//        Node idastar = ida.ida(start, goal);
//        BFS.printResult(idastar, 0);
    }
}
