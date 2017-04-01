package shreshta.com.air_help.Utils;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import shreshta.com.air_help.Models.User;

/**
 * Created by amrith on 3/28/17.
 */

public interface RestApiInterface {
    @FormUrlEncoded
    @POST("user/auth/login")
    Call<User> login(@Field("idToken") String idToken);
}
