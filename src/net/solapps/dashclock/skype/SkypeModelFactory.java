package net.solapps.dashclock.skype;

import java.util.List;

import net.solapps.dashclock.notification.service.IModelFactory;
import net.solapps.dashclock.skype.notification.GenericNotification;
import net.solapps.dashclock.skype.notification.MissedCall;
import net.solapps.dashclock.skype.notification.UnreadMessages;
import android.service.notification.StatusBarNotification;

public class SkypeModelFactory implements IModelFactory<SkypeModel> {

    private static final int GENERIC_NOTIFICATION = 2;
    private static final int IM_NOTIFICATION = 48;
    private static final int CALL_NOTIFICATION_MASK = 0x70000000;

    @Override
    public SkypeModel createFrom(List<StatusBarNotification> sbns) {
        SkypeModel model = new SkypeModel().packageName("com.skype.raider");
        for (StatusBarNotification sbn : sbns) {
            if (sbn.getId() == IM_NOTIFICATION) {
                model.addMessages(new UnreadMessages(sbn));
            } else if ((sbn.getId() & CALL_NOTIFICATION_MASK) == CALL_NOTIFICATION_MASK) {
                model.addCall(sbn.getId(), new MissedCall(sbn));
            } else if (sbn.getId() == GENERIC_NOTIFICATION) {
                model.addGenericNotification(new GenericNotification(sbn));
            }
        }
        if (sbns.size() == 1){
            model.contentIntent(sbns.get(0).getNotification().contentIntent);
        }

        return model;
    }
}