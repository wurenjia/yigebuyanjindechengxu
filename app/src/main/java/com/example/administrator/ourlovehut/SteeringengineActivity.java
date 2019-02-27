package com.example.administrator.ourlovehut;
/**                            _ooOoo_
 //                           o8888888o
 //                           88" . "88
 //                           (| -_- |)
 //                            O\ = /O
 //                        ____/`---'\____
 //                      .   ' \\| |// `.
 //                       / \\||| : |||// \
 //                     / _||||| -:- |||||- \
 //                       | | \\\ - /// | |
 //                     | \_| ''\---/'' | |
 //                      \ .-\__ `-` ___/-. /
 //                   ___`. .' /--.--\ `. . __
 //                ."" '< `.___\_<|>_/___.' >'"".
 //               | | : `- \`.;`\ _ /`;.`/ - ` : | |
 //                 \ \ `-. \_ __\ /__ _/ .-` / /
 //         ======`-.____`-.___\_____/___.-`____.-'======
 //                            `=---='
 //
 //         .............................................
 //                  佛祖保佑             永无BUG
 */
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SteeringengineActivity extends CommonMethodActivity {
    //AccessToken
    String accessTokenData_SGG = "";//上一个活动创递过来的AccessToken值
    //定义进度条
    SeekBar nl_steeringengine1_SB_sgg;
    SeekBar nl_steeringengine2_SB_sgg;
    //定义进度条进度显示
    TextView nl_steeringengine1_TV_sgg;
    TextView nl_steeringengine2_TV_sgg;
    //定义按钮
    Button sggSetting_btn_SGG;
    Button sggNowState_btn_SGG;
    //舵机放回数据
    String resultData = "";
    String isSendOk = "";
    int sggState;
    int sggState_sgg1;
    int sggState_sgg2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steeringengine);
        nl_steeringengine1_SB_sgg = findViewById(R.id.nl_steeringengine1_SB_sgg);
        nl_steeringengine2_SB_sgg = findViewById(R.id.nl_steeringengine2_SB_sgg);
        nl_steeringengine1_TV_sgg = findViewById(R.id.nl_steeringengine1_TV_sgg);
        nl_steeringengine2_TV_sgg = findViewById(R.id.nl_steeringengine2_TV_sgg);

        sggSetting_btn_SGG = findViewById(R.id.sggSetting_btn_SGG);
        sggNowState_btn_SGG = findViewById(R.id.sggNowState_btn_SGG);
        /************************************************显示进度*************************************************/
        nl_steeringengine1_TV_sgg.setText(String.valueOf(nl_steeringengine1_SB_sgg.getProgress()));
        nl_steeringengine2_TV_sgg.setText(String.valueOf(nl_steeringengine2_SB_sgg.getProgress()));

        Intent intent = getIntent();
        accessTokenData_SGG = intent.getStringExtra("Accesstoken");
        Log.d("accessTokenData_SGG",accessTokenData_SGG);
        /************************************************控制事件*************************************************/
        //获取舵机状态
        IntSGGState();
        changeTVDataWithSeekBar(nl_steeringengine1_SB_sgg,nl_steeringengine2_SB_sgg
                ,nl_steeringengine1_TV_sgg,nl_steeringengine2_TV_sgg);//设置数字显示随拖动改变
        contorlSGGBtn();//控制舵机
    }
/////////////////////////////////////////////////onCreate结束///////////////////////////////////////////////////////////////////


    //设置按钮
     void contorlSGGBtn(){
        sggSetting_btn_SGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        contorlSGGToClound(nl_steeringengine1_TV_sgg.getText().toString(),apiTag_duoji1);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        contorlSGGToClound(nl_steeringengine2_TV_sgg.getText().toString(),apiTag_duoji2);
                    }
                }).start();

            }
        });
        sggNowState_btn_SGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntSGGState();
            }
        });
     }

    //发送舵机控制码
    void contorlSGGToClound(String cmdsCode, final String apiTag){
        sendDataWithOkHttpPost("Cmds",accessTokenData_SGG,"\""+cmdsCode+"\"",deviceId,apiTag);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    if (response.body() != null) {
                        resultData = response.body().string();
                        Log.d("SGGData",resultData);
                        Gson gson = new Gson();
                        java.lang.reflect.Type type = new TypeToken<JsonData_Cmds>() {}.getType();
                        JsonData_Cmds jsonData_cmds = gson.fromJson(resultData,type);
                        isSendOk = String.valueOf(jsonData_cmds.getStatus());
                        Log.d(apiTag+"是否成功发送请求", isSendOk);
                        response.close();
                        Message message = new Message();
                        message.what=  0x03;
                        handler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    void IntSGGState(){
        getSGGStateFromClound(apiTag_duoji1);
        delay(1000);
        getSGGStateFromClound(apiTag_duoji2);

    }
    //获取舵机状态
    void getSGGStateFromClound(final String apiTag){
        getDataWithOkHttpGet("devices/"+deviceId+"/Sensors/"+apiTag,accessTokenData_SGG);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    Thread.sleep(1000);
                    if (response.body() != null) {
                        resultData = response.body().string();
                        Log.d("sggResult",resultData);
                        /*******************************************解析风扇Json数据**********************************************/
                        Gson gson = new Gson();
                        java.lang.reflect.Type type = new TypeToken<JsonData_DevicesSensors_zhixin>() {}.getType();
                        JsonData_DevicesSensors_zhixin jsonData_devicesSensors_zhixin = gson.fromJson(resultData,type);

                        sggState =  jsonData_devicesSensors_zhixin.getResultObj().getValue();
                        Log.d("sggState"+apiTag, String.valueOf(sggState));
                        if(apiTag.equals(apiTag_duoji1)){
                            sggState_sgg1 = sggState;
                            Log.d("apiTag_duoji1",sggState+""+apiTag);
                        }else if(apiTag.equals(apiTag_duoji2)){
                            sggState_sgg2 = sggState;
                            Log.d("apiTag_duoji2",sggState+""+apiTag);
                            Message message = new Message();
                            message.what = 0x04;
                            handler.sendMessage(message);
                        }
                        Thread.sleep(2000);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x03:
                    Toast.makeText(SteeringengineActivity.this, "请求发送成功", Toast.LENGTH_SHORT).show();
                    break;
                case  0x04:
                    nl_steeringengine1_SB_sgg.setProgress(sggState_sgg1);
                    nl_steeringengine1_TV_sgg.setText(sggState_sgg1 +"");
                    Log.d("sggState_sgg1_TV001", String.valueOf(sggState_sgg1));
                    nl_steeringengine2_SB_sgg.setProgress(sggState_sgg2);
                    nl_steeringengine2_TV_sgg.setText(sggState_sgg2 +"");
                    Log.d("sggState_sgg2_TV001", String.valueOf(sggState_sgg2));
//                    IntSGGState();

                    break;
                case 0x05:

                    break;
            }
        }
    };
}
