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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.wty.app.phonegame.bluetooth.BluetoothChatService;
import com.wty.app.phonegame.bluetooth.DeviceListActivity;
import com.wty.app.phonegame.event.FinishEvent;
import com.wty.app.phonegame.event.RefreshEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
        btn_end = (Button) findViewById(R.id.btn_endgame);
        btn_start = (Button) findViewById(R.id.btn_startgame);
        btn_connect = (ImageView) findViewById(R.id.btn_connect);

        btn_start.setOnClickListener(this);
        btn_end.setOnClickListener(this);
        btn_connect.setOnClickListener(this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "手机无蓝牙设备", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }else{
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (mChatService != null) {
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
        }
    }

    @Subscribe
    public void onEventMainThread(RefreshEvent event){
        Log.d("Login wutingyou:",event.getMsg());
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    @Subscribe
    public void onEventMainThread(FinishEvent event){
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
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
                    Log.d("收到蓝牙模块数据：wutingyou:",readMessage);
                    EventBus.getDefault().post(new RefreshEvent(readMessage));
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
