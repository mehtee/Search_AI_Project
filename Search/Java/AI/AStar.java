// Mahdi Rasouli - 9812223249

package AI;

import model.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class AStar {
    public Node search(Node startNode, Node goalNode){
        PriorityQueue<Node> closedList = new PriorityQueue<>();
        PriorityQueue<Node> openList = new PriorityQueue<>();
        HashMap<String, Boolean> closedTable = new HashMap<>();
        HashMap<String, Boolean> openTable = new HashMap<>();
        startNode.f = startNode.sum + startNode.heuristic(goalNode);
        openList.add(startNode);
        while (!openList.isEmpty()){
            Node n = openList.peek();

            if(n.isGoal()){
                BFS.printResult(n, 0);
                System.out.println(n.sum);
                return n;
            }

            ArrayList<Node> neighbors = n.successor();
            for(Node child: neighbors){
                int totalWeight = n.sum + child.heuristic(goalNode);

                if(!openTable.containsKey(child.hash()) && !closedTable.containsKey(child.hash())){
                    child.f = totalWeight + child.heuristic(goalNode);
                    openList.add(child);
                    openTable.put(child.hash(), true);
                } else {
                    if(totalWeight < child.sum){
                        child.f = child.sum + child.heuristic(goalNode);

                        if(closedList.contains(child)){
                            closedList.remove(child);
                            closedTable.remove(child.hash());
                            openList.add(child);
                            openTable.put(child.hash(), true);
                        }
                    }
                }
            }
            openList.remove(n);
            openTable.remove(n.hash());
            closedList.add(n);
            closedTable.put(n.hash(), true);
        }

        return null;
    }
}
