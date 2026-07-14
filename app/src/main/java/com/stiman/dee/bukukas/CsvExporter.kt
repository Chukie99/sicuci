package com.stiman.dee.bukukas

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {
    fun exportToCsv(context: Context, transactions: List<Transaction>): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "buku_kas_stiman_dee_$timestamp.csv"
        val exportDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "exports")

        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }

        val file = File(exportDir, fileName)

        FileWriter(file).use { writer ->
            // CSV Header
            writer.append("Tanggal,Waktu,Kategori,Jenis,Catatan,Jumlah\n")

            // Sort by date DESC, then time DESC
            val sorted = transactions.sortedWith(
                compareByDescending<Transaction> { it.date }
                    .thenByDescending { it.time }
            )

            for (t in sorted) {
                val type = if (t.type == "income") "Pemasukan" else "Pengeluaran"
                val note = escapeCsv(t.note)
                val category = escapeCsv(t.category)
                writer.append("${t.date},${t.time},$category,$type,$note,${t.amount}\n")
            }
        }

        return file
    }

    fun shareCsv(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Laporan SiCuci")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Bagikan Laporan CSV"))
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }
}
