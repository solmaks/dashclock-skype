package net.solapps.dashclock.skype;

import net.solapps.dashclock.notification.service.DashClockExtensionClient;
import net.solapps.dashclock.notification.service.NotificationListenerClient;
import android.app.Application;
import android.preference.PreferenceManager;

public class Skype extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        NotificationListenerClient.init(new NotificationListenerClient());
        DashClockExtensionClient.init(new DashClockExtensionClient<SkypeModel>(NotificationListenerClient.instance(), new SkypeModelFactory(),
                new SkypeExtensionDataFactory(this)));
    }
}
