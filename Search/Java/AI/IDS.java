// Mahdi Rasouli - 9812223249

package AI;

import model.Node;

import java.util.*;

public class IDS {
    int depth = 0;
    public void search(Node startNode){
        IDDFS(startNode, Integer.MAX_VALUE);
    }

    public void IDDFS(Node startNode, int maxDepth){
        for(int depth = 0; depth <= maxDepth; depth++){
            List<Object> dls = DLS(startNode, depth);
            Node result = (Node) dls.get(0); // node
            Status resultStatus = (Status) dls.get(1); // cutoff / node / failure
            if(result != null){
                System.out.println(depth);
                System.out.println("sum: " + result.sum);
                return;
            }
        }
    }

    public List<Object> DLS(Node node, int depth){

        if (node.isGoal()){
            printRes(node);
            return pair(node, Status.node);
        }

        else if (depth == 0){
            return pair(null, Status.cutoff);
        }
        else {
            ArrayList<Node> children = node.successor();

            for (Node child : children) {
                List<Object> dls = DLS(child, depth - 1);
                Node result = (Node) dls.get(0);
                if (result != null) {
                    depth += 1;
                    printRes(node);
                    return dls;
                }
            }
        }
        return pair(null, Status.failure);

    }

    public enum Status {
        node,
        failure,
        cutoff
    }

    public static List<Object> pair(Node node, Status status){
        List<Object> res = new ArrayList<>();
        res.add(node);
        res.add(status);
        return res;
    }

    public static void printRes(Node node){
        int i = node.currentCell.i + 1;
        int j = node.currentCell.j + 1;
        System.out.println(i + " " + j);
    }
}
