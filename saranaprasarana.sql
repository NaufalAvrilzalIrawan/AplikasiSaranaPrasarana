-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 17, 2025 at 02:15 PM
-- Server version: 10.4.24-MariaDB
-- PHP Version: 8.2.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `saranaprasarana`
--

-- --------------------------------------------------------

--
-- Table structure for table `aset`
--

CREATE TABLE `aset` (
  `id_aset` int(4) NOT NULL,
  `nama_aset` varchar(100) NOT NULL,
  `kategori` varchar(100) NOT NULL,
  `kondisi` enum('baik','rusak','perawatan') NOT NULL,
  `lokasi` varchar(100) NOT NULL,
  `tanggal_beli` date DEFAULT NULL,
  `hari_pemeliharaan` varchar(20) DEFAULT NULL,
  `kode_inventaris` varchar(150) DEFAULT NULL,
  `status` enum('tersedia','tidak tersedia') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `aset`
--

INSERT INTO `aset` (`id_aset`, `nama_aset`, `kategori`, `kondisi`, `lokasi`, `tanggal_beli`, `hari_pemeliharaan`, `kode_inventaris`, `status`) VALUES
(1, 'Aula Tari', 'Fasilitas', 'baik', 'Gedung B', NULL, 'Jumat', '', 'tersedia'),
(3, 'Proyektor Epson EB-X500', 'Elektronik', 'baik', 'Gudang A', '2023-01-15', 'Kamis', 'INV/2023/01/001', 'tersedia'),
(4, 'Laptop Dell Latitude 5420', 'Elektronik', 'baik', 'Ruang Rapat', '2022-11-20', 'Selasa', 'INV/2022/11/002', 'tersedia'),
(5, 'Meja Rapat Kayu Jati', 'Furnitur', 'perawatan', 'Ruang Rapat', '2025-07-06', 'Sabtu', 'INV/2020/05/003', 'tersedia'),
(6, 'Kursi Kantor Ergonomis', 'Furnitur', 'baik', 'Gudang B', '2025-07-06', 'Tidak ada', 'INV/2021/02/004', 'tidak tersedia'),
(7, 'Laptop', 'Elektronik', 'baik', 'Kelas 2A', '2021-02-22', 'Senin', 'INV/2021/02/005', 'tidak tersedia'),
(13, 'Ruang Musik', 'Fasilitas', 'baik', 'Gedung A', NULL, 'Kamis', '', 'tidak tersedia'),
(15, 'Lapang Voli', 'Fasilitas', 'baik', 'Gedung B', NULL, 'Jumat', '', 'tersedia');

-- --------------------------------------------------------

--
-- Table structure for table `kerusakan`
--

CREATE TABLE `kerusakan` (
  `id_laporan` int(4) NOT NULL,
  `id_user` int(4) NOT NULL,
  `id_aset` int(4) NOT NULL,
  `deskripsi` text NOT NULL,
  `tanggal_lapor` datetime NOT NULL,
  `status` enum('terlapor','ditangani','selesai') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `kerusakan`
--

INSERT INTO `kerusakan` (`id_laporan`, `id_user`, `id_aset`, `deskripsi`, `tanggal_lapor`, `status`) VALUES
(1, 4, 7, 'Keyboard nya ada yang lepas', '2025-07-06 22:10:12', 'selesai'),
(2, 5, 1, 'Ada tikus. Mamah aku takut TT', '2025-07-06 22:16:22', 'selesai'),
(3, 5, 13, 'Ada lubang di tembok sebesar truk', '2025-07-06 22:46:25', 'selesai'),
(6, 6, 15, 'Ada kuda backflip', '2025-07-09 22:31:39', 'selesai');

-- --------------------------------------------------------

--
-- Table structure for table `peminjaman`
--

CREATE TABLE `peminjaman` (
  `id_peminjaman` int(4) NOT NULL,
  `id_user` int(4) NOT NULL,
  `id_aset` int(4) NOT NULL,
  `tanggal_pinjam` datetime NOT NULL,
  `tanggal_kembali` datetime DEFAULT NULL,
  `status` enum('menunggu','disetujui','ditolak','selesai') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `peminjaman`
--

INSERT INTO `peminjaman` (`id_peminjaman`, `id_user`, `id_aset`, `tanggal_pinjam`, `tanggal_kembali`, `status`) VALUES
(1, 7, 4, '2025-07-10 08:00:00', '2025-07-10 14:00:00', 'selesai'),
(2, 4, 6, '2025-07-01 00:11:03', '2025-07-09 00:11:03', 'disetujui'),
(3, 7, 3, '2025-07-17 10:00:00', '2025-07-17 12:30:00', 'menunggu');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id_user` int(4) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('admin','petugas','user') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id_user`, `nama`, `username`, `password`, `role`) VALUES
(1, 'Naufal AI', 'N.A.I', 'abcde', 'admin'),
(4, 'Gazi', 'GAZI', '12345', 'user'),
(5, 'Adam', 'a', 'a', 'petugas'),
(6, 'Bima Admin', 'q', 'q', 'admin'),
(7, 'Chandra User', 'z', 'z', 'user'),
(8, 'ss', 'ss', 'ss', 'petugas');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `aset`
--
ALTER TABLE `aset`
  ADD PRIMARY KEY (`id_aset`);

--
-- Indexes for table `kerusakan`
--
ALTER TABLE `kerusakan`
  ADD PRIMARY KEY (`id_laporan`),
  ADD KEY `FK_laporan_user` (`id_user`),
  ADD KEY `FK_laporan_aset` (`id_aset`);

--
-- Indexes for table `peminjaman`
--
ALTER TABLE `peminjaman`
  ADD PRIMARY KEY (`id_peminjaman`),
  ADD KEY `FK_peminjaman_user` (`id_user`),
  ADD KEY `FK_peminjaman_aset` (`id_aset`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id_user`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `aset`
--
ALTER TABLE `aset`
  MODIFY `id_aset` int(4) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `kerusakan`
--
ALTER TABLE `kerusakan`
  MODIFY `id_laporan` int(4) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `peminjaman`
--
ALTER TABLE `peminjaman`
  MODIFY `id_peminjaman` int(4) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id_user` int(4) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `kerusakan`
--
ALTER TABLE `kerusakan`
  ADD CONSTRAINT `FK_laporan_aset` FOREIGN KEY (`id_aset`) REFERENCES `aset` (`id_aset`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_laporan_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `peminjaman`
--
ALTER TABLE `peminjaman`
  ADD CONSTRAINT `FK_peminjaman_aset` FOREIGN KEY (`id_aset`) REFERENCES `aset` (`id_aset`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_peminjaman_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
