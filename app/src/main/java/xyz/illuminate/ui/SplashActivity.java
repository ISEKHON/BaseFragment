package xyz.illuminate.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

import xyz.illuminate.app.BuildVars;
import xyz.illuminate.app.FileLog;
import xyz.illuminate.app.R;
import xyz.illuminate.app.Utilities;
import xyz.illuminate.server.ConnectionsManager;
import xyz.illuminate.ui.ActionBar.Theme;
import xyz.illuminate.ui.Components.AlertsCreator;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends Activity {

    String TAG = "SplashActivity";
    HashMap<String, String> data = new HashMap<>();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        RelativeLayout root = findViewById(R.id.root);
        root.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        getWindow().setStatusBarColor(Theme.getColor(Theme.key_windowBackgroundWhite));
    }

    @Override
    protected void onStart() {
        super.onStart();
        startActivity(new Intent(SplashActivity.this, LaunchActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectionsManager.getInstance(this).cancelAll(TAG);
    }

    private void connectWithServer() {
        ConnectionsManager.getInstance(SplashActivity.this).get(BuildVars.DB + "getStatus.php", new ConnectionsManager.RequestListener() {
            @Override
            public void onErrorResponse(String error) {
                FileLog.e("Error message: " + error);
            }

            @Override
            public void onResponse(Object response) {
                try {
                    data = new Gson().fromJson((String) response, new TypeToken<HashMap<String, String>>() {
                    }.getType());
                    String isServerAvailable = data.get("server_status");
                    int version = Utilities.parseInt(data.get("version"));
                    if (isServerAvailable.equals("false")) {
                        AlertsCreator.showMaintenanceAlert(SplashActivity.this, data);
                    } else if (version > 3) {
                        AlertsCreator.showUpdateAppAlert(SplashActivity.this, data);
                    } else {
                        startActivity(new Intent(SplashActivity.this, LaunchActivity.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                } catch (Exception e) {
                    FileLog.e("Failed to parse response: " + e.getMessage());
                }
            }

            @Override
            public void onResponse(byte[] bytes) {
            }
        }, TAG);
    }
}