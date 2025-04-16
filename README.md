# Cash Flow Optimizer

A Java command-line application that optimizes cash flow management using Reinforcement Learning (RL) and Min-Cost Max-Flow (MCMF) algorithms.

## Overview

This project implements a financial cash flow optimization system using graph theory. It represents bank accounts, revenue sources, and expense accounts as nodes in a directed graph, with cash flow transactions as edges with associated costs and capacities.

The system combines:
- **Min-Cost Max-Flow Algorithm** to find the optimal cash flow allocation
- **Reinforcement Learning (Q-learning)** to improve the allocation strategy over time

## Features

- Define financial nodes (bank accounts, revenue sources, expenses)
- Define cash flow edges with transaction limits and costs
- Run cash flow optimization using RL and MCMF algorithms
- Track improvements in cost savings over multiple iterations
- Save and view optimization results

## Project Structure

```
/cashflow-optimizer
├── src/
│   ├── cashflowoptimizer/
│       ├── Main.java                  # Entry point for CLI
│       ├── RLAgent.java               # Reinforcement Learning logic
│       ├── Graph.java                 # Financial graph representation
│       ├── Edge.java                  # Graph edge implementation
│       ├── MinCostMaxFlow.java        # Implementation of MCMF algorithm
│       ├── CashFlowOptimizer.java     # Core logic for cash flow management
│       ├── NodeType.java              # Enum for node types
│       ├── State.java                 # State representation for RL
│       ├── Action.java                # Action representation for RL
│       ├── TransactionRecord.java     # Records of cash transfers
├── data/
│   ├── financial_data.json            # Sample financial data
├── README.md                          # Instructions to run the project
```

## How to Build and Run

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Command-line interface

### Building the Project
1. Clone or download the repository
2. Navigate to the project root directory
3. Compile the Java files:

```
mkdir -p out
javac -d out src/cashflowoptimizer/*.java
```

### Running the Application
```
java -cp out cashflowoptimizer.Main
```

## Usage Guide

1. **Add financial nodes**: Define your bank accounts, revenue sources, and expense accounts
2. **Add cash flow transactions**: Define possible cash transfers between accounts with cost and capacity
3. **Run optimization**: Execute the optimization process with a specified number of iterations
4. **Show results**: View the optimized cash allocation and cost savings

### Example Session

```
Welcome to Cash Flow Optimizer CLI

1. Add financial node (bank account, revenue, expense)
2. Add cash flow transaction (define cost & limit)
3. Run optimization
4. Show results
5. Exit
Enter your choice: 1

--- Add Financial Node ---
Enter node ID: RevenueA
Enter node type (ACCOUNT, REVENUE, EXPENSE): REVENUE
Enter initial balance: 10000
Node added successfully!

1. Add financial node (bank account, revenue, expense)
2. Add cash flow transaction (define cost & limit)
3. Run optimization
4. Show results
5. Exit
Enter your choice: 1

--- Add Financial Node ---
Enter node ID: AccountB
Enter node type (ACCOUNT, REVENUE, EXPENSE): ACCOUNT
Enter initial balance: 5000
Node added successfully!

... (add more nodes and transactions) ...

1. Add financial node (bank account, revenue, expense)
2. Add cash flow transaction (define cost & limit)
3. Run optimization
4. Show results
5. Exit
Enter your choice: 3

--- Run Optimization ---
Enter number of iterations: 10
Iteration 1: Total Cost = $500.00, Exploration Rate = 0.29
Iteration 2: Total Cost = $485.00, Exploration Rate = 0.27
...
Optimization complete!

1. Add financial node (bank account, revenue, expense)
2. Add cash flow transaction (define cost & limit)
3. Run optimization
4. Show results
5. Exit
Enter your choice: 4

--- Optimization Results ---
Optimized Cash Flow Allocation:
- Transferred $3000.00 from RevenueA to AccountB (Cost: $15.00)
- Transferred $7000.00 from RevenueA to ExpenseC (Cost: $35.00)
Total Cost: $400.00
Total Cost Savings: $100.00 (20.00%)
RL Policy Updated: Yes

Current Node Balances:
RevenueA (REVENUE): $0.00
AccountB (ACCOUNT): $8000.00
ExpenseC (EXPENSE): $7000.00
```

## Algorithm Details

### Min-Cost Max-Flow
The project implements the Successive Shortest Path Algorithm, which repeatedly finds the shortest path from source to sink and pushes the maximum possible flow along that path.

### Reinforcement Learning
The project uses Q-learning with:
- States: Represent the current balance distribution across nodes
- Actions: Represent potential cash flow transactions
- Rewards: Negative of transaction costs (lower costs = higher rewards)
- Exploration vs. Exploitation: Uses epsilon-greedy policy with decreasing exploration rate

## License
This project is released as open source software.