package cashflowoptimizer;

public class Edge {
    private int source;
    private int dest;
    private double capacity;
    private double flow;
    private double cost;
    private Edge reverseEdge;
    
    public Edge(int source, int dest, double capacity, double cost) {
        this.source = source;
        this.dest = dest;
        this.capacity = capacity;
        this.flow = 0;
        this.cost = cost;
    }
    
    public int getSource() {
        return source;
    }
    
    public int getDest() {
        return dest;
    }
    
    public double getCapacity() {
        return capacity;
    }
    
    public double getFlow() {
        return flow;
    }
    
    public double getCost() {
        return cost;
    }
    
    public Edge getReverseEdge() {
        return reverseEdge;
    }
    
    public void setReverseEdge(Edge reverseEdge) {
        this.reverseEdge = reverseEdge;
    }
    
    public double getResidualCapacity() {
        return capacity - flow;
    }
    
    public void addFlow(double additionalFlow) {
        flow += additionalFlow;
        reverseEdge.flow -= additionalFlow;
    }
    
    @Override
    public String toString() {
        return String.format("Edge(%d->%d, cap: %.2f, flow: %.2f, cost: %.2f)", 
            source, dest, capacity, flow, cost);
    }
}
