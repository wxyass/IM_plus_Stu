package et.tsingtaopad;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yuntongxun.plugin.common.common.utils.TextUtil;

import java.util.ArrayList;

/**
 * Created by born on 2016/12/23.
 */

public class AddOthersIntoGroup extends Activity {
    private EditText other1, other2;
    private Button btn;
    private String userID;
    protected ArrayList<String> list = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addothers_layout);
       /* String[] userIDses = getIntent().getStringArrayExtra("userIDs");
        for (int i = 0; i < userIDses.length; i++) {

            String TAG = "AddOthersIntoGroup";
            LogUtil.i(TAG, "haha== " + userIDses[i]);

        }*/


        userID = getIntent().getStringExtra("userID");
        list.clear();

        other1 = (EditText) findViewById(R.id.other01);
        other2 = (EditText) findViewById(R.id.other02);
        btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String st1 = other1.getText().toString();
                String st2 = other2.getText().toString();

                if (!TextUtil.isEmpty(userID)) {
                    list.add(userID);
                }
                if (!TextUtil.isEmpty(st1)) {
                    list.add(st1);
                }
                if (!TextUtil.isEmpty(st2)) {
                    list.add(st2);
                }
                if (list.size() != 0) {

                    String[] users = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        users[i] = "" + list.get(i);
                    }

                    if (IMApplication.mOnReturnIdsCallback != null) {
                        IMApplication.mOnReturnIdsCallback.returnIds(users);
                    } else {
                        IMManagerImpl.setResult(users);
                    }

                    finish();

                }
            }
        });

        findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
