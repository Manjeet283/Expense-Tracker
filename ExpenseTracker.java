import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    double amount;
    String category;
    String type; // "Income" or "Expense"
    LocalDate date;

    public Transaction(double amount, String category, String type, LocalDate date) {
        this.amount = amount;
        this.category = category;
        this.type = type;
        this.date = date;
    }

    public String toString() {
        return String.format("[%s] %s - %s: ₹%.2f", date, type, category, amount);
    }

    public int getMonth() {
        return date.getMonthValue();
    }

    public int getYear() {
        return date.getYear();
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }
}

public class ExpenseTracker {
    private static final String DATA_FILE = "transactions.ser";
    private static ArrayList<Transaction> transactions = new ArrayList<>();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadData();
        while (true) {
            System.out.println("\n=== Expense Tracker ===");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. View Monthly Summary");
            System.out.println("4. View All Transactions");
            System.out.println("5. Save and Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1 -> addTransaction("Income");
                case 2 -> addTransaction("Expense");
                case 3 -> viewMonthlySummary();
                case 4 -> viewAllTransactions();
                case 5 -> {
                    saveData();
                    System.out.println("Data saved. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void addTransaction(String type) {
        System.out.print("Enter amount: ₹");
        double amount = sc.nextDouble();
        sc.nextLine(); // consume newline

        System.out.print("Enter category (e.g., Food, Rent, Travel): ");
        String category = sc.nextLine();

        System.out.print("Enter date (YYYY-MM-DD): ");
        String dateStr = sc.nextLine();
        LocalDate date = LocalDate.parse(dateStr);

        transactions.add(new Transaction(amount, category, type, date));
        System.out.println(type + " added successfully.");
    }

    private static void viewAllTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        System.out.println("\n--- All Transactions ---");
        for (Transaction t : transactions) {
            System.out.println(t);
        }
    }

    private static void viewMonthlySummary() {
        System.out.print("Enter year (e.g., 2025): ");
        int year = sc.nextInt();
        System.out.print("Enter month (1-12): ");
        int month = sc.nextInt();

        double income = 0;
        double expenses = 0;
        Map<String, Double> categoryTotals = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.getYear() == year && t.getMonth() == month) {
                if (t.getType().equals("Income")) {
                    income += t.getAmount();
                } else {
                    expenses += t.getAmount();
                    categoryTotals.put(t.getCategory(),
                        categoryTotals.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
                }
            }
        }

        System.out.println("\n--- Monthly Summary for " + year + "-" + String.format("%02d", month) + " ---");
        System.out.printf("Total Income: ₹%.2f\n", income);
        System.out.printf("Total Expenses: ₹%.2f\n", expenses);
        System.out.printf("Net Savings: ₹%.2f\n", income - expenses);
        System.out.println("\nExpenses by Category:");
        for (String cat : categoryTotals.keySet()) {
            System.out.printf("%s: ₹%.2f\n", cat, categoryTotals.get(cat));
        }
    }

    private static void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(transactions);
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    private static void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            transactions = (ArrayList<Transaction>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
}
