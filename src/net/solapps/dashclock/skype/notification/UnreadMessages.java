package net.solapps.dashclock.skype.notification;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Notification;
import android.service.notification.StatusBarNotification;

public class UnreadMessages {

    private static final Pattern PATTERN = Pattern.compile("\\d+");

    private int mUnread;
    private String mFrom;
    private String mMessage;

    public UnreadMessages(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        mUnread = notification.number;

        boolean parsed = false;
        if (notification.tickerText != null) {
            String[] parts = notification.tickerText.toString().split("\n");
            mFrom = parts[0];
            if (parts.length > 1) {
                // getting here means we have 1 new message
                mUnread = 1;
                mMessage = parts[1];
                parsed = true;
            }
        }

        if (!parsed && notification.contentView != null) {
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
                                // try parsing originator
                                String[] parts = results.get(1).split(": ");
                                if (parts.length > 1) {
                                    mFrom = parts[1];
                                }
                                // try parsing new message number
                                Matcher matcher = PATTERN.matcher(results.get(0));
                                if (matcher.find()) {
                                    try {
                                        mUnread = Integer.parseInt(matcher.group());
                                    } catch (NumberFormatException e) {
                                        // Should not happen
                                    }
                                }
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

    public String getLastFrom() {
        return mFrom;
    }

    public String getMessage() {
        return mMessage;
    }

    public int getUnread() {
        return mUnread;
    }
}