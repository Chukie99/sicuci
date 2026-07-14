<div align="center">

# 🚿 SiCuci

### Kasir Digital Cuci Motor & Mobil

**Aplikasi antrian, kasir, dan pencatatan keuangan untuk bisnis cuci motor dan mobil**

[![Android](https://img.shields.io/badge/Android-26%2B-brightgreen?style=flat&logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-blue?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024-purple?style=flat)](https://developer.android.com/jetpack/compose)
[![Room DB](https://img.shields.io/badge/Room%20DB-2.6-orange?style=flat)](https://developer.android.com/training/data-storage/room)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

</div>

## ✨ Fitur Utama

<table>
<tr>
<td>

### 📋 Manajemen Antrian
- Tambah antrian cuci motor & mobil
- Status: Menunggu → Dicuci → Selesai → Bayar
- Nomor antrian otomatis
- Filter & cari berdasarkan plat nomor

</td>
<td>

### 💰 Kasir Digital
- Pembayaran QRIS & Tunai
- Perhitungan kembalian otomatis
- QR Code QRIS real-time
- Cetak struk digital

</td>
</tr>
<tr>
<td>

### 📊 Laporan Keuangan
- Dashboard harian & keseluruhan
- Grafik pendapatan 7 hari
- Filter: Hari Ini, Minggu, Bulan, Pilih Bulan
- Export ke CSV

</td>
<td>

### 👤 Riwayat Pelanggan
- Cari riwayat berdasarkan plat nomor
- Total kunjungan & total belanja
- History lengkap setiap pelanggan

</td>
</tr>
</table>

## 📸 Screenshots

<div align="center">

| Queue Screen | Dashboard | Report |
|:---:|:---:|:---:|
| *Antrian Cuci Motor & Mobil* | *Dashboard Keuangan* | *Laporan Detail* |

</div>

## 🛠️ Tech Stack

| Komponen | Teknologi |
|----------|-----------|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose |
| **Architecture** | MVVM |
| **Database** | Room (SQLite) |
| **Async** | Kotlin Coroutines + Flow |
| **QR Code** | ZXing |
| **DI** | Manual (ViewModelProvider) |

## 📦 Instalasi

### Prasyarat
- Android Studio Hedgehog (2023.1.1) atau lebih baru
- JDK 17
- Device/Emulator dengan Android 8.0 (API 26) atau lebih baru

### Cara Install

```bash
# 1. Clone repository
git clone https://github.com/Chukie99/sicuci.git

# 2. Buka project di Android Studio
# File → Open → pilih folder sicuci

# 3. Tunggu Gradle sync selesai

# 4. Run app
# Klik tombol Run ▶️ atau Shift+F10
```

### Build APK

```bash
# Debug APK
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease
```

APK akan tersedia di: `app/build/outputs/apk/`

## 📁 Struktur Project

```
sicuci/
├── app/
│   ├── src/main/
│   │   ├── java/com/stiman/dee/bukukas/
│   │   │   ├── ui/                    # Compose UI
│   │   │   │   ├── DashboardScreen.kt
│   │   │   │   ├── QueueScreen.kt
│   │   │   │   ├── ReportScreen.kt
│   │   │   │   ├── SettingsScreen.kt
│   │   │   │   ├── CustomerHistoryScreen.kt
│   │   │   │   ├── PaymentDialog.kt
│   │   │   │   ├── AddOrderDialog.kt
│   │   │   │   ├── IncomeDialog.kt
│   │   │   │   ├── ExpenseDialog.kt
│   │   │   │   ├── Theme.kt
│   │   │   │   └── Color.kt
│   │   │   ├── TransactionViewModel.kt  # ViewModel
│   │   │   ├── TransactionDao.kt        # Database DAO
│   │   │   ├── CustomerOrderDao.kt
│   │   │   ├── AppDatabase.kt           # Room Database
│   │   │   ├── Transaction.kt           # Entity
│   │   │   ├── CustomerOrder.kt
│   │   │   ├── PriceManager.kt          # SharedPreferences
│   │   │   ├── CsvExporter.kt           # Export CSV
│   │   │   ├── DatabaseBackup.kt        # Backup/Restore
│   │   │   ├── QrCodeGenerator.kt       # QR Code
│   │   │   ├── ServiceOptions.kt
│   │   │   ├── SplashActivity.kt
│   │   │   └── MainActivity.kt
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

## 🎨 Screenshots App

| Feature | Description |
|---------|-------------|
| 🏠 **Splash Screen** | Logo animasi SiCuci |
| 📋 **Antrian** | Kelola antrian cuci motor |
| 💳 **Pembayaran** | Bayar QRIS atau Tunai |
| 📊 **Dashboard** | Ringkasan keuangan harian |
| 📈 **Grafik** | Chart pendapatan 7 hari |
| 📑 **Laporan** | Filter periode & export CSV |
| ⚙️ **Pengaturan** | Atur harga & QRIS |
| 💾 **Backup** | Backup & restore database |
| 👤 **Pelanggan** | Riwayat berdasarkan plat |

## 🔧 Konfigurasi

### Mengatur Harga Layanan
1. Buka menu **Harga** (Settings)
2. Edit harga setiap layanan
3. Tap **Simpan Semua**

### Mengatur QRIS
1. Buka menu **Harga** (Settings)
2. Masukkan kode/link QRIS
3. Tap **Simpan Semua**

### Backup Data
1. Buka menu **Harga** (Settings)
2. Scroll ke bawah → **Backup & Restore**
3. Tap **Backup** → Pilih tempat simpan

## 📝 Changelog

### v1.0.0 (2026-07-14)
- ✅ Initial release
- ✅ Queue management (Motor & Mobil)
- ✅ Transaction tracking
- ✅ Dashboard & Reports
- ✅ QRIS & Cash payment
- ✅ CSV export
- ✅ Backup/Restore
- ✅ Customer history
- ✅ Support cuci mobil (Sedan, SUV, MPV, dll)

## 🤝 Contributing

Kontribusi sangat welcome! 

1. Fork repository
2. Buat branch baru (`git checkout -b feature/fitur-baru`)
3. Commit perubahan (`git commit -m 'Add fitur baru'`)
4. Push ke branch (`git push origin feature/fitur-baru`)
5. Buka Pull Request

## 📄 License

Project ini menggunakan license [MIT License](LICENSE).

## 👨‍💻 Author

**Chukie99**
- GitHub: [@Chukie99](https://github.com/Chukie99)

## ⭐ Star History

Kalau project ini berguna, bantu star ya! ⭐

---

<div align="center">

**Made with ❤️ for Indonesian Motor Wash Businesses**

</div>
