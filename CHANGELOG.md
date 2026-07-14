# Changelog

Semua perubahan penting pada SiCuci akan didokumentasikan di file ini.

Format berdasarkan [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [1.0.0] - 2026-07-14

### ✨ Fitur Baru
- **Splash Screen** - Logo animasi saat membuka app
- **Manajemen Antrian** - Tambah, mulai, selesai, dan bayar antrian
- **Kasir Digital** - Pembayaran QRIS dan Tunai dengan QR Code
- **Dashboard** - Ringkasan keuangan harian dan keseluruhan
- **Grafik** - Chart pendapatan vs pengeluaran 7 hari terakhir
- **Laporan** - Filter periode (Hari Ini, Minggu, Bulan, Pilih Bulan, Semua)
- **Riwayat Pelanggan** - Cari berdasarkan plat nomor
- **Backup & Restore** - Cadangkan dan pulihkan database
- **Export CSV** - Ekspor transaksi ke file CSV
- **Pengaturan Harga** - Ubah harga layanan dan QRIS

### 🔧 Perbaikan Bug
- Filter "Minggu Ini" sekarang benar (7 hari, bukan 8)
- CoroutineScope diganti lifecycleScope untuk mencegah memory leak
- Database migration tidak lagi menghapus data saat upgrade
- CSV export handle karakter khusus (quote, comma, newline)
- Settings screen state reset setelah save
- Time format aman dari crash
- NPE prevention di date formatting

### 🎨 UI/UX
- Dark theme profesional
- Snackbar dengan undo untuk hapus data
- Search bar di antrian dan laporan
- Statistik pelanggan

---

## [Unreleased]

### Planned
- [ ] Notifikasi untuk antrian
- [ ] Multi-user / login
- [ ] Cloud sync
- [ ] Print struk via Bluetooth
- [ ] Dark/Light theme toggle
- [ ] Widget Android
