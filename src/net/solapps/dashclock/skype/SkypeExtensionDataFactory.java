package net.solapps.dashclock.skype;

import net.solapps.dashclock.notification.service.IExtensionDataFactory;
import net.solapps.dashclock.notification.ui.ProxyActivity;
import net.solapps.dashclock.skype.notification.GenericNotification;
import net.solapps.dashclock.skype.notification.MissedCall;
import net.solapps.dashclock.skype.notification.UnreadMessages;
import net.solapps.dashclock.skype.ui.SettingsActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.apps.dashclock.api.ExtensionData;

public class SkypeExtensionDataFactory implements IExtensionDataFactory<SkypeModel> {

    private Context mContext;
    private SharedPreferences mPrefs;

    public SkypeExtensionDataFactory(Context context) {
        mContext = context.getApplicationContext();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public ExtensionData createFrom(SkypeModel model) {
        ExtensionData data = new ExtensionData();
        data.clickIntent(new Intent(mContext, ProxyActivity.class)).icon(R.drawable.ic_skype);

        if (model.hasMissedCalls()) {
            if (model.hasUnreadMessages()) {
                publishCallsAndMessages(model, data);
            } else {
                if (model.getMissedCallsCount() > 1) {
                    publishMultipleCalls(model, data);
                } else {
                    publishSingleCall(model, data);
                }
            }
            data.status(mContext.getString(R.string.status, model.getUnreadMessagesCount(), model.getMissedCallsCount())).visible(true);
        } else if (model.hasUnreadMessages()) {
            if (model.getUnreadMessagesCount() > 1) {
                publishMultipleMessages(model, data);
            } else {
                publishSingleMessage(model, data);
            }
            data.status(mContext.getString(R.string.status_short, model.getUnreadMessagesCount())).visible(true);
        } else if (model.hasGenericNotification()) {
            publishGenericNotification(model, data);
            data.status(mContext.getString(R.string.status_short, 1)).visible(true);
        } else if (isPersistentNotificationEnabled()) {
            publishPersistentNotification(data);
            data.status(mContext.getString(R.string.status_short, 0)).visible(true);
        }

        return data;
    }

    @Override
    public ExtensionData createFrom(Error error) {
        ExtensionData data = new ExtensionData();
        data.status(mContext.getString(R.string.error_status)).icon(R.drawable.ic_skype).visible(true);

        if (error == Error.NOTIFICATION_ACCESS) {
            data.expandedTitle(mContext.getString(R.string.error_notification_access_title))
                    .expandedBody(mContext.getString(R.string.error_notification_access_body))
                    .clickIntent(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        } else if (error == Error.EXTENSION_ACCESS) {
            data.expandedTitle(mContext.getString(R.string.error_extension_access_title))
                    .expandedBody(mContext.getString(R.string.error_extension_access_body)).clickIntent(new Intent(mContext, SettingsActivity.class));
        }

        return data;
    }

    private void publishPersistentNotification(ExtensionData data) {
        data.expandedTitle(mContext.getString(R.string.no_new_messages_title));
    }

    private void publishGenericNotification(SkypeModel model, ExtensionData data) {
        GenericNotification generic = model.getGenericNotification();
        data.expandedTitle(generic.getSummary()).expandedBody(generic.getDescription());
    }

    private void publishCallsAndMessages(SkypeModel model, ExtensionData data) {
        MissedCall lastCall = model.getLastCall();
        UnreadMessages messages = model.getMessages();
        String messagesTitle = mContext.getResources().getQuantityString(R.plurals.unread_messages_title, model.getUnreadMessagesCount(),
                model.getUnreadMessagesCount());
        String callsTitle = mContext.getResources().getQuantityString(R.plurals.missed_calls_title, model.getMissedCallsCount(),
                model.getMissedCallsCount());
        String messagesBody = mContext.getString(R.string.unread_messages_body, messages.getLastFrom());
        String callsBody = mContext.getString(R.string.missed_calls_body, lastCall.getFrom());
        data.expandedTitle(mContext.getString(R.string.title, messagesTitle, callsTitle)).expandedBody(
                mContext.getString(R.string.body, messagesBody, callsBody));
    }

    private void publishMultipleCalls(SkypeModel model, ExtensionData data) {
        MissedCall lastCall = model.getLastCall();
        data.expandedTitle(
                mContext.getResources().getQuantityString(R.plurals.missed_calls_title, model.getMissedCallsCount(), model.getMissedCallsCount()))
                .expandedBody(mContext.getString(R.string.missed_calls_body, lastCall.getFrom()));
    }

    private void publishSingleCall(SkypeModel model, ExtensionData data) {
        MissedCall lastCall = model.getLastCall();
        data.expandedTitle(lastCall.getFrom()).expandedBody(mContext.getString(R.string.missed_call_body));
    }

    private void publishSingleMessage(SkypeModel model, ExtensionData data) {
        UnreadMessages messages = model.getMessages();
        data.expandedTitle(messages.getLastFrom());

        if (mPrefs.getBoolean(mContext.getString(R.string.setting_key_message_content), false)) {
            data.expandedBody(messages.getMessage());
        }
    }

    private void publishMultipleMessages(SkypeModel model, ExtensionData data) {
        UnreadMessages messages = model.getMessages();
        String title = mContext.getResources().getQuantityString(R.plurals.unread_messages_title, model.getUnreadMessagesCount(),
                model.getUnreadMessagesCount());
        data.expandedTitle(title).expandedBody(mContext.getString(R.string.unread_messages_body, messages.getLastFrom()));
    }

    private boolean isPersistentNotificationEnabled() {
        return mPrefs.getBoolean(mContext.getString(R.string.setting_key_persistent_notification), false);
    }
}