/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aplikasisaranaprasarana;

/**
 *
 * @author N.A.I
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConnectSQL {
    private static Connection koneksi;

    /**
     * Metode untuk mendapatkan koneksi ke database.
     * Jika koneksi belum ada atau sudah ditutup, metode ini akan membuat koneksi baru.
     * @return Objek Connection yang siap digunakan.
     */
    public static Connection getKoneksi() {
        // Periksa apakah koneksi masih null atau sudah tertutup
        if (koneksi == null) {
            try {
                // URL untuk koneksi JDBC ke MySQL.
                String url = "jdbc:mysql://localhost:3306/sarpras?allowPublicKeyRetrieval=true&useSSL=false";
                String user = "root"; // User default XAMPP
                String password = ""; // Password default XAMPP (biasanya kosong)

                // Mendaftarkan driver MySQL
                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());

                // Membuat koneksi
                koneksi = DriverManager.getConnection(url, user, password);
                System.out.println("Koneksi ke database berhasil!");

            } catch (SQLException e) {
                // Menangani error jika koneksi gagal
                JOptionPane.showMessageDialog(null, "Gagal terkoneksi ke database: " + e.getMessage(), "Error Koneksi", JOptionPane.ERROR_MESSAGE);
                System.exit(0); // Keluar dari aplikasi jika database tidak bisa diakses
            }
        }
        return koneksi;
    }
}
