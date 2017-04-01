package shreshta.com.air_help;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

public class Recorder extends Activity implements View.OnClickListener, SurfaceHolder.Callback {
    MediaRecorder recorder;
    SurfaceHolder holder;
    boolean recording = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        recorder = new MediaRecorder();
        initRecorder();
        setContentView(R.layout.activity_recorder);
        SurfaceView cameraView = (SurfaceView) findViewById(R.id.surfaceView);
        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraView.setClickable(true);
        cameraView.setOnClickListener(this);
    }
    private void initRecorder() {
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        CamcorderProfile cpHigh = CamcorderProfile
                .get(CamcorderProfile.QUALITY_LOW);
        recorder.setProfile(cpHigh);
        recorder.setOutputFile(Environment.getExternalStorageDirectory()+"/video.mp4");
        recorder.setMaxDuration(15000); // 50 seconds
//        recorder.setMaxFileSize(5000000); // Approximately 5 megabytes
    }
    private void prepareRecorder() {
        recorder.setPreviewDisplay(holder.getSurface());
        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }
    public void onClick(View v) {
        if (recording) {
            recorder.stop();
            recording = false;
            finish();
            // Let's initRecorder so we can record again
//            initRecorder();
//            prepareRecorder();
        } else {
            recording = true;
            recorder.start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"Uploading",Toast.LENGTH_SHORT).show();
                    fileUpload();
                    startActivity(new Intent(Recorder.this,Home.class));
                }
            },16000);
        }
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
                    Uri uri=Uri.parse(Environment.getExternalStorageDirectory()+"/video.mp4");
                    File file=new File(Environment.getExternalStorageDirectory()+"/video.mp4");
                    RequestBody requestFile =
                            RequestBody.create(
                                    MediaType.parse(getContentResolver().getType(uri)),
                                    file
                            );
                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData("file",Global.distressId,requestFile);
                    Call<Distress> call = service.fileUpload(token,Global.distressId,body);
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
    public void surfaceCreated(SurfaceHolder holder) {
        prepareRecorder();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (recording) {
            recorder.stop();
            recording = false;
        }
        recorder.release();
        finish();
    }
}

