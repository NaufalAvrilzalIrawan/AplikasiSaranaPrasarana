/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package aplikasisaranaprasarana;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author user
 */
public class DashboardForm extends javax.swing.JFrame {

    // Simpan role pengguna untuk referensi
    protected int userId;
    protected String userRole;
    protected String userName;
    
    /**
     * Creates new form DashboardForm
     */
    public DashboardForm(int id, String nama, String role) {
        initComponents();
        setLocationRelativeTo(null); // Posisikan di tengah layar

        this.userId = id;
        this.userRole = role;
        this.userName = nama;
        lblHeader.setText("Selamat Datang, " + nama);

        // Atur menu berdasarkan role
        aturMenuBerdasarkanRole();
        
        // Mengatur menu
        setupMenuStyles();
        
        // Memunculkan jam real time
        startRealTimeClock();
        
        // Memulculkan data statistik
        tampilkanData();
        jumlahKategori();
        tampilkanDataAset();
        tampilkanDataPeminjaman();
    }
    
    /**
     * Metode untuk mengatur visibilitas tombol menu berdasarkan role pengguna.
     */
    private void aturMenuBerdasarkanRole() {
        // Secara default, semua menu untuk admin/petugas terlihat
        // Jika role adalah 'user', sembunyikan menu tertentu
        jPanel6.setVisible(false);
        if (this.userRole.equals("user")) {
            
            btnAset.setVisible(false);
            btnPeminjaman.setVisible(false);
            btnPemeliharaan.setVisible(false);
            btnLaporan.setVisible(false);
            btnUser.setVisible(false);
            jPanel1.setVisible(false);
            jPanel2.setVisible(false);
            jPanel3.setVisible(false);
            jPanel4.setVisible(false);
            jPanel5.setVisible(false);
            jPanel6.setVisible(true);
            
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
    
    private String getHariIni() {
        LocalDate tanggalSekarang = LocalDate.now();
        DayOfWeek hariDalamMinggu = tanggalSekarang.getDayOfWeek();

        switch (hariDalamMinggu) {
            case MONDAY:    return "Senin";
            case TUESDAY:   return "Selasa";
            case WEDNESDAY: return "Rabu";
            case THURSDAY:  return "Kamis";
            case FRIDAY:    return "Jumat";
            case SATURDAY:  return "Sabtu";
            case SUNDAY:    return "Minggu";
            default:        return "";
        }
    }
    
    private void tampilkanData() {
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(*) FROM aset WHERE kategori != 'fasilitas'");
        StringBuilder sqlBuilder2 = new StringBuilder("SELECT COUNT(*) FROM aset WHERE kategori = 'fasilitas'");
        StringBuilder sqlBuilder3 = new StringBuilder("SELECT COUNT(*) FROM peminjaman WHERE status = 'disetujui'");
        StringBuilder sqlBuilder4 = new StringBuilder("SELECT COUNT(*) FROM aset WHERE kondisi = 'rusak'");

        try {
            Connection conn = ConnectSQL.getKoneksi();
            Statement st = conn.createStatement();
            int jumlahAset = 0;
            int jumlahFasilitas = 0;
            int jumlahPinjaman = 0;
            int jumlahRusak = 0;

            // Data jumlah aset
            ResultSet rs = st.executeQuery(sqlBuilder.toString());
            if (rs.next()) {
                jumlahAset = rs.getInt(1);
            }

            // Data jumlah fasilitas
            ResultSet rs2 = st.executeQuery(sqlBuilder2.toString());
            if (rs2.next()) {
                jumlahFasilitas = rs2.getInt(1);
            }

            // Data jumlah peminjaman aktif
            ResultSet rs3 = st.executeQuery(sqlBuilder3.toString());
            if (rs3.next()) {
                jumlahPinjaman = rs3.getInt(1);
            }

            // Data jumlah aset rusak
            ResultSet rs4 = st.executeQuery(sqlBuilder4.toString());
            if (rs4.next()) {
                jumlahRusak = rs4.getInt(1);
            }

            jmlAset.setText(String.valueOf(jumlahAset));
            jmlFasilitas.setText(String.valueOf(jumlahFasilitas));
            jmlPinjam.setText(String.valueOf(jumlahPinjaman));
            jmlRusak.setText(String.valueOf(jumlahRusak));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data aset: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void jumlahKategori() {
        javax.swing.DefaultListModel<String> model = new javax.swing.DefaultListModel<>();
        String sql = "SELECT kondisi, COUNT(*) AS jumlah FROM aset GROUP BY kondisi";
        
        try {
            Connection conn = ConnectSQL.getKoneksi();
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(sql.toString());
            System.out.println(st);
            boolean adaData = false;
            
            while (rs.next()) {
                // Terdapat data
                adaData = true;
                
                // Ambil data dari setiap kolom
                String kategori = rs.getString("kondisi");
                String jumlah = rs.getString("jumlah");

                // Format data menjadi satu String yang informatif
                String displayText = kategori + " = " + jumlah;

                // Tambahkan String yang sudah diformat ke dalam model
                model.addElement(displayText);
            }
            if (!adaData) {
                String displayText = "Tidak ada data aset ";
                model.addElement(displayText);
            }

            listKategori.setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data pemeliharaan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void tampilkanDataAset() {
        // Kolom untuk tabel
        String[] kolom = {"ID", "Nama Aset", "Kategori", "Kondisi", "Lokasi", "Hari Pemeliharaan"};
        DefaultTableModel model = new DefaultTableModel(null, kolom);
        jTable1.setModel(model);
        String today = getHariIni();
        
        StringBuilder sqlBuilder = new StringBuilder("SELECT id_aset, nama_aset, kategori, kondisi, lokasi, hari_pemeliharaan FROM aset WHERE hari_pemeliharaan=?");
        sqlBuilder.append(" ORDER BY id_aset ASC");

        try {
            Connection conn = ConnectSQL.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sqlBuilder.toString());
            pst.setString(1, today);

            System.out.println(pst);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_aset"),
                    rs.getString("nama_aset"),
                    rs.getString("kategori"),
                    rs.getString("kondisi"),
                    rs.getString("lokasi"),
                    rs.getString("hari_pemeliharaan")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data penjadwalan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void tampilkanDataPeminjaman() {
        // Kolom untuk tabel
        String[] kolom = {"ID", "ID Aset", "Nama Aset", "ID User", "Nama Peminjam", "Waktu Mulai", "Waktu Selesai", "Status"};
        DefaultTableModel model = new DefaultTableModel(null, kolom);
        jTable2.setModel(model);
        
        StringBuilder sqlBuilder = new StringBuilder("SELECT k.id_peminjaman, k.id_aset, a.nama_aset, k.id_user, u.nama, " +
             "k.tanggal_pinjam, k.tanggal_kembali, k.status " +
             "FROM peminjaman k " +
             "JOIN aset a ON k.id_aset = a.id_aset " +
             "JOIN user u ON k.id_user = u.id_user WHERE k.id_user=? AND k.status!='ditolak' AND k.status!='selesai'");
        java.util.List<String> conditions = new java.util.ArrayList<>();
        
        sqlBuilder.append(" ORDER BY id_peminjaman ASC");

        System.out.println(sqlBuilder);
        try {
            Connection conn = ConnectSQL.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sqlBuilder.toString());
            pst.setInt(1, userId);

            System.out.println(pst);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_peminjaman"),
                    rs.getInt("id_aset"),
                    rs.getString("nama_aset"),
                    rs.getInt("id_user"),
                    rs.getString("nama"),
                    rs.getString("tanggal_pinjam"),
                    rs.getString("tanggal_kembali"),
                    rs.getString("status")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data peminjaman: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jmlAset = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jmlFasilitas = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jmlPinjam = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jmlRusak = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listKategori = new javax.swing.JList<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(248, 249, 252));

        sidebarPanel.setBackground(new java.awt.Color(34, 45, 50));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("SARANA PRASARANA");

        btnDashboard.setBackground(new java.awt.Color(52, 152, 219));
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 394, Short.MAX_VALUE)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        contentPanel.setBackground(new java.awt.Color(248, 249, 252));

        lblHeader.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblHeader.setText("SELAMAT DATANG");

        lblTime.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTime.setText("Time");

        jPanel1.setBackground(new java.awt.Color(52, 152, 219));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Jumlah Aset");

        jmlAset.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jmlAset.setForeground(new java.awt.Color(255, 255, 255));
        jmlAset.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(61, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jmlAset)
                .addGap(23, 23, 23))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(29, 29, 29)
                .addComponent(jmlAset)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(51, 204, 0));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Jumlah Fasilitas");

        jmlFasilitas.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jmlFasilitas.setForeground(new java.awt.Color(255, 255, 255));
        jmlFasilitas.setText("0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(41, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jmlFasilitas)
                .addGap(23, 23, 23))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(29, 29, 29)
                .addComponent(jmlFasilitas)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(102, 0, 204));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Jumlah Peminjaman");

        jmlPinjam.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jmlPinjam.setForeground(new java.awt.Color(255, 255, 255));
        jmlPinjam.setText("0");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Aktif");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jmlPinjam)
                        .addGap(22, 22, 22))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jmlPinjam)))
                .addGap(43, 43, 43))
        );

        jPanel4.setBackground(new java.awt.Color(255, 0, 0));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Jumlah Kerusakan");

        jmlRusak.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jmlRusak.setForeground(new java.awt.Color(255, 255, 255));
        jmlRusak.setText("0");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Aktif");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addContainerGap(22, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jmlRusak)
                        .addGap(23, 23, 23))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jmlRusak))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(248, 249, 252));

        listKategori.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        listKategori.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(listKategori);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Data Kondisi Aset & Fasilitas");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setText("Data Aset Pemmeliharaan Hari Ini");

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
        jScrollPane2.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(jTable2);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("Data Peminjaman Anda");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 559, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblTime))
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGap(230, 230, 230)
                        .addComponent(lblHeader)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(lblHeader)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTime)
                .addGap(18, 18, 18)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 121, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(sidebarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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

    private void btnPemeliharaanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPemeliharaanActionPerformed
        new PemeliharaanForm(userId, userName, userRole).setVisible(true);

        this.dispose();
    }//GEN-LAST:event_btnPemeliharaanActionPerformed

    private void btnLaporanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLaporanActionPerformed
        new LaporanForm(userId, userName, userRole).setVisible(true);

        this.dispose();
    }//GEN-LAST:event_btnLaporanActionPerformed

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

    private void btnPeminjamanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPeminjamanActionPerformed
        new PeminjamanForm(userId, userName, userRole).setVisible(true);

        this.dispose();
    }//GEN-LAST:event_btnPeminjamanActionPerformed

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
            java.util.logging.Logger.getLogger(DashboardForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DashboardForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DashboardForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DashboardForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAset;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnLapor;
    private javax.swing.JButton btnLaporan;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPemeliharaan;
    private javax.swing.JButton btnPeminjaman;
    private javax.swing.JButton btnPinjamAset;
    private javax.swing.JButton btnPinjamFasil;
    private javax.swing.JButton btnUser;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JLabel jmlAset;
    private javax.swing.JLabel jmlFasilitas;
    private javax.swing.JLabel jmlPinjam;
    private javax.swing.JLabel jmlRusak;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblTime;
    private javax.swing.JList<String> listKategori;
    private javax.swing.JPanel sidebarPanel;
    // End of variables declaration//GEN-END:variables
}
