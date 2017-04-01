package shreshta.com.air_help;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shreshta.com.air_help.Models.Distress;
import shreshta.com.air_help.Utils.AuthUtil;
import shreshta.com.air_help.Utils.NetworkUtil;
import shreshta.com.air_help.Utils.RestApiClient;
import shreshta.com.air_help.Utils.RestApiInterface;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.distress)
    Button distress;
    String distressId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ProgressDialog progressDialog=new ProgressDialog(this);
        distress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkUtil.isNetworkAvailable(getApplicationContext())) {
                    progressDialog.show();
                    AuthUtil.getFirebaseToken(new AuthUtil.Listener() {
                        @Override
                        public void tokenObtained(String token) {
                            RestApiInterface service = RestApiClient.getService();
                            String lat="",lng="";
                            Call<Distress> call = service.distress(token,lat,lng);
                            call.enqueue(new Callback<Distress>() {
                                @Override
                                public void onResponse(Call<Distress> call, Response<Distress> response) {
                                    if(response.code()==200) {
                                        progressDialog.dismiss();
                                        Distress distress=response.body();
                                        distressId=distress.id;
                                        Toast.makeText(getApplicationContext(),"Successfully Sent Signal",Toast.LENGTH_SHORT).show();
                                        finish();
                                    }else{
                                        Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<Distress> call, Throwable t) {
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
    public void fileUpload()
    {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        if(NetworkUtil.isNetworkAvailable(getApplicationContext())) {
            progressDialog.show();
            AuthUtil.getFirebaseToken(new AuthUtil.Listener() {
                @Override
                public void tokenObtained(String token) {
                    RestApiInterface service = RestApiClient.getService();
                    MultipartBody.Part file = null;
                    Call<Distress> call = service.fileUpload(token,distressId,file);
                    call.enqueue(new Callback<Distress>() {
                        @Override
                        public void onResponse(Call<Distress> call, Response<Distress> response) {
                            if(response.code()==200) {
                                progressDialog.dismiss();
                                Distress distress=response.body();
                                Toast.makeText(getApplicationContext(),"Uploaded File",Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Distress> call, Throwable t) {
                            progressDialog.dismiss();
                        }
                    });
                }
            });
        }else {
            Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
        }
    }
}
