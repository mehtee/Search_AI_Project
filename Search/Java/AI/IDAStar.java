// Mahdi Rasouli - 9812223249

package AI;

import model.Node;

import java.util.ArrayList;

public class IDAStar {
    public Node ida(Node startNode, Node goalNode){
        int threshold = startNode.heuristic(goalNode);
        while(true){
            ArrayList<Object> temp = search(startNode, goalNode, startNode.sum, threshold);
            if((Type) temp.get(0) == Type.found){
                return (Node) temp.get(2);
            }
            threshold = (int) temp.get(1);
        }
    }

    public ArrayList<Object> search(Node node, Node goal, int g, int threshold){

        int f = g + node.heuristic(goal);

        if(f > threshold){
            ArrayList<Object> arr = new ArrayList<>();
            arr.add(Type.f);
            arr.add(f);
            arr.add(null);
            return arr;
        }

        if(node.isGoal()){
            ArrayList<Object> arr = new ArrayList<>();
            arr.add(Type.found);
            arr.add(-1);
            arr.add(node);
            return arr;
        }

        int min = Integer.MAX_VALUE;
        ArrayList<Node> successors = node.successor();

        for(Node child: successors){
            ArrayList<Object> temp_arr = search(child, goal, child.sum, threshold);
            if((Type) temp_arr.get(0) == Type.found){
                return temp_arr;
            }
            if((int) temp_arr.get(1) < min){
                min = (int) temp_arr.get(1);
            }
        }

        ArrayList<Object> arr = new ArrayList<>();
        arr.add(Type.f);
        arr.add(min);
        arr.add(null);
        return arr;

    }
    public enum Type{
        f,
        found
    }
}
