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
public class PinjamForm extends javax.swing.JFrame {

    // Simpan role pengguna untuk referensi
    protected String userRole;
    protected String userName;
    protected int userId;
    protected String menuKategori;
    
    /**
     * Creates new form DashboardForm
     */
    public PinjamForm(int id, String nama, String role, String menuKategori) {
        initComponents();
        setLocationRelativeTo(null); // Posisikan di tengah layar

        this.userId = id;
        this.userName = nama;
        this.userRole = role;
        this.menuKategori = menuKategori;
        
        if (menuKategori == "fasilitas"){
            lblHeader.setText("MENU PINJAM FASILITAS");    
        }

        // Panggil metode untuk memuat data ke dalam combo box
        muatFilterKategori();
        // Buat form text field ID tidak dapat di ubah
        txtId.setEditable(false);
        txtNama.setEditable(false);
        txtKategori.setEditable(false);
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
        
        if (menuKategori == "fasilitas"){
            jLabel2.setVisible(false);
            jLabel3.setVisible(false);
            filterKategori.setVisible(false);
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
        Color higlightColor = new Color(52,152,219);    // Warna menu yang dipilih

        // Buat array dari tombol menu lainnya
        JButton[] menuButtons = {btnDashboard, btnAset, btnPeminjaman, btnPemeliharaan, btnLaporan, btnLapor, btnPinjamAset, btnPinjamFasil, btnUser};

        if (menuKategori == "fasilitas") {
            btnPinjamAset.setBackground(originalColor);
            btnPinjamFasil.setBackground(higlightColor);
        }
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
        String[] kolom = {"ID", "Nama Aset", "Kategori"};
        DefaultTableModel model = new DefaultTableModel(null, kolom);
        jTable1.setModel(model);

        // [PERUBAHAN] Penjaga (guard clause) untuk mencegah error jika suatu saat
        // method ini terpanggil sebelum filter siap.
        if (filterKategori.getSelectedItem() == null) {
            return; // Jangan lakukan apa-apa jika filter belum siap.
        }
        
        String kategori = filterKategori.getSelectedItem().toString();
        
        StringBuilder sqlBuilder = new StringBuilder("SELECT id_aset, nama_aset, kategori FROM aset WHERE kondisi = 'baik' AND status='tersedia' AND kategori='fasilitas'");
        if (menuKategori == "aset"){
            sqlBuilder = new StringBuilder("SELECT id_aset, nama_aset, kategori FROM aset WHERE kondisi = 'baik' AND status='tersedia' AND kategori!='fasilitas'");        
        }
        java.util.List<String> conditions = new java.util.ArrayList<>();

        if (!kategori.equals("Semua Kategori")) {
            conditions.add("kategori = '" + kategori + "'");
        }

        if (!conditions.isEmpty()) {
            sqlBuilder.append(" AND ");
            sqlBuilder.append(String.join(" AND ", conditions));
        }
        
        sqlBuilder.append(" ORDER BY id_aset ASC");
        System.out.println(sqlBuilder);

        try {
            Connection conn = ConnectSQL.getKoneksi();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlBuilder.toString());

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_aset"),
                    rs.getString("nama_aset"),
                    rs.getString("kategori")
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
            String sql = "SELECT DISTINCT kategori FROM aset WHERE kategori!='fasilitas' ORDER BY kategori ASC";
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
        String namaAset = String.valueOf(model.getValueAt(barisTerpilih, 1));
        String kategori = String.valueOf(model.getValueAt(barisTerpilih, 2));

        // Isi data ke dalam JTextField
        txtId.setText(id);
        txtNama.setText(namaAset);
        txtKategori.setText(kategori);

    }
    
    /**
    * Mengosongkan semua field input pada form.
    */
    private void kosongkanForm() {
        txtId.setText("");
        txtNama.setText("");
        txtKategori.setText("");
        
    }
    
    /**
    * Menyimpan data ke tabel 'aset'.
    */
    private void simpanPinjam() {
        // Pastikan data telah terpilih
        if (txtId.getText().isEmpty()){
            System.out.println(txtId.getText());
            JOptionPane.showMessageDialog(this, "Mohon pilih data dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Logic yang sama dengan tambah data
        int asetId = Integer.parseInt(txtId.getText());
        String nama = txtNama.getText();
        String kategori = txtKategori.getText();
        LocalDateTime mulaiA = dateTimeMulai.getDateTimePermissive();
        LocalDateTime selesaiA = dateTimeSelesai.getDateTimePermissive();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String mulai = mulaiA.format(formatter);
        String selesai = selesaiA.format(formatter);
        
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon masukkan deskripsi kerusakan!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return; // Hentikan proses jika validasi gagal
        }

        // Siapkan query SQL menggunakan PreparedStatement untuk keamanan
        String sql = "INSERT INTO `peminjaman`(`id_user`, `id_aset`, `tanggal_pinjam`, `tanggal_kembali`, `status`) VALUES (?,?,?,?,?)";

        try {
            Connection conn = ConnectSQL.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql);

            // Atur nilai parameter berdasarkan data dari form
            pst.setInt(1, userId);
            pst.setInt(2, asetId);
            pst.setString(3, mulai);
            pst.setString(4, selesai);
            pst.setString(5, "menunggu");
            
            System.out.println(pst);
            // Berikan konfirmasi
            int input = JOptionPane.showConfirmDialog(null,"laporkan kerusakan pada " + nama+"?", "Lapor", JOptionPane.INFORMATION_MESSAGE);
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
        txtId = new javax.swing.JTextField();
        txtNama = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtKategori = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        btnPinjam = new javax.swing.JButton();
        dateTimeMulai = new com.github.lgooddatepicker.components.DateTimePicker();
        dateTimeSelesai = new com.github.lgooddatepicker.components.DateTimePicker();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

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

        btnPinjamAset.setBackground(new java.awt.Color(52, 152, 219));
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
        lblHeader.setText("MENU PINJAM ASET");

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

        jLabel6.setText("ID");

        jLabel7.setText("Nama");

        jLabel12.setText("Kategori");

        jLabel4.setText("Waktu Pengambilan");

        btnPinjam.setBackground(new java.awt.Color(51, 204, 0));
        btnPinjam.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnPinjam.setForeground(new java.awt.Color(242, 242, 242));
        btnPinjam.setText("Pinjam");
        btnPinjam.setBorder(null);
        btnPinjam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPinjamActionPerformed(evt);
            }
        });

