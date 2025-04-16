package cashflowoptimizer;

import java.util.*;

public class MinCostMaxFlow {
    private Graph graph;
    private double totalCost;
    private double totalFlow;
    private List<TransactionRecord> transactions;

    public MinCostMaxFlow(Graph graph) {
        this.graph = graph;
        this.totalCost = 0;
        this.totalFlow = 0;
        this.transactions = new ArrayList<>();
    }

    public void computeMinCostMaxFlow(String sourceId, String sinkId) {
        int source = graph.getNodeIndex(sourceId);
        int sink = graph.getNodeIndex(sinkId);

        if (source == -1 || sink == -1) {
            System.out.println("Source or sink node not found.");
            return;
        }

        totalCost = 0;
        totalFlow = 0;
        transactions.clear();

        // Successive Shortest Path Algorithm
        while (true) {
            Dijkstra dijkstra = new Dijkstra(graph, source);
            List<Edge> path = dijkstra.getShortestPathTo(sink);

            if (path.isEmpty()) {
                break; // No more augmenting paths
            }

            // Find minimum residual capacity
            double minResidualCapacity = Double.MAX_VALUE;
            for (Edge edge : path) {
                minResidualCapacity = Math.min(minResidualCapacity, edge.getResidualCapacity());
            }

            if (minResidualCapacity <= 1e-6) {
                break; // No more capacity
            }

            // Check source node balance before flow
            String topSourceNodeId = graph.getNodeId(path.get(0).getSource());
            double availableFunds = graph.getBalance(topSourceNodeId);

            if (minResidualCapacity > availableFunds) {
                minResidualCapacity = availableFunds;
            }

            if (minResidualCapacity <= 0) {
                break;
            }

            // Augment flow along the path
            for (Edge edge : path) {
                edge.addFlow(minResidualCapacity);

                String srcId = graph.getNodeId(edge.getSource());
                String dstId = graph.getNodeId(edge.getDest());

                // Update node balances
                graph.setBalance(srcId, -minResidualCapacity);
                graph.setBalance(dstId, minResidualCapacity);

                totalCost += edge.getCost() * minResidualCapacity;

                // Log transaction
                transactions.add(new TransactionRecord(srcId, dstId, minResidualCapacity, edge.getCost()));
            }

            totalFlow += minResidualCapacity;
        }
    }

    public double getTotalCost() {
        return totalCost;
    }

    public double getTotalFlow() {
        return totalFlow;
    }

    public List<TransactionRecord> getTransactions() {
        return transactions;
    }

    // Inner Dijkstra class
    private class Dijkstra {
        private Graph graph;
        private double[] distance;
        private int[] previous;
        private Edge[] previousEdge;
        private boolean[] visited;
        private int source;

        public Dijkstra(Graph graph, int source) {
            this.graph = graph;
            this.source = source;
            int n = graph.getNodeCount();

            distance = new double[n];
            previous = new int[n];
            previousEdge = new Edge[n];
            visited = new boolean[n];

            Arrays.fill(distance, Double.MAX_VALUE);
            Arrays.fill(previous, -1);

            distance[source] = 0;

            PriorityQueue<Integer> queue = new PriorityQueue<>(Comparator.comparingDouble(node -> distance[node]));
            queue.offer(source);

            while (!queue.isEmpty()) {
                int u = queue.poll();

                if (visited[u]) continue;
                visited[u] = true;

                for (Edge edge : graph.getAdjacentEdges(u)) {
                    if (edge.getResidualCapacity() <= 0) continue;

                    int v = edge.getDest();
                    double newDist = distance[u] + edge.getCost();

                    if (newDist < distance[v]) {
                        distance[v] = newDist;
                        previous[v] = u;
                        previousEdge[v] = edge;
                        queue.offer(v);
                    }
                }
            }
        }

        public List<Edge> getShortestPathTo(int target) {
            List<Edge> path = new ArrayList<>();
            if (distance[target] == Double.MAX_VALUE) return path;

            int current = target;
            while (current != source) {
                Edge edge = previousEdge[current];
                path.add(0, edge);
                current = previous[current];
            }
            return path;
        }
    }
}
