package com.example.administrator.ourlovehut;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.litepal.crud.LitePalSupport;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@SuppressLint("Registered")
public class CommonMethodActivity extends AppCompatActivity {
    public static String resultData="";
    public Call call;
    //设备ID,与标识符
    public int deviceId = 11496;
    //执行器
    public String apiTag_fengshan = "nl_fan";
    public String apiTag_dengguang = "nl_lamp";
    public String apiTag_duoji1 = "nl_steeringengine1";
    public String apiTag_duoji2 = "nl_steeringengine2";
    //传感器
    public String apiTag_temp = "nl_temperature";
    public String apiTag_weighing = "nl_weighing";
    public String apiTag_light = "nl_light";
    /*******************************************Intent界面切换,并传递AccessToken值**********************************************/
    public void changeUI(Context fromActivyty , Class toActivity,String Accesstoken){
        Intent intent = new Intent(fromActivyty ,toActivity);
        intent.putExtra("Accesstoken",Accesstoken);
        startActivity(intent);
    }
    /*******************************************请求项目**********************************************/
    public void getDataWithOkHttpGet(final String UrlSuffix, final String accessToken){

                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://api.nlecloud.com/"+UrlSuffix)
                        .header("AccessToken",accessToken)
                        .build();
                 call =okHttpClient.newCall(request);
    }
    /*******************************************控制设备**********************************************/
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public void sendDataWithOkHttpPost(final String UrlSuffix, final String accessToken,String cmdsCode
    ,int deviceId , String apiTag){


        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON,""+cmdsCode);//创建发送Json格式用的body
        Request request = new Request.Builder()
                .url("http://api.nlecloud.com/"+UrlSuffix+"?deviceId="+deviceId+"&apiTag="+apiTag)
                .addHeader("AccessToken",accessToken)
                .post(body)
                .build();
        call =okHttpClient.newCall(request);
    }

    /*******************************************登录Json表**********************************************/
    public static class JsonData_Login {

        //LoginResult {
        private ResultObj ResultObj;
        private int Status;
        private int StatusCode;
        private String Msg;
        private Object ErrorObj;

        public static class ResultObj {
            private int UserID;
            private String UserName;
            private String Email;
            private String Telephone;
            private boolean Gender;
            private int CollegeID;
            private String CollegeName;
            private String RoleName;
            private int RoleID;
            private String AccessToken;
            private String ReturnUrl;
            private String DataToken;

            public String getAccessToken() {
                return AccessToken;
            }

            public void setAccessToken(String accessToken) {
                AccessToken = accessToken;
            }
        }



        /***************************************id****************************************/
        public int getStatus() {
            return Status;
        }
        public void setStatus(int Status) {
            this.Status = Status;
        }
        /***************************************id****************************************/
        public int getStatusCode() {
            return StatusCode;
        }
        public void setStatusCode(int StatusCode) {
            this.StatusCode = StatusCode;
        }
        /***************************************id****************************************/
        public String getMsg() {
            return Msg;
        }
        public void setMsg(String Msg) {
            this.Msg = Msg;
        }

        /***************************************id****************************************/
        public Object getErrorObj() {
            return ErrorObj;
        }

        public JsonData_Login.ResultObj getResultObj() {
            return ResultObj;
        }
        /***************************************id****************************************/
        public void setResultObj(JsonData_Login.ResultObj resultObj) {
            ResultObj = resultObj;
        }

        public void setErrorObj(Object ErrorObj) {
            this.ErrorObj = ErrorObj;
        }
    }
    /*******************************************单个项目Json表**********************************************/
    public static class JsonData_Project{
        private ProjectInfoDTO ResultObj;
        private int Status;
        private int StatusCode;
        private String Msg;
        private Object ErrorObj;
        public static class ProjectInfoDTO{
            private int ProjectID;
            private String Name;
            private String Industry;
            private String NetWorkKind;
            private String ProjectTag;
            private String CreateDate;
            private String Remark;

            public int getProjectID() {
                return ProjectID;
            }

            public String getName() {
                return Name;
            }

            public String getIndustry() {
                return Industry;
            }

            public String getNetWorkKind() {
                return NetWorkKind;
            }

            public String getProjectTag() {
                return ProjectTag;
            }

            public String getCreateDate() {
                return CreateDate;
            }

            public String getRemark() {
                return Remark;
            }
        }

        public ProjectInfoDTO getResultObj() {
            return ResultObj;
        }

        public void setResultObj(ProjectInfoDTO resultObj) {
            ResultObj = resultObj;
        }

        public int getStatus() {
            return Status;
        }

        public void setStatus(int status) {
            Status = status;
        }

        public int getStatusCode() {
            return StatusCode;
        }

        public void setStatusCode(int statusCode) {
            StatusCode = statusCode;
        }

        public String getMsg() {
            return Msg;
        }

        public void setMsg(String msg) {
            Msg = msg;
        }

        public Object getErrorObj() {
            return ErrorObj;
        }

        public void setErrorObj(Object errorObj) {
            ErrorObj = errorObj;
        }
    }
    /*******************************************单个设备Json表**********************************************/
    public static class JsonData_Devices{
        private ResultObj ResultObj;
        private int Status;
        private int StatusCode;
        private String Msg;
        private Object ErrorObj;
        public static class ResultObj{
            private int DeviceID;
            private String Name;
            private Datas Datas;
            public static class Datas{
                private String ApiTag;
                private Object Value;
                private String RecordTime;

                public String getApiTag() {
                    return ApiTag;
                }

                public Object getValue() {
                    return Value;
                }

                public String getRecordTime() {
                    return RecordTime;
                }
            }

            public int getDeviceID() {
                return DeviceID;
            }

            public String getName() {
                return Name;
            }

            public JsonData_Devices.ResultObj.Datas getDatas() {
                return Datas;
            }
        }

        public JsonData_Devices.ResultObj getResultObj() {
            return ResultObj;
        }

        public int getStatus() {
            return Status;
        }

        public int getStatusCode() {
            return StatusCode;
        }

        public String getMsg() {
            return Msg;
        }

        public Object getErrorObj() {
            return ErrorObj;
        }
    }
    /*******************************************单个设备Json表_传感器**********************************************/
    public static class JsonData_DevicesSensors_chuang {
        private ResultObj ResultObj;
        private int Status;
        private int StatusCode;
        private String Msg;
        private Object ErrorObj;
        public static class ResultObj{
            private String ApiTag;
            private byte Groups;
            private byte Protocol;
            private String Name;
            private String createData;
            private byte TransType;
            private byte DataType;
            private Object TypeAttrs;
            private int DevicesID;
            private String SensorType;
            private Object Value;
            private String RecordTime;
            //传感器
            private String Unit;
            //执行器
//            private byte OperType;
//            private String OperTypeAttrs;

            public Object getValue() {
                return Value;
            }
        }

        public int getStatus() {
            return Status;
        }

        public int getStatusCode() {
            return StatusCode;
        }

        public String getMsg() {
            return Msg;
        }

        public Object getErrorObj() {
            return ErrorObj;
        }

        public JsonData_DevicesSensors_chuang.ResultObj getResultObj() {
            return ResultObj;
        }
    }
    /*******************************************单个设备Json表_执行器**********************************************/
    public static class JsonData_DevicesSensors_zhixin{
        private ResultObj ResultObj;
        private int Status;
        private int StatusCode;
        private String Msg;
        private Object ErrorObj;
        public static class ResultObj{
            private String ApiTag;
            private byte Groups;
            private byte Protocol;
            private String Name;
            private String createData;
            private byte TransType;
            private byte DataType;
            private Object TypeAttrs;
            private int DevicesID;
            private String SensorType;
            private int Value;
            private String RecordTime;
//            //传感器
//            private String Unit;
            //执行器
            private byte OperType;
            private String OperTypeAttrs;

            public int getValue() {
                return Value;
            }
        }

        public JsonData_DevicesSensors_zhixin.ResultObj getResultObj() {
            return ResultObj;
        }

        public int getStatus() {
            return Status;
        }

        public int getStatusCode() {
            return StatusCode;
        }

        public String getMsg() {
            return Msg;
        }

        public Object getErrorObj() {
            return ErrorObj;
        }
    }
    /*******************************************设备控制Json表**********************************************/
    public static class JsonData_Cmds{
        private int Status;
        private int StatusCode;
        private String Msg;
        private Object ErrorObj;

        public int getStatus() {
            return Status;
        }

        public int getStatusCode() {
            return StatusCode;
        }

        public String getMsg() {
            return Msg;
        }

        public Object getErrorObj() {
            return ErrorObj;
        }
    }
    /*******************************************LitePal数据库**********************************************/
    public class db001 extends LitePalSupport{
        String data001_db001;
        String data002_db001;
    }

    /*******************************************判断按钮是否快速点击**********************************************/
    private static long lastClickTime;
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1000) {
            return false;
        }
        lastClickTime = time;
        return true;
    }
    /************************************************获取当前时间**************************************************************/
    public String getNewTime(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        Log.d("当前日期时间",""+simpleDateFormat.format(date));
        return simpleDateFormat.format(date);
    }
    /*******************************************延时（毫秒|非线程）***********************************************************/
    public void delay(final long mills){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(mills);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /*******************************************设置数字显示随拖动改变***********************************************************/
    void changeTVDataWithSeekBar(SeekBar sb1, SeekBar sb2, final TextView sb1_tv, final TextView sb2_tv){
        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sb1_tv.setText(progress+"");
                Log.d("progress_1",progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sb2_tv.setText(progress+"");
                Log.d("progress_2",progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
