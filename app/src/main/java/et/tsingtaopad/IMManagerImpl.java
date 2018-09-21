package et.tsingtaopad;

import android.content.Context;
import android.content.Intent;

import com.yuntongxun.plugin.common.common.utils.ToastUtil;
import com.yuntongxun.plugin.im.manager.bean.RETURN_TYPE;
import com.yuntongxun.plugin.im.manager.port.OnIMBindViewListener;
import com.yuntongxun.plugin.im.manager.port.OnNotificationClickListener;
import com.yuntongxun.plugin.im.manager.port.OnReturnIdsCallback;
import com.yuntongxun.plugin.im.manager.port.OnReturnIdsClickListener;

import java.util.ArrayList;
import java.util.List;

import static et.tsingtaopad.MainActivity.getNameById;


/**
 * Created by Administrator on 2017/3/21 0021.
 */

public class IMManagerImpl implements OnReturnIdsClickListener, OnIMBindViewListener, OnNotificationClickListener {
    private static List<String> HeadList = new ArrayList<>();

    private IMManagerImpl() {
    }

    private static IMManagerImpl instance;
    private OnReturnIdsCallback monReturnIdsCallback;

    public static IMManagerImpl getInstance() {
        if (instance == null) {
            synchronized (IMManagerImpl.class) {
                instance = new IMManagerImpl();
            }
            HeadList.add("http://4493bz.1985t.com/uploads/allimg/150127/4-15012G52133.jpg");
            HeadList.add("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1484031904&di=91b67952b067fc0403dbfc2825422f03&src=http://pic41.nipic.com/20140503/9908010_145213320111_2.jpg");
            HeadList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1484041987878&di=3a58317c2b0e5fca1e9bbecf760a6c34&imgtype=0&src=http%3A%2F%2Fpic45.nipic.com%2F20140805%2F7447430_145001150000_2.jpg");
            HeadList.add("http://new-img4.ol-img.com/moudlepic/199_module_images/201612/5865e0b6a6600_273.jpg");
            HeadList.add("http://new-img4.ol-img.com/moudlepic/199_module_images/201612/5861ddf858113_790.jpg");
            HeadList.add("http://new-img1.ol-img.com/moudlepic/199_module_images/201612/5861dcaf8305d_296.jpg");
            HeadList.add("http://new-img3.ol-img.com/moudlepic/199_module_images/201612/5861db8b4c8d3_630.jpg");
        }

        return instance;
    }

    public static void setResult(String... ids) {
        if (getInstance() == null) {
            return;
        }
        if (getInstance().monReturnIdsCallback == null) {
            return;
        }
        getInstance().monReturnIdsCallback.returnIds(ids);
        getInstance().monReturnIdsCallback = null;
    }

    /**
     * 配置头像、昵称和点击事件
     *
     * @param context
     * @param userId
     * @return
     */
    @Override
    public String onBindNickName(Context context, String userId) {

        return getNameById(userId);
    }

    @Override
    public void OnAvatarClickListener(Context context, String id) {
        ToastUtil.showMessage("点击了" + getNameById(id) + "的头像");
    }

    @Override
    public String onBindAvatarByUrl(Context context, String userId) {
        return HeadList.get(4);
    }

    /**
     * 此方法为  设置IM通知消息点击事件监听，如果不配置默认跳转到聊天页面
     *
     * @param context
     * @param contactId
     * @param intent
     */
    @Override
    public void onNotificationClick(Context context, String contactId, Intent intent) {
        // 代码示例，用户只需要配置好intent。指定跳转的页面及传递的数据 不需要startActivity（）
        intent.setClassName(context, "com.yuntongxun.imdemo.MainActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("contactId", contactId);
    }

    /**
     * 配置添加成员和转发消息的加号
     *
     * @param context
     * @param return_type
     * @param callback
     */
    @Override
    public void onReturnIdsClick(Context context, RETURN_TYPE return_type, OnReturnIdsCallback callback,String... ids) {
        monReturnIdsCallback = callback;
        //当转发和增加群成员用于同个界面时，用当前枚举做判断
        if (return_type == RETURN_TYPE.ADDMEMBER_USERID) {
            //添加群成员
            context.startActivity(new Intent(context, AddOthersIntoGroup.class));
        } else if (return_type == RETURN_TYPE.TRANSMIT_CONTACTID) {
            //转发
            context.startActivity(new Intent(context, AddOthersIntoGroup.class));
        }
    }


}
