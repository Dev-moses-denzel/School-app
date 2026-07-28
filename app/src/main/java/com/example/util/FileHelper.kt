package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.StudentResult
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object FileHelper {

    fun openPdfFile(context: Context, pdfFile: File) {
        try {
            val authority = "${context.packageName}.fileprovider"
            val uri: Uri = FileProvider.getUriForFile(context, authority, pdfFile)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Open Result PDF"))
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open PDF viewer: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun sharePdfFile(context: Context, pdfFile: File, result: StudentResult) {
        try {
            val authority = "${context.packageName}.fileprovider"
            val uri: Uri = FileProvider.getUriForFile(context, authority, pdfFile)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Official Result - ${result.studentName} (${result.rollNumber})")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Official Online Result for ${result.studentName} (${result.rollNumber}). Board: ${result.boardOrUniversity}. CGPA: ${result.cgpa}."
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Result PDF"))
        } catch (e: Exception) {
            Toast.makeText(context, "Error sharing PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun printPdfFile(context: Context, pdfFile: File, jobName: String) {
        try {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            if (printManager == null) {
                Toast.makeText(context, "Print service not available on device", Toast.LENGTH_SHORT).show()
                return
            }

            val pda = object : PrintDocumentAdapter() {
                override fun onLayout(
                    oldAttributes: PrintAttributes?,
                    newAttributes: PrintAttributes?,
                    cancellationSignal: CancellationSignal?,
                    callback: LayoutResultCallback?,
                    extras: Bundle?
                ) {
                    if (cancellationSignal?.isCanceled == true) {
                        callback?.onLayoutCancelled()
                        return
                    }

                    val info = PrintDocumentInfo.Builder(jobName)
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(1)
                        .build()

                    callback?.onLayoutFinished(info, true)
                }

                override fun onWrite(
                    pages: Array<out PageRange>?,
                    destination: ParcelFileDescriptor?,
                    cancellationSignal: CancellationSignal?,
                    callback: WriteResultCallback?
                ) {
                    var input: FileInputStream? = null
                    var output: FileOutputStream? = null
                    try {
                        input = FileInputStream(pdfFile)
                        output = FileOutputStream(destination?.fileDescriptor)

                        val buf = ByteArray(1024)
                        var bytesRead: Int
                        while (input.read(buf).also { bytesRead = it } > 0) {
                            if (cancellationSignal?.isCanceled == true) {
                                callback?.onWriteCancelled()
                                return
                            }
                            output.write(buf, 0, bytesRead)
                        }

                        callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                    } catch (e: Exception) {
                        callback?.onWriteFailed(e.message)
                    } finally {
                        try {
                            input?.close()
                            output?.close()
                        } catch (e: Exception) {
                        }
                    }
                }
            }

            printManager.print(jobName, pda, null)
        } catch (e: Exception) {
            Toast.makeText(context, "Printing error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun copyToClipboard(context: Context, text: String, label: String = "Verification Hash") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }
}
