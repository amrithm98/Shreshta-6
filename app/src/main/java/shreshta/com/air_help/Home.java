package shreshta.com.air_help;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
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
import shreshta.com.air_help.services.Shake_service;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.distressButton)
    Button distress;
    String distressId;
    private LocationManager locationManager;
    private LocationListener locationListener;
    Double lat = 0.0, lng = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
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
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, locationListener);
        distress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, locationListener);
                //startActivity(new Intent(Home.this,MainActivity.class));
                if(NetworkUtil.isNetworkAvailable(getApplicationContext())) {
                    progressDialog.show();
                    Log.d("Location",lat.toString()+" "+lng.toString());
                    AuthUtil.getFirebaseToken(new AuthUtil.Listener() {
                        @Override
                        public void tokenObtained(String token) {
                            RestApiInterface service = RestApiClient.getService();
                            Call<Distress> call = service.distress(token,lat.toString(),lng.toString());
                            call.enqueue(new Callback<Distress>() {
                                @Override
                                public void onResponse(Call<Distress> call, Response<Distress> response) {
                                    if(response.code()==200) {
                                        progressDialog.dismiss();
                                        Distress distress=response.body();
                                        distressId=distress.id;
                                        Global.distressId=distressId.toString();
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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        Intent shakeService = new Intent(this, Shake_service.class);
//        startService(shakeService);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {

            Intent intent=new Intent(Home.this,Profile.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {
            Intent intent=new Intent(Home.this,Settings.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            //Firebase Logout
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Home.this,Login.class));
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
