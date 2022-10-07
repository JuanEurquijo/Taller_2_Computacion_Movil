package com.edu.compumovil.taller2.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.edu.compumovil.taller2.adapters.ContactsAdapter;
import com.edu.compumovil.taller2.databinding.ActivityContactsBinding;
import com.edu.compumovil.taller2.utils.AlertsHelper;
import com.edu.compumovil.taller2.utils.PermissionHelper;

public class ContactsActivity extends BasicActivity {
    private ActivityContactsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        permissionHelper.getContactsPermission(this);
    }

    private void initListContacts() {
        Cursor cursor = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                ContactsAdapter.PROYECTION,
                null,
                null,
                null);
        ContactsAdapter adapter = new ContactsAdapter(this, cursor, 0);
        binding.contactsList.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionHelper.PERMISSIONS_REQUEST_READ_CONTACTS) {
            // Verificar que se tenga el permiso
            if (permissionHelper.isMContactsPermissionGranted()) {
                initListContacts();
            } else {
                // Obtener los permisos
                permissionHelper.getContactsPermission(this);
            }
        }
    }
}
