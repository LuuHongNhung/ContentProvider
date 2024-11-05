package com.example.btcontentprovider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 101;
    private static final int CONTACT_PERMISSION_CODE = 102;
    private static final int CALL_LOG_PERMISSION_CODE = 103;

    private ListView listViewMessages;
    private Button btnShowMessages;
    private Button btnShowContacts;
    private Button btnShowCallLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        listViewMessages = findViewById(R.id.listViewMessages);
        btnShowMessages = findViewById(R.id.btnaccessmessages);
        btnShowContacts = findViewById(R.id.btnshowallcontact);
        btnShowCallLog = findViewById(R.id.btnaccesscalllog);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnShowMessages.setOnClickListener(v -> {
            if (checkPermission(Manifest.permission.READ_SMS, SMS_PERMISSION_CODE)) {
                showMessages();
            }
        });

        btnShowContacts.setOnClickListener(v -> {
            if (checkPermission(Manifest.permission.READ_CONTACTS, CONTACT_PERMISSION_CODE)) {
                showContacts();
            }
        });

        btnShowCallLog.setOnClickListener(v -> {
            if (checkPermission(Manifest.permission.READ_CALL_LOG, CALL_LOG_PERMISSION_CODE)) {
                showCallLog();
            }
        });
    }

    private void showCallLog() {
        ArrayList<String> callLogs = new ArrayList<>();
        Uri uri = CallLog.Calls.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));

                String log = "Number: " + number + ", Type: " + type + ", Date: " + date + ", Duration: " + duration;
                callLogs.add(log);
            }
            cursor.close();
        } else {
            Toast.makeText(this, "No call logs found", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, callLogs);
        listViewMessages.setAdapter(adapter);
    }

    private boolean checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case SMS_PERMISSION_CODE:
                    showMessages();
                    break;
                case CONTACT_PERMISSION_CODE:
                    showContacts();
                    break;
                case CALL_LOG_PERMISSION_CODE:
                    showCallLog();
                    break;
            }
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessages() {
        ArrayList<String> messages = new ArrayList<>();
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                messages.add(body);
            }
            cursor.close();
        } else {
            Toast.makeText(this, "No messages found", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        listViewMessages.setAdapter(adapter);
    }

    private void showContacts() {
        ArrayList<String> contacts = new ArrayList<>();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                contacts.add(name);
            }
            cursor.close();
        } else {
            Toast.makeText(this, "No contacts found", Toast.LENGTH_SHORT).show();