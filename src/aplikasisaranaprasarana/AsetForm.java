/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package aplikasisaranaprasarana;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener; 
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.util.Date;
/**
 *
 * @author N.A.I
 */
public class AsetForm extends javax.swing.JFrame {

    // Simpan role pengguna untuk referensi
    protected String userRole;
    protected String userName;
    protected int userId;
    
    /**
     * Creates new form DashboardForm
     */
    public AsetForm(int id, String nama, String role) {
        initComponents();
        setLocationRelativeTo(null); // Posisikan di tengah layar

        this.userId = id;
        this.userName = nama;
        this.userRole = role;

        // Panggil metode untuk memuat data ke dalam combo box
        muatFilterKategori();
        muatFilterKondisi();
        muatFilterPelihara();
        // Buat form text field ID tidak dapat di ubah
        txtId.setEditable(false); 
        txtId.setBackground(new java.awt.Color(236, 240, 241));
        // Atur menu berdasarkan role
        aturMenuBerdasarkanRole();
        // Mengatur menu
        setupMenuStyles();
        // Memunculkan jam real time
        startRealTimeClock();
        // Memumnculkan data tabel
        tampilkanDataAset();
        attachFilterListeners();
    }
    
    /**
     * Metode untuk mendeteksi perubahan pada filter
     */
    private void attachFilterListeners() {
        // Buat satu listener yang bisa dipakai bersama untuk efisiensi.
        ActionListener filterListener = e -> tampilkanDataAset();
        
        filterKategori.addActionListener(filterListener);
        filterKondisi.addActionListener(filterListener);
        filterPelihara.addActionListener(filterListener);
    }
    
    /**
     * Metode untuk mengatur visibilitas tombol menu berdasarkan role pengguna.
     */
    private void aturMenuBerdasarkanRole() {
        // Secara default, semua menu untuk admin/petugas terlihat
        // Jika role adalah 'user', sembunyikan menu tertentu
        if (this.userRole.equals("user")) {
            btnAset.setVisible(false);
            btnPeminjaman.setVisible(false);
            btnPemeliharaan.setVisible(false);
            btnLaporan.setVisible(false);
            btnUser.setVisible(false);
        }
        else if (this.userRole.equals("petugas")) {
            btnPinjamAset.setVisible(false);
            btnPinjamFasil.setVisible(false);
            btnLapor.setVisible(false);
            btnUser.setVisible(false);
            
            cmbHari.setEditable(false);
            cmbHari.setEnabled(false);
        }
    }
    
