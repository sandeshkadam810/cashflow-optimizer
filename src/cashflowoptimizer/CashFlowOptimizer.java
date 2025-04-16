package cashflowoptimizer;
import java.util.*;
import java.io.*;
public class CashFlowOptimizer {
    private Graph graph;
    private RLAgent agent;
    private List<TransactionRecord> bestTransactions;
    private double bestTotalCost;
    private boolean hasRun;
    private List<Double> costHistory;
    
    public CashFlowOptimizer() {
        graph = new Graph();
        agent = new RLAgent(0.1, 0.9, 0.3);  // learning rate, discount factor, exploration rate
        bestTransactions = new ArrayList<>();
        bestTotalCost = Double.MAX_VALUE;
        hasRun = false;
        costHistory = new ArrayList<>();
    }
    
    public void addNode(String nodeId, NodeType type, double balance) {
        graph.addNode(nodeId, type, balance);
    }
    
    public boolean addEdge(String sourceId, String destId, double capacity, double cost) {
        return graph.addEdge(sourceId, destId, capacity, cost);
    }
    
    public void optimize(int iterations) {
        if (iterations <= 0) {
            System.out.println("Number of iterations must be positive.");
            return;
        }
        
        // Find all revenue and expense nodes
        List<String> revenueNodes = new ArrayList<>();
        List<String> expenseNodes = new ArrayList<>();
        
        for (String nodeId : graph.getNodeIds()) {
            NodeType type = graph.getNodeType(nodeId);
            if (type == NodeType.REVENUE) {
                revenueNodes.add(nodeId);
            } else if (type == NodeType.EXPENSE) {
                expenseNodes.add(nodeId);
            }
        }
        
        if (revenueNodes.isEmpty() || expenseNodes.isEmpty()) {
            System.out.println("Need at least one revenue source and one expense destination.");
            return;
        }
        
        hasRun = true;
        costHistory.clear();
        
        for (int i = 0; i < iterations; i++) {
            // For each revenue-expense pair, determine optimal flow
            double iterationCost = 0;
            
            for (String revenueId : revenueNodes) {
                for (String expenseId : expenseNodes) {
                    // Get current state
                    Map<String, Double> balances = new HashMap<>();
                    for (String nodeId : graph.getNodeIds()) {
                        balances.put(nodeId, graph.getBalance(nodeId));
                    }
                    State currentState = new State(balances);
                    
                    // Create action for this revenue-expense pair
                    Action action = new Action(revenueId, expenseId);
                    List<Action> possibleActions = new ArrayList<>();
                    possibleActions.add(action);
                    
                    // Check if this is the best action according to Q-learning
                    Action selectedAction = agent.selectAction(currentState, possibleActions);
                    
                    if (selectedAction != null) {
                        // Create a temporary graph for simulation
                        Graph tempGraph = graph.copy();
                        
                        // Run Min-Cost Max-Flow algorithm
                        MinCostMaxFlow mcmf = new MinCostMaxFlow(tempGraph);
                        mcmf.computeMinCostMaxFlow(selectedAction.getSourceNodeId(), 
                                                  selectedAction.getSinkNodeId());
                        
                        double flowCost = mcmf.getTotalCost();
                        iterationCost += flowCost;
                        
                        // // Update balances based on flow
                        // for (TransactionRecord tr : mcmf.getTransactions()) {
                        //     String sourceId = tr.getSourceNode();
                        //     String destId = tr.getDestNode();
                        //     double amount = tr.getAmount();
                            
                        //     // Update balances in the actual graph
                        //     graph.setBalance(sourceId, graph.getBalance(sourceId) - amount);
                        //     graph.setBalance(destId, graph.getBalance(destId) + amount);
                        // }
                        
                        // Create next state
                        Map<String, Double> nextBalances = new HashMap<>();
                        for (String nodeId : graph.getNodeIds()) {
                            nextBalances.put(nodeId, tempGraph.getBalance(nodeId));
                        }
                        State nextState = new State(nextBalances);
                        
                        // Calculate reward (negative cost is better)
                        double reward = -flowCost;

                        
                        

                        
                        // Update Q-values
                        agent.updateQValue(currentState, selectedAction, reward, nextState, possibleActions);
                        
                        // If this is the best flow, remember it
                        if (i == iterations - 1 || flowCost < bestTotalCost) {
                            bestTotalCost = flowCost;
                            bestTransactions = new ArrayList<>(mcmf.getTransactions());
                        }
                    }
                }
            }
            
            costHistory.add(iterationCost);
            
            // Decrease exploration rate over time
            agent.decreaseExplorationRate(0.95);
            
            System.out.printf("Iteration %d: Total Cost = $%.2f, Exploration Rate = %.2f\n", 
                            i + 1, iterationCost, agent.getExplorationRate());
        }
        
        // Save optimization results
        saveOptimizationData();
    }

