package shreshta.com.air_help.Utils;

/**
 * Created by amrith on 3/30/17.
 */

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

/**
 * Created by kevin on 2/9/17.
 */

public class AuthUtil {
    public static void getFirebaseToken(final Listener listener ){
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mUser!=null) {
            mUser.getToken(false).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()) {
                        listener.tokenObtained(task.getResult().getToken());
                    } else {
                        //handle
                    }
                }
            });
        }else{
            listener.tokenObtained("");
        }

    }
    public static interface Listener{
        public void tokenObtained(String token);
    }
}
