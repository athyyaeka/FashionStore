package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;


public class DBConnection {


    private static final String SERVER   = "localhost";
    private static final String PORT     = "1433";
    private static final String DATABASE = "BD_TugasAkhir";


    private static final String URL =
        "jdbc:sqlserver://" + SERVER + ";portNumber=" + PORT + ";"
        + "databaseName=" + DATABASE + ";"
        + "integratedSecurity=true;"
        + "encrypt=true;"
        + "trustServerCertificate=true;";

    private static Connection connection = null;


    private DBConnection() {}

    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {

                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                

                connection = DriverManager.getConnection(URL);
                System.out.println("[DB] Koneksi Windows Auth ke " + DATABASE + " berhasil.");
            }
        } catch (ClassNotFoundException e) {
            showError("Driver SQL Server tidak ditemukan.\n"
                    + "Pastikan file mssql-jdbc-xx.jar sudah ada di folder /lib\n"
                    + "dan sudah ditambahkan ke Build Path project.\n\n"
                    + "Detail: " + e.getMessage());
        } catch (SQLException e) {
            showError("Gagal terhubung ke database.\n"
                    + "Periksa:\n"
                    + "  1. SQL Server sudah berjalan (Services aktif)\n"
                    + "  2. Nama server & Port sudah benar (" + SERVER + ":" + PORT + ")\n"
                    + "  3. Database '" + DATABASE + "' sudah dibuat di SSMS\n"
                    + "  4. File 'sqljdbc_auth.dll' berada di path yang benar (jika diminta)\n\n"
                    + "Detail: " + e.getMessage());
        }
        return connection;
    }

    
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
                connection = null;
                System.out.println("[DB] Koneksi ditutup.");
            } catch (SQLException e) {
                System.err.println("[DB] Gagal menutup koneksi: " + e.getMessage());
            }
        }
    }

    
    private static void showError(String pesan) {
        System.err.println("[DB ERROR] " + pesan);
        JOptionPane.showMessageDialog(null, pesan, "Error Koneksi Database",
                JOptionPane.ERROR_MESSAGE);
    }
}