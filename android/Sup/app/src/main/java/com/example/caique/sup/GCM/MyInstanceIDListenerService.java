package com.example.caique.sup.GCM;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by caique on 18/08/15.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        Log.i(getClass().getName(), "ON ID REFRESH");
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
