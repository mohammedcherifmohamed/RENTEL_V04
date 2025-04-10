package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:rental.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    
    
    public static void AddUser(String name, String contactDetails, String email, String password) {
        String sql = "INSERT INTO Agents(name, contact_details, email, password) VALUES(?, ?, ?, ?)";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, contactDetails);
            pstmt.setString(3, email);
            pstmt.setString(4, password);

            pstmt.executeUpdate();
            System.out.println("Agent added successfully!");

        } catch (SQLException e) {
            System.out.println("Error inserting agent: " + e.getMessage());
        }
    }

    public static void AddCar(String registration_number, String brand, String model, String category, double price, String availability_status, String path) {
        String sql = "INSERT INTO Vehicles (registration_number, brand, model, category, imagepath, price, availability_status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, registration_number);
            pstmt.setString(2, brand);
            pstmt.setString(3, model);
            pstmt.setString(4, category);
            pstmt.setString(5, (path != null && !path.trim().isEmpty()) ? path : null);  // Insert NULL if no path
            pstmt.setDouble(6, price);
            pstmt.setString(7, availability_status);

            pstmt.executeUpdate();
            System.out.println("Car added successfully: " + brand + " " + model + " (" + registration_number + ")");
            
        } catch (SQLException e) {
            System.out.println("Error inserting vehicle: " + e.getMessage());
        }
    }
    
    public static boolean validateAgent(String name, String password) {
        String sql = "SELECT * FROM Agents WHERE name = ? AND password = ?";
        
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return true; // User found
            }
        } catch (SQLException e) {
            System.out.println("Error validating agent: " + e.getMessage());
        }
        return false; // No user found
    }

    public static boolean isExists(String name, String email) {
        String sql = "SELECT 1 FROM Agents WHERE name = ? AND email = ? LIMIT 1";
        
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); 
            }
        } catch (SQLException e) {
            System.err.println("Error validating agent: " + e.getMessage());
            return false; 
        }
    }



    public static void createTables() {

        String[] sqlStatements = {
            "CREATE TABLE IF NOT EXISTS Vehicles (" +
            " vehicle_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " registration_number TEXT UNIQUE," +
            " brand TEXT NOT NULL," +
            " model TEXT NOT NULL," +
            " category TEXT NOT NULL," +
            " imagepath TEXT NOT NULL," +
            " price DOUBLE NOT NULL," +
            " availability_status TEXT CHECK(availability_status IN ('Available', 'Rented')) NOT NULL);",

            "CREATE TABLE IF NOT EXISTS Clients (" +
            " client_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " name TEXT NOT NULL," +
            " contact_details TEXT NOT NULL," +
            " rental_history TEXT);",

            "CREATE TABLE IF NOT EXISTS Agents (" +
            " agent_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " name TEXT NOT NULL," +
            " contact_details TEXT NOT NULL," +
            " email TEXT UNIQUE NOT NULL," +
            " password TEXT NOT NULL);",

            "CREATE TABLE IF NOT EXISTS Rentals (" +
            " rental_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " vehicle_id INTEGER NOT NULL," +
            " client_id INTEGER NOT NULL," +
            " agent_id INTEGER NOT NULL," +
            " start_date TEXT NOT NULL," +
            " end_date TEXT NOT NULL," +
            " total_cost REAL NOT NULL," +
            " FOREIGN KEY (vehicle_id) REFERENCES Vehicles(vehicle_id)," +
            " FOREIGN KEY (client_id) REFERENCES Clients(client_id)," +
            " FOREIGN KEY (agent_id) REFERENCES Agents(agent_id));",

            "CREATE TABLE IF NOT EXISTS Payments (" +
            " payment_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " rental_id INTEGER NOT NULL," +
            " amount REAL NOT NULL," +
            " payment_date TEXT NOT NULL," +
            " FOREIGN KEY (rental_id) REFERENCES Rentals(rental_id));",

            "CREATE TABLE IF NOT EXISTS Reports (" +
            " report_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " generated_date TEXT NOT NULL," +
            " report_data TEXT NOT NULL);"
        };

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            for (String sql : sqlStatements) {
                stmt.execute(sql);
            }
            System.out.println("All tables have been created or already exist.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        // String sql = "SELECT * FROM Vehicles";

        // try (Connection conn = connect();
        //     Statement stmt = conn.createStatement();
        //     ResultSet rs = stmt.executeQuery(sql)) {

        //     while (rs.next()) {
        //        System.out.println("Vehicle ID: " + rs.getInt("vehicle_id"));
        //        System.out.println("Registration Number: " + rs.getString("registration_number"));
        //        System.out.println("Brand: " + rs.getString("brand"));
        //        System.out.println("Model: " + rs.getString("model"));
        //        System.out.println("Category: " + rs.getString("category"));
        //        System.out.println("Image Path: " + rs.getString("imagepath"));
        //        System.out.println("Price: " + rs.getDouble("price"));
        //        System.out.println("Availability Status: " + rs.getString("availability_status"));
        //        System.out.println("-------------------------------");
        //     }

        // } catch (SQLException e) {
        //     System.out.println("Error retrieving data: " + e.getMessage());
        // }
        // String addcolumn = "ALTER TABLE Vehicles ADD COLUMN imagepath TEXT";
        // try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
        //     stmt.execute(addcolumn);
        //     System.out.println("Column 'imagepath' added successfully to Vehicles table.");
        // } catch (SQLException e) {
        //     System.out.println("Error adding column: " + e.getMessage());
        // }

        // String deleteData = "DELETE FROM Vehicles";      
        // try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
        //     stmt.execute(deleteData);
        //     System.out.println("All data deleted from Vehicles table.");
        // } catch (SQLException e) {
        //     System.out.println("Error deleting data: " + e.getMessage());
        // }
        
                        
        // createTables();
        // insertAgent("admin", "123-456-7890", "a@example.com", "a");
        // AddCar("9696", "Toyota", "supra", "car" ,999.87,"Available","C:\\Users\\Informatics\\Desktop\\java\\cars\\car1.jpg");
        // AddCar("8888", "BMW", "M30", "bus" ,999.87,"Available","C:\\Users\\Informatics\\Desktop\\java\\cars\\car2.jpg");

        // AddCar("6666", "mercedess", "GLS", "car" ,999.87,"Available");
        // AddCar("1111", "BMW", "M30", "bus" ,999.87,"Available");
        // AddCar("5555", "volswagen", "Van-T02", "car" ,456.87,"Available");
        // AddCar("7777", "decathlon", "VTT", "bike" ,789.87,"Available");
        // V-2025-001
        // B-2025-001
        // B-218-0001
        //  String newImagePath = "C:\\Users\\Informatics\\Desktop\\java\\cars\\car1.jpg_________________";  // Path to the new image
        // String query = "UPDATE Vehicles SET imagepath = ? WHERE registration_number IN (?, ?, ?)";

        // try (Connection conn = DatabaseConnection.connect();
        //     PreparedStatement pstmt = conn.prepareStatement(query)) {

        //     pstmt.setString(1, newImagePath);  
        //     pstmt.setString(2, "V-2025-001");  
        //     pstmt.setString(3, "B-2025-001");  
        //     pstmt.setString(4, "B-218-0001");  

        //     int affectedRows = pstmt.executeUpdate();
        //     if (affectedRows > 0) {
        //         System.out.println("Image path updated for specified registration numbers.");
        //     } else {
        //         System.out.println("No matching records found to update.");
        //     }


        // } catch (SQLException e) {
        //     e.printStackTrace();
        // }
        }
}
