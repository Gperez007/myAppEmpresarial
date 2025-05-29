package com.example.myapplication.activitys.util;

import java.util.HashMap;

public class Constant {

    public static final String BASE_URL = "http://192.168.73.199:8080/";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER_ID = "usuarioID";
    public static final String KEY_COLLECTION_USER = "user";
    public static final String KEY_PREFERENCE_NAME = "SingIn";
    public static final String KEY_NAME = "Nombre";
    public static final String KEY_EMAIL= "Email";
    public static final String KEY_USER = "user";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_ID= "senderId";
    public static final String KEY_RECEIVER_ID= "receiverId";
    public static final String KEY_MESSAGE= "message";
    public static final String KEY_TIMESTAMP= "timeStamp";
    public static final String KEY_COLECTION_CONVERSATION= "conversation";
    public static final String KEY_SENDER_NAME= "senderName";
    public static final String KEY_RECEIVER_NAME= "receiverName";
    public static final String KEY_RECEIVER_IMAGE= "receiverImage";
    public static final String KEY_SENDER_IMAGE= "senderImage";
    public static final String KEY_LAST_MESAGGE= "lastMessage";
    public static final String KEY_AVAILABILITY= "availability";
    public static final String REMOTE_MSG_AUTORIZATION= "Authorization";
    public static final String REMOTE_MSG_CONTENT_TIPE = "Content-Type";
    public static final String KEY_IS_SIGNED_IN = "SingIn";

    public static final String KEY_IS_SIGNED_EMPRESA = "SingInEmpresa";
    public static final String KEY_EMPRESA_ID = "3";

    public static final String REMOTE_MGS_DATA = "data";
    public static final String REMOTE_MGS_REGISTRATION_IDS = "registration_ids";

    public static HashMap<String, String> remoteMsgHeader = null;
    public static HashMap<String, String>  getRemoteMsgHeader(){

        if(remoteMsgHeader == null){

            remoteMsgHeader = new HashMap<>();
            remoteMsgHeader.put(REMOTE_MSG_AUTORIZATION,
             "key=AAAAxUR97Tk:APA91bHBSgyy24QTUI1IzGe71Kfmuc7bkRcy-97pb32z9YrFfCWQDt7HxCOTwiZAhduoiGhIbvBcADMRUSwpiX2_-HhiUHkLHUxMR9019rjYGXWMXU6jzS0euNeSM0WT80bzHaKkthyW"

            );

            remoteMsgHeader.put(REMOTE_MSG_CONTENT_TIPE, "application/json");

        }

        return remoteMsgHeader;
    }

}
