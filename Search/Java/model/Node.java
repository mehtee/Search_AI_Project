package model;

import core.Constants;

import java.util.ArrayList;
import java.util.Hashtable;

import static model.ACTION_TYPE.*;

public class Node implements Comparable<Node>{
    public Board board;
    public int sum = 0;
    public Node parent = null;
    public Cell currentCell;
    private Cell[][] cells;
    private int goalValue;
    private Hashtable<String, Boolean> repeatedStates;
    private ACTION_TYPE previousAction;
    public int f;
    public boolean astar = false;

    public Node(Cell currentCell, int currentValue, int goalValue, Board board, Node parent, Hashtable<String, Boolean> repeated, ACTION_TYPE previousAction) {
        this.currentCell = currentCell;
        this.sum = currentValue;
        this.board = board;
        this.cells = board.getCells();
        this.parent = parent;
        this.previousAction = previousAction;
        this.goalValue = goalValue;
        Hashtable<String, Boolean> hashtableTemp = new Hashtable<String, Boolean>(repeated);
        hashtableTemp.put(this.toString(), true);
        this.repeatedStates = hashtableTemp;
        setGoalValue();
        if(parent != null && parent.astar){
            this.astar = true;
        }
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.f, other.f);
    }

    public ArrayList<Node> successor() {
        ArrayList<Node> result = new ArrayList<Node>();
        fetchLeftCell(result);
        fetchDownCell(result);
        fetchRightCell(result);
        fetchUpCell(result);
        return result;
    }

    private void fetchRightCell(ArrayList<Node> result) {
        if (canMoveRight()) {
            Cell rightCell = this.cells[this.currentCell.i][this.currentCell.j + 1];
            if (isValidMove(rightCell)) {
                int calculatedValue = calculate(rightCell);
                Node rightNode = new Node(rightCell, calculatedValue, goalValue, board, this, repeatedStates, LEFT);
                result.add(rightNode);
            }
        }
    }

    private void fetchLeftCell(ArrayList<Node> result) {
        if (canMoveLeft()) {
            Cell leftCell = this.cells[this.currentCell.i][this.currentCell.j - 1];
            if (isValidMove(leftCell)) {
                int calculatedValue = calculate(leftCell);
                Node leftNode = new Node(leftCell, calculatedValue, goalValue, board, this, repeatedStates, RIGHT);
                result.add(leftNode);
            }
        }
    }

    private void fetchDownCell(ArrayList<Node> result) {
        if (canMoveDown()) {
            Cell downCell = this.cells[this.currentCell.i + 1][this.currentCell.j];
            if (isValidMove(downCell)) {
                int calculatedValue = calculate(downCell);
                Node downNode = new Node(downCell, calculatedValue, goalValue, board, this, repeatedStates, UP);
                result.add(downNode);
            }
        }
    }

    private void fetchUpCell(ArrayList<Node> result) {
        if (canMoveUp()) {
            Cell upCell = this.cells[this.currentCell.i - 1][this.currentCell.j];
            if (isValidMove(upCell)) {
                int calculatedValue = calculate(upCell);
                Node upNode = new Node(upCell, calculatedValue, goalValue, board, this, repeatedStates, DOWN);
                result.add(upNode);
            }
        }
    }

    public static int pathCost(OPERATION_TYPE type) {
        return switch (type) {
            case MINUS, DECREASE_GOAL -> 1;
            case ADD, INCREASE_GOAL -> 2;
            case MULT -> 5;
            case POW -> 11;
            default -> 1;
        };
    }

    public int calculate(Cell cell) {
        if(this.astar){
            return switch (cell.getOperationType()) {
                case MINUS -> (pathCost(OPERATION_TYPE.MINUS) * sum) - cell.getValue();
                case ADD ->  (pathCost(OPERATION_TYPE.ADD) * sum)  + cell.getValue();
                case POW -> (int) Math.pow( (pathCost(OPERATION_TYPE.POW) * sum) , cell.getValue());
                case MULT ->  (pathCost(OPERATION_TYPE.MULT) * sum)  * cell.getValue();
                default -> sum;
            };
        }
        return switch (cell.getOperationType()) {
            case MINUS -> sum - cell.getValue();
            case ADD -> sum + cell.getValue();
            case POW -> (int) Math.pow(sum, cell.getValue());
            case MULT -> sum * cell.getValue();
            default -> sum;
        };

    }


    public static int calculate(int sum, OPERATION_TYPE type, int value) {
        return switch (type) {
            case MINUS -> sum - value;
            case ADD -> sum + value;
            case POW -> (int) Math.pow(sum, value);
            case MULT -> sum * value;
            default -> sum;
        };
    }


    public int heuristic(Node goal) {
        return Math.abs(this.currentCell.i - goal.currentCell.i) + Math.abs(this.currentCell.j - goal.currentCell.j);
    }

    public String hash() {
        StringBuilder hash = new StringBuilder();
        hash.append(currentCell.i)
                .append("@")
                .append(currentCell.j)
                .append("@sum:")
                .append(sum)
                .append("@op:")
                .append(currentCell.op)
                .append("@val:")
                .append(currentCell.getValue()
                ).append("@prev_action:").append(this.previousAction);
        return hash.toString();
    }

    public void drawState() {

        for (int i = 0; i < board.getRow(); i++) {
            for (int j = 0; j < board.getCol(); j++) {
                if (cells[i][j].getOperationType() == OPERATION_TYPE.GOAL) {
                    System.out.print(Constants.ANSI_BRIGHT_GREEN +
                            OPERATION_TYPE.getOperationTag(cells[i][j].getOperationType())
                            + goalValue + spaceRequired(cells[i][j])
                    );
                    continue;
                }
                if (currentCell.j == j && currentCell.i == i) {
                    System.out.print(Constants.ANSI_BRIGHT_GREEN + Constants.PLAYER + sum + spaceRequired(cells[i][j]));
                } else {
                    System.out.print(Constants.ANSI_BRIGHT_GREEN +
                            OPERATION_TYPE.getOperationTag(cells[i][j].getOperationType())
                            + cells[i][j].getValue() + spaceRequired(cells[i][j])
                    );
                }
            }
            System.out.println();

        }
        System.out.println("-----------------------------------------");

    }

    private void setGoalValue() {
        if (currentCell.getOperationType() == OPERATION_TYPE.DECREASE_GOAL)
            goalValue -= currentCell.getValue();
        if (currentCell.getOperationType() == OPERATION_TYPE.INCREASE_GOAL)
            goalValue += currentCell.getValue();
    }

    public static void setGoalValue(Node dest, OPERATION_TYPE type, int value) {
        if (type == OPERATION_TYPE.DECREASE_GOAL)
            dest.goalValue -= value;
        if (type == OPERATION_TYPE.INCREASE_GOAL)
            dest.goalValue += value;
    }

    private String spaceRequired(Cell cell) {
        int length = String.valueOf(cell.getValue()).length();
        String result = " ".repeat(5 - length);
        if (cell.op.equals("+") || cell.op.equals("-") || cell.op.equals("*") || cell.op.equals("^")) {
            result += " ";
        }
        return result;
    }

    private boolean canEnterGoal(Cell cell) {
        if (cell != Cell.getGoal()) return true;
        else {
            return sum > goalValue;
        }
    }

    private boolean isWall(Cell cell) {
        return cell.getOperationType() == OPERATION_TYPE.WALL;
    }

    private boolean canMoveRight() {
        return this.currentCell.j < this.board.getRow() - 1;
    }

    private boolean canMoveLeft() {
        return this.currentCell.j > 0;
    }

    private boolean canMoveUp() {
        return this.currentCell.i > 0;
    }

    private boolean canMoveDown() {
        return this.currentCell.i < this.board.getCol() - 1;
    }

    public Hashtable<String, Boolean> getRepeatedStates() {
        return repeatedStates;
    }

    private Boolean isValidMove(Cell destCell) {
        return destCell != Cell.getStart() && canEnterGoal(destCell) && !isWall(destCell) && !repeatedStates.containsKey(destCell.toString());
    }

    public boolean isGoal() {
        if (currentCell.getOperationType() == OPERATION_TYPE.GOAL) {
            return sum > goalValue;
        }
        return false;
    }

    public int getGoalValue(){
        return goalValue;
    }
    @Override
    public String toString() {
        return "(" + this.currentCell.i + "," + this.currentCell.j + ")";
    }
}
