/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aplikasisaranaprasarana;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

/**
 *
 * @author user
 */
public class DatabaseInitializer {
    public static void inisialisasiTabel() {
        // Array yang berisi semua perintah CREATE TABLE
        String[] createTableStatements = {
            "CREATE TABLE IF NOT EXISTS user (" +
            "id_user INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nama TEXT NOT NULL," +
            "username TEXT NOT NULL UNIQUE," +
            "password TEXT NOT NULL," +
            "role TEXT NOT NULL CHECK(role IN ('admin', 'petugas', 'user'))" +
            ");",

            "CREATE TABLE IF NOT EXISTS aset (" +
            "id_aset INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nama_aset TEXT NOT NULL," +
            "kategori TEXT," +
            "kondisi TEXT NOT NULL CHECK(kondisi IN ('baik', 'rusak', 'perawatan'))," +
            "lokasi TEXT," +
            "tanggal_beli TEXT," +
            "hari_pemeliharaan TEXT," +
            "kode_inventaris TEXT," +
            "status TEXT NOT NULL CHECK(status IN ('tersedia', 'tidak tersedia'))" +
            ");",

            "CREATE TABLE IF NOT EXISTS peminjaman (" +
            "id_peminjaman INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_user INTEGER," +
            "id_aset INTEGER," +
            "tanggal_pinjam TEXT," +
            "tanggal_kembali TEXT," +
            "status TEXT NOT NULL CHECK(status IN ('menunggu', 'disetujui', 'ditolak', 'selesai'))," +
            "FOREIGN KEY (id_user) REFERENCES user(id_user)," +
            "FOREIGN KEY (id_aset) REFERENCES aset(id_aset)" +
            ");",

            "CREATE TABLE IF NOT EXISTS kerusakan (" +
            "id_laporan INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_user INTEGER," +
            "id_aset INTEGER," +
            "deskripsi TEXT," +
            "tanggal_lapor TEXT," +
            "status TEXT NOT NULL CHECK(status IN ('terlapor', 'ditangani', 'selesai'))," +
            "FOREIGN KEY (id_user) REFERENCES user(id_user)," +
            "FOREIGN KEY (id_aset) REFERENCES aset(id_aset)" +
            ");"
        };
        
        // Dapatkan koneksi di luar blok try-with-resources
        try {
            Connection conn = ConnectSQL.getKoneksi();
            
            // Gunakan try-with-resources HANYA untuk Statement, bukan untuk Connection
            try (Statement stmt = conn.createStatement()) {
                System.out.println("Memeriksa dan membuat tabel jika belum ada...");
                for (String sql : createTableStatements) {
                    stmt.execute(sql);
                }
                System.out.println("Semua tabel berhasil disiapkan.");
            }
            // Setelah blok ini selesai, HANYA 'stmt' yang ditutup. 'conn' tetap terbuka.

        } catch (SQLException e) {
            System.out.println("Error saat inisialisasi database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
