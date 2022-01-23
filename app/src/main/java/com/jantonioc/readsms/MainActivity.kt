package com.jantonioc.readsms

import android.Manifest
import android.app.ListActivity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.cursoradapter.widget.CursorAdapter
import java.util.*

class MainActivity : ListActivity() {
    val sms = Uri.parse("content://sms")
    private val PERMISSIONS_REQUEST_READ_SMS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            readSMS()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_SMS),
                PERMISSIONS_REQUEST_READ_SMS)
        }

    }

        object smsColumns {
            val ID = "_id"
            val ADDRESS = "address"
            val DATE = "date"
            val BODY = "body"
        }

        private inner class SmsCursorAdapter(context: Context, c: Cursor?, autoRequery: Boolean) :
            CursorAdapter(context, c, autoRequery) {

            override fun newView(context: Context, cursor: Cursor, viewGroup: ViewGroup): View {
                return View.inflate(context, R.layout.activity_main, null)
            }

            override fun bindView(view: View, context: Context, cursor: Cursor) {
                view.findViewById<TextView>(R.id.smsOrigin).text =
                    cursor.getString(cursor.getColumnIndexOrThrow(smsColumns.ADDRESS))
                view.findViewById<TextView>(R.id.smsBody).text =
                    cursor.getString(cursor.getColumnIndexOrThrow(smsColumns.BODY))
                view.findViewById<TextView>(R.id.smsDate).text =
                    Date(cursor.getLong(cursor.getColumnIndexOrThrow(smsColumns.DATE))).toString()
            }

        }

        private fun readSMS() {
            val cursor = contentResolver.query(
                sms,
                arrayOf(
                    smsColumns.ID,
                    smsColumns.ADDRESS,
                    smsColumns.DATE,
                    smsColumns.BODY
                ),
                null,
                null,
                smsColumns.DATE + " DESC"
            )
            val adapter = SmsCursorAdapter(this, cursor, true)
            listAdapter = adapter
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            PERMISSIONS_REQUEST_READ_SMS -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    readSMS()
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()

                return
            }
        }
    }

    }