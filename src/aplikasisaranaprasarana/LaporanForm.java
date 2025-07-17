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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
/**
 *
 * @author N.A.I
 */
public class LaporanForm extends javax.swing.JFrame {

    // Simpan role pengguna untuk referensi
    protected String userRole;
    protected String userName;
    protected int userId;
    
    /**
     * Creates new form DashboardForm
     */
    public LaporanForm(int id, String nama, String role) {
        initComponents();
        setLocationRelativeTo(null); // Posisikan di tengah layar

        this.userId = id;
        this.userName = nama;
        this.userRole = role;

        // Panggil metode untuk memuat data ke dalam combo box
        muatFilterStatus();
        // Buat form text field ID tidak dapat di ubah
        txtId.setEditable(false);
        txtIdAset.setEditable(false);
        txtNamAset.setEditable(false);
        txtUser.setEditable(false);
        // Atur menu berdasarkan role
        aturMenuBerdasarkanRole();
        // Mengatur menu
        setupMenuStyles();
        // Memunculkan jam real time
        startRealTimeClock();
        // Memumnculkan data tabel
        tampilkanDataKerusakan();
        attachFilterListeners();
    }
    
    /**
     * Metode untuk mendeteksi perubahan pada filter
     */
    private void attachFilterListeners() {
        // Buat satu listener yang bisa dipakai bersama untuk efisiensi.
        ActionListener filterListener = e -> tampilkanDataKerusakan();
        
        filterStatus.addActionListener(filterListener);
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

    private void tampilkanDataKerusakan() {
        // Kolom untuk tabel
        String[] kolom = {"ID", "ID Aset", "Nama Aset", "ID User", "Nama Pelapor", "Deskripsi", "Waktu Lapor", "Status"};
        DefaultTableModel model = new DefaultTableModel(null, kolom);
        jTable1.setModel(model);

        // [PERUBAHAN] Penjaga (guard clause) untuk mencegah error jika suatu saat
        // method ini terpanggil sebelum filter siap.
        if (filterStatus.getSelectedItem() == null) {
            return; // Jangan lakukan apa-apa jika filter belum siap.
        }
        
        String status = filterStatus.getSelectedItem().toString();
        
        StringBuilder sqlBuilder = new StringBuilder("SELECT k.id_laporan, k.id_aset, a.nama_aset, k.id_user, u.nama, " +
             "k.deskripsi, k.tanggal_lapor, k.status " +
             "FROM kerusakan k " +
             "JOIN aset a ON k.id_aset = a.id_aset " +
             "JOIN user u ON k.id_user = u.id_user");
        
        java.util.List<String> conditions = new java.util.ArrayList<>();

        if (!status.equals("Semua Status")) {
            conditions.add("k.status = '" + status + "'");
        }

        if (!conditions.isEmpty()) {
            sqlBuilder.append(" WHERE ");
            sqlBuilder.append(String.join(" AND ", conditions));
        }
        
        sqlBuilder.append(" ORDER BY tanggal_lapor DESC");

        try {
            Connection conn = ConnectSQL.getKoneksi();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlBuilder.toString());

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_laporan"),
                    rs.getInt("id_aset"),
                    rs.getString("nama_aset"),
                    rs.getInt("id_user"),
                    rs.getString("nama"),
                    rs.getString("deskripsi"),
                    rs.getString("tanggal_lapor"),
                    rs.getString("status")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data laporan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
    * Mengambil data status unik dari database dan memuatnya ke dalam filterStatus.
    */
    private void muatFilterStatus() {
        try {
            Connection conn = ConnectSQL.getKoneksi();
            Statement st = conn.createStatement();
            String sql = "SELECT DISTINCT status FROM kerusakan ORDER BY status ASC";
            ResultSet rs = st.executeQuery(sql);

            filterStatus.removeAllItems(); // Bersihkan item lama
            filterStatus.addItem("Semua Status"); // Tambahkan item default
            while (rs.next()) {
                filterStatus.addItem(rs.getString("status"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat filter status: " + e.getMessage());
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
        String idAset = String.valueOf(model.getValueAt(barisTerpilih, 1));
        String namaAset = String.valueOf(model.getValueAt(barisTerpilih, 2));
        String namaUser = String.valueOf(model.getValueAt(barisTerpilih, 4));
        String deskripsi = String.valueOf(model.getValueAt(barisTerpilih, 5));

        // Isi data ke dalam JTextField
        txtId.setText(id);
        txtIdAset.setText(idAset);
        txtNamAset.setText(namaAset);
        txtUser.setText(namaUser);
        txtDeskripsi.setText(deskripsi);
    }
    
    /**
    * Mengosongkan semua field input pada form.
    */
    private void kosongkanForm() {
        txtId.setText("");
        txtIdAset.setText("");
        txtNamAset.setText("");
        txtUser.setText("");
        txtDeskripsi.setText("");


        // Jika Anda menggunakan placeholder, panggil lagi di sini
        // addPlaceholderStyle(txtTanggal, "TTTT-BB-HH");
    }
    
    private void setStatus(String neoStatus) {
        // Pastikan data telah terpilih
        if (txtId.getText().isEmpty()){
            System.out.println(txtId.getText());
            JOptionPane.showMessageDialog(this, "Mohon pilih data dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = Integer.parseInt(txtId.getText());
        int idAset = Integer.parseInt(txtIdAset.getText());
        String nama = txtNamAset.getText();
        
        String sql = "UPDATE `kerusakan` SET `status`=? WHERE `id_laporan`=?";
        String sql2 = null;
        // Mengubah kondisi aset sesuai status kerusakan
        if (neoStatus == "ditangani") {
            sql2 = "UPDATE `aset` SET `kondisi`='perawatan' WHERE id_aset=?";
        } else if (neoStatus == "selesai") {
            sql2 = "UPDATE `aset` SET `kondisi`='baik', status='tersedia' WHERE id_aset=?";
        }
        try {
            Connection conn = ConnectSQL.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql);
            PreparedStatement pst2 = conn.prepareStatement(sql2);

            pst.setInt(2, id);
            pst.setString(1, neoStatus);

            pst2.setInt(1, idAset);
            
            System.out.println(pst);
            System.out.println(pst2);
            // Berikan konfirmasi
            int input = JOptionPane.showConfirmDialog(null,"Apakah "+ nama+" dalam keadaan "+ neoStatus +"?", "Ubah Status", JOptionPane.INFORMATION_MESSAGE);
            if (input == 0){
                int rowsAffected = pst.executeUpdate();
                int rowsAffected2 = pst2.executeUpdate();

                if (rowsAffected > 0 || rowsAffected2 > 0) {
                    JOptionPane.showMessageDialog(this, "Status laporan berhasil diubah menjadi "+ neoStatus +"!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    tampilkanDataKerusakan();
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
    * Mengubah data tabel 'aset'.
    */
    private void ubahLaporan() {
        // Pastikan data telah terpilih
        if (txtId.getText().isEmpty()){
            System.out.println(txtId.getText());
            JOptionPane.showMessageDialog(this, "Mohon pilih data dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = Integer.parseInt(txtId.getText());
        String nama = txtIdAset.getText();
        String deskripsi = txtDeskripsi.getText();
        
        // Lakukan validasi sederhana (pastikan field penting tidak kosong)
        if (deskripsi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon masukkan deskripsi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return; // Hentikan proses jika validasi gagal
        }
        
        String sql = "UPDATE `kerusakan` SET `deskripsi`=? WHERE `id_laporan`=?";
        
        try {
            Connection conn = ConnectSQL.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setInt(2, id);
            pst.setString(1, deskripsi);
            
            System.out.println(pst);
            // Berikan konfirmasi
            int input = JOptionPane.showConfirmDialog(null,"Apakah deskripsi laporan "+ nama+" ingin diubah?", "Ubah Deskripsi", JOptionPane.INFORMATION_MESSAGE);
            if (input == 0){
                int rowsAffected = pst.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Deskripsi laporan berhasil diubah!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    tampilkanDataKerusakan();
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
    private void hapusLaporan(){
        // Pastikan data telah terpilih
        if (txtId.getText().isEmpty()){
            System.out.println(txtId.getText());
            JOptionPane.showMessageDialog(this, "Mohon pilih data dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // mengambil ID dari data yang ingin dihapus
        int id = Integer.parseInt(txtId.getText());
        int idAset = Integer.parseInt(txtIdAset.getText());
        String nama = txtNamAset.getText();
        
        // Query berbeda umtuk update
        String sql = "DELETE FROM `kerusakan` WHERE id_laporan=?";
        String sql2 = "UPDATE `aset` SET `kondisi`='baik', `status`='tersedia' WHERE id_aset=?";
        
        try {
            Connection conn = ConnectSQL.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql);
            PreparedStatement pst2 = conn.prepareStatement(sql2);

            pst.setInt(1, id);
            pst2.setInt(1, idAset);
            System.out.println(pst);
            System.out.println(pst2);
            // Berikan konfirmasi
            int input = JOptionPane.showConfirmDialog(null,"Hapus data laporan dari " + nama+"?", "Hapus Data", JOptionPane.INFORMATION_MESSAGE);
            if (input == 0){
                int rowsAffected = pst.executeUpdate();
                int rowsAffected2 = pst2.executeUpdate();

                if (rowsAffected > 0 || rowsAffected2 > 0) {
                    JOptionPane.showMessageDialog(this, "Data aset berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    tampilkanDataKerusakan();
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
        filterStatus = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        txtIdAset = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        btnTangani = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        txtNamAset = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDeskripsi = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        btnSelesai = new javax.swing.JButton();
        txtUser = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();

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

        btnAset.setBackground(new java.awt.Color(34, 45, 50));
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

        btnLaporan.setBackground(new java.awt.Color(52, 152, 219));
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
        lblHeader.setText("MENU LAPORAN");

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

        filterStatus.setBackground(new java.awt.Color(52, 152, 219));
        filterStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        filterStatus.setBorder(null);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Filter");

        jLabel3.setText("Status");

        jLabel6.setText("ID");

        jLabel7.setText("ID Aset");

        jLabel12.setText("Nama Aset");

        btnTangani.setBackground(new java.awt.Color(52, 152, 219));
        btnTangani.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnTangani.setForeground(new java.awt.Color(242, 242, 242));
        btnTangani.setText("Ditangani");
        btnTangani.setBorder(null);
        btnTangani.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTanganiActionPerformed(evt);
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

        txtDeskripsi.setColumns(20);
        txtDeskripsi.setRows(5);
        jScrollPane2.setViewportView(txtDeskripsi);

        jLabel4.setText("Deskripsi Kerusakan");

        btnSelesai.setBackground(new java.awt.Color(51, 204, 0));
        btnSelesai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSelesai.setForeground(new java.awt.Color(242, 242, 242));
        btnSelesai.setText("Selesai");
        btnSelesai.setBorder(null);
        btnSelesai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelesaiActionPerformed(evt);
            }
        });

        jLabel5.setText("Nama Pelapor");

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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(filterStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel12)
                                    .addComponent(txtNamAset, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                                    .addComponent(txtId, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                                    .addComponent(jLabel7)
                                    .addComponent(txtIdAset))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(contentPanelLayout.createSequentialGroup()
                                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jScrollPane2)
                                            .addComponent(jLabel4)
                                            .addComponent(txtUser))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(btnTangani, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnSelesai, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(contentPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        .addGap(18, 18, 18)
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnUbah, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(19, 19, 19))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblHeader)
                .addGap(237, 237, 237))
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
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel6))
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnUbah, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnTangani, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel7)
                                        .addGroup(contentPanelLayout.createSequentialGroup()
                                            .addGap(17, 17, 17)
                                            .addComponent(txtIdAset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jLabel12)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtNamAset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(contentPanelLayout.createSequentialGroup()
                                            .addGap(22, 22, 22)
                                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jLabel4)))))))
                .addContainerGap(38, Short.MAX_VALUE))
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
            .addGroup(layout.createSequentialGroup()
                .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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
        ubahLaporan();
    }//GEN-LAST:event_btnUbahActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        isiFormDariTabel();
    }//GEN-LAST:event_jTable1MouseClicked

    private void btnTanganiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTanganiActionPerformed
        setStatus("ditangani");
    }//GEN-LAST:event_btnTanganiActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        hapusLaporan();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnSelesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelesaiActionPerformed
        setStatus("selesai");
    }//GEN-LAST:event_btnSelesaiActionPerformed

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
            java.util.logging.Logger.getLogger(LaporanForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LaporanForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LaporanForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LaporanForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
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
    private javax.swing.JButton btnSelesai;
    private javax.swing.JButton btnTangani;
    private javax.swing.JButton btnUbah;
    private javax.swing.JButton btnUser;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JComboBox<String> filterStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblTime;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JTextArea txtDeskripsi;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtIdAset;
    private javax.swing.JTextField txtNamAset;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables
}
