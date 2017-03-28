package shreshta.com.air_help.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by amrith on 3/28/17.
 */

public class RestApiClient {
    public static final String  HOST_URL = "" ;
    public static final String  NODE_PORT = "";
    public static final String  BASE_URL = HOST_URL ;
    public static RestApiClient getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(RestApiClient.class);
    }
}