        jLabel5.setText("Waktu Pengembalian");

        jLabel8.setText("Hingga");

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
                            .addComponent(jLabel6)
                            .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel12)
                                .addComponent(txtKategori, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE))
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addContainerGap(468, Short.MAX_VALUE))
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnPinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(72, 72, 72))))
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(filterKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(contentPanelLayout.createSequentialGroup()
                                        .addComponent(dateTimeMulai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel8))
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(dateTimeSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblHeader)
                .addGap(217, 217, 217))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(lblHeader, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTime)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(jLabel7))
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnPinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dateTimeMulai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateTimeSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(47, Short.MAX_VALUE))
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

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        isiFormDariTabel();
    }//GEN-LAST:event_jTable1MouseClicked

    private void btnPinjamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPinjamActionPerformed
        simpanPinjam();
    }//GEN-LAST:event_btnPinjamActionPerformed

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
            java.util.logging.Logger.getLogger(PinjamForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PinjamForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PinjamForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PinjamForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
    private javax.swing.JButton btnLapor;
    private javax.swing.JButton btnLaporan;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPemeliharaan;
    private javax.swing.JButton btnPeminjaman;
    private javax.swing.JButton btnPinjam;
    private javax.swing.JButton btnPinjamAset;
    private javax.swing.JButton btnPinjamFasil;
    private javax.swing.JButton btnUser;
    private javax.swing.JPanel contentPanel;
    private com.github.lgooddatepicker.components.DateTimePicker dateTimeMulai;
    private com.github.lgooddatepicker.components.DateTimePicker dateTimeSelesai;
    private javax.swing.JComboBox<String> filterKategori;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblTime;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtKategori;
    private javax.swing.JTextField txtNama;
    // End of variables declaration//GEN-END:variables
}
