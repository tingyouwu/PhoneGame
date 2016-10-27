package com.wty.app.phonegame;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.wty.app.phonegame.adapter.DialogCenterListAdapter;
import com.wty.app.phonegame.bluetooth.BluetoothChatService;
import com.wty.app.phonegame.bluetooth.DeviceListActivity;
import com.wty.app.phonegame.event.RefreshEvent;
import com.wty.app.phonegame.service.MusicServiceManager;
import com.wty.app.phonegame.utils.PreferenceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.wty.app.phonegame.bluetooth.BluetoothChatService.DEVICE_NAME;
import static com.wty.app.phonegame.bluetooth.BluetoothChatService.MESSAGE_DEVICE_NAME;
import static com.wty.app.phonegame.bluetooth.BluetoothChatService.MESSAGE_READ;
import static com.wty.app.phonegame.bluetooth.BluetoothChatService.MESSAGE_STATE_CHANGE;
import static com.wty.app.phonegame.bluetooth.BluetoothChatService.MESSAGE_TOAST;
import static com.wty.app.phonegame.bluetooth.BluetoothChatService.MESSAGE_WRITE;
import static com.wty.app.phonegame.bluetooth.BluetoothChatService.TOAST;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    Button btn_start;
    Button btn_end;
    ImageView btn_connect;
    TextView tv_setting;
    TextView tv_checkkey;

    Map<Integer,String> msgMap = new LinkedHashMap<>();
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;
    // Name of the connected device
    private String mConnectedDeviceName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        btn_end = (Button) findViewById(R.id.btn_endgame);
        btn_start = (Button) findViewById(R.id.btn_startgame);
        btn_connect = (ImageView) findViewById(R.id.btn_connect);
        tv_setting = (TextView) findViewById(R.id.tv_setting);
        tv_checkkey = (TextView) findViewById(R.id.tv_checkkey);

        btn_start.setOnClickListener(this);
        btn_end.setOnClickListener(this);
        btn_connect.setOnClickListener(this);
        tv_setting.setOnClickListener(this);
        tv_checkkey.setOnClickListener(this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "手机无蓝牙设备", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }else{

            if(!mBluetoothAdapter.isEnabled()){
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
            }else{
               // Initialize the BluetoothChatService to perform bluetooth connections
                mChatService = new BluetoothChatService(this, mHandler);
            }
        }
        msgMap.put(1,"容易");
        msgMap.put(2,"中级");
        msgMap.put(3,"困难");
        tv_setting.setText(msgMap.get(PreferenceUtil.getInstance().getLevel()));

    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicServiceManager.stopService(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicServiceManager.startService(this);
        EventBus.getDefault().register(this);
        if (mChatService != null && mBluetoothAdapter.isEnabled()) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) mChatService.stop();
        if(mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.disable();
        }
        // TODO Auto-generated method stub
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_endgame){
            finish();
        }else if(v.getId()==R.id.btn_startgame){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else if(v.getId()==R.id.btn_connect){
            Intent serverIntent = new Intent(LoginActivity.this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        }else if(v.getId()==R.id.tv_setting){
            showPhotoDialog();
        }else if(v.getId()==R.id.tv_checkkey){
            Intent serverIntent = new Intent(LoginActivity.this, CheckKeyActivity.class);
            startActivity(serverIntent);
        }
    }

    @Subscribe
    public void onEventMainThread(RefreshEvent event){
        Log.d("Login wutingyou:",event.getMsg());
        if(event.getMsg().equals("2")){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else if(event.getMsg().equals("1")){
            //切换难易程度
            int level = PreferenceUtil.getInstance().getLevel();
            if(level==1){
                //从容易-》中级
                PreferenceUtil.getInstance().writePreferences(PreferenceUtil.MAX,11000);
                PreferenceUtil.getInstance().writePreferences(PreferenceUtil.EACH,1000);
                PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LEVEL,2);
                tv_setting.setText(msgMap.get(PreferenceUtil.getInstance().getLevel()));
            }else if(level==2){
                //从中级-》困难
                PreferenceUtil.getInstance().writePreferences(PreferenceUtil.MAX,10500);
                PreferenceUtil.getInstance().writePreferences(PreferenceUtil.EACH,500);
                PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LEVEL,3);
                tv_setting.setText(msgMap.get(PreferenceUtil.getInstance().getLevel()));
            }else{
                //从困难-》容易
                PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LEVEL,1);
                PreferenceUtil.getInstance().writePreferences(PreferenceUtil.MAX,12000);
                PreferenceUtil.getInstance().writePreferences(PreferenceUtil.EACH,2000);
                tv_setting.setText(msgMap.get(PreferenceUtil.getInstance().getLevel()));
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    if(mChatService == null){
                        mChatService = new BluetoothChatService(this, mHandler);
                        mChatService.start();
                    }
                    mChatService.connect(device);
                }
                break;

            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                } else {
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    /**
     * 弹出底部框
     */
    private void showPhotoDialog() {
        List<String> data = new ArrayList<String>();
        data.add("容易");
        data.add("中级");
        data.add("困难");

        final DialogPlus dialog = DialogPlus.newDialog(LoginActivity.this)
                .setContentHolder(new ListHolder())
                .setCancelable(true)
                .setGravity(Gravity.BOTTOM)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setContentBackgroundResource(R.drawable.bg_dialog_list)
                .setAdapter(new DialogCenterListAdapter(LoginActivity.this, data))
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        switch (position){
                            case 0://
                                PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LEVEL,1);
                                PreferenceUtil.getInstance().writePreferences(PreferenceUtil.MAX,12000);
                                PreferenceUtil.getInstance().writePreferences(PreferenceUtil.EACH,2000);
                                tv_setting.setText(msgMap.get(PreferenceUtil.getInstance().getLevel()));
                                dialog.dismiss();
                                break;
                            case 1://
                                PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LEVEL,2);
                                PreferenceUtil.getInstance().writePreferences(PreferenceUtil.MAX,11000);
                                PreferenceUtil.getInstance().writePreferences(PreferenceUtil.EACH,1000);
                                tv_setting.setText(msgMap.get(PreferenceUtil.getInstance().getLevel()));
                                dialog.dismiss();
                                break;
                            case 2:
                                PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LEVEL,3);
                                PreferenceUtil.getInstance().writePreferences(PreferenceUtil.MAX,10500);
                                PreferenceUtil.getInstance().writePreferences(PreferenceUtil.EACH,500);
                                tv_setting.setText(msgMap.get(PreferenceUtil.getInstance().getLevel()));
                                dialog.dismiss();
                                break;
                            default:
                                break;
                        }

                    }
                })
                .create();
        dialog.show();
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            Toast.makeText(getApplicationContext(), "正在连接该蓝牙设备", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d("wutingyou","收到按键事件："+readMessage);
                    if(readMessage != null){
                        if(readMessage.startsWith("1"))
                            readMessage = "1";
                        if(readMessage.startsWith("2"))
                            readMessage = "2";
                        EventBus.getDefault().post(new RefreshEvent(readMessage));
                    }

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "连接上 "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
