package shreshta.com.air_help.Models;

import java.util.ArrayList;

/**
 * Created by amrith on 3/30/17.
 */

public class User {
    public String fcmId,name,uid,phone,sex,picture,status,email,id;
    public boolean registered;
    public int yob;
    public ArrayList<ContactModel> contacts;
}
