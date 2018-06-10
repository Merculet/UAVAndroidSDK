package io.merculet.uav.sdk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import io.merculet.uav.sdk.MConfiguration;
import io.merculet.uav.sdk.domain.event.EventsProxy;
import io.merculet.uav.sdk.log.DebugLog;
import io.merculet.uav.sdk.util.FileUtils;
import io.merculet.uav.sdk.util.Preconditions;
import io.merculet.uav.sdk.util.SPHelper;


public class MessageUtils {
    public static final String TYPE_COMMON = "0";
    //数据多于5000条时删除最早的1000条
    private static final int MAX_MESSAGE = 5000;
    //空间小于8M时不再存储
    private static final int FREE_SIZE = 8000000;

    /**
     * 插入统计消息
     *
     * @param c
     * @param
     */
    public static synchronized int getCount(Context c) {
        DBProvider provider = DBProvider.getDBProvider(c.getApplicationContext());
        return provider.getCount(DBHelp.TABLE_ANALYTICS);
    }

    /**
     * 插入统计消息
     *
     * @param
     */
    public static synchronized long insertMsg(final String data, final String type) {
        Context c = MConfiguration.get().getContext();
        if (Preconditions.isBlank(data)) {
            return -1;
        }

        DBProvider provider = DBProvider.getDBProvider(c);

        // 先判断数据库表里记录数是否超过5000,超过则删除第一条
        if (getCount(c) >= MAX_MESSAGE) {
            provider.excSQL("delete from " + DBHelp.TABLE_ANALYTICS + " limit 1000");
        }

        if (!checkSpaceFree()) {
            DebugLog.e("The disk is full,please free the disk.");
            return 0;
        }
        ContentValues value = new ContentValues();
        value.put(DBHelp.COLUMN_EVENT_TYPE, type);
        value.put(DBHelp.COLUMN_EVENT_DATA, data);
        value.put(DBHelp.COLUMN_EVENT_USERID, TextUtils.isEmpty(SPHelper.create().getUserId()) ? MConfiguration.get().userId : SPHelper.create().getUserId());
        return provider.insert(DBHelp.TABLE_ANALYTICS, value);
    }

    private static boolean checkSpaceFree() {
        return FileUtils.getTotalInternalMemorySize() > FREE_SIZE;
    }

    /**
     * delect msg by id
     *
     * @param id
     * @return
     */
    public static synchronized long deleteMsgByID(String id) {
        DBProvider provider = DBProvider.getDBProvider(MConfiguration.get().getContext());
        return provider.delete(DBHelp.TABLE_ANALYTICS, DBHelp.COLUMN_ID + "= ?", new String[]{id});
    }

    /**
     * 获取wifi状态下的数据
     *
     * @param context
     * @param selection
     * @param selectionArgs
     * @param exitDataID    exit_data ID
     * @return
     */
    private static synchronized MsgModel getEventMsgLimit(Context context, String selection,
                                                          String[] selectionArgs, String
                                                                  exitDataID, String limit) {

        MsgModel msgModel = new MsgModel();
        String eventID;
        String eventData;
        String eventType;
        String eventUserId;
        DBProvider provider = DBProvider.getDBProvider(context.getApplicationContext());
        Cursor cursor = provider.query(DBHelp.TABLE_ANALYTICS, new String[]{DBHelp.COLUMN_ID,
                DBHelp.COLUMN_EVENT_DATA, DBHelp.COLUMN_EVENT_TYPE, DBHelp.COLUMN_EVENT_USERID}, selection, selectionArgs, null, limit);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    eventID = cursor.getString(cursor.getColumnIndex(DBHelp.COLUMN_ID));
                    //如果等于退出ID,则跳过
                    if (eventID.equals(exitDataID)) {
                        continue;
                    }
                    eventData = cursor.getString(cursor.getColumnIndex(DBHelp.COLUMN_EVENT_DATA));
                    eventType = cursor.getString(cursor.getColumnIndex(DBHelp.COLUMN_EVENT_TYPE));
                    eventUserId = cursor.getString(cursor.getColumnIndex(DBHelp.COLUMN_EVENT_USERID));
                    if (MessageUtils.TYPE_COMMON.equals(eventType)) {
                        msgModel.addDataList(eventData);
                        msgModel.addIdList(eventID);
                        msgModel.addUserIdList(eventUserId);
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }

        } catch (Exception e) {
            DebugLog.e(e.getMessage());
        }
        return msgModel;
    }


    /**
     * get msg by network state of wifi
     *
     * @param
     * @return
     */
    public static synchronized MsgModel getEventMsgLimit(Context c, long exitDataID) {
        String exclusiveID = "";
        if (exitDataID != -1) {
            exclusiveID = String.valueOf(exitDataID);
        }
        DBProvider provider = DBProvider.getDBProvider(c.getApplicationContext());
        int msgCount = provider.getCount(DBHelp.TABLE_ANALYTICS);
        DebugLog.i("db get message count ==>>" + msgCount);
        if (msgCount > 0) {
            return getEventMsgLimit(c, null, null, exclusiveID, String.valueOf(MAX_MESSAGE));
        } else {
            // 无数据
            return new MsgModel();
        }
    }

    /**
     * 插入消息
     */
    public static void insertEventByMsg() {
        EventsProxy eventsProxy = EventsProxy.create();
        insertMsg(eventsProxy.getJsonString(), MessageUtils.TYPE_COMMON);
        eventsProxy.clearEvents();
    }

    public static void insertEventByMsg(String upData) {
        insertMsg(upData, MessageUtils.TYPE_COMMON);
    }
}
