// package cashflowoptimizer;

// import java.util.Scanner;

// public class Main {
//     private static CashFlowOptimizer optimizer;
//     private static Scanner scanner;

//     public static void main(String[] args) {
//         System.out.println("Welcome to Cash Flow Optimizer CLI");
        
//         scanner = new Scanner(System.in);
//         optimizer = new CashFlowOptimizer();
//         boolean running = true;
//         while (running) {
//             printMenu();
//             int choice = getChoice();
            
//             switch (choice) {
//                 case 1:
//                     addFinancialNode();
//                     break;
//                 case 2:
//                     addCashFlowTransaction();
//                     break;
//                 case 3:
//                     runOptimization();
//                     break;
//                 case 4:
//                     showResults();
//                     break;
//                 case 5:
//                     running = false;
//                     System.out.println("Exiting Cash Flow Optimizer. Goodbye!");
//                     break;
//                 default:
//                     System.out.println("Invalid choice. Please try again.");
//             }
//         }
        
//         scanner.close();
//     }
    
//     private static void printMenu() {
//         System.out.println("\n1. Add financial node (bank account, revenue, expense)");
//         System.out.println("2. Add cash flow transaction (define cost & limit)");
//         System.out.println("3. Run optimization");
//         System.out.println("4. Show results");
//         System.out.println("5. Exit");
//         System.out.print("Enter your choice: ");
//     }
    
//     private static int getChoice() {
//         try {
//             return Integer.parseInt(scanner.nextLine());
//         } catch (NumberFormatException e) {
//             return -1;
//         }
//     }
    
//     private static void addFinancialNode() {
//         System.out.println("\n--- Add Financial Node ---");
//         System.out.print("Enter node ID: ");
//         String nodeId = scanner.nextLine();
        
//         System.out.print("Enter node type (ACCOUNT, REVENUE, EXPENSE): ");
//         String nodeTypeStr = scanner.nextLine();
//         NodeType nodeType;
//         try {
//             nodeType = NodeType.valueOf(nodeTypeStr.toUpperCase());
//         } catch (IllegalArgumentException e) {
//             System.out.println("Invalid node type. Using default: ACCOUNT");
//             nodeType = NodeType.ACCOUNT;
//         }
        
//         System.out.print("Enter initial balance: ");
//         double balance = 0;
//         try {
//             balance = Double.parseDouble(scanner.nextLine());
//         } catch (NumberFormatException e) {
//             System.out.println("Invalid balance. Using default: 0");
//         }
        
//         optimizer.addNode(nodeId, nodeType, balance);
//         System.out.println("Node added successfully!");
//     }
    
//     private static void addCashFlowTransaction() {
//         System.out.println("\n--- Add Cash Flow Transaction ---");
//         System.out.print("Enter source node ID: ");
//         String sourceId = scanner.nextLine();
        
//         System.out.print("Enter destination node ID: ");
//         String destId = scanner.nextLine();
        
//         System.out.print("Enter transaction capacity: ");
//         double capacity = 0;
//         try {
//             capacity = Double.parseDouble(scanner.nextLine());
//         } catch (NumberFormatException e) {
//             System.out.println("Invalid capacity. Using default: 0");
//         }
        
//         System.out.print("Enter transaction cost: ");
//         double cost = 0;
//         try {
//             cost = Double.parseDouble(scanner.nextLine());
//         } catch (NumberFormatException e) {
//             System.out.println("Invalid cost. Using default: 0");
//         }
        
//         boolean success = optimizer.addEdge(sourceId, destId, capacity, cost);
//         if (success) {
//             System.out.println("Transaction added successfully!");
//         } else {
//             System.out.println("Failed to add transaction. Please check if both nodes exist.");
//         }
//     }
    
//     private static void runOptimization() {
//         System.out.println("\n--- Run Optimization ---");
//         optimizer.optimize(1);
//         System.out.println("Optimization complete!");
//     }
    
//     private static void showResults() {
//         System.out.println("\n--- Optimization Results ---");
//         optimizer.displayResults();
//     }
// }


package cashflowoptimizer;

public class Main {
    public static void main(String[] args) {
        // Launch the GUI application
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CashFlowOptimizerGUI gui = new CashFlowOptimizerGUI();
                gui.setVisible(true);
            }
        });
    }
}