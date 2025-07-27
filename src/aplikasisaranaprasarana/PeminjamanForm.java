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
import java.time.DayOfWeek;
import java.time.LocalDate;
/**
 *
 * @author N.A.I
 */
public class PeminjamanForm extends javax.swing.JFrame {

    // Simpan role pengguna untuk referensi
    protected String userRole;
    protected String userName;
    protected int userId;
    
    /**
     * Creates new form DashboardForm
     */
    public PeminjamanForm(int id, String nama, String role) {
        initComponents();
        setLocationRelativeTo(null); // Posisikan di tengah layar

        this.userId  = id;
        this.userRole = role;
        this.userName = nama;

        // Panggil metode untuk memuat data ke dalam combo box
        muatFilterStatus();
        // Buat form tidak dapat di ubah
        txtId.setEditable(false);
        txtAset.setEditable(false);
        txtPeminjam.setEditable(false); 
        txtStatus.setEditable(false);
        txtMulai.setEditable(false);
        txtSelesai.setEditable(false);
        jLabel9.setVisible(false);
        // Atur menu berdasarkan role
        aturMenuBerdasarkanRole();
        // Mengatur menu
        setupMenuStyles();
        // Memunculkan jam real time
        startRealTimeClock();
        // Memumnculkan data tabel
        tampilkanDataPeminjaman();
        waitPeminjaman();
        attachFilterListeners();
    }
    
    /**
     * Metode untuk mendeteksi perubahan pada filter
     */
    private void attachFilterListeners() {
        // Buat satu listener yang bisa dipakai bersama untuk efisiensi.
        ActionListener filterListener = e -> tampilkanDataPeminjaman();
        
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
            txtMulai.setVisible(false);
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
            lblTime.setText(getHariIni() + ", " + now.format(formatter) + " WIB");
        });

        // Memulai timer
        timer.start();
    }
    
    /**
     * Mengambil tanggal hari ini dan mengembalikan hari dalam Bahasa Indonesia.
     * @return String nama hari (e.g., "Senin", "Selasa").
     */
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

    private void tampilkanDataPeminjaman() {
        // Kolom untuk tabel
        String[] kolom = {"ID", "ID Aset", "Nama Aset", "ID User", "Nama Peminjam", "Waktu Mulai", "Waktu Selesai", "Status"};
        DefaultTableModel model = new DefaultTableModel(null, kolom);
        jTable1.setModel(model);

        // Penjaga (guard clause) untuk mencegah error jika suatu saat
        // method ini terpanggil sebelum filter siap.
        if (filterStatus.getSelectedItem() == null) {
            return; // Jangan lakukan apa-apa jika filter belum siap.
        }
        
        String status = filterStatus.getSelectedItem().toString();
        
        StringBuilder sqlBuilder = new StringBuilder("SELECT k.id_peminjaman, k.id_aset, a.nama_aset, k.id_user, u.nama, " +
             "k.tanggal_pinjam, k.tanggal_kembali, k.status " +
             "FROM peminjaman k " +
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
        
        sqlBuilder.append(" ORDER BY id_peminjaman ASC");

        System.out.println(sqlBuilder);
        try {
            Connection conn = ConnectSQL.getKoneksi();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlBuilder.toString());

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
    
    private void waitPeminjaman() {
        javax.swing.DefaultListModel<String> model = new javax.swing.DefaultListModel<>();
        String today = getHariIni();
        String sql = "SELECT k.id_peminjaman, k.id_aset, a.nama_aset, k.id_user, u.nama, " +
             "k.tanggal_pinjam, k.status " +
             "FROM peminjaman k " +
             "JOIN aset a ON k.id_aset = a.id_aset " +
             "JOIN user u ON k.id_user = u.id_user WHERE k.status=?";
        
        try {
            Connection conn = ConnectSQL.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, "menunggu");

            System.out.println(pst);
            ResultSet rs = pst.executeQuery();
            boolean adaData = false;
            
            while (rs.next()) {
                // Terdapat data
                adaData = true;

                // Ambil data menggunakan nama kolomnya saja, bukan alias tabel.
                int id = rs.getInt("id_peminjaman");
                String nama = rs.getString("nama_aset");
                String user = rs.getString("nama");
                String tanggal = rs.getString("tanggal_pinjam");

                // Format data menjadi satu String yang informatif
                String displayText = "ID: " + id + " - " + user + " = " + nama +"("+tanggal+")";

                // Tambahkan String yang sudah diformat ke dalam model
                model.addElement(displayText);
            }
            if (!adaData) {
                String displayText = "Tidak ada peminjaman baru";
                model.addElement(displayText);
            }

            waitList.setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data peminjaman: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
    * Mengambil data status unik dari database dan memuatnya ke dalam filterStatus.
    */
    private void muatFilterStatus() {
        try {
            Connection conn = ConnectSQL.getKoneksi();
            Statement st = conn.createStatement();
            String sql = "SELECT DISTINCT status FROM peminjaman ORDER BY status ASC";
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
        String namaPeminjam = String.valueOf(model.getValueAt(barisTerpilih, 4));
        String mulai = String.valueOf(model.getValueAt(barisTerpilih, 5));
        String selesai = String.valueOf(model.getValueAt(barisTerpilih, 6));
        String status = String.valueOf(model.getValueAt(barisTerpilih, 7));

        // Isi data ke dalam JTextField
        txtId.setText(id);
        txtAset.setText(namaAset);
        txtPeminjam.setText(namaPeminjam);
        txtStatus.setText(status);
        txtMulai.setText(mulai);
        txtSelesai.setText(selesai);
        jLabel9.setText(idAset);
    }
    
    /**
    * Mengosongkan semua field input pada form.
    */
    private void kosongkanForm() {
        txtId.setText("");
        txtAset.setText("");
        txtPeminjam.setText("");
        txtStatus.setText("");
        txtMulai.setText("");
        txtSelesai.setText("");
    }
    
    
    private void setStatus(String neoStatus) {
        // Pastikan data telah terpilih
        if (txtId.getText().isEmpty()){
            System.out.println(txtId.getText());
            JOptionPane.showMessageDialog(this, "Mohon pilih data dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = Integer.parseInt(txtId.getText());
        int idAset = Integer.parseInt(jLabel9.getText());
        String nama = txtAset.getText();
        
        String sql = "UPDATE `peminjaman` SET `status`=? WHERE `id_peminjaman`=?";
        String sql2 = "UPDATE `aset` SET `status`='tersedia' WHERE id_aset=?";
        // Mengubah kondisi aset sesuai status kerusakan
        if (neoStatus == "disetujui") {
            sql2 = "UPDATE `aset` SET `status`='tidak tersedia' WHERE id_aset=?";
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
                    tampilkanDataPeminjaman();
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
        txtAset = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        btnSelesai = new javax.swing.JButton();
        btnAcc = new javax.swing.JButton();
        btnTolak = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        waitList = new javax.swing.JList<>();
        jLabel8 = new javax.swing.JLabel();
        txtMulai = new javax.swing.JTextField();
        txtPeminjam = new javax.swing.JTextField();
        txtSelesai = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtStatus = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

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

        btnPeminjaman.setBackground(new java.awt.Color(52, 152, 219));
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
        lblHeader.setText("MENU PEMINJAMAN");

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

        jLabel7.setText("Nama Aset");

        jLabel11.setText("Waktu Mulai");

        jLabel12.setText("Nama Peminjam");

        btnSelesai.setBackground(new java.awt.Color(52, 152, 219));
        btnSelesai.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSelesai.setForeground(new java.awt.Color(242, 242, 242));
        btnSelesai.setText("Selesai");
        btnSelesai.setBorder(null);
        btnSelesai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelesaiActionPerformed(evt);
            }
        });

        btnAcc.setBackground(new java.awt.Color(255, 255, 0));
        btnAcc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAcc.setForeground(new java.awt.Color(153, 153, 153));
        btnAcc.setText("Acc");
        btnAcc.setBorder(null);
        btnAcc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAccActionPerformed(evt);
            }
        });

        btnTolak.setBackground(new java.awt.Color(255, 0, 0));
        btnTolak.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnTolak.setForeground(new java.awt.Color(242, 242, 242));
        btnTolak.setText("Tolak");
        btnTolak.setBorder(null);
        btnTolak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTolakActionPerformed(evt);
            }
        });

        waitList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(waitList);

        jLabel8.setText("Permintaan Peminjaman");

        jLabel4.setText("Waktu Selesai");

        jLabel5.setText("Status");

        jLabel9.setText("jLabel9");

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblHeader)
                .addGap(199, 199, 199))
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTime))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(filterStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addComponent(txtMulai, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addGap(18, 18, 18)
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5)))
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtAset, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7))
                                .addGap(18, 18, 18)
                                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(txtPeminjam, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(btnSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnTolak, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(jLabel9)))
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                                .addComponent(jLabel8)
                                .addGap(241, 241, 241))
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane2)
                                .addContainerGap())))))
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
                .addGap(12, 12, 12)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtAset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPeminjam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMulai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnTolak, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(44, Short.MAX_VALUE))
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

    private void btnAccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAccActionPerformed
        // Buat perubahan kondisi perawatan ke database dengan fungsi setKondisi 
        setStatus("disetujui");
    }//GEN-LAST:event_btnAccActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        isiFormDariTabel();
    }//GEN-LAST:event_jTable1MouseClicked

    private void btnSelesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelesaiActionPerformed
        // Buat perubahan kondisi baik ke database dengan fungsi setKondisi 
        setStatus("selesai");
    }//GEN-LAST:event_btnSelesaiActionPerformed

    private void btnTolakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTolakActionPerformed
        // Buat perubahan kondisi rusak ke database dengan fungsi setKondisi 
        setStatus("ditolak");
    }//GEN-LAST:event_btnTolakActionPerformed

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
            java.util.logging.Logger.getLogger(PeminjamanForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PeminjamanForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PeminjamanForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PeminjamanForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
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
    private javax.swing.JButton btnAcc;
    private javax.swing.JButton btnAset;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnLapor;
    private javax.swing.JButton btnLaporan;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPemeliharaan;
    private javax.swing.JButton btnPeminjaman;
    private javax.swing.JButton btnPinjamAset;
    private javax.swing.JButton btnPinjamFasil;
    private javax.swing.JButton btnSelesai;
    private javax.swing.JButton btnTolak;
    private javax.swing.JButton btnUser;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JComboBox<String> filterStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblTime;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JTextField txtAset;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtMulai;
    private javax.swing.JTextField txtPeminjam;
    private javax.swing.JTextField txtSelesai;
    private javax.swing.JTextField txtStatus;
    private javax.swing.JList<String> waitList;
    // End of variables declaration//GEN-END:variables
}
