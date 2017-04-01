package shreshta.com.air_help;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        final double[] longitude = {location.getLongitude()};
        final double[] latitude = {location.getLatitude()};
        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                longitude[0] = location.getLongitude();
                latitude[0] = location.getLatitude();
                Log.d("Lat",String.valueOf(longitude[0]));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
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
                                        fileUpload();
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
                    Uri uri= Uri.parse("/sdcard/video.mp4");
                    File file=new File(Environment.getExternalStorageDirectory(),"/sdcard/video.mp4");
                    RequestBody requestFile =
                            RequestBody.create(
                                    MediaType.parse(getContentResolver().getType(uri)),
                                    file
                            );
                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData("picture", file.getName(), requestFile);
                    Call<Distress> call = service.fileUpload(token,distressId,body);
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
