// State.java
package cashflowoptimizer;

import java.util.*;

public class State {
    private Map<String, Double> nodeBalances;
    
    public State(Map<String, Double> nodeBalances) {
        this.nodeBalances = new HashMap<>(nodeBalances);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<String> sortedKeys = new ArrayList<>(nodeBalances.keySet());
        Collections.sort(sortedKeys);
        
        for (String key : sortedKeys) {
            sb.append(key).append(":").append(String.format("%.2f", nodeBalances.get(key))).append(";");
        }
        
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        State other = (State) obj;
        return toString().equals(other.toString());
    }
    
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}

