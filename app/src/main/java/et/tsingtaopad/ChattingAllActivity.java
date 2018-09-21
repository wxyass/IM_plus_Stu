package et.tsingtaopad;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yuntongxun.plugin.common.common.utils.LogUtil;
import com.yuntongxun.plugin.common.common.utils.ToastUtil;
import com.yuntongxun.plugin.im.manager.IMPluginManager;
import com.yuntongxun.plugin.im.manager.port.OnCreateGroupStateListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 邀请群聊  一个or多个
 * 一个为点对点
 * 多个为群聊
 */
public class ChattingAllActivity extends Activity {
    private final String TAG = LogUtil.getLogUtilsTag(ChattingAllActivity.class);
    private EditText e1, e2, e3, e4, e5;
    private Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commit();
            }
        });
        findViewById(R.id.chattingCancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private List<String> inputAccount() {
        List<String> ids = new ArrayList<>();
        if (!TextUtils.isEmpty(e1.getText())) {
            ids.add(e1.getText().toString());
        }
        if (!TextUtils.isEmpty(e2.getText())) {
            ids.add(e2.getText().toString());
        }
        if (!TextUtils.isEmpty(e3.getText())) {
            ids.add(e3.getText().toString());
        }
        if (!TextUtils.isEmpty(e4.getText())) {
            ids.add(e4.getText().toString());
        }
        if (!TextUtils.isEmpty(e5.getText())) {
            ids.add(e5.getText().toString());
        }

        return ids;
    }

    private void commit() {
        List<String> ids = inputAccount();
        if (ids.size() == 0) return;
        IMPluginManager.getManager().startChatting(ChattingAllActivity.this, ids, new OnCreateGroupStateListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Context context, String groupId) {
                IMPluginManager.getManager().startChatting(ChattingAllActivity.this, groupId);
                finish();
            }

            @Override
            public void onFailed(int errorCode) {
                ToastUtil.showMessage("创建群聊失败[" + errorCode + "]");
            }
        });

    }


    private void initView() {
        e1 = (EditText) findViewById(R.id.ed01);
        e2 = (EditText) findViewById(R.id.ed02);
        e3 = (EditText) findViewById(R.id.ed03);
        e4 = (EditText) findViewById(R.id.ed04);
        e5 = (EditText) findViewById(R.id.ed05);
        btn = (Button) findViewById(R.id.chattingBtn);
    }

}
