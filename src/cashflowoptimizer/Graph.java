package cashflowoptimizer;

import java.util.*;

public class Graph {
    private Map<String, Integer> nodeIdToIndex;
    private Map<Integer, String> indexToNodeId;
    private Map<String, NodeType> nodeTypes;
    private Map<String, Double> balances;
    private int nodeCount;
    private List<List<Edge>> adjacencyList;
    
    public Graph() {
        nodeIdToIndex = new HashMap<>();
        indexToNodeId = new HashMap<>();
        nodeTypes = new HashMap<>();
        balances = new HashMap<>();
        nodeCount = 0;
        adjacencyList = new ArrayList<>();
    }
    
    public int addNode(String nodeId, NodeType type, double balance) {
        if (!nodeIdToIndex.containsKey(nodeId)) {
            int index = nodeCount++;
            nodeIdToIndex.put(nodeId, index);
            indexToNodeId.put(index, nodeId);
            nodeTypes.put(nodeId, type);
            balances.put(nodeId, balance);
            adjacencyList.add(new ArrayList<>());
            return index;
        }
        return nodeIdToIndex.get(nodeId);
    }
    
    public boolean addEdge(String sourceId, String destId, double capacity, double cost) {
        if (!nodeIdToIndex.containsKey(sourceId) || !nodeIdToIndex.containsKey(destId)) {
            return false;
        }
        
        int source = nodeIdToIndex.get(sourceId);
        int dest = nodeIdToIndex.get(destId);
        
        // Create forward edge
        Edge forwardEdge = new Edge(source, dest, capacity, cost);
        // Create reverse edge with initial capacity 0
        Edge reverseEdge = new Edge(dest, source, 0, -cost);
        
        // Set reverse edge pointers
        forwardEdge.setReverseEdge(reverseEdge);
        reverseEdge.setReverseEdge(forwardEdge);
        
        // Add edges to the adjacency list
        adjacencyList.get(source).add(forwardEdge);
        adjacencyList.get(dest).add(reverseEdge);
        
        return true;
    }
    
    public NodeType getNodeType(String nodeId) {
        return nodeTypes.get(nodeId);
    }
    
    public double getBalance(String nodeId) {
        return balances.getOrDefault(nodeId, 0.0);
    }
    
    public void setBalance(String nodeId, double balance) {
        balances.put(nodeId, balance);
    }
    
    public Set<String> getNodeIds() {
        return nodeIdToIndex.keySet();
    }
    
    public int getNodeCount() {
        return nodeCount;
    }
    
    public List<Edge> getAdjacentEdges(int nodeIndex) {
        return adjacencyList.get(nodeIndex);
    }
    
    public int getNodeIndex(String nodeId) {
        return nodeIdToIndex.getOrDefault(nodeId, -1);
    }
    
    public String getNodeId(int index) {
        return indexToNodeId.get(index);
    }
    
    public List<List<Edge>> getAdjacencyList() {
        return adjacencyList;
    }
    
    // Deep copy method for RL simulations
    public Graph copy() {
        Graph newGraph = new Graph();
        
        // Copy nodes and their properties
        for (String nodeId : nodeIdToIndex.keySet()) {
            newGraph.addNode(nodeId, nodeTypes.get(nodeId), balances.get(nodeId));
        }
        
        // Copy edges
        for (int i = 0; i < adjacencyList.size(); i++) {
            for (Edge edge : adjacencyList.get(i)) {
                if (edge.getCapacity() > 0 && edge.getReverseEdge().getCapacity() == 0) {
                    // Only add forward edges to avoid duplicates
                    String sourceId = indexToNodeId.get(edge.getSource());
                    String destId = indexToNodeId.get(edge.getDest());
                    newGraph.addEdge(sourceId, destId, edge.getCapacity(), edge.getCost());
                }
            }
        }
        
        return newGraph;
    }

    public void updateBalance(String nodeId, double amount) {
        // Check if the nodeId exists in the balances map
        if (balances.containsKey(nodeId)) {
            // Update the balance by adding the amount to the current balance
            double currentBalance = balances.get(nodeId);
            double newBalance = currentBalance + amount;
            
            // Update the balance in the map
            balances.put(nodeId, newBalance);
            
            // Optionally, log the balance update for debugging purposes
            System.out.println("Updated Balance for Node " + nodeId + ": " + newBalance);
        } else {
            // If nodeId is not found in the balances map, print an error message
            System.out.println("Error: Node " + nodeId + " not found.");
        }
    }
    
}
