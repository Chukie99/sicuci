package com.stiman.dee.bukukas

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object DatabaseBackup {

    fun backupDatabase(context: Context): Result<File> {
        return try {
            val dbFile = context.getDatabasePath("buku_kas_stiman_dee.db")
            val dbWalFile = File(dbFile.path + "-wal")
            val dbShmFile = File(dbFile.path + "-shm")

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val backupDir = File(context.getExternalFilesDir(null), "backups")
            if (!backupDir.exists()) backupDir.mkdirs()

            val backupFile = File(backupDir, "buku_kas_backup_$timestamp.zip")

            ZipOutputStream(FileOutputStream(backupFile)).use { zipOut ->
                // Add main database file
                if (dbFile.exists()) {
                    ZipEntry("buku_kas_stiman_dee.db").let { entry ->
                        zipOut.putNextEntry(entry)
                        FileInputStream(dbFile).use { it.copyTo(zipOut) }
                        zipOut.closeEntry()
                    }
                }

                // Add WAL file if exists
                if (dbWalFile.exists()) {
                    ZipEntry("buku_kas_stiman_dee.db-wal").let { entry ->
                        zipOut.putNextEntry(entry)
                        FileInputStream(dbWalFile).use { it.copyTo(zipOut) }
                        zipOut.closeEntry()
                    }
                }

                // Add SHM file if exists
                if (dbShmFile.exists()) {
                    ZipEntry("buku_kas_stiman_dee.db-shm").let { entry ->
                        zipOut.putNextEntry(entry)
                        FileInputStream(dbShmFile).use { it.copyTo(zipOut) }
                        zipOut.closeEntry()
                    }
                }
            }

            Result.success(backupFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun restoreDatabase(context: Context, backupUri: Uri): Result<Unit> {
        return try {
            val dbFile = context.getDatabasePath("buku_kas_stiman_dee.db")
            val dbDir = dbFile.parentFile ?: return Result.failure(Exception("Database directory not found"))

            context.contentResolver.openInputStream(backupUri)?.use { inputStream ->
                ZipInputStream(inputStream).use { zipIn ->
                    var entry = zipIn.nextEntry
                    while (entry != null) {
                        val outFile = File(dbDir, entry.name)
                        FileOutputStream(outFile).use { out ->
                            zipIn.copyTo(out)
                        }
                        zipIn.closeEntry()
                        entry = zipIn.nextEntry
                    }
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun shareBackup(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/zip"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Backup SiCuci")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Bagikan Backup"))
    }

    fun getBackupFiles(context: Context): List<File> {
        val backupDir = File(context.getExternalFilesDir(null), "backups")
        return if (backupDir.exists()) {
            backupDir.listFiles { file -> file.extension == "zip" }
                ?.sortedByDescending { it.lastModified() }
                ?: emptyList()
        } else {
            emptyList()
        }
    }
}
