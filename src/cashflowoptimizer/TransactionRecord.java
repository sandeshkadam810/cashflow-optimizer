package cashflowoptimizer;

public class TransactionRecord {
    private String sourceNode;
    private String destNode;
    private double amount;
    private double cost;
    
    public TransactionRecord(String sourceNode, String destNode, double amount, double cost) {
        this.sourceNode = sourceNode;
        this.destNode = destNode;
        this.amount = amount;
        this.cost = cost;
    }
    
    public String getSourceNode() {
        return sourceNode;
    }
    
    public String getDestNode() {
        return destNode;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public double getCost() {
        return cost;
    }
    
    @Override
    public String toString() {
        return String.format("- Transferred $%.2f from %s to %s (Cost: $%.2f)",
            amount, sourceNode, destNode, cost * amount);
    }
}