    /**
     * Metode baru untuk mengatur gaya visual menu.
     * Memberi highlight pada tombol Dashboard dan efek hover pada tombol lain.
     */
    private void setupMenuStyles() {
        // Definisikan warna-warna yang akan digunakan
        Color originalColor = new Color(34,45,50);   // Warna asli tombol
        Color hoverColor = new Color(93, 109, 126);    // Warna saat di-hover

        // Buat array dari tombol menu lainnya
        JButton[] menuButtons = {btnDashboard, btnAset, btnPeminjaman, btnPemeliharaan, btnLaporan, btnLapor, btnPinjamAset, btnPinjamFasil, btnUser};

        // Tambahkan MouseListener menggunakan MouseAdapter
        btnLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                // Ubah warna latar belakang saat mouse masuk
                btnLogout.setBackground(new Color(255,102,102));
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                // Kembalikan warna ke semula saat mouse keluar
                btnLogout.setBackground(new Color(255,0,0));
            }
        });
        
        // Terapkan efek hover ke setiap tombol di array
        for (JButton button : menuButtons) {
            // Hanya terapkan efek jika tombol tersebut terlihat (visible)
            if (button.isVisible() && button.getBackground().equals(originalColor)) {
                // Atur warna awal
                button.setBackground(originalColor);
                
                // Tambahkan MouseListener menggunakan MouseAdapter
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent evt) {
                        // Ubah warna latar belakang saat mouse masuk
                        button.setBackground(hoverColor);
                    }

                    @Override
                    public void mouseExited(MouseEvent evt) {
                        // Kembalikan warna ke semula saat mouse keluar
                        button.setBackground(originalColor);
                    }
                });
            }
        }
    }
    
    /**
    * Memulai timer yang akan memperbarui label jam (lblTime) setiap detik
    * dengan waktu saat ini untuk zona waktu Jakarta (Asia/Jakarta).
    */
    private void startRealTimeClock() {
        // Membuat Timer yang akan berjalan setiap 1000 milidetik (1 detik).
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            // Menggunakan java.time untuk manajemen waktu yang modern dan lebih baik.

            // Tentukan Zona Waktu (WIB adalah Asia/Jakarta)
            java.time.ZoneId zoneId = java.time.ZoneId.of("Asia/Jakarta");

            // Dapatkan waktu saat ini di zona waktu tersebut
            java.time.ZonedDateTime now = java.time.ZonedDateTime.now(zoneId);

            // Buat format tampilan jam (HH untuk jam 24-jam, mm untuk menit, ss untuk detik)
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");

            // Format waktu saat ini dan atur sebagai teks untuk lblTime
            lblTime.setText(now.format(formatter) + " WIB");
        });

        // Memulai timer
        timer.start();
    }

    private void tampilkanDataAset() {
        // Kolom untuk tabel
        String[] kolom = {"ID", "Kode Inventaris", "Nama Aset", "Kategori", "Kondisi", "Lokasi", "Tgl Beli", "Hari Pemeliharaan","status"};
        DefaultTableModel model = new DefaultTableModel(null, kolom);
        jTable1.setModel(model);

        // Penjaga (guard clause) untuk mencegah error jika suatu saat
        // method ini terpanggil sebelum filter siap.
        if (filterKategori.getSelectedItem() == null || 
            filterKondisi.getSelectedItem() == null || 
            filterPelihara.getSelectedItem() == null) {
            return; // Jangan lakukan apa-apa jika filter belum siap.
        }
        
        String kategori = filterKategori.getSelectedItem().toString();
        String kondisi = filterKondisi.getSelectedItem().toString();
        String hari = filterPelihara.getSelectedItem().toString();
        
        StringBuilder sqlBuilder = new StringBuilder("SELECT id_aset, kode_inventaris, nama_aset, kategori, kondisi, lokasi, tanggal_beli, hari_pemeliharaan, status FROM aset");
        java.util.List<String> conditions = new java.util.ArrayList<>();

        if (!kategori.equals("Semua Kategori")) {
            conditions.add("kategori = '" + kategori + "'");
        }
        if (!kondisi.equals("Semua Kondisi")) {
            conditions.add("kondisi = '" + kondisi + "'");
        }
        if (!hari.equals("Semua Hari")) {
            conditions.add("hari_pemeliharaan = '" + hari + "'");
        }

        if (!conditions.isEmpty()) {
            sqlBuilder.append(" WHERE ");
            sqlBuilder.append(String.join(" AND ", conditions));
        }
        
        sqlBuilder.append(" ORDER BY id_aset ASC");

        try {
            Connection conn = ConnectSQL.getKoneksi();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlBuilder.toString());

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_aset"),
                    rs.getString("kode_inventaris"),
                    rs.getString("nama_aset"),
                    rs.getString("kategori"),
                    rs.getString("kondisi"),
                    rs.getString("lokasi"),
                    rs.getDate("tanggal_beli"),
                    rs.getString("hari_pemeliharaan"),
                    rs.getString("status")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data aset: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
    * Mengambil data kategori unik dari database dan memuatnya ke dalam filterKategori.
    */
    private void muatFilterKategori() {
        try {
            Connection conn = ConnectSQL.getKoneksi();
            Statement st = conn.createStatement();
            String sql = "SELECT DISTINCT kategori FROM aset ORDER BY kategori ASC";
            ResultSet rs = st.executeQuery(sql);

            filterKategori.removeAllItems(); // Bersihkan item lama
            filterKategori.addItem("Semua Kategori"); // Tambahkan item default
            while (rs.next()) {
                filterKategori.addItem(rs.getString("kategori"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat filter kategori: " + e.getMessage());
        }
    }

    /**
     * Mengambil data kondisi unik dari database dan memuatnya ke dalam filterKondisi.
     */
    private void muatFilterKondisi() {
        try {
            Connection conn = ConnectSQL.getKoneksi();
            Statement st = conn.createStatement();
            // ENUM biasanya tidak perlu diurutkan, tapi bisa jika diperlukan
            String sql = "SELECT DISTINCT kondisi FROM aset";
            ResultSet rs = st.executeQuery(sql);

            filterKondisi.removeAllItems();
            filterKondisi.addItem("Semua Kondisi");
            while (rs.next()) {
                filterKondisi.addItem(rs.getString("kondisi"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat filter kondisi: " + e.getMessage());
        }
    }

    /**
     * Mengambil data hari pemeliharaan unik dari database dan memuatnya ke dalam filterPelihara.
     */
    private void muatFilterPelihara() {
        try {
            Connection conn = ConnectSQL.getKoneksi();
            Statement st = conn.createStatement();
            // Ambil data yang tidak null agar tidak ada pilihan kosong
            String sql = "SELECT DISTINCT hari_pemeliharaan FROM aset WHERE hari_pemeliharaan IS NOT NULL ORDER BY hari_pemeliharaan ASC";
            ResultSet rs = st.executeQuery(sql);

            filterPelihara.removeAllItems();
            filterPelihara.addItem("Semua Hari");
            while (rs.next()) {
                filterPelihara.addItem(rs.getString("hari_pemeliharaan"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat filter pemeliharaan: " + e.getMessage());
        }
    }
    
    /**
    * Mengambil data dari baris yang dipilih di tabel dan mengisinya
    * ke dalam field-field input di bawah.
    */
    private void isiFormDariTabel() {
        // Dapatkan indeks baris yang dipilih
        int barisTerpilih = jTable1.getSelectedRow();

        // Pastikan ada baris yang benar-benar dipilih (bukan header atau area kosong)
        if (barisTerpilih == -1) {
            return; // Keluar dari metode jika tidak ada baris yang dipilih
        }

        // Ambil model dari tabel untuk mengakses datanya
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        // 4Ambil data dari setiap kolom di baris yang dipilih.
        String id = String.valueOf(model.getValueAt(barisTerpilih, 0));
        String kodeInventaris = String.valueOf(model.getValueAt(barisTerpilih, 1));
        String namaAset = String.valueOf(model.getValueAt(barisTerpilih, 2));
        String kategori = String.valueOf(model.getValueAt(barisTerpilih, 3));
        String kondisi = String.valueOf(model.getValueAt(barisTerpilih, 4));
        String lokasi = String.valueOf(model.getValueAt(barisTerpilih, 5));
        String status = String.valueOf(model.getValueAt(barisTerpilih, 8));
        Object hariPeliharaObj = model.getValueAt(barisTerpilih, 7); // Ambil sebagai Object untuk handle null
        
        // Data dari database biasanya bertipe java.sql.Date, yang bisa langsung di-set.
        Date tanggalBeli = (Date) model.getValueAt(barisTerpilih, 6);
        dateTanggal.setDate(tanggalBeli);

        // Isi data ke dalam JTextField
        txtId.setText(id);
        txtKInven.setText(kodeInventaris);
        txtNama.setText(namaAset);
        txtKategori.setText(kategori);
        txtLokasi.setText(lokasi);
        dateTanggal.setDate(tanggalBeli);

        // Atur item yang terpilih di JComboBox
        cmbKondisi.setSelectedItem(kondisi);
        cmbStatus.setSelectedItem(status);

        // Untuk cmbHari, tangani jikalau datanya null
        if (hariPeliharaObj == null) {
            cmbHari.setSelectedIndex(0); 
        } else {
            cmbHari.setSelectedItem(String.valueOf(hariPeliharaObj));
        }
    }
    
    /**
    * Mengosongkan semua field input pada form.
    */
    private void kosongkanForm() {
        txtId.setText("");
        txtNama.setText("");
        txtKategori.setText("");
        txtLokasi.setText("");
        txtKInven.setText("");
        dateTanggal.setDate(null);

        // Atur JComboBox ke item pertama (biasanya pilihan default)
        if (cmbKondisi.getItemCount() > 0) {
            cmbKondisi.setSelectedIndex(0);
        }
        if (cmbHari.getItemCount() > 0) {
            cmbHari.setSelectedIndex(0);
        }

    }
    
    /**
    * Menyimpan data ke tabel 'aset'.
    */
    private void simpanDataAset() {
        // Ambil data dari setiap komponen form
        String nama = txtNama.getText();
        String kategori = txtKategori.getText();
        String lokasi = txtLokasi.getText();
        String kodeInventaris = txtKInven.getText();
        // Ambil tanggal dari JDateChooser
        Date utilDate = dateTanggal.getDate();

        // Pastikan item terpilih sebelum mengambil stringnya
        String kondisi = cmbKondisi.getSelectedItem() != null ? cmbKondisi.getSelectedItem().toString() : "";
        String status = cmbStatus.getSelectedItem() != null ? cmbStatus.getSelectedItem().toString() : "";
        String hariPelihara = cmbHari.getSelectedItem() != null ? cmbHari.getSelectedItem().toString() : "";

        // Lakukan validasi sederhana (pastikan field penting tidak kosong)
        if (nama.isEmpty() || kategori.isEmpty() || lokasi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Aset, Kategori, dan Lokasi tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return; // Hentikan proses jika validasi gagal
        }

        // Siapkan query SQL menggunakan PreparedStatement untuk keamanan
        String sql = "INSERT INTO aset (nama_aset, kategori, kondisi, lokasi, tanggal_beli, hari_pemeliharaan, kode_inventaris, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = ConnectSQL.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql);

            // Atur nilai parameter berdasarkan data dari form
            pst.setString(1, nama);
            pst.setString(2, kategori);
            pst.setString(3, kondisi);
            pst.setString(4, lokasi);

            // [PERUBAHAN] Handle penyimpanan tanggal
            if (utilDate != null) {
                // Konversi java.util.Date ke java.sql.Date
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                pst.setDate(5, sqlDate);
            } else {
                pst.setNull(5, java.sql.Types.DATE); // Simpan sebagai NULL jika kosong
            }

            pst.setString(6, hariPelihara);
            pst.setString(7, kodeInventaris);
            pst.setString(8, status);
            
            System.out.println(pst);
            // Berikan konfirmasi
            int input = JOptionPane.showConfirmDialog(null,"Tambahkan data " + nama+"?", "Tambah Data", JOptionPane.INFORMATION_MESSAGE);
            if (input == 0){
                // Eksekusi query
                int rowsAffected = pst.executeUpdate();

                // Beri notif dan perbarui UI
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Data aset berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    tampilkanDataAset();
                    kosongkanForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menambahkan data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menyimpan data: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Cetak error ke konsol untuk debugging
        }
    }
    
    /**
    * Mengubah data tabel 'aset'.
    */
    private void ubahDataAset() {
        // Pastikan data telah terpilih
        if (txtId.getText().isEmpty()){
            System.out.println(txtId.getText());
            JOptionPane.showMessageDialog(this, "Mohon pilih data dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Logic yang sama dengan tambah data
        int id = Integer.parseInt(txtId.getText());
        String nama = txtNama.getText();
        String kategori = txtKategori.getText();
        String lokasi = txtLokasi.getText();
        String kodeInventaris = txtKInven.getText();
        Date utilDate = dateTanggal.getDate();
        
        String kondisi = cmbKondisi.getSelectedItem() != null ? cmbKondisi.getSelectedItem().toString() : "";
        String status = cmbStatus.getSelectedItem() != null ? cmbStatus.getSelectedItem().toString() : "";
        String hariPelihara = cmbHari.getSelectedItem() != null ? cmbHari.getSelectedItem().toString() : "";
        
        if (nama.isEmpty() || kategori.isEmpty() || lokasi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Aset, Kategori, dan Lokasi tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return; // Hentikan proses jika validasi gagal
        }

        // Query berbeda umtuk update
        String sql = "UPDATE `aset` SET `nama_aset`=?,`kategori`=?,`kondisi`=?,`lokasi`=?,`tanggal_beli`=?,`hari_pemeliharaan`=?,`kode_inventaris`=?, `status`=? WHERE id_aset=?";

        try {
            Connection conn = ConnectSQL.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setInt(9, id);
            pst.setString(1, nama);
            pst.setString(2, kategori);
            pst.setString(3, kondisi);
            pst.setString(4, lokasi);

            // [PERUBAHAN] Handle penyimpanan tanggal
            if (utilDate != null) {
                // Konversi java.util.Date ke java.sql.Date
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                pst.setDate(5, sqlDate);
            } else {
                pst.setNull(5, java.sql.Types.DATE); // Simpan sebagai NULL jika kosong
            }

            pst.setString(6, hariPelihara);
            pst.setString(7, kodeInventaris);
            pst.setString(8, status);

            System.out.println(pst);
            // Berikan konfirmasi
            int input = JOptionPane.showConfirmDialog(null,"Ubah data " + nama+"?", "Ubah Data", JOptionPane.INFORMATION_MESSAGE);
            if (input == 0){
                int rowsAffected = pst.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Data aset berhasil diubah!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    tampilkanDataAset();
                    kosongkanForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal mengubah data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mengubah data: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
    * Mengahpus data tabel 'aset'.
    */
    private void hapusDataAset(){
        // Pastikan data telah terpilih
        if (txtId.getText().isEmpty()){
            System.out.println(txtId.getText());
            JOptionPane.showMessageDialog(this, "Mohon pilih data dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // mengambil ID dari data yang ingin dihapus
        int id = Integer.parseInt(txtId.getText());
        String nama = txtNama.getText();
        
        // Query berbeda umtuk update
        String sql = "DELETE FROM `aset` WHERE id_aset=?";
        
        try {
            Connection conn = ConnectSQL.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setInt(1, id);
            System.out.println(pst);
            // Berikan konfirmasi
            int input = JOptionPane.showConfirmDialog(null,"Hapus data " + nama+"?", "Hapus Data", JOptionPane.INFORMATION_MESSAGE);
            if (input == 0){
                int rowsAffected = pst.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Data aset berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    tampilkanDataAset();
                    kosongkanForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menghapus data: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sidebarPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnDashboard = new javax.swing.JButton();
        btnAset = new javax.swing.JButton();
        btnPeminjaman = new javax.swing.JButton();
        btnPemeliharaan = new javax.swing.JButton();
        btnLaporan = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        btnLapor = new javax.swing.JButton();
        btnPinjamAset = new javax.swing.JButton();
        btnPinjamFasil = new javax.swing.JButton();
        btnUser = new javax.swing.JButton();
        contentPanel = new javax.swing.JPanel();
        lblHeader = new javax.swing.JLabel();
        lblTime = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        filterKategori = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        filterKondisi = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        filterPelihara = new javax.swing.JComboBox<>();
        txtId = new javax.swing.JTextField();
        txtNama = new javax.swing.JTextField();
        txtKategori = new javax.swing.JTextField();
        txtKInven = new javax.swing.JTextField();
        txtLokasi = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cmbHari = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        cmbKondisi = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        btnTambah = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        dateTanggal = new com.toedter.calendar.JDateChooser();
        cmbStatus = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(248, 249, 252));

        sidebarPanel.setBackground(new java.awt.Color(34, 45, 50));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("SARANA PRASARANA");

        btnDashboard.setBackground(new java.awt.Color(34, 45, 50));
        btnDashboard.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnDashboard.setForeground(new java.awt.Color(255, 255, 255));
        btnDashboard.setText("Dashboard");
        btnDashboard.setBorder(null);
        btnDashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDashboardActionPerformed(evt);
            }
        });

        btnAset.setBackground(new java.awt.Color(52, 152, 219));
        btnAset.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnAset.setForeground(new java.awt.Color(255, 255, 255));
        btnAset.setText("Aset");
        btnAset.setBorder(null);
        btnAset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAsetActionPerformed(evt);
            }
        });

        btnPeminjaman.setBackground(new java.awt.Color(34, 45, 50));
        btnPeminjaman.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnPeminjaman.setForeground(new java.awt.Color(255, 255, 255));
        btnPeminjaman.setText("Peminjaman");
        btnPeminjaman.setBorder(null);
        btnPeminjaman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPeminjamanActionPerformed(evt);
            }
        });

        btnPemeliharaan.setBackground(new java.awt.Color(34, 45, 50));
        btnPemeliharaan.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnPemeliharaan.setForeground(new java.awt.Color(255, 255, 255));
        btnPemeliharaan.setText("Pemeliharaan");
        btnPemeliharaan.setBorder(null);
        btnPemeliharaan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPemeliharaanActionPerformed(evt);
            }
        });

        btnLaporan.setBackground(new java.awt.Color(34, 45, 50));
        btnLaporan.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnLaporan.setForeground(new java.awt.Color(255, 255, 255));
        btnLaporan.setText("Laporan");
        btnLaporan.setBorder(null);
        btnLaporan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLaporanActionPerformed(evt);
            }
        });

        btnLogout.setBackground(new java.awt.Color(255, 0, 0));
        btnLogout.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("Logout");
        btnLogout.setBorder(null);
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        btnLapor.setBackground(new java.awt.Color(34, 45, 50));
        btnLapor.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnLapor.setForeground(new java.awt.Color(255, 255, 255));
        btnLapor.setText("Lapor");
        btnLapor.setBorder(null);
        btnLapor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLaporActionPerformed(evt);
            }
        });

        btnPinjamAset.setBackground(new java.awt.Color(34, 45, 50));
        btnPinjamAset.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnPinjamAset.setForeground(new java.awt.Color(255, 255, 255));
        btnPinjamAset.setText("Pinjam Aset");
        btnPinjamAset.setBorder(null);
        btnPinjamAset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPinjamAsetActionPerformed(evt);
            }
        });

        btnPinjamFasil.setBackground(new java.awt.Color(34, 45, 50));
        btnPinjamFasil.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnPinjamFasil.setForeground(new java.awt.Color(255, 255, 255));
        btnPinjamFasil.setText("Pinjam Fasilitas");
        btnPinjamFasil.setBorder(null);
        btnPinjamFasil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPinjamFasilActionPerformed(evt);
            }
        });

        btnUser.setBackground(new java.awt.Color(34, 45, 50));
        btnUser.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnUser.setForeground(new java.awt.Color(255, 255, 255));
        btnUser.setText("Manajemen User");
        btnUser.setBorder(null);
        btnUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUserActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sidebarPanelLayout = new javax.swing.GroupLayout(sidebarPanel);
        sidebarPanel.setLayout(sidebarPanelLayout);
        sidebarPanelLayout.setHorizontalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(btnDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnAset, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnPeminjaman, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnPemeliharaan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnLaporan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnLapor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnPinjamAset, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnPinjamFasil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        sidebarPanelLayout.setVerticalGroup(
            sidebarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAset, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPeminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPemeliharaan, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLaporan, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLapor, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPinjamAset, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPinjamFasil, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUser, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        contentPanel.setBackground(new java.awt.Color(248, 249, 252));

        lblHeader.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblHeader.setText("MENU ASET");

        lblTime.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTime.setText("Time");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        filterKategori.setBackground(new java.awt.Color(52, 152, 219));
        filterKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        filterKategori.setBorder(null);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Filter");

        jLabel3.setText("Kategori");

        jLabel4.setText("Kondisi");

        filterKondisi.setBackground(new java.awt.Color(52, 152, 219));
        filterKondisi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        filterKondisi.setBorder(null);

        jLabel5.setText("Hari Pemeliharaan");

        filterPelihara.setBackground(new java.awt.Color(52, 152, 219));
        filterPelihara.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        filterPelihara.setBorder(null);

        jLabel6.setText("ID");

        jLabel7.setText("Nama");

        jLabel8.setText("Kategori");

        jLabel9.setText("Kode Inventaris (boleh kosong)");

        jLabel10.setText("Lokasi");

        cmbHari.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tidak ada", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu" }));

        jLabel11.setText("Hari Pemeliharaan");

        cmbKondisi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "baik", "perawatan", "rusak" }));

        jLabel12.setText("Kondisi");

        jLabel13.setText("Tanggal Beli");

        btnTambah.setBackground(new java.awt.Color(52, 152, 219));
        btnTambah.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnTambah.setForeground(new java.awt.Color(242, 242, 242));
        btnTambah.setText("Tambah");
        btnTambah.setBorder(null);
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnUbah.setBackground(new java.awt.Color(255, 255, 0));
        btnUbah.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUbah.setForeground(new java.awt.Color(153, 153, 153));
        btnUbah.setText("Ubah");
        btnUbah.setBorder(null);
        btnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahActionPerformed(evt);
            }
        });

        btnHapus.setBackground(new java.awt.Color(255, 0, 0));
        btnHapus.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnHapus.setForeground(new java.awt.Color(242, 242, 242));
        btnHapus.setText("Hapus");
        btnHapus.setBorder(null);
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "tersedia", "tidak tersedia" }));

        jLabel14.setText("Status");

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTime))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                                .addComponent(lblHeader)
                                .addGap(265, 265, 265))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                                .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnUbah, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(419, 419, 419))))
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(filterKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(filterKondisi, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addGap(18, 18, 18)
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(filterPelihara, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(txtKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addComponent(txtLokasi, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cmbKondisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12))
                                .addGap(18, 18, 18)
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cmbHari, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addComponent(dateTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtKInven, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9))
                                .addGap(18, 18, 18)
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14)
                                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(lblHeader)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTime)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(filterKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filterKondisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filterPelihara, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel9)
                                .addComponent(jLabel14))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtKInven, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(18, 18, 18)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(dateTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtLokasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                            .addComponent(jLabel12)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cmbKondisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                            .addComponent(jLabel11)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cmbHari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(77, 77, 77)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUbah, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(sidebarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sidebarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashboardActionPerformed
        new DashboardForm(userId, userName, userRole).setVisible(true);

        this.dispose();
    }//GEN-LAST:event_btnDashboardActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        int input = JOptionPane.showConfirmDialog(null,"Ingin Logout?", "Logout", JOptionPane.INFORMATION_MESSAGE);
        if (input == 0){
            new LoginForm().setVisible(true);

            this.dispose(); 
        }
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnAsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAsetActionPerformed
        new AsetForm(userId, userName, userRole).setVisible(true);

        this.dispose(); 
    }//GEN-LAST:event_btnAsetActionPerformed

    private void btnPeminjamanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPeminjamanActionPerformed
        new PeminjamanForm(userId, userName, userRole).setVisible(true);

        this.dispose();
    }//GEN-LAST:event_btnPeminjamanActionPerformed

    private void btnPemeliharaanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPemeliharaanActionPerformed
        new PemeliharaanForm(userId, userName, userRole).setVisible(true);

        this.dispose();
    }//GEN-LAST:event_btnPemeliharaanActionPerformed

    private void btnLaporanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLaporanActionPerformed
        new LaporanForm(userId, userName, userRole).setVisible(true);

        this.dispose();
    }//GEN-LAST:event_btnLaporanActionPerformed

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahActionPerformed
        ubahDataAset();
    }//GEN-LAST:event_btnUbahActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        isiFormDariTabel();
    }//GEN-LAST:event_jTable1MouseClicked

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        simpanDataAset();
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        hapusDataAset();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnLaporActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLaporActionPerformed
        new LaporForm(userId, userName, userRole).setVisible(true);

        this.dispose();
    }//GEN-LAST:event_btnLaporActionPerformed

    private void btnPinjamAsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPinjamAsetActionPerformed
        new PinjamForm(userId, userName, userRole, "aset").setVisible(true);

        this.dispose();
    }//GEN-LAST:event_btnPinjamAsetActionPerformed

    private void btnPinjamFasilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPinjamFasilActionPerformed
        new PinjamForm(userId, userName, userRole, "fasilitas").setVisible(true);

        this.dispose();
    }//GEN-LAST:event_btnPinjamFasilActionPerformed

    private void btnUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUserActionPerformed
        new UserForm(userId, userName, userRole).setVisible(true);

        this.dispose();
    }//GEN-LAST:event_btnUserActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AsetForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AsetForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AsetForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AsetForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAset;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnLapor;
    private javax.swing.JButton btnLaporan;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPemeliharaan;
    private javax.swing.JButton btnPeminjaman;
    private javax.swing.JButton btnPinjamAset;
    private javax.swing.JButton btnPinjamFasil;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnUbah;
    private javax.swing.JButton btnUser;
    private javax.swing.JComboBox<String> cmbHari;
    private javax.swing.JComboBox<String> cmbKondisi;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JPanel contentPanel;
    private com.toedter.calendar.JDateChooser dateTanggal;
    private javax.swing.JComboBox<String> filterKategori;
    private javax.swing.JComboBox<String> filterKondisi;
    private javax.swing.JComboBox<String> filterPelihara;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblTime;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtKInven;
    private javax.swing.JTextField txtKategori;
    private javax.swing.JTextField txtLokasi;
    private javax.swing.JTextField txtNama;
    // End of variables declaration//GEN-END:variables
}
