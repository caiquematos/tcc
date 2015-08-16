package Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by caique on 01/08/15.
 */
public class Preferences {
    static SharedPreferences.Editor editor;
    static SharedPreferences account;
    static SharedPreferences user;
    static SharedPreferences coordinator;

    public static void saveAccountStatus(Context context, boolean isUserLogged) {
        account = PreferenceManager.getDefaultSharedPreferences(context);
        editor = account.edit();
        editor.putBoolean("status", isUserLogged).commit();
    }

    public static boolean getAccountStatus(Context context) {
        try {
            account = PreferenceManager.getDefaultSharedPreferences(context);
            boolean status = account.getBoolean("status", false);
            return status;
        } catch (Exception e) {
            saveAccountStatus(context, false);
            return getAccountStatus(context);
        }
    }

    public static void saveUserId(Context context, int id){
        user = PreferenceManager.getDefaultSharedPreferences(context);
        editor = user.edit();
        editor.putInt("id", id).commit();
        Log.e(Preferences.class.getName(), "id changed: " + getUserId(context));
    }

    public static int getUserId(Context context) {
        try{
            user = PreferenceManager.getDefaultSharedPreferences(context);
            int id = user.getInt("id", -1);
            Log.e(Preferences.class.getName(), "id requested: " + id );
            return id;
        }catch (NullPointerException e){
            e.printStackTrace();
            saveUserId(context, -1);
            return getUserId(context);
        }
    }

    public static void saveCurrentCoordinatorId(Context context, int id){
        coordinator = PreferenceManager.getDefaultSharedPreferences(context);
        editor = coordinator.edit();
        editor.putInt("coordinatorId", id).commit();
        Log.e(Preferences.class.getName(), "coordinatorId changed: " + getCurrentCoordinatorId(context));
    }

    public static int getCurrentCoordinatorId(Context context) {
        try{
            coordinator = PreferenceManager.getDefaultSharedPreferences(context);
            int id = coordinator.getInt("coordinatorId", -1);
            Log.e(Preferences.class.getName(), "coordinatorId requested: " + id );
            return id;
        }catch (NullPointerException e){
            e.printStackTrace();
            saveCurrentCoordinatorId(context, -1);
            return getCurrentCoordinatorId(context);
        }
    }
}
