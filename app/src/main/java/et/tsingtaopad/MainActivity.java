package et.tsingtaopad;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.PersonInfo;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.plugin.common.AppMgr;
import com.yuntongxun.plugin.common.SDKCoreHelper;
import com.yuntongxun.plugin.common.common.utils.LogUtil;
import com.yuntongxun.plugin.common.common.utils.RongXInUtils;
import com.yuntongxun.plugin.common.common.utils.TextUtil;
import com.yuntongxun.plugin.common.common.utils.ToastUtil;
import com.yuntongxun.plugin.im.manager.IMPluginManager;


public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = LogUtil.getLogUtilsTag(MainActivity.class);

    private EditText editText;
    private Button oneBtn, groupBtn, myChatBtn, myGroup, mLogoutView, setting, getPersonInfo;
    private Context mContext = this;

    private BroadcastReceiver mSDKNotifyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (RongXInUtils.ACTION_KICK_OFF.equals(intent.getAction())) {
                //代码示例
                ToastUtil.showMessage("您的账号被他人登陆，请确定您的账号安全");
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.tv_show_id);
        String str = AppMgr.getClientUser() != null ? AppMgr.getClientUser().getUserName() : " 无名氏";
        textView.setText(AppMgr.getUserId() + " -- " + str);

        editText = (EditText) findViewById(R.id.et_chattingId);
        groupBtn = (Button) findViewById(R.id.chattinggroupBtn);
        oneBtn = (Button) findViewById(R.id.chattingOneBtn);
        myChatBtn = (Button) findViewById(R.id.myChatting);
        myGroup = (Button) findViewById(R.id.myGroup);
        mLogoutView = (Button) findViewById(R.id.logout);
        setting = (Button) findViewById(R.id.setting);
        getPersonInfo = (Button) findViewById(R.id.getPersonInfo);

        mLogoutView.setOnClickListener(this);
        groupBtn.setOnClickListener(this);
        oneBtn.setOnClickListener(this);
        myChatBtn.setOnClickListener(this);
        myGroup.setOnClickListener(this);
        setting.setOnClickListener(this);
        getPersonInfo.setOnClickListener(this);
        getPersonInfo.setVisibility(View.GONE);
        NotifyIntent(getIntent());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SDKCoreHelper.ACTION_SDK_CONNECT);// SDK状态广播
        intentFilter.addAction(RongXInUtils.ACTION_KICK_OFF);// 账号异地登入广播
        registerReceiver(mSDKNotifyReceiver, intentFilter);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chattingOneBtn:
                //发起点对点聊天
                String recipients = editText.getText().toString();
                if (TextUtil.isEmpty(recipients)) {
                    return;
                }
                IMPluginManager.getManager().startChatting(this, recipients);
                break;
            case R.id.chattinggroupBtn:
                //邀请群组并会话
                Intent intent2 = new Intent(MainActivity.this, ChattingAllActivity.class);
                startActivity(intent2);
                break;
            case R.id.myChatting:
                //我的沟通页面
                Intent intent1 = new Intent(MainActivity.this, ConversationActivity.class);
                startActivity(intent1);
                break;
            case R.id.myGroup:
                //我的群组
                Intent intent3 = new Intent(MainActivity.this, GroupActivity.class);
                startActivity(intent3);
                break;
            case R.id.logout:
                SDKCoreHelper.logout();
                finish();
                break;
            case R.id.setting:
                Intent intent4 = new Intent(MainActivity.this, SettingCommonActivity.class);
                startActivity(intent4);
                break;
            case R.id.getPersonInfo:
                String id = editText.getText().toString();
                ECDevice.getPersonInfo(id, new ECDevice.OnGetPersonInfoListener() {
                    @Override
                    public void onGetPersonInfoComplete(ECError ecError, PersonInfo personInfo) {
                        if (ecError.errorCode == SdkErrorCode.REQUEST_SUCCESS && personInfo != null) {
                            LogUtil.e(MainActivity.class.getSimpleName(), "personinfo " + personInfo.toString());
                        }
                    }
                });
                break;
        }
    }

    public static String getNameById(String id) {
        return "名字" + id;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy mSDKNotifyReceiver:" + mSDKNotifyReceiver);
        if (mSDKNotifyReceiver != null) {
            // 注销广播
            unregisterReceiver(mSDKNotifyReceiver);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        NotifyIntent(intent);
    }

    private void NotifyIntent(Intent intent) {
        String contactId = intent.getStringExtra("contactId");
        if (TextUtils.isEmpty(contactId)) {
            return;
        }
        // 获取到contactID后调用该方法，方法为自动判断是单聊还是群聊
        IMPluginManager.getManager().startChatting(mContext, contactId);
    }


}
