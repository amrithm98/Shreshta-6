package shreshta.com.air_help.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by amrith on 3/28/17.
 */

public class RestApiClient {
    public static final String  HOST_URL = "http://192.168.43.154:3000" ;
    public static final String  NODE_PORT = "";
    public static final String  BASE_URL = HOST_URL ;
    public static RestApiInterface getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(RestApiInterface.class);
    }
}
