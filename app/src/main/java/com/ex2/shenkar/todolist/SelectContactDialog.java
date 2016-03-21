package com.ex2.shenkar.todolist;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.shenkar.managy.adapters.SelectContactsAdapter;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by shnizle on 1/14/2016.
 */
public class SelectContactDialog extends Dialog {

    private static boolean contactsLoading = false;
    private static ArrayList<ContactListItem> contacts;
    ArrayList<Contact> selectedContacts;
    private static ListView contactsListView;
    private static SelectContactsAdapter contactsAdapter;
    private ProgressBar loader;

    private static final String[] PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Email.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Email.DATA
    };

    public SelectContactDialog(Context context, final SelectContactCallback callback){

        super(context);

        contacts = new ArrayList<>();
        selectedContacts = new ArrayList<>();

        //
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_select_contacts);

        contactsListView = (ListView) findViewById(R.id.contactsListView);

        loader = (ProgressBar) findViewById(R.id.selectContacts_loader);

        if(!contactsLoading) {

            getContacts(context);

            contactsAdapter = new SelectContactsAdapter(context, contacts);
            contactsListView.setAdapter(contactsAdapter);

        }

        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (contacts.get(position).getSelected()) {

                    selectedContacts.remove(contacts.get(position));
                } else {

                    selectedContacts.add(contacts.get(position));
                }
                ContactListItem item = (ContactListItem) contactsAdapter.getItem(position);
                item.setSelected(!item.getSelected());
                contactsAdapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.selectContactsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSelected(selectedContacts);
                cancel();
            }
        });

    }

    @Override
    public void show() {

        selectedContacts = new ArrayList<>();
        super.show();
    }

    public void loadContacts(Context context){

        getContacts(context);
    }

    private void getContacts(final Context context) {

        contactsLoading = true;

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {

                 contacts = new ArrayList<>();

                ContentResolver cr = context.getContentResolver();

                String[] PROJECTION = new String[] { ContactsContract.RawContacts._ID,

                        ContactsContract.Contacts.DISPLAY_NAME,

                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,

                        ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,

                        ContactsContract.Contacts.PHOTO_ID,

                        ContactsContract.CommonDataKinds.Email.DATA,

                        ContactsContract.CommonDataKinds.Photo.CONTACT_ID };

                String order = "CASE WHEN "

                        + ContactsContract.Contacts.DISPLAY_NAME

                        + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "

                        + ContactsContract.Contacts.DISPLAY_NAME

                        + ", "

                        + ContactsContract.CommonDataKinds.Email.DATA

                        + " COLLATE NOCASE ";

                String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";

                Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);

                if( cur != null && cur.moveToFirst() ){

                    int emailColumnIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

                    String email = cur.getString(emailColumnIndex);

                    for(cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {

//check doubles

                        if(email.equals(cur.getString(emailColumnIndex))) continue;

                        email = cur.getString(emailColumnIndex);

                        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),

                                ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, cur.getLong(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))));

//person = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

                        String given = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        String family = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));

// The Cursor is now set to the right position

//Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, cur.getLong(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));

                        contacts.add(new ContactListItem(email, "" , given
                        ));

                    }

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                contactsLoading = false;

                // check if view already available
                if(contactsListView != null){

                    contactsAdapter = new SelectContactsAdapter(context, contacts);
                    contactsListView.setAdapter(contactsAdapter);

                    loader.setVisibility(View.GONE);
                }
            }
        }.execute();

    }
    private ArrayList<ContactListItem> refreshData(Context context) {
        //String emaildata = "";
        ArrayList<ContactListItem> contacts = new ArrayList<>();

        try {

            /**************************************************/

            ContentResolver cr = context
                    .getContentResolver();
            Cursor cur = cr
                    .query(ContactsContract.Contacts.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

            if (cur.getCount() > 0) {

                //Log.i("Content provider", "Reading contact  emails");

                while (cur
                        .moveToNext()) {

                    String contactId = cur
                            .getString(cur
                                    .getColumnIndex(ContactsContract.Contacts._ID));

                    String name=cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));

                    // Create query to use CommonDataKinds classes to fetch emails
                     /*Cursor emails = cr.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID
                                    + " = " + contactId, null, null);


                            //You can use all columns defined for ContactsContract.Data
                            // Query to get phone numbers by directly call data table column

                            Cursor c = getContentResolver().query(Data.CONTENT_URI,
                                      new String[] {Data._ID, Phone.NUMBER, Phone.TYPE, Phone.LABEL},
                                      Data.CONTACT_ID + "=?" + " AND "
                                              + Data.MIMETYPE + "= + Phone.CONTENT_ITEM_TYPE + ",
                                      new String[] {String.valueOf(contactId)}, null);


                    while (emails.moveToNext()) {

                        // This would allow you get several email addresses
                        String emailAddress = emails
                                .getString(emails
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                        //Log.e("email==>", emailAddress);


                    }*/

                    contacts.add(new ContactListItem("asd","",name));

                    //emails.close();
                }

            }
            else
            {


            }
            cur.close();


        } catch (Exception e) {

            e.printStackTrace();
        }

        return contacts;
    }

}
