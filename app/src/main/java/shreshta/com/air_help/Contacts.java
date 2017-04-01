package shreshta.com.air_help;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import shreshta.com.air_help.Adapters.ContactListAdapter;
import shreshta.com.air_help.Models.ContactModel;

import static android.R.id.list;

public class Contacts extends AppCompatActivity {
    @BindView(R.id.add)
    Button add;
    @BindView(R.id.lv)
    ListView listView;
    ArrayList<ContactModel> list;
    private static final int REQUEST_CODE = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (REQUEST_CODE):
                if (resultCode == Activity.RESULT_OK) {
                    Cursor cursor = null;
                    try {
                        String phoneNo = null ;
                        String name = null;
                        Uri uri = data.getData();
                        cursor = getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        phoneNo = cursor.getString(phoneIndex);
                        name = cursor.getString(nameIndex);
                        ContactModel contact=new ContactModel();
                        contact.name=name;
                        contact.phone=phoneNo;
                        Log.d("Phone",phoneNo);
                        contactListAdapter.addContact(contact);
                        contactListAdapter.addContact(contact);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
                break;
        }
    }

    ContactListAdapter contactListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ButterKnife.bind(this);
        list = new ArrayList<ContactModel>();
        contactListAdapter = new ContactListAdapter(list,this);
        listView.setAdapter(contactListAdapter);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }
}
