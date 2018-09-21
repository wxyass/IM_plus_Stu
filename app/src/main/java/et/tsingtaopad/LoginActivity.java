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

import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.plugin.common.AppMgr;
import com.yuntongxun.plugin.common.ClientUser;
import com.yuntongxun.plugin.common.SDKCoreHelper;
import com.yuntongxun.plugin.common.common.dialog.ECProgressDialog;
import com.yuntongxun.plugin.common.common.utils.ECPreferences;
import com.yuntongxun.plugin.common.common.utils.EasyPermissionsEx;
import com.yuntongxun.plugin.common.common.utils.LogUtil;
import com.yuntongxun.plugin.common.common.utils.ToastUtil;
import com.yuntongxun.plugin.common.ui.RongXinFragmentActivity;
import com.yuntongxun.plugin.greendao3.helper.DaoHelper;
import com.yuntongxun.plugin.im.dao.helper.IMDao;

public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText et_account, et_name;
    private Button btn_login;
    private ECProgressDialog dialog;
    private EditText et_pwd;

    private void showDialog() {
        if (dialog == null) {
            dialog = new ECProgressDialog(this, "请稍后...");
        }
        dialog.show();
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private BroadcastReceiver mSDKNotifyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SDKCoreHelper.ACTION_SDK_CONNECT.equals(intent.getAction())) {
                if (SDKCoreHelper.isLoginSuccess(intent)) {
                    String pushToken = ECPreferences.getSharedPreferences().getString("pushToken", null);
                    LogUtil.d(TAG, "SDK connect Success ,reportToken:" + pushToken);
                    if (!TextUtils.isEmpty(pushToken)) {
                        // 上报华为/小米推送设备token
                        ECDevice.reportHuaWeiToken(pushToken);
                    }
                    // 初始化IM数据库
                    DaoHelper.init(LoginActivity.this, new IMDao());
                    dismissDialog();


                    Intent action = new Intent(LoginActivity.this, MainActivity.class);
                    action.putExtra("userid", AppMgr.getUserId());
                    startActivity(action);


                    /*//我的群组
                    Intent intent3 = new Intent(LoginActivity.this, GroupActivity.class);
                    startActivity(intent3);*/

                    finish();
                } else {
                    int error = intent.getIntExtra("error", 0);
                    if (error == SdkErrorCode.CONNECTING) return;
                    dismissDialog();
                    LogUtil.e(TAG, "登入失败[" + error + "]");
                    ToastUtil.showMessage("登入失败[" + error + "]");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initPermission();
        et_account = (EditText) findViewById(R.id.et_account);
        btn_login = (Button) findViewById(R.id.btn_login);
        et_name = (EditText) findViewById(R.id.et_name);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        btn_login.setOnClickListener(onClickListener);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SDKCoreHelper.ACTION_SDK_CONNECT);
        registerReceiver(mSDKNotifyReceiver, intentFilter);

        if (AppMgr.getClientUser() != null) {
            LogUtil.d(TAG, "SDK auto connect...");
            SDKCoreHelper.init(getApplicationContext());
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String account = et_account.getText().toString();
            String name = et_name.getText().toString();
            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(name)) {
                ToastUtil.showMessage("ID和名字两者都必须填写");
                return;
            }
            showDialog();
            ClientUser.UserBuilder builder = new ClientUser.UserBuilder(account, name);
            // 以下setXXX参数都是可选
            // builder.setAppKey("appKey");// AppId(私有云使用)
            // builder.setAppToken("appToken");// AppToken(私有云使用)
            // builder.setPwd(et_pwd.getText().toString());// Password不为空情况即通讯账号密码登入
            // 下面三个参数是调用REST接口使用(如:语音会议一键静音功能)
            // builder.setAccountSid("accountSid");// 主账号Id(REST使用)
            // builder.setAuthToken("autoToken");// 账户授权令牌(REST使用)


            // 公有云使用一个参数login登入
             builder.setRestHost("http://app.cloopen.com:8881");// REST 协议+ip+端口(REST使用)
             builder.setLvsHost("http://app.cloopen.com:8888");
             SDKCoreHelper.login(builder.build());
        }
    };


    private String rationInit = "需要存储、相机和麦克风的权限";

    private void initPermission() {
        if (EasyPermissionsEx.hasPermissions(LoginActivity.this, RongXinFragmentActivity.needPermissionsCamera)) {
            LogUtil.d(TAG, "permission is userful");
        } else {
            EasyPermissionsEx.requestPermissions(LoginActivity.this, rationInit, RongXinFragmentActivity.PERMISSIONS_REQUEST_CAMERA, RongXinFragmentActivity.needPermissionsCamera);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播
        unregisterReceiver(mSDKNotifyReceiver);
    }
}
