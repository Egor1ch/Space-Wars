package io.github.space_wars.android;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import io.github.space_wars.Main;

import java.io.IOException;

public class AndroidLauncher extends AndroidApplication {
    private static final String TAG = "AndroidLauncher";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;

        setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        try {
            checkAssets();
            initialize(new Main(), config);
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize game", e);
        }
    }

    private void checkAssets() {
        try {
            Log.i(TAG, "Checking assets...");

            String[] files = getAssets().list("");
            if (files != null) {
                boolean foundBackground = false;
                boolean foundPlayerShip = false;
                boolean foundEnemyShip = false;
                boolean foundBullet = false;

                for (String file : files) {
                    Log.i(TAG, "Found asset: " + file);
                    if ("background.png".equals(file)) foundBackground = true;
                    if ("player_ship.png".equals(file)) foundPlayerShip = true;
                    if ("enemy_ship.png".equals(file)) foundEnemyShip = true;
                    if ("bullet.png".equals(file)) foundBullet = true;
                }

                if (!foundBackground) Log.e(TAG, "background.png not found!");
                if (!foundPlayerShip) Log.e(TAG, "player_ship.png not found!");
                if (!foundEnemyShip) Log.e(TAG, "enemy_ship.png not found!");
                if (!foundBullet) Log.e(TAG, "bullet.png not found!");
            } else {
                Log.e(TAG, "No assets found!");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error checking assets", e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error checking assets", e);
        }
    }
}
