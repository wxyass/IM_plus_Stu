package et.tsingtaopad;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.yuntongxun.plugin.im.ui.group.GroupListFragment;

/**
 * 群组界面
 * Fragment
 */
public class GroupActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        /**
         * 开发者在容器activity中按照一般fragment嵌入得方法使用
         */
        getSupportFragmentManager().beginTransaction()
                .add(R.id.convert_frame, Fragment.instantiate(this,
                        GroupListFragment.class.getName(), null)).commit();
        findViewById(R.id.unreadMsg).setVisibility(View.GONE);
    }
}
