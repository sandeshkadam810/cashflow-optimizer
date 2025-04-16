// Action.java
package cashflowoptimizer;

public class Action {
    private String sourceNodeId;
    private String sinkNodeId;
    
    public Action(String sourceNodeId, String sinkNodeId) {
        this.sourceNodeId = sourceNodeId;
        this.sinkNodeId = sinkNodeId;
    }
    
    public String getSourceNodeId() {
        return sourceNodeId;
    }
    
    public String getSinkNodeId() {
        return sinkNodeId;
    }
    
    @Override
    public String toString() {
        return sourceNodeId + "->" + sinkNodeId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Action other = (Action) obj;
        return sourceNodeId.equals(other.sourceNodeId) && 
               sinkNodeId.equals(other.sinkNodeId);
    }
    
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
