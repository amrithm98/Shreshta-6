package shreshta.com.air_help;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import shreshta.com.air_help.ServiceHelpers.NotificationEventReceiver;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onSendNotificationsButtonClick(View view) {
        NotificationEventReceiver.setupAlarm(getApplicationContext());
    }
}
