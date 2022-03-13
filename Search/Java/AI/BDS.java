// Mahdi Rasouli - 9812223249

package AI;

import model.Cell;
import model.Node;
import model.OPERATION_TYPE;

import java.util.*;

public class BDS {

    public LinkedList<Node> findPathBDS(Node startNode, Node goalNode){

        BFSData sourceData = new BFSData(startNode);
        sourceData.goalValue = goalNode;
        BFSData destData = new BFSData(goalNode);
        destData.goalValue = startNode;

        while (!sourceData.isFinished() && !destData.isFinished()){

            ArrayList<Object> collision = searchLevel(sourceData, destData, Type.forward);
            if(collision != null){
                return BFSData.mergePaths(sourceData, destData, collision);
            }

            collision = searchLevel(destData, sourceData, Type.backward);
            if(collision != null){
                return BFSData.mergePaths(sourceData, destData, collision);
            }
        }

        return null;
    }

    ArrayList<Object> searchLevel(BFSData source, BFSData dest, Type type){

        Hashtable<String, Boolean> explored = new Hashtable<>();
        int count = source.toVisit.size();
        for(int i = 0; i < count; i++){
            PathNode pathNode = source.toVisit.poll();
            assert pathNode != null;
            explored.put(pathNode.getNode().hash(), true);
            ///////////
            String nodeHash = pathNode.getNode().toString();
            Set<String> dest_visited_nodes = dest.visited.keySet();
            for(String str: dest_visited_nodes){

                String[] arrOfStr = str.split("@");
                String splitedCell = "(" + arrOfStr[0] + "," + arrOfStr[1] + ")";
                if(nodeHash.equals(splitedCell)) {
                    int ab = 0;
                    // From Start To Goal
                    Set<String> start_to_goal_nodes = new HashSet<>();

                    PathNode sourcePathNode;
                    PathNode sourcePathNode_keep;
                    if(type == Type.forward) {
                        sourcePathNode = source.visited.get(pathNode.getNode().hash());
                        sourcePathNode_keep = source.visited.get(pathNode.getNode().hash());
                    } else {
                        sourcePathNode = dest.visited.get(str);
                        sourcePathNode_keep = dest.visited.get(str);
                    }

                    start_to_goal_nodes.add(sourcePathNode.getNode().toString());
                    while(sourcePathNode.getPreviousPathNode() != null){
                        sourcePathNode = sourcePathNode.getPreviousPathNode();
                        if(sourcePathNode.getNode().currentCell.getOperationType().equals(OPERATION_TYPE.DECREASE_GOAL)){
                            ab -= sourcePathNode.getNode().currentCell.getValue();
                        }
                        if(sourcePathNode.getNode().currentCell.getOperationType().equals(OPERATION_TYPE.INCREASE_GOAL)){
                            ab += sourcePathNode.getNode().currentCell.getValue();
                        }
                        start_to_goal_nodes.add(sourcePathNode.getNode().toString());
                    }

                    // From Goal to Start
                    Set<String> goal_to_start_nodes = new HashSet<>();

                    PathNode destPathNode;
                    PathNode destPathNode_keep;
                    if(type == Type.forward) {
                        destPathNode = dest.visited.get(str);
                        destPathNode_keep = dest.visited.get(str);
                    } else {
                        destPathNode = source.visited.get(pathNode.getNode().hash());
                        destPathNode_keep = source.visited.get(pathNode.getNode().hash());
                    }

                    destPathNode.setSum(pathNode.getSum());
                    goal_to_start_nodes.add(destPathNode.getNode().toString());

                    if(destPathNode.getNode().currentCell.getOperationType().equals(OPERATION_TYPE.DECREASE_GOAL)){
                        ab -= destPathNode.getNode().currentCell.getValue();
                    }
                    if(destPathNode.getNode().currentCell.getOperationType().equals(OPERATION_TYPE.INCREASE_GOAL)){
                        ab += destPathNode.getNode().currentCell.getValue();
                    }

                    while(destPathNode.getPreviousPathNode() != null){
                        int sum_e_ghabli = destPathNode.getSum();
                        destPathNode = destPathNode.getPreviousPathNode();

                        if(destPathNode.getNode().currentCell.getOperationType().equals(OPERATION_TYPE.DECREASE_GOAL)){
                            ab -= destPathNode.getNode().currentCell.getValue();
                        }
                        if(destPathNode.getNode().currentCell.getOperationType().equals(OPERATION_TYPE.INCREASE_GOAL)){
                            ab += destPathNode.getNode().currentCell.getValue();
                        }

                        Cell current_cell = destPathNode.getNode().currentCell;
                        destPathNode.setSum(Node.calculate(sum_e_ghabli, current_cell.getOperationType(), current_cell.getValue()));
                        goal_to_start_nodes.add(destPathNode.getNode().toString());

                    }

                    start_to_goal_nodes.retainAll(goal_to_start_nodes);

                    int goal_val = 0;
                    if(type == Type.forward) {
                        goal_val = source.goalValue.currentCell.getValue();
                    } else {
                        goal_val = dest.goalValue.currentCell.getValue();
                    }

                    if (start_to_goal_nodes.size() == 1 && destPathNode.getSum() > (goal_val + ab)) {
                        ArrayList<Object> nodes = new ArrayList<>();
                        nodes.add(sourcePathNode_keep.getNode());
                        nodes.add(destPathNode_keep.getNode());
                        nodes.add((destPathNode.getSum()));
                        return nodes;
                    }

                }
            }

            Node node = pathNode.getNode();
            if(type == Type.forward) {
                pathNode.setSum(pathNode.getNode().sum);
            }
            if (node != null){
                ArrayList<Node> children = node.successor();

                for(Node child: children){
                    if (!source.visited.containsKey(child.hash()) && !explored.containsKey(child.hash())) {
                        PathNode next = new PathNode(child, pathNode);

                        if (type == Type.forward) {
                            if(child.currentCell == source.goalValue.currentCell){
                                break;
                            }
                            next.setSum(next.getNode().sum);
                        } else {
                            if(child.currentCell == dest.goalValue.currentCell){
                                break;
                            }
                        }

                        source.visited.put(child.hash(), next);
                        source.toVisit.add(next);
                    }
                }

            }
        }
        return null;
    }


