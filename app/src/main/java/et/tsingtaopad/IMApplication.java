package et.tsingtaopad;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.huawei.android.pushagent.api.PushManager;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.plugin.common.SDKCoreHelper;
import com.yuntongxun.plugin.common.common.utils.DeviceUtil;
import com.yuntongxun.plugin.common.common.utils.LogUtil;
import com.yuntongxun.plugin.common.common.utils.ToastUtil;
import com.yuntongxun.plugin.greendao3.helper.DaoHelper;
import com.yuntongxun.plugin.im.dao.helper.IMDao;
import com.yuntongxun.plugin.im.manager.IMPluginHelper;
import com.yuntongxun.plugin.im.manager.IMPluginManager;
import com.yuntongxun.plugin.im.manager.bean.IMConfiguration;
import com.yuntongxun.plugin.im.manager.bean.RETURN_TYPE;
import com.yuntongxun.plugin.im.manager.port.OnBindViewHolderListener;
import com.yuntongxun.plugin.im.manager.port.OnIMBindViewListener;
import com.yuntongxun.plugin.im.manager.port.OnMessagePreproccessListener;
import com.yuntongxun.plugin.im.manager.port.OnNotificationClickListener;
import com.yuntongxun.plugin.im.manager.port.OnReturnIdsCallback;
import com.yuntongxun.plugin.im.manager.port.OnReturnIdsClickListener;

import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.custom.OnBindViewHolderListenerImpl;

import static et.tsingtaopad.MainActivity.getNameById;


/**
 * Created by erica on 2016/12/22.
 */

public class IMApplication extends Application {
    private final String TAG = LogUtil.getLogUtilsTag(IMApplication.class);
    public static OnReturnIdsCallback mOnReturnIdsCallback;
    private List<String> HeadList = new ArrayList<>();

    @Override
     protected void attachBaseContext(Context base) {
                super.attachBaseContext(base);
                 MultiDex.install(this) ;
            }

    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
        if (!IMPluginHelper.shouldInit(this)) {
            //防止多进程初始化多次
            return;
        }

        SDKCoreHelper.setContext(this);
        // 插件日志开启(放在setContext之后)
        LogUtil.setDebugMode(true);
        DaoHelper.init(this, new IMDao());
        if (DeviceUtil.isHuaweiDevices()) {
            PushManager.requestToken(this);
        }
        if (DeviceUtil.isXiaomiDevices()) {
            initXiaoMiPush("2882303761517568985", "5601756880985");
        }


        HeadList.add("http://123.57.204.169:8888/vtm/8ab3bdf156e3e63d0156e43bb86a0006/rxhf12926/1485158266332197170.png_thum");
        initIMPlugin();

        // 崩溃收集
        CrashHandler.getInstance().init(this);
    }


    /**
     * 小米初始化
     */
    private void initXiaoMiPush(String appid, String appkey) {
        // 注册push服务，注册成功后会向DemoMessageReceiver发送广播
        // 可以从DemoMessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
        MiPushClient.registerPush(this, appid, appkey);

        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);
    }

    private OnIMBindViewListener onIMBindViewListener = new OnIMBindViewListener() {
        public String onBindNickName(Context context, String userId) {
            return getNameById(userId);
        }

        @Override
        public void OnAvatarClickListener(Context context, String id) {
            ToastUtil.showMessage("点击了" + getNameById(id) + "的头像");
        }

        @Override
        public String onBindAvatarByUrl(Context context, String userId) {
            return HeadList.get(0);
        }
    };
    private OnReturnIdsClickListener onReturnIdsCallback = new OnReturnIdsClickListener() {
        @Override
        public void onReturnIdsClick(Context context, RETURN_TYPE return_type, OnReturnIdsCallback callback, String... ids) {
            mOnReturnIdsCallback = callback;
            if (return_type == RETURN_TYPE.ADDMEMBER_USERID) {
                //添加群成员
                context.startActivity(new Intent(context, AddOthersIntoGroup.class));
            } else if (return_type == RETURN_TYPE.TRANSMIT_CONTACTID) {
                //转发
                context.startActivity(new Intent(context, AddOthersIntoGroup.class));
            }
            for (String id : ids) {
                LogUtil.i(id + "");
            }
        }
    };
    private OnNotificationClickListener onNotificationClickListener = new OnNotificationClickListener() {
        @Override
        public void onNotificationClick(Context context, String contactId, Intent intent) {
            intent.setClassName(context, "et.tsingtaopad.MainActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("contactId", contactId);
        }
    };
    /**
     * This is the implementation of custom UI
     */
    private OnBindViewHolderListener mBindViewHolderListner = new OnBindViewHolderListenerImpl();
    /**
     * This is the processing of implementing message forwarding logic
     */
    private OnMessagePreproccessListener mOnMessagePreproccessListener = new OnMessagePreproccessListener() {
        @Override
        public boolean dispatchMessage(ECMessage message) {
            // 返回true意义是消费当前这条消息不交给插件内部处理,返回false意义是交给插件进行处理
            if (message.getForm().equals("10086")) {
                LogUtil.d(TAG, "dispatchMessage 10086...");
                return true;
            }
            return false;
        }
    };



    private void initIMPlugin() {

        /**
         * 推荐配置样式1
         */
        IMConfiguration imConfiguration = new IMConfiguration.IMConfigBuilder(this)
                .setOnIMBindViewListener(onIMBindViewListener)
                .setOnReturnIdsClickListener(onReturnIdsCallback)
                .setOnNotificationClickListener(onNotificationClickListener)
                // 沟通列表和聊天列表的UI自定义接口（私有云接口）
                .setOnBindViewHolderListener(mBindViewHolderListner)
                // IM消息转发接口 （私有云接口）
                .setOnMessagePreproccessListener(mOnMessagePreproccessListener)
                // (私有云功能)
                .showMsgState(true)
                .notifyIcon(R.drawable.choose_icon)
                /*.topBarBackgroundColorRecource(R.color.appid_color)
                .topBarTitleColorResource(R.color.red)
                .topBarSubTitleColorResource(R.color.red)
                .topBarRightTextColorResource(R.color.red)
                .topBarLeftImageDrawableResource(R.drawable.ic_launcher)
                .topBarLeftImageDrawableResource(R.drawable.ic_launcher)
                .topBarGroupImageDrawableResource(R.drawable.ic_launcher)
                .chatTipsBackgroundColorResource(R.color.blue)
                .chatTipsTextColorResource(R.color.black)
                .defaultAvaterDrawable(ContextCompat.getDrawable(this, R.drawable.im_attach_back))
                .defaultGroupAvaterResource(R.drawable.ic_launcher)*/
                .build();

        IMPluginManager.getManager().init(imConfiguration);
        /**
         * 推荐配置样式2
         */
        /*IMConfiguration imConfiguration1 = new IMConfiguration.IMConfigBuilder(this)
                .setOnIMBindViewListener(IMManagerImpl.getInstance())
                .setOnNotificationClickListener(IMManagerImpl.getInstance())
                .setOnReturnIdsClickListener(IMManagerImpl.getInstance())
                .notifyIcon(R.drawable.male_icon)
                .build();
        IMPluginManager.getManager().init(imConfiguration1);*/

    }

}
