package shreshta.com.air_help.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import shreshta.com.air_help.Models.ContactModel;
import shreshta.com.air_help.Models.User;
import shreshta.com.air_help.Register;

/**
 * Created by amrith on 3/28/17.
 */

public interface RestApiInterface {
    @FormUrlEncoded
    @POST("user/auth/login")
    Call<User> login(@Field("idToken") String idToken);

    @FormUrlEncoded
    @POST("user/auth/register")
    Call<String> register(@Header("x-auth-token") String idToken,@Field("phone") String phone,@Field("sex") String sex,@Field("yob") int yob);

    @POST("user/profile/contacts")
    Call <List<ContactModel>> updateContact(@Header("x-auth-token") String idToken, @Body ArrayList<ContactModel> contactModels);

    @GET("user/profile")
    Call <User> getProfile(@Header("x-auth-token") String idToken);
}
