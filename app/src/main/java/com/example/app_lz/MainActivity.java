package com.example.app_lz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.suke.widget.SwitchButton;

import Util.HttpUtil;
import Util.MutilClickUtil;
import Util.VibratorUtil;
import gson.SensorBean;

public class MainActivity extends AppCompatActivity implements SwitchButton.OnCheckedChangeListener{

    private TextView tv_humi_earth;
    private TextView tv_humi_air;
    private TextView tv_p;
    private TextView tv_d;

    private SwitchButton p_btn;
    private SwitchButton d_btn;
    private Button intro_btn;
    private Button conn_btn;

    private SwipeRefreshLayout swipeRefreshLayout;

    private Handler handler;
    private int HumiEarthData;
    private int HumiAirData;

    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

//    private String Url_Get = "http://api.heclouds.com/devices/9080309/datapoints";
//    private String Url_Post = "http://api.heclouds.com/devices/9080309/datapoints?type=5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        //图片覆盖状态栏
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        //绑定控件
        tv_humi_earth = (TextView) findViewById(R.id.earth_text);
        tv_humi_air = (TextView) findViewById(R.id.air_text);
        tv_p = (TextView)findViewById(R.id.p_text);
        tv_d = (TextView)findViewById(R.id.d_text);
        p_btn = (SwitchButton) findViewById(R.id.p_switch_bt);
        d_btn = (SwitchButton) findViewById(R.id.d_switch_bt);
        intro_btn = (Button) findViewById(R.id.intro_btn);
        conn_btn = (Button) findViewById(R.id.connet_btn);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        //下拉进度条颜色
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        Handler_yee();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isOnline()){
                    Toast.makeText(MainActivity.this, "网络未连接，请先连接网络！", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else {
                    Toast.makeText(MainActivity.this, "刷新数据中...", Toast.LENGTH_SHORT).show();
                    refreshData();
                }
            }
        });
        p_btn.setOnCheckedChangeListener(this);
        d_btn.setOnCheckedChangeListener(this);

        NetworkChange();
        if (isOnline()){
            sendRequest_Get_WithOkHttp_yee();
        }
    }

    public void Handler_yee(){
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    //UI操作
                    tv_humi_earth.setText(getHumiEarthData()+"%");
                }
                else if (msg.what == 2){
                    tv_humi_air.setText(getHumiAirData()+"%");
                }
            }
        };
    }

    //监测网络变化
    public void NetworkChange(){
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    //将动态注册的广播接收器 取消注册
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    //内部类继承至广播接收器 监听网络变化的广播 并做处理
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isAvailable()){
                Toast.makeText(context, "网络已连接", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context, "网络已断开", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //判断是否联网
    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void sendRequest_Get_WithOkHttp_yee(){
        new Thread() {
            @Override
            public void run() {
                getData("356403","410161",1);
            }
        }.start();
        //补充空气湿度传感器发送的请求
    }

    private void sendRequest_Post_WithOkHttp_yee(final String on_offID, final String value){
        new Thread(new Runnable() {
            @Override
            public void run() {
                postData("356403",on_offID, value);
            }
        }).start();
    }

    public void getData(String device, String sensorID, int type) {
        try{
            String url = "http://api.yeelink.net/v1.1/device/"+device+"/sensor/"+sensorID+"/datapoints";
            String responseData = HttpUtil.sendGetRequest_yee(url);
            SensorBean sensorBean = parseResponse_yee(responseData);
            switch (type) {
                case 1:
                    setHumiEarthData(sensorBean.getValue());
                    break;
                case 2:
                    setHumiAirData(sensorBean.getValue());
                    break;
                default:break;
            }
            Message message = new Message();
            message.what = type;
            handler.sendMessage(message);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("信息：","建立连接失败！");
        }
    }

    public void postData(String device, String sensorID, String value){
        try {
            //发送Post请求
            String url = "http://api.yeelink.net/v1.0/device/"+device+"/sensor/"+sensorID+"/datapoints";
            String responseData = HttpUtil.sendPostRequest_yee(url, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SensorBean parseResponse_yee(String response){
        Gson gson = new Gson();
        SensorBean sensorBean = gson.fromJson(response, SensorBean.class);
        return sensorBean;
    }

    private void refreshData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(2000);
                }catch (InterruptedException ex){
                    ex.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //发送获取数据请求 oneNet方法
//                        sendRequest_Get_WithOkHttp(Url_Get);
                        //yeelink方法
                        sendRequest_Get_WithOkHttp_yee();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        if (!isOnline()){
            Toast.makeText(MainActivity.this, "网络未连接，请先连接网络！", Toast.LENGTH_LONG).show();
        }
        else if (!MutilClickUtil.isNormalClick()){
            //添加操作频率限制 5秒
            Toast.makeText(MainActivity.this, "操作频率过快", Toast.LENGTH_SHORT).show();
        }
        else if (view.getId() == R.id.d_switch_bt)
        {
            VibratorUtil.Vibrate(MainActivity.this, 50);
            if(!isChecked)
            {
                tv_d.setText("滴灌已关闭");
                //发送0数据，断开继电器开关
//                String jsonString = ",;D_On-off,0";
//                sendRequest_Post_WithOkHttp(Url_Post, jsonString);
                //yeelink
                sendRequest_Post_WithOkHttp_yee("410166", "0");
            }
            else
            {
                tv_d.setText("滴灌已开启");
                //发送
//                String jsonString = ",;D_On-off,1";
//                sendRequest_Post_WithOkHttp(Url_Post, jsonString);
                //yeelink
                sendRequest_Post_WithOkHttp_yee("410166", "1");
            }
            //Toast.makeText(MainActivity.this, "已点击滴灌开关!", Toast.LENGTH_SHORT).show();
        }
        else if(view.getId() == R.id.p_switch_bt)
        {
            VibratorUtil.Vibrate(MainActivity.this, 50);
            if(!isChecked)
            {
                tv_p.setText("喷雾已关闭");
                //发送0数据，断开继电器开关
//                String jsonString = ",;P_On-off,0";
//                sendRequest_Post_WithOkHttp(Url_Post, jsonString);
                //yeelink 参数为开关传感器号，传递参数值string类型
                sendRequest_Post_WithOkHttp_yee("410167", "0");
            }
            else
            {
                tv_p.setText("喷雾已开启");
                //发送
//                String jsonString = ",;P_On-off,1";
//                sendRequest_Post_WithOkHttp(Url_Post, jsonString);
                //yeelink
                sendRequest_Post_WithOkHttp_yee("410167", "1");
            }
            //Toast.makeText(MainActivity.this, "已点击喷雾开关!", Toast.LENGTH_SHORT).show();
        }

    }

//    private void sendRequest_Get_WithOkHttp(final String Url_Get){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    //发送get请求
//                    String responseData =  HttpUtil.sendGetRequest(Url_Get);
//                    //setTempData(responseData);
//                    //解析json字符串，映射到dataBean类
//                    DataBean dataBean = parseResponse(responseData);
//                    //解析出的结果赋值给对应数据变量
//                    setData(dataBean);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                //发送消息给handle进行控件操作
//                Message message = new Message();
//                message.what = 1;
//                handler.sendMessage(message);
//            }
//        }).start();
//    }
//
//    private void sendRequest_Post_WithOkHttp(final String Url_Post, final String jsonString){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    //发送Post请求
//                    String responseData = HttpUtil.sendPostRequest(Url_Post, jsonString);
//                    //解析
//                    DataBean dataBean = parseResponse(responseData);
//                    //解析出的结果赋值给对应数据变量
//                    setData(dataBean);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
////                //发送消息给handle进行控件操作
////                Message message = new Message();
////                message.what = 2;
////                handler.sendMessage(message);
//            }
//        }).start();
//    }
//
//    private DataBean parseResponse(String response){
//        Gson gson = new Gson();
//        DataBean dataBean = gson.fromJson(response, DataBean.class);
//        return  dataBean;
//    }

//    public void setData(DataBean dataBean){
//        setStatus(dataBean.getError());
//        setTempData(dataBean.getData().getDatastreams().get(2).getDatapoints().get(0).getValue());
//        setHumiAirData(dataBean.getData().getDatastreams().get(1).getDatapoints().get(0).getValue());
//        setHumiEarthData(dataBean.getData().getDatastreams().get(3).getDatapoints().get(0).getValue());
//        //补充填入的控件
//    }


//    oneNet测试用
//    public int getTempData() {
//        return TempData;
//    }
//
//    public void setTempData(int tempData) {
//        TempData = tempData;
//    }
//
//    public int getHumiAirData() {
//        return HumiAirData;
//    }
//
//    public void setHumiAirData(int humiAirData) {
//        HumiAirData = humiAirData;
//    }

    public int getHumiEarthData() {
        return HumiEarthData;
    }

    public void setHumiEarthData(int humiEarthData) {
        HumiEarthData = humiEarthData;
    }

    public int getHumiAirData() {
        return HumiAirData;
    }

    public void setHumiAirData(int humiAirData) {
        HumiAirData = humiAirData;
    }
}
