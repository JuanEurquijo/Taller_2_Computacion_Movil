package com.edu.compumovil.taller2.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.edu.compumovil.taller2.R;
import com.edu.compumovil.taller2.databinding.ContactsAdapterBinding;

public class ContactsAdapter extends CursorAdapter {
    public static final String[] PROYECTION = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_URI
    };

    public ContactsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ContactsAdapterBinding binding = ContactsAdapterBinding.inflate(LayoutInflater.from(context));
        return binding.getRoot();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ContactsAdapterBinding binding;
        binding = ContactsAdapterBinding.bind(view);

        binding.contactsId.setText(String.valueOf(cursor.getPosition()+1));
        binding.contactsName.setText(cursor.getString(1));
        if (cursor.getString(2) != null)
            binding.contactsImg.setImageURI(Uri.parse(cursor.getString(2)));
        else
            binding.contactsImg.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_account_circle_24));
    }
}