    public void applyTransactionsAndUpdateBalancesToJson(List<TransactionRecord> transactions) {
        // Apply each transaction to update the node balances
        for (TransactionRecord tr : transactions) {
            String sourceId = tr.getSourceNode();
            String destId = tr.getDestNode();
            double amount = tr.getAmount();
    
            // Deduct from source
            double newSourceBalance = graph.getBalance(sourceId) - amount;
            graph.setBalance(sourceId, newSourceBalance);
    
            // Add to destination
            double newDestBalance = graph.getBalance(destId) + amount;
            graph.setBalance(destId, newDestBalance);
        }
    
        // Now save the updated balances to the JSON file
        saveOptimizationData();
    }
    
    
    public void displayResults() {
        if (!hasRun) {
            System.out.println("No optimization has been run yet.");
            return;
        }
        
        System.out.println("Optimized Cash Flow Allocation:");
        for (TransactionRecord tr : bestTransactions) {
            System.out.println(tr);
        }
        
        System.out.printf("Total Cost: $%.2f\n", bestTotalCost);
        
        if (costHistory.size() > 1) {
            double initialCost = costHistory.get(0);
            double finalCost = costHistory.get(costHistory.size() - 1);
            double savings = initialCost - finalCost;
            
            System.out.printf("Total Cost Savings: $%.2f (%.2f%%)\n", 
                           savings, (savings / initialCost) * 100);
            System.out.println("RL Policy Updated: " + (agent.hasImproved() ? "Yes" : "No"));
        }

        applyTransactionsAndUpdateBalancesToJson(bestTransactions);
        
        // Display current node balances
        System.out.println("\nCurrent Node Balances:");
        for (String nodeId : graph.getNodeIds()) {
            System.out.printf("%s (%s): $%.2f\n", 
                           nodeId, graph.getNodeType(nodeId), graph.getBalance(nodeId));
        }
    }
    
private void saveOptimizationData() {
    try {
        // Create data directory if it doesn't exist
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }

        // Save financial data in JSON format
        PrintWriter writer = new PrintWriter(new File("data/financial_data.json"));
        writer.println("{");
        writer.println("  \"nodes\": [");

        List<String> nodeIds = new ArrayList<>(graph.getNodeIds());
        for (int i = 0; i < nodeIds.size(); i++) {
            String nodeId = nodeIds.get(i);
            writer.printf("    {\"id\": \"%s\", \"type\": \"%s\", \"balance\": %.2f}%s\n",
                       nodeId, graph.getNodeType(nodeId), graph.getBalance(nodeId),
                       (i < nodeIds.size() - 1 ? "," : ""));
        }

        writer.println("  ],");
        writer.println("  \"transactions\": [");

        for (int i = 0; i < bestTransactions.size(); i++) {
            TransactionRecord tr = bestTransactions.get(i);
            writer.printf("    {\"source\": \"%s\", \"destination\": \"%s\", \"amount\": %.2f, \"cost\": %.2f}%s\n",
                       tr.getSourceNode(), tr.getDestNode(), tr.getAmount(), tr.getCost(),
                       (i < bestTransactions.size() - 1 ? "," : ""));
        }

        writer.println("  ],");
        writer.println("  \"costHistory\": [");

        for (int i = 0; i < costHistory.size(); i++) {
            writer.printf("    %.2f%s\n", costHistory.get(i),
                       (i < costHistory.size() - 1 ? "," : ""));
        }

        writer.println("  ],");
        writer.printf("  \"totalCost\": %.2f,\n", bestTotalCost);
        writer.println("  \"rlImproved\": " + agent.hasImproved());
        writer.println("}");

        writer.close();
        System.out.println("Financial data saved to data/financial_data.json");

    } catch (IOException e) {
        System.out.println("Error saving financial data: " + e.getMessage());
    }
}

 /**
     * Gets the list of best transactions after optimization
     * @return List of TransactionRecord objects
     */
    public List<TransactionRecord> getBestTransactions() {
        return bestTransactions;
    }
    
    /**
     * Gets the total cost of the best solution
     * @return The total cost
     */
    public double getBestTotalCost() {
        return bestTotalCost;
    }
    
    /**
     * Gets the history of costs during optimization
     * @return List of costs for each iteration
     */
    public List<Double> getCostHistory() {
        return costHistory;
    }
    
    /**
     * Gets the type of a specific node
     * @param nodeId The ID of the node
     * @return The NodeType of the specified node
     */
    public NodeType getNodeType(String nodeId) {
        return graph.getNodeType(nodeId);
    }
    
    /**
     * Gets the current balance of a specific node
     * @param nodeId The ID of the node
     * @return The current balance of the specified node
     */
    public double getNodeBalance(String nodeId) {
        return graph.getBalance(nodeId);
    }
    
    /**
     * Gets a list of all node IDs in the graph
     * @return List of node IDs
     */
    public List<String> getNodeIds() {
        return new ArrayList<>(graph.getNodeIds());
    }



}