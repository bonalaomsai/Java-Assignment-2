import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

class Recipe {
    private String name;
    private String ingredients;
    private String instructions;

    public Recipe(String name, String ingredients, String instructions) {
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public String getName() {
        return name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }
}

public class RecipeBookApp1 extends JFrame {
    private Connection connection;
    private Statement statement;
    private DefaultTableModel tableModel;

    public RecipeBookApp1() {
        setTitle("Recipe Book");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Create a JTable to display the recipes
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Name");
        tableModel.addColumn("Ingredients");
        tableModel.addColumn("Instructions");
        JTable recipeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(recipeTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton addButton = new JButton("Add Recipe");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // showAddRecipeDialog();
                // Implement the add recipe dialog
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        connectToDatabase();
        createTableIfNotExists();
        loadRecipesFromDatabase();

        // Insert sample recipes
        Recipe recipe1 = new Recipe("Pasta", "Pasta, Sauce, Cheese", "Cook pasta, add sauce, sprinkle cheese.");
        Recipe recipe2 = new Recipe("Salad", "Lettuce, Tomatoes, Cucumbers", "Chop veggies, mix, add dressing.");
        insertRecipeIntoDatabase(recipe1);
        insertRecipeIntoDatabase(recipe2);

        updateRecipeTable(); // Update the table with loaded recipes
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db", "sai", "omsaibonala");
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS recipes (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "ingredients TEXT NOT NULL," +
                    "instructions TEXT NOT NULL)";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadRecipesFromDatabase() {
        try {
            String sql = "SELECT * FROM recipes";
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String ingredients = resultSet.getString("ingredients");
                String instructions = resultSet.getString("instructions");
                Recipe recipe = new Recipe(name, ingredients, instructions);
                // recipes.add(recipe);
                // updateRecipeTextArea();
                tableModel.addRow(new Object[] { name, ingredients, instructions });
            }
            // updateRecipeTextArea();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertRecipeIntoDatabase(Recipe recipe) {
        try {
            String sql = "INSERT INTO recipes (name, ingredients, instructions) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, recipe.getName());
            preparedStatement.setString(2, recipe.getIngredients());
            preparedStatement.setString(3, recipe.getInstructions());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateRecipeTable() {
        // Clear existing data in the table model
        tableModel.setRowCount(0);
        loadRecipesFromDatabase();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RecipeBookApp1().setVisible(true);
            }
        });
    }
}
