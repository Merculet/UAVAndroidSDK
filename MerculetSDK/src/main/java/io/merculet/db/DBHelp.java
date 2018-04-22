/**
 * ************************************************************
 * An open source analytics android sdk for mobile applications
 * ************************************************************
 *
 * *****************************************************
 * This project is available under the following license
 * *****************************************************
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.merculet.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelp extends SQLiteOpenHelper {

    //DB的数据库名称

    static final String DB_NAME = "mw_blockchain_sdk_analytics.db";
    //	public static final String DBParentFile = Environment.getExternalStorageDirectory().getAbsolutePath();
    //版本号
    public static final int VERSION = 1;

    //表名
    static final String TABLE_ANALYTICS = "analytics_event";
    //ID
    static final String COLUMN_ID = "_id";
    //track数据类型,现在已经用不上，但是为了兼容以前版本，依旧带着
    static final String COLUMN_EVENT_TYPE = "event_type";
    //信息体
    static final String COLUMN_EVENT_DATA = "event_data";
    //创建表语句
    private static final String CREATE_ANALYTICS_SQL = "create table if not exists " + TABLE_ANALYTICS + "(" + COLUMN_ID + " integer primary key autoincrement," + COLUMN_EVENT_TYPE + " text not null, " + COLUMN_EVENT_DATA + " text not null);";

    DBHelp(Context c, String dbPath) {
        super(c.getApplicationContext(), dbPath, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ANALYTICS_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    public void clear() {
        getWritableDatabase().execSQL(" delete from " + TABLE_ANALYTICS);
    }

}
