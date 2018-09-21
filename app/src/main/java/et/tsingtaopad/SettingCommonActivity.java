package et.tsingtaopad;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.yuntongxun.ecsdk.platformtools.ECHandlerHelper;
import com.yuntongxun.plugin.common.common.dialog.ECAlertDialog;
import com.yuntongxun.plugin.common.common.dialog.ECProgressDialog;
import com.yuntongxun.plugin.common.view.SettingItem;
import com.yuntongxun.plugin.im.manager.IMPluginManager;

/**
 * 设置界面
 */
public class SettingCommonActivity extends Activity implements
		OnClickListener {

	private SettingItem headSet;
	private SettingItem clear;
	private SettingItem showNotify;
	private SettingItem audio;
	private SettingItem vibrate;

	private ECProgressDialog mPostingdialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_setting);
		initView();
		initResourceRefs();
	}

	private void initView() {
		headSet = (SettingItem) findViewById(R.id.headSet);
		clear = (SettingItem) findViewById(R.id.clear);


		showNotify = (SettingItem) findViewById(R.id.show_notify);
		audio = (SettingItem) findViewById(R.id.audio);
		vibrate = (SettingItem) findViewById(R.id.vibrate);


		clear.setOnClickListener(this);

	}

	private void initResourceRefs() {
		headSet.getCheckedTextView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				headSet.toggle();
				IMPluginManager.getManager().useHandSetToPlayVoice(headSet.isChecked());
				initHeadSetting();
			}
		});

		showNotify.getCheckedTextView().setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						showNotify.toggle();
						IMPluginManager.getManager().setReceiveMessagesNotify(showNotify.isChecked());
						initNewsNotifySettings();
					}
				});
		audio.getCheckedTextView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				audio.toggle();
				IMPluginManager.getManager().useSoundToNotify(audio.isChecked());
			}
		});
		vibrate.getCheckedTextView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				vibrate.toggle();
				IMPluginManager.getManager().useShakeToNotify(vibrate.isChecked());
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		initSettings();
	}

	private void initSettings() {
		boolean mUseHeadSet =IMPluginManager.getManager().getHandSetSetting();
		headSet.setChecked(mUseHeadSet);
		initNewsNotifySettings();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.clear:
			showClearChatRecordDialog();
			break;
		default:
			break;
		}
	}

		

	private void showClearChatRecordDialog() {
	    ECAlertDialog buildAlert = ECAlertDialog.buildAlert(SettingCommonActivity.this, R.string.fmt_delcontactmsg_confirm, null, new DialogInterface.OnClickListener() {
	
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            mPostingdialog = new ECProgressDialog(SettingCommonActivity.this, "清空聊天记录");
	            mPostingdialog.show();
	            ECHandlerHelper handlerHelper = new ECHandlerHelper();
	            handlerHelper.postRunnOnThead(new Runnable() {
	                @Override
	                public void run() {
						IMPluginManager.getManager().clearAllChatRecord();
	                    runOnUiThread(new Runnable() {
	                        @Override
	                        public void run() {
	                            dismissPostingDialog();
	                        }
	                    });
	                }
	            });
		    }
		});
		buildAlert.setTitle(R.string.app_tip);
		buildAlert.show();
	}
	
	
    /**
     * 关闭对话框
     */
    private void dismissPostingDialog() {
        if(mPostingdialog == null || !mPostingdialog.isShowing()) {
            return ;
        }
        mPostingdialog.dismiss();
        mPostingdialog = null;
    }


	/**
	 * 初始化接收新消息通知设置参数  包括声音与振动
	 */
	private void initNewsNotifySettings() {
		if (showNotify == null) {
			return;
		}
		boolean mShowNotifySetting = IMPluginManager.getManager().getReceiveMessagesNotifySetting();
		showNotify.setChecked(mShowNotifySetting);
		showNotify.showDivider(true);
		
		if(!mShowNotifySetting){
			audio.setVisibility(View.GONE);
			vibrate.setVisibility(View.GONE);
			showNotify.showDivider(false);
			return;
		}
		
		boolean mAudioSetting = IMPluginManager.getManager().getSoundNotifySetting();
		audio.setChecked(mAudioSetting);
		audio.setVisibility(View.VISIBLE);
		
		boolean mVibrateSetting =IMPluginManager.getManager().getShakeNotifySetting();
		vibrate.setChecked(mVibrateSetting);
		vibrate.setVisibility(View.VISIBLE);
	}
	private void initHeadSetting(){
		if(headSet == null){
			return;
		}
		boolean mHeadSetting=IMPluginManager.getManager().getHandSetSetting();
		headSet.setChecked(mHeadSetting);
	}

}
