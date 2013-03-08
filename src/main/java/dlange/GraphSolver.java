package dlange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


/**
 * Generic Graph solver with implementation for blind BFS
 * 
 * @author dlange
 *
 * @param <T>
 */
public class GraphSolver<T> {
    // cross-cutting field used to prune graph (only used if blind transitions)
    private Map<String,String> exists;
    // cross-cutting field used to backtrack from target to root when generating solution output
    private Map<GraphNode<T>, GraphNode<T>> path;
    

    /**
     * Generic Bredth First Search given a root node in a graph, a goal node to search for, and an object
     * which generates transition states from a given state.
     * Answer back List<T> representing the path of <T> states from root to solution.
     *   
     * @see http://en.wikipedia.org/wiki/Breadth-first_search
     * 
     * @param root
     * @param goal
     * @param transition
     * @return
     */
    protected List<T> bfs(T root, Goal<T> goal, Transition<T> transition) {
    	initCollections();
    	// queue to hold successor nodes
        Queue<GraphNode<T>> queue = new LinkedList<GraphNode<T>>();
        // root node 
        GraphNode<T> start = new GraphNode<T>(root);
        // begin by marking root visited and enqueing
        visit(start, null);
        queue.offer(start);
        
        // continue until queue empty
        while (queue.size() > 0) {
            GraphNode<T> f = queue.remove();
            // trival test
            if (goal.isGoal(f.data)) {
            	return buildSolution(f);
            }
            // blind BFS uses transition object to generate edges to this node on the fly
            populateEdges(f, transition);
            
            // traverse these generated edges
            for (GraphNode<T> edge : f.edges) {
            	// if not visited, visit and queue
                if (!edge.visited) {
                	visit(edge, f);
                    queue.offer(edge);
                    // check for goal 
                    if (goal.isGoal(edge.data)) {
                    	return buildSolution(edge);
                    }
                }
            }
        }
        // by contract return empty list if target not found
        return new ArrayList<T>();
    }

    /**
     * The fields initialized below should be scoped to the bfs() method to keep it stateless; however, for 
     * readability of the bfs algorithm they are defined as fields and always initialized here. They are not
     * central to BFS but support pruning and output generation.
     */
    private void initCollections() {
        exists = new HashMap<String,String>();
        path = new HashMap<GraphNode<T>, GraphNode<T>>();
    }
    
    /**
     * Generate transition state nodes from given node using given Transition object. 
     * Use the exists Map to prune previously traversed states. Add un-pruned nodes
     * to the given node as an edge.
     * 
     * @param node
     * @param transition
     */
    private void populateEdges(GraphNode<T> node, Transition<T> transition) {
        // successor states
    	for (T mixture : transition.transitions((T)node.data)) {
            String key = mixture.toString();
            if (!exists.containsKey(key)) {
                exists.put(key, key);
                node.addEdge(new GraphNode<T>(mixture));
            }
    	}
    }
    
    /**
     * Starting at given target node and working backwards until root reached, add each node's data
     * to a collection. Reverse the collection to represent the forward sequence of data.
     * 
     * @param target
     * @return
     */
    private List<T> buildSolution(GraphNode<T> target) {
    	List<T> solution = new ArrayList<T>();
        while (null != target) {
            solution.add((T) target.data);
            target = path.get(target);
        }
        Collections.reverse(solution);
        return solution;
    }

    /**
     * helper method ot mark node visited. Add to path.
     * 
     * @param node
     * @param parent
     */
    private void visit(GraphNode<T> node, GraphNode<T> parent) {
    	node.visited = true;
    	path.put(node, parent);
    }


    /**
     * Class representing a node of a graph. The node contains a collection of connected nodes representing the
     * nodes directly connected to this node via an edge. The node also contains a generic data type and a flag
     * to mark if it was visited by a search algorithm.
     * 
     * @author dlange
     *
     * @param <T>
     */
    class GraphNode<T> {
        List<GraphNode<T>> edges = new ArrayList<GraphNode<T>>();
        T data;
        boolean visited;
        
        public GraphNode(T data) {
            this.data = data;
            this.visited = false;
        }
        public void addEdge(GraphNode<T> node) {
            this.edges.add(node);
        }
    }
    
    /**
     * Interface for identifying transitions from a given node
     * 
     * @author dlange
     *
     * @param <T>
     */
    public interface Transition<T> {
    	List<T> transitions(T root);
    }
    
    /**
     * Interface for identifying if given candidate node satisfied goal state
     * 
     * @author dlange
     *
     * @param <T>
     */
    public interface Goal<T> {
    	boolean isGoal(T candidate);
    }
}   
