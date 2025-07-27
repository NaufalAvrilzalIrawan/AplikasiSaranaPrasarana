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
                // Mendaftarkan driver SQLite (opsional untuk JDBC 4.0+)
                // Class.forName("org.sqlite.JDBC");

                // URL koneksi untuk SQLite
                // Ini akan membuat (atau membuka) file database bernama 'sarpras.db' di folder utama proyek Anda.
                String url = "jdbc:sqlite:sarpras.db";

                // 3. Membuat koneksi. Tidak perlu username atau password.
                koneksi = DriverManager.getConnection(url);
                System.out.println("Koneksi ke database SQLite berhasil!");

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Gagal terkoneksi ke database SQLite: " + e.getMessage(), "Error Koneksi", JOptionPane.ERROR_MESSAGE);
                System.exit(0); // Keluar dari aplikasi jika database tidak bisa diakses
            }
        }
        return koneksi;
    }
}
