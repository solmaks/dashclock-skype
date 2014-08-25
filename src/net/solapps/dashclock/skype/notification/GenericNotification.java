package net.solapps.dashclock.skype.notification;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.service.notification.StatusBarNotification;

public class GenericNotification {

    private String mSummary;
    private String mDescription;

    public GenericNotification(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        if (notification.tickerText != null) {
            mSummary = notification.tickerText.toString();
        }

        if (notification.contentView != null) {
            List<String> results = new ArrayList<String>(2);
            try {
                Field field = notification.contentView.getClass().getDeclaredField("mActions");
                field.setAccessible(true);
                if (field.get(notification.contentView) instanceof List) {
                    @SuppressWarnings("rawtypes")
                    List actions = (List) field.get(notification.contentView);
                    for (Object action : actions) {
                        Field typeField;
                        try {
                            typeField = action.getClass().getDeclaredField("type");
                        } catch (NoSuchFieldException e) {
                            continue;
                        }
                        typeField.setAccessible(true);
                        if (10 /* CHAR_SEQUENCE */== typeField.getInt(action)) {
                            Field valueField = action.getClass().getDeclaredField("value");
                            valueField.setAccessible(true);
                            if (valueField.get(action) != null) {
                                results.add(valueField.get(action).toString());
                            }

                            if (results.size() > 1) {
                                mSummary = results.get(0);
                                mDescription = results.get(1);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Ignored
            }
        }
    }

    public String getSummary() {
        return mSummary;
    }

    public String getDescription() {
        return mDescription;
    }
}