package com.example.administrator.ourlovehut;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.litepal.crud.LitePalSupport;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Response;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends CommonMethodActivity {
    String accessTokenData_main=" ";//上个活动传递过来的数据
    //按钮
    Button fengshan_btn_open_main;//手动开关
    Button fengshan_btn_close_main;
    Button autoControl_open_btn_main;//自动开关
    Button autoControl_close_btn_main;
    Button maxminSet_btn_main;//温度范围设置
    Button getdata_btn_main; // 手动请求数据
    Button toSGG_btn_main; //跳转到舵机操作
    //TextView
    TextView tempNow_tv_main;//显示现在温度
    TextView projectData_tv_main;//显示历史温度
    //EditText
    EditText maxTemp_et_main;//设置温度范围输入框
    EditText minTemp_et_main;
    //图片
    GifImageView fengshan_gif_main;//风扇图片显示视图
    GifDrawable gifDrawable;//gif资源对象
    ImageView zhishi_iv_main; //指示灯显示视图
    //温度代码
    float temp_min_main;//温度下限
    float temp_max_main ;//温度上限
    Double temp_now_main = 25.3;//现在温度
    String projectData_data_main = "";
    //自动控制状态码（默认开启）
    int autoControl_btn_main_Code = 1;
    //风扇请求返回码
    String fengshanCodeFromClund="5";
    //风扇状态
     String fengshanState = "5.0";
    //是否改变可风扇状态（默认为是）
    boolean isOpenFengshan = false;
    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*****************************************初始化视图***************************************************************************************/
        fengshan_btn_open_main = findViewById(R.id.fengshan_btn_open_main);
        fengshan_btn_close_main = findViewById(R.id.fengshan_btn_close_main);
        autoControl_open_btn_main = findViewById(R.id.autoControl_open_btn_main);
        autoControl_close_btn_main = findViewById(R.id.autoControl_close_btn_main);
        maxminSet_btn_main = findViewById(R.id.maxminSet_btn_main);
        getdata_btn_main = findViewById(R.id.getdata_btn_main);

        tempNow_tv_main = findViewById(R.id.tempNow_tv_main);
        projectData_tv_main = findViewById(R.id.projectData_tv_main);

        maxTemp_et_main = findViewById(R.id.maxTemp_et_main);
        minTemp_et_main = findViewById(R.id.minTemp_et_main);

        fengshan_gif_main = findViewById(R.id.fengshan_gif_main);
        zhishi_iv_main = findViewById(R.id.zhishi_iv_main);

        toSGG_btn_main = findViewById(R.id.toSGG_btn_main);

        /*****************************************设置gif图***************************************************************************************/
        try {
            gifDrawable = new GifDrawable(getResources(),R.drawable.fengshan);
            gifDrawable.setSpeed(2.0f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*****************************************获取上个Activity传递过来的Accesstoken值***************************************************************************************/
        Intent intent = getIntent();
        accessTokenData_main = intent.getStringExtra("Accesstoken");
        Log.d("accessTokenData_main",accessTokenData_main);
        temp_min_main = Float.parseFloat(minTemp_et_main.getText().toString());
        temp_max_main = Float.parseFloat(maxTemp_et_main.getText().toString());
        projectData_tv_main.setMovementMethod(ScrollingMovementMethod.getInstance());//设置TextView的移动方式为滚动条
        toSGG_btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUI(MainActivity.this,SteeringengineActivity.class,accessTokenData_main);
            }
        });
        /*****************************************根据从平台获取的风扇状态，初始化风扇显示状态***************************************************************************************/
        getFengshanStateFromClount();
        /*****************************************按钮控制***************************************************************************************/
        //获取当前风扇设备状态
        getdata_btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFengshanStateFromClount();
            }
        });
        tempThresholdStting();//温度阈值
        controlFengshanBtn();//手动控制
        controlFengshanAuto();//自动控制
        getTempDataFromClund();//实时更新当前温度
    }
/////////////////////////////////////////////////onCreate结束///////////////////////////////////////////////////////////////////
//*******************************************数据库操作****************************************************************/
    void useDB(){
//        LitePalSupport.
    }
    //手动控制
    void controlFengshanBtn(){
        // 手动按钮点击事件
        fengshan_btn_open_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOpenFengshan){
                    fengshanOpen();
                    isOpenFengshan = false;
                }else{
                    Toast.makeText(MainActivity.this, "设备已经开启，请不要重复点击", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fengshan_btn_close_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOpenFengshan){
                    fengshanClose();
                    isOpenFengshan = true;
                }else{
                    Toast.makeText(MainActivity.this, "设备已经关闭，请不要重复点击", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 自动控制按钮点击事件
        autoControl_open_btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoControl_btn_main_Code = 1;
            }
        });
        autoControl_close_btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoControl_btn_main_Code = 0;
            }
        });
    }

    //自动控制开关判断
    void controlFengshanAuto(){
        if(autoControl_btn_main_Code==1){
            if(temp_now_main<temp_max_main && temp_now_main>temp_min_main){
                if(!isOpenFengshan){
                    fengshanClose();
                    isOpenFengshan = true;
                    Log.d("风扇开关","关闭");
                }
            }else{
                if(isOpenFengshan){
                    fengshanOpen();
                    isOpenFengshan = false;
                    Log.d("风扇开关","开启");
                }
            }
        }
    }

    /*****************************************风扇开关***************************************************************************************/
    //开启风扇
    void fengshanOpen(){
        contorlFengshanByPost("1");//向平台请求开启风扇
        if(fengshanCodeFromClund.equals("0")){
            fengshan_gif_main.setImageResource(R.drawable.fengshan004);//风扇转动
        }else {
            Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
        }
    }
    //关闭风扇
    void fengshanClose(){
        contorlFengshanByPost("0");//向平台请求关闭风扇
        if(fengshanCodeFromClund.equals("0")){
            fengshan_gif_main.setImageResource(R.drawable.fengshan02);//风扇静止
        }else {
            Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
        }
    }
    //温度阈值
    void tempThresholdStting(){
        maxminSet_btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp_min_main = Float.parseFloat(minTemp_et_main.getText().toString());
                temp_max_main = Float.parseFloat(maxTemp_et_main.getText().toString());
            }
        });
    }
    //获取温度数据
    void getTempDataFromClund(){

        getDataWithOkHttpGet("devices/"+deviceId+"/Sensors/"+apiTag_temp,accessTokenData_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    if (response.body() != null) {
                        resultData = response.body().string();
                        Log.d("nl_temperature",resultData);
                        /*******************************************解析温度Json数据**********************************************/
                        Gson gson = new Gson();
                        java.lang.reflect.Type type = new TypeToken<JsonData_DevicesSensors_chuang>() {}.getType();
                        JsonData_DevicesSensors_chuang jsonData_devicesSensors = gson.fromJson(resultData,type);
                        temp_now_main = (Double) jsonData_devicesSensors.getResultObj().getValue();
                    }
                    response.close();
                    Thread.sleep(3000);
                    Message message = new Message();
                    message.what = 0x01;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //获取风扇状态
    void getFengshanStateFromClount(){
        getDataWithOkHttpGet("devices/"+deviceId+"/Sensors/"+apiTag_fengshan,accessTokenData_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    if (response.body() != null) {
                        resultData = response.body().string();
                        Log.d("api_fengshan",resultData);
                        /*******************************************解析风扇Json数据**********************************************/
                        Gson gson = new Gson();
                        java.lang.reflect.Type type = new TypeToken<JsonData_DevicesSensors_zhixin>() {}.getType();
                        JsonData_DevicesSensors_zhixin jsonData_devicesSensors_zhixin = gson.fromJson(resultData,type);
                        fengshanState = String.valueOf(jsonData_devicesSensors_zhixin.getResultObj().getValue());
                        Log.d("fengshanState", String.valueOf(fengshanState));
                        Thread.sleep(3000);
                        Message message = new Message();
                        message.what = 0x02;
                        handler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //控制风扇设备
    void contorlFengshanByPost(String cmdsCode){
        sendDataWithOkHttpPost("Cmds",accessTokenData_main,cmdsCode,deviceId,apiTag_fengshan);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    if (response.body() != null) {
                        resultData = response.body().string();
                        Log.d("fengshan",resultData);
                        /*******************************************解析温度Json数据**********************************************/
                        Gson gson = new Gson();
                        java.lang.reflect.Type type = new TypeToken<JsonData_Cmds>() {}.getType();
                        JsonData_Cmds jsonData_cmds = gson.fromJson(resultData,type);
                        fengshanCodeFromClund = String.valueOf(jsonData_cmds.getStatus());
                        Log.d("fengshanCodeFromClund", fengshanCodeFromClund);
                        response.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //handler类，线程更新
    @SuppressLint("HandlerLeak") final Handler handler = new Handler(){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x01:
                    tempNow_tv_main.setText(""+temp_now_main+"℃");//设置当前温度
                    projectData_data_main = getNewTime()+" 温度："+temp_now_main+"\n"+projectData_data_main;
                    projectData_tv_main.setText(projectData_data_main);//设置历史数据显示
                    controlFengshanAuto();
                    getTempDataFromClund();//构成循环
                    break;
                case 0x02:
                    if(fengshanState.equals("1")){
                        fengshan_gif_main.setImageResource(R.drawable.fengshan004);
                        isOpenFengshan = false;
                    }else{
                        fengshan_gif_main.setImageResource(R.drawable.fengshan02);
                        isOpenFengshan = true;
                    }
                    break;
            }
        }
    };

}
