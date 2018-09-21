package et.tsingtaopad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageHelper;
import com.yuntongxun.plugin.common.common.utils.LogUtil;

public class PushReceiverActivity extends AppCompatActivity {
    private static final String TAG = LogUtil.getLogUtilsTag(PushReceiverActivity.class);
    private TextView intentParam;
    private TextView extras;
    private TextView intentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_receiver);

        intentParam = (TextView) findViewById(R.id.ed_intent_param);
        extras = (TextView) findViewById(R.id.et_extras);
        intentTextView = (TextView) findViewById(R.id.et_intent);
        Intent intent = getIntent();
        int pushType = intent.getIntExtra("pushType", 0);
        if (pushType == 1) {
            setTitle("二级界面(华为推送)");
            String message = intent.getStringExtra("message");
            extras.setText(message);
        } else {
            setTitle("二级界面(小米推送)");
            MiPushMessage miPushMessage = (MiPushMessage) getIntent().getSerializableExtra(PushMessageHelper.KEY_MESSAGE);
            extras.setText(miPushMessage != null ? miPushMessage.toString() : " miPushMessage null...");
        }

        String uri = intent.toUri(Intent.URI_INTENT_SCHEME);
        intentParam.setText(TextUtils.isEmpty(uri) ? "not intent " : uri);
        intentTextView.setText(intent.getExtras().toString());
        LogUtil.d(TAG, "[onCreate] " + uri);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String uri = getIntent().toUri(Intent.URI_INTENT_SCHEME);
        intentParam.setText("[onNewIntent]" + uri);
        LogUtil.d(TAG, "[onNewIntent] " + uri);

    }
}
