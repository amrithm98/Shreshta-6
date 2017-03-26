package shreshta.com.air_help;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.Date;

public class VideoActivity extends AppCompatActivity {
    Button start;
    String strVideoPath=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCamera();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (resultCode == VideoActivity.RESULT_OK) {
                //do stuff here on success
                Toast.makeText(getApplicationContext(),"Video Saved to Air-Help Folder",Toast.LENGTH_SHORT).show();
            }
            else{
                strVideoPath=null;
            }
        }
    }

    void displayCamera() {
        File imagesFolder = new File(Environment
                .getExternalStorageDirectory(), getResources()
                .getString(R.string.app_name));
        try {
            imagesFolder.mkdirs();
        } catch (Exception e) {
        }
        File f_image = new File(imagesFolder, new Date().getTime() + ".mp4");
        Uri uriSavedVideo = Uri.fromFile(f_image);
        Intent intent = new Intent(
                MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedVideo);
        strVideoPath = f_image.getAbsolutePath();

        try {
            startActivityForResult(intent, 111);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
