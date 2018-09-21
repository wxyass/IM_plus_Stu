package et.tsingtaopad.service;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.huawei.android.pushagent.api.PushEventReceiver;
import com.yuntongxun.plugin.common.common.utils.ECPreferences;
import com.yuntongxun.plugin.common.common.utils.ToastUtil;

import et.tsingtaopad.PushReceiverActivity;

/*
 * 接收Push所有消息的广播接收器
 */
public class HuaWeiReceiver extends PushEventReceiver {
    private static final String TAG = HuaWeiReceiver.class.getSimpleName();

    @Override
    public void onToken(Context context, String token, Bundle extras) {
        String belongId = extras.getString("belongId");
        String content = "获取token和belongId成功，token = " + token + ",belongId = " + belongId;
        Log.e(TAG, content + " token " + extras);
        ToastUtil.showMessage(content);

        // 缓存到本地 在登入SDK成功之后上报给服务器
        ECPreferences.getSharedPreferencesEditor().putString("pushToken", token).commit();
    }

    @Override
    public void onPushMsg(Context context, byte[] bytes, String s) {
        super.onPushMsg(context, bytes, s);
        Log.e(TAG, "[onPushMsg]" + s);
        ToastUtil.showMessage(s);
    }

    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
            String content = "收到一条Push消息： " + new String(msg, "UTF-8");
            Log.e(TAG, "[onPushMsg]" + content);
            ToastUtil.showMessage(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onEvent(Context context, Event event, Bundle extras) {
        Log.d(TAG, "[onEvent] " + context + " event " + event + " extras " + extras);
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
            String content = "收到华为通知附加消息： " + extras.getString(BOUND_KEY.pushMsgKey);
            ToastUtil.showMessage(content);

            Intent intent = new Intent(context, PushReceiverActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("message", content);
            intent.putExtra("pushType", 1);
            context.startActivity(intent);
            Log.d(TAG, "[onEvent] " + content);
        } else if (Event.PLUGINRSP.equals(event)) {

        }
        super.onEvent(context, event, extras);
    }
}
