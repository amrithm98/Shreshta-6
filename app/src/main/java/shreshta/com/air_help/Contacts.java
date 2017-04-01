package shreshta.com.air_help;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shreshta.com.air_help.Adapters.ContactListAdapter;
import shreshta.com.air_help.Models.ContactModel;
import shreshta.com.air_help.Models.User;
import shreshta.com.air_help.Utils.AuthUtil;
import shreshta.com.air_help.Utils.NetworkUtil;
import shreshta.com.air_help.Utils.RestApiClient;
import shreshta.com.air_help.Utils.RestApiInterface;

import static android.R.id.list;

public class Contacts extends AppCompatActivity {
    @BindView(R.id.add)
    Button add;
    @BindView(R.id.lv)
    ListView listView;
    @BindView(R.id.save)
    Button save;
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
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#993b3c4e")));
        setContentView(R.layout.activity_contacts);
        ButterKnife.bind(this);
        list = new ArrayList<ContactModel>();
        final ProgressDialog progressDialog=new ProgressDialog(this);
        if(NetworkUtil.isNetworkAvailable(getApplicationContext())) {
            progressDialog.show();
            AuthUtil.getFirebaseToken(new AuthUtil.Listener() {
                @Override
                public void tokenObtained(String token) {
                    RestApiInterface service = RestApiClient.getService();
                    Call<User> call = service.getProfile(token);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if(response.code()==200) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Got Profile",Toast.LENGTH_SHORT).show();
                                User user=response.body();
                                list=user.contacts;
                                Log.d("Contacts",user.contacts.toString());
                                contactListAdapter = new ContactListAdapter(list,Contacts.this);
                                listView.setAdapter(contactListAdapter);
                            }else{
                                Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),"Unable to get Profile",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else {
            Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
        }
//        contactListAdapter = new ContactListAdapter(list,this);
//        listView.setAdapter(contactListAdapter);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkUtil.isNetworkAvailable(getApplicationContext())) {
                    progressDialog.show();
                    AuthUtil.getFirebaseToken(new AuthUtil.Listener() {
                        @Override
                        public void tokenObtained(String token) {
                            RestApiInterface service = RestApiClient.getService();
                            Call<List<ContactModel>> call = service.updateContact(token,list);
                            call.enqueue(new Callback<List<ContactModel>>() {
                                @Override
                                public void onResponse(Call<List<ContactModel>> call, Response<List<ContactModel>> response) {
                                    if(response.code()==200) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Updated Contact",Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<List<ContactModel>> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(),"Failed to update Contact",Toast.LENGTH_SHORT).show();
                                    Log.d("ERROR",t.toString());
                                }
                            });
                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
