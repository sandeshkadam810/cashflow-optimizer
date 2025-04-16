package cashflowoptimizer;

import java.util.*;

public class RLAgent {
    private Map<String, Double> qValues;  // State-action Q-values
    private double learningRate;
    private double discountFactor;
    private double explorationRate;
    private Random random;

    private boolean verbose = false; // Toggle for logging

    public RLAgent(double learningRate, double discountFactor, double explorationRate) {
        this.qValues = new HashMap<>();
        this.learningRate = learningRate;
        this.discountFactor = discountFactor;
        this.explorationRate = explorationRate;
        this.random = new Random();
    }

    // Epsilon-greedy strategy
    public Action selectAction(State state, List<Action> possibleActions) {
        if (possibleActions.isEmpty()) return null;

        if (random.nextDouble() < explorationRate) {
            Action chosen = possibleActions.get(random.nextInt(possibleActions.size()));
            if (verbose) System.out.println("[Explore] Chose random action: " + chosen);
            return chosen;
        }

        double bestValue = Double.NEGATIVE_INFINITY;
        List<Action> bestActions = new ArrayList<>();

        for (Action action : possibleActions) {
            String key = getStateActionKey(state, action);
            double value = qValues.getOrDefault(key, 0.0);

            if (value > bestValue) {
                bestValue = value;
                bestActions.clear();
                bestActions.add(action);
            } else if (value == bestValue) {
                bestActions.add(action); // Tie-breaking
            }
        }

        Action chosen = bestActions.isEmpty() ? possibleActions.get(0) :
                        bestActions.get(random.nextInt(bestActions.size()));

        if (verbose) System.out.printf("[Exploit] Chose best action: %s (Q=%.2f)\n", chosen, bestValue);
        return chosen;
    }
    

    // Q-learning update
    public void updateQValue(State state, Action action, double reward, State nextState, List<Action> nextActions) {
        String key = getStateActionKey(state, action);
        double currentQ = qValues.getOrDefault(key, 0.0);

        double maxNextQ = 0.0;
        for (Action nextAction : nextActions) {
            String nextKey = getStateActionKey(nextState, nextAction);
            maxNextQ = Math.max(maxNextQ, qValues.getOrDefault(nextKey, 0.0));
        }

        double updatedQ = currentQ + learningRate * (reward + discountFactor * maxNextQ - currentQ);
        qValues.put(key, updatedQ);

        if (verbose) {
            System.out.printf("Q-Update: [%s] %.4f -> %.4f (reward=%.2f, maxNextQ=%.2f)\n",
                              key, currentQ, updatedQ, reward, maxNextQ);
        }
    }

    public void decreaseExplorationRate(double factor) {
        explorationRate *= factor;
        if (explorationRate < 0.01) {
            explorationRate = 0.01;
        }
    }

    public void setExplorationRate(double rate) {
        this.explorationRate = rate;
    }

    public double getExplorationRate() {
        return explorationRate;
    }

    public boolean hasImproved() {
        return !qValues.isEmpty();
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    private String getStateActionKey(State state, Action action) {
        return state.toString() + "|" + action.toString();
    }

    public void save(String path) {
        System.out.println("Saving Q-values to: " + path + " [Not implemented]");
    }

    public void load(String path) {
        System.out.println("Loading Q-values from: " + path + " [Not implemented]");
    }
}
