package AI;

import model.Node;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

public class BFS {

    public void search(Node startNode) {
        Queue<Node> frontier = new LinkedList<Node>();
        Hashtable<String, Boolean> inFrontier = new Hashtable<>();
        Hashtable<String, Boolean> explored = new Hashtable<>();

        if (startNode.isGoal()) {
            System.out.println("score: " + startNode.sum);
            printResult(startNode, 0);
            return;
        }
        frontier.add(startNode);
        inFrontier.put(startNode.hash(), true);
        while (!frontier.isEmpty()) {

            // in java we first poll (get the last item), then remove! :))
            Node temp = frontier.poll();
            inFrontier.remove(temp.hash());


            explored.put(temp.hash(), true); // we put the first node in explored

            // we get the successors of the temp child
            ArrayList<Node> children = temp.successor();


            for (Node child : children) {
                boolean not_contain = !(inFrontier.containsKey(child.hash()));
                not_contain = not_contain && !(explored.containsKey(child.hash()));
                if (not_contain) {
                    if (child.isGoal()) {
                        printResult(child, 0);
                        System.out.println(child.sum);
                        return;
                    }
                    frontier.add(child);
                    inFrontier.put(child.hash(), true);
                }
            }
        }

        System.out.println("no solution");

    }

    public static void printResult(Node node, int depthCounter) {
        if (node.parent == null) {
            System.out.println("problem solved at a depth of: " + depthCounter);
            return;
        }

        System.out.println(node);
        node.drawState();
        printResult(node.parent, depthCounter + 1);
    }


}
