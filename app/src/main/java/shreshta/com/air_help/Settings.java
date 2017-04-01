package shreshta.com.air_help;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Settings extends AppCompatActivity {
    @BindView(R.id.message)
    CheckBox Message;
    @BindView(R.id.notif)
    CheckBox Notifs;
    @BindView(R.id.shake)
    CheckBox Shake;
    @BindView(R.id.contact)
    Button setContact;
    Boolean msg,notifications,shake;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences=getSharedPreferences("safety", Context.MODE_PRIVATE);
        Message.setChecked(Boolean.valueOf(sharedPreferences.getString("messageState","true")));
        Notifs.setChecked(Boolean.valueOf(sharedPreferences.getString("notifsState","true")));
        Shake.setChecked(Boolean.valueOf(sharedPreferences.getString("shakeState","true")));
        setContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this,Contacts.class));
            }
        });
        Message.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                msg=isChecked;
                SharedPreferences sharedPreferences = getSharedPreferences("safety", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("messageState",msg.toString());
                editor.commit();
            }
        });
        Notifs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notifications=isChecked;
                SharedPreferences sharedPreferences = getSharedPreferences("safety", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("notifsState",notifications.toString());
                editor.commit();
            }
        });
        Shake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                shake=isChecked;
                SharedPreferences sharedPreferences = getSharedPreferences("safety", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("shakeState",shake.toString());
                editor.commit();
            }
        });
    }
}