    public enum Type {
        forward,
        backward
    }
}

class PathNode{
    private Node node = null;
    private PathNode previousPathNode = null;
    private int sum = 0;
    public PathNode(Node n, PathNode prev){
        node = n;
        previousPathNode = prev;
        sum = 0;
    }
    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public Node getNode() {
        return node;
    }
    public PathNode getPreviousPathNode(){
        return previousPathNode;
    }
    public LinkedList<Node> join(boolean reverse){
        LinkedList<Node> path = new LinkedList<Node>();
        PathNode path_node = this;
        while(path_node != null){
            if (reverse){
                path.addLast(path_node.getNode());
            } else {
                path.addFirst(path_node.getNode());
            }
            path_node = path_node.getPreviousPathNode();
        }
        return path;
    }
}

class BFSData{
    public Queue<PathNode> toVisit = new LinkedList<PathNode>();
    public HashMap<String, PathNode> visited = new HashMap<String, PathNode>();
    public Node goalValue;
    public BFSData(Node root){
        PathNode sourcePath = new PathNode(root, null);
        toVisit.add(sourcePath);
        visited.put(root.hash(), sourcePath);
    }
    public boolean isFinished(){
        return toVisit.isEmpty();
    }

    static LinkedList<Node> mergePaths(BFSData bfs1, BFSData bfs2, ArrayList<Object> intersection){
        String node1 = ((Node) intersection.get(0)).hash();
        String node2 = ((Node) intersection.get(1)).hash();
        int sum = (int) intersection.get(2);
        PathNode end1 = bfs1.visited.get(node1);
        PathNode end2 = bfs2.visited.get(node2);
        LinkedList<Node> pathOne = end1.join(false);
        LinkedList<Node> pathTwo = end2.join(true);

        pathTwo.removeFirst();
        pathOne.addAll(pathTwo);

        System.out.println(pathOne.size());
        for(int i = 0; i<pathOne.size(); i++){
            Node n = pathOne.get(i);
            int ii = n.currentCell.i + 1;
            int jj = n.currentCell.j + 1;
            System.out.println(ii + " " + jj);
        }

        System.out.println(sum);

        return pathOne;
    }

}
