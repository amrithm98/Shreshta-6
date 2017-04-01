package shreshta.com.air_help;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shreshta.com.air_help.Utils.AuthUtil;
import shreshta.com.air_help.Utils.NetworkUtil;
import shreshta.com.air_help.Utils.RestApiClient;
import shreshta.com.air_help.Utils.RestApiInterface;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    @BindView(R.id.reg)
    Button reg;
    @BindView(R.id.edit_phone)
    EditText phone;
    @BindView(R.id.spinner)
    Spinner gender;
    @BindView(R.id.yob)
    EditText yob;
    String sex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);
        final ProgressDialog progressDialog=new ProgressDialog(this);
        gender.setOnItemSelectedListener(this);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phoneText=phone.getText().toString();
                final int yobVal= Integer.parseInt(yob.getText().toString());
                if(NetworkUtil.isNetworkAvailable(getApplicationContext())) {
                    progressDialog.show();
                    AuthUtil.getFirebaseToken(new AuthUtil.Listener() {
                        @Override
                        public void tokenObtained(String token) {
                            RestApiInterface service = RestApiClient.getService();
                            Call<String> call = service.register(token,phoneText,sex,yobVal);
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if(response.code()==200) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Successfully Registered",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Register.this, Home.class));
                                        finish();
                                    }else{
                                        Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    progressDialog.dismiss();
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
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        sex=parent.getItemAtPosition(position).toString();
        Log.d("Sex",sex);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
