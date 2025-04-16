package cashflowoptimizer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class CashFlowOptimizerGUI extends JFrame {
    private CashFlowOptimizer optimizer;
    private JPanel contentPane;
    private JTabbedPane tabbedPane;
    private JTable nodeTable;
    private JTable transactionTable;
    private JTable resultsTable;
    private DefaultTableModel nodeTableModel;
    private DefaultTableModel transactionTableModel;
    private DefaultTableModel resultsTableModel;
    private List<String> nodes = new ArrayList<>();
    private JTextField iterationsField;
    private JTextArea logArea;
    private JComboBox<String> sourceNodeCombo;  // Made this a class field
    private JComboBox<String> destNodeCombo;    // Made this a class field

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    CashFlowOptimizerGUI frame = new CashFlowOptimizerGUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public CashFlowOptimizerGUI() {
        optimizer = new CashFlowOptimizer();
        
        setTitle("Cash Flow Optimizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 700);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        
        // Add tab change listener to update dropdowns when switching to transactions tab
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) { // Transactions tab
                updateNodeDropdowns();
            }
        });
        
        contentPane.add(tabbedPane, BorderLayout.CENTER);
        
        // Create tabs
        createNodesTab();
        createTransactionsTab();
        createOptimizationTab();
        createResultsTab();
        createLogTab();
    }
    
    private void createNodesTab() {
        JPanel nodesPanel = new JPanel();
        nodesPanel.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Financial Nodes", null, nodesPanel, null);
        
        // Create table to display nodes
        nodeTableModel = new DefaultTableModel(
            new Object[][] {},
            new String[] {"Node ID", "Type", "Balance"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        nodeTable = new JTable(nodeTableModel);
        JScrollPane scrollPane = new JScrollPane(nodeTable);
        nodesPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create form panel for adding nodes
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        formPanel.add(new JLabel("Node ID:"));
        JTextField nodeId = new JTextField();
        formPanel.add(nodeId);
        
        formPanel.add(new JLabel("Node Type:"));
        String[] nodeTypes = {"ACCOUNT", "REVENUE", "EXPENSE"};
        JComboBox<String> nodeType = new JComboBox<>(nodeTypes);
        formPanel.add(nodeType);
        
        formPanel.add(new JLabel("Initial Balance:"));
        JTextField balance = new JTextField();
        formPanel.add(balance);
        
        JButton addNodeButton = new JButton("Add Node");
        addNodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (nodeId.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Node ID cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    String id = nodeId.getText().trim();
                    NodeType type = NodeType.valueOf(nodeType.getSelectedItem().toString());
                    double initialBalance = Double.parseDouble(balance.getText().trim());
                    
                    optimizer.addNode(id, type, initialBalance);
                    nodes.add(id);
                    
                    nodeTableModel.addRow(new Object[]{id, type, initialBalance});
                    
                    // Clear form
                    nodeId.setText("");
                    balance.setText("");
                    
                    // Update dropdowns whenever a new node is added
                    updateNodeDropdowns();
                    
                    log("Added node: " + id + " (" + type + ") with balance $" + initialBalance);
                    JOptionPane.showMessageDialog(null, "Node added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid balance value", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addNodeButton);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        nodesPanel.add(southPanel, BorderLayout.SOUTH);
    }
    
    private void createTransactionsTab() {
        JPanel transactionsPanel = new JPanel();
        transactionsPanel.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Cash Flow Transactions", null, transactionsPanel, null);
        
        // Create table to display transactions
        transactionTableModel = new DefaultTableModel(
            new Object[][] {},
            new String[] {"Source", "Destination", "Capacity", "Cost"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(transactionTableModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        transactionsPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create form panel for adding transactions
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        formPanel.add(new JLabel("Source Node:"));
        sourceNodeCombo = new JComboBox<>();  // Initialize as class field
        formPanel.add(sourceNodeCombo);
        
        formPanel.add(new JLabel("Destination Node:"));
        destNodeCombo = new JComboBox<>();    // Initialize as class field
        formPanel.add(destNodeCombo);
        
        formPanel.add(new JLabel("Transaction Capacity:"));
        JTextField capacity = new JTextField();
        formPanel.add(capacity);
        
        formPanel.add(new JLabel("Transaction Cost:"));
        JTextField cost = new JTextField();
        formPanel.add(cost);
        
        // Initialize dropdowns with any existing nodes
        updateNodeDropdowns();
        
        JButton addTransactionButton = new JButton("Add Transaction");
        addTransactionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (sourceNodeCombo.getItemCount() == 0 || destNodeCombo.getItemCount() == 0) {
                    JOptionPane.showMessageDialog(null, "Please add nodes before creating transactions", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    String source = sourceNodeCombo.getSelectedItem().toString();
                    String dest = destNodeCombo.getSelectedItem().toString();
                    double transCapacity = Double.parseDouble(capacity.getText().trim());
                    double transCost = Double.parseDouble(cost.getText().trim());
                    
                    boolean success = optimizer.addEdge(source, dest, transCapacity, transCost);
                    
                    if (success) {
                        transactionTableModel.addRow(new Object[]{source, dest, transCapacity, transCost});
                        
                        // Clear form
                        capacity.setText("");
                        cost.setText("");
                        
                        log("Added transaction: " + source + " -> " + dest + 
                            " (Capacity: $" + transCapacity + ", Cost: $" + transCost + ")");
                        JOptionPane.showMessageDialog(null, "Transaction added successfully", 
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to add transaction. Make sure both nodes exist.", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid numeric value", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addTransactionButton);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        transactionsPanel.add(southPanel, BorderLayout.SOUTH);
    }
    
    // New method to update the node dropdowns
    private void updateNodeDropdowns() {
        if (sourceNodeCombo != null && destNodeCombo != null) {
            // Remember selected items if any
            String selectedSource = sourceNodeCombo.getItemCount() > 0 ? 
                sourceNodeCombo.getSelectedItem().toString() : null;
            String selectedDest = destNodeCombo.getItemCount() > 0 ? 
                destNodeCombo.getSelectedItem().toString() : null;
            
            // Clear existing items
            sourceNodeCombo.removeAllItems();
            destNodeCombo.removeAllItems();
            
            // Add all current nodes
            for (String node : nodes) {
                sourceNodeCombo.addItem(node);
                destNodeCombo.addItem(node);
            }
            
            // Restore selection if possible
            if (selectedSource != null && nodes.contains(selectedSource)) {
                sourceNodeCombo.setSelectedItem(selectedSource);
            }
            if (selectedDest != null && nodes.contains(selectedDest)) {
                destNodeCombo.setSelectedItem(selectedDest);
            }
        }
    }
    
    private void createOptimizationTab() {
        JPanel optimizationPanel = new JPanel();
        optimizationPanel.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Optimization", null, optimizationPanel, null);
        
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayout(0, 2, 10, 10));
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Optimization Settings"));
        
        settingsPanel.add(new JLabel("Number of Iterations:"));
        iterationsField = new JTextField("10");
        settingsPanel.add(iterationsField);
        
        JButton runButton = new JButton("Run Optimization");
        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int iterations = Integer.parseInt(iterationsField.getText().trim());
                    
                    if (iterations <= 0) {
                        JOptionPane.showMessageDialog(null, "Number of iterations must be positive", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Run optimization in a separate thread to avoid UI freezing
                    new Thread(new Runnable() {
                        public void run() {
                            final JButton button = runButton;
                            SwingUtilities.invokeLater(() -> button.setEnabled(false));
                            log("Starting optimization with " + iterations + " iterations...");
                            
                            // Create a custom output stream to redirect System.out to our log
                            PrintStream originalOut = System.out;
                            PrintStream customOut = new PrintStream(new OutputStream() {
                                private StringBuilder sb = new StringBuilder();
                                
                                @Override
                                public void write(int b) {
                                    if (b == '\n') {
                                        final String text = sb.toString();
                                        SwingUtilities.invokeLater(new Runnable() {
                                            public void run() {
                                                log(text);
                                            }
                                        });
                                        sb = new StringBuilder();
                                    } else {
                                        sb.append((char) b);
                                    }
                                }
                            });
                            
                            System.setOut(customOut);
                            
                            // Run the optimization
                            optimizer.optimize(iterations);
                            
                            // Restore original output
                            System.setOut(originalOut);
                            
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    button.setEnabled(true);
                                    log("Optimization completed!");
                                    updateResultsTable();
                                    tabbedPane.setSelectedIndex(3); // Switch to Results tab
                                    JOptionPane.showMessageDialog(null, "Optimization completed successfully", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                                }
                            });
                        }
                    }).start();
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid number of iterations", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(settingsPanel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(runButton);
        
        optimizationPanel.add(centerPanel, BorderLayout.CENTER);
        optimizationPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void createResultsTab() {
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Results", null, resultsPanel, null);
        
        // Create table to display results
        resultsTableModel = new DefaultTableModel(
            new Object[][] {},
            new String[] {"From", "To", "Amount", "Cost", "Total Cost"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable = new JTable(resultsTableModel);
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        
        JButton applyButton = new JButton("Apply Transactions and Update Balances");
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // The optimizer already saves optimization data by default
                // Refresh the node table to show updated balances
                updateNodeBalances();
                tabbedPane.setSelectedIndex(0); // Switch to Nodes tab
                JOptionPane.showMessageDialog(null, "Transactions applied and balances updated", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(applyButton);
        
        resultsPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void createLogTab() {
        JPanel logPanel = new JPanel();
        logPanel.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab("Log", null, logPanel, null);
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        JButton clearButton = new JButton("Clear Log");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logArea.setText("");
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(clearButton);
        
        logPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void log(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                logArea.append(message + "\n");
                // Scroll to the bottom
                logArea.setCaretPosition(logArea.getDocument().getLength());
            }
        });
    }
    
    private void updateResultsTable() {
        resultsTableModel.setRowCount(0); // Clear previous results
        
        // Get the optimizer to display results to our console/log
        optimizer.displayResults();
        
        // Get the updated best transactions from the optimizer and display them in the table
        for (TransactionRecord tr : optimizer.getBestTransactions()) {
            resultsTableModel.addRow(new Object[]{
                tr.getSourceNode(),
                tr.getDestNode(),
                tr.getAmount(),
                tr.getCost(),
                tr.getAmount() * tr.getCost()
            });
        }
    }
    
    private void updateNodeBalances() {
        nodeTableModel.setRowCount(0); // Clear the table
        
        // Re-populate with updated balances
        for (String nodeId : nodes) {
            nodeTableModel.addRow(new Object[]{
                nodeId,
                optimizer.getNodeType(nodeId),
                optimizer.getNodeBalance(nodeId)
            });
        }
    }
}