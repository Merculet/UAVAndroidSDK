package io.merculet.uav.sdk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import io.merculet.uav.sdk.log.DebugLog;


public class DBProvider {

    private DBHelp dbHelp = null;
    private static DBProvider provider = null;

    private DBProvider(Context c) {
        if (c == null) {
            DebugLog.e("create db fail, context is null!");
            return;
        }
        Context context = c.getApplicationContext();
        String dBPath = DBHelp.DB_NAME;
        /*if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dbHelp = new DBHelp(context, dBPath);
		} else if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && Util.checkPermissions(context, "android.permission.WRITE_EXTERNAL_STORAGE")) {
			try {
				File dbPath=new File(DBHelp.DBParentFile, c.getPackageName());
				File dbFile=new File(dbPath, DBHelp.DB_NAME);
				 if(!dbPath.exists()){
					 dbPath.mkdir();
				 }
				 if (!dbFile.exists()) {
					 dbFile.createNewFile();
				 }
			} catch (Exception e) {
				e.printStackTrace();
			}
			dBPath = DBHelp.DBParentFile+"/"+c.getPackageName()+"/"+DBHelp.DB_NAME;
			dbHelp = new DBHelp(context, dBPath);*/
        dbHelp = new DBHelp(context, dBPath);
//		} else {
//			DebugLog.e("create db fail, lost permission--->android.permission.WRITE_EXTERNAL_STORAGE");
//		}
    }

    public static synchronized DBProvider getDBProvider(Context c) {
        if (provider == null) {
            provider = new DBProvider(c.getApplicationContext());
        }
        return provider;
    }

    /**
     * rawQuery查询
     *
     * @param sql
     * @return Cursor
     */
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        if (dbHelp == null) {
            DebugLog.e("create db fail, lost permission--->android.permission.WRITE_EXTERNAL_STORAGE");
            return null;
        }


        Cursor c = null;
        try {
            SQLiteDatabase db = dbHelp.getWritableDatabase();
            DebugLog.d("rawQuery : " + sql);
            c = db.rawQuery(sql, selectionArgs);
        } catch (Exception e) {
            DebugLog.e("when query database occur error :" + sql + e.getMessage());
        }
        return c;
    }

    /**
     * excSQL 执行原生的sql语句
     *
     * @param sql
     * @return Cursor
     */
    void excSQL(String sql) {
        if (dbHelp == null) {
            DebugLog.e("create db fail, lost permission--->android.permission.WRITE_EXTERNAL_STORAGE");
            return;
        }
        try {
            SQLiteDatabase db = dbHelp.getWritableDatabase();
            DebugLog.d("excSQL : " + sql);
            db.execSQL(sql);
        } catch (Exception e) {
            DebugLog.e("when query database occur error :" + sql + e.getMessage());
        }
    }

    /**
     * 查询
     *
     * @param table
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String sortOrder, String limit) {
        if (dbHelp == null) {
            DebugLog.e("create db fail, lost permission--->android.permission.WRITE_EXTERNAL_STORAGE");
            return null;
        }

        Cursor c = null;
        try {
            SQLiteDatabase db = dbHelp.getWritableDatabase();
            DebugLog.d("Query table: " + table + "Query table");
            c = db.query(table, columns, selection, selectionArgs, null, null, sortOrder, limit);
        } catch (Exception e) {
            DebugLog.e("when query database occur error table:" + table + e.getMessage());
        }
        return c;
    }

    /**
     * 插入
     *
     * @param table
     * @param values
     * @return
     */
    long insert(String table, ContentValues values) {
        if (dbHelp == null) {
            DebugLog.e("create db fail, lost permission--->android.permission.WRITE_EXTERNAL_STORAGE");
            return -1;
        }

        long rowId = -1;
        try {
            SQLiteDatabase db = dbHelp.getWritableDatabase();
            DebugLog.i("insert database, insert: " + values.get("event_data"));
            rowId = db.replace(table, null, values);
        } catch (Exception e) {
            DebugLog.e("when insert database occur error table:" + table + e.getMessage());
        }

        return rowId;
    }

    /**
     * 批量插入，数据越多越好用；最好多于100条
     *
     * @param table
     * @param
     * @return
     */
    public long transactInsert(String table, List<ContentValues> list) {
        if (dbHelp == null) {
            DebugLog.i("create db fail, lost permission--->android.permission.WRITE_EXTERNAL_STORAGE");
            return -1;
        }

        SQLiteDatabase db = null;
        //开启事务
        long rowId = -1;
        try {
            db = dbHelp.getWritableDatabase();
            db.beginTransaction();
            for (int i = 0; i < list.size(); i++) {
                rowId = db.insert(table, null, list.get(i));
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            DebugLog.e("when insert database occur error table:" + table + e.getMessage());
        } finally {
            if (db != null) {
                db.endTransaction();
            }
        }

        return rowId;
    }

    /**
     * 删除
     *
     * @param table
     * @param selection
     * @param selectionArgs
     * @return
     */
    int delete(String table, String selection, String[] selectionArgs) {
        if (dbHelp == null) {
            DebugLog.e("create db fail, lost permission--->android.permission.WRITE_EXTERNAL_STORAGE");
            return 0;
        }

        int count = 0;
        try {
            SQLiteDatabase db = dbHelp.getWritableDatabase();
            count = db.delete(table, selection, selectionArgs);
            DebugLog.d("Deleted " + table + " rows from table: " + count + "delete");
        } catch (Exception e) {
            DebugLog.e("when delete database occur error table:" + table + e.getMessage());
        }

        return count;
    }

    /**
     * 更新
     *
     * @param table
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    public int update(String table, ContentValues values, String selection, String[] selectionArgs) {
        if (dbHelp == null) {
            DebugLog.e("create db fail, lost permission--->android.permission.WRITE_EXTERNAL_STORAGE");
            return 0;
        }

        int count = 0;
        try {
            SQLiteDatabase db = dbHelp.getWritableDatabase();
            count = db.update(table, values, selection, selectionArgs);
            DebugLog.d("Updated " + table + " row from table: %s" + count + "update");
        } catch (Exception e) {
            DebugLog.e("when update database occur error table:" + table + e.getMessage());
        }

        return count;
    }

    public void close() {
        if (dbHelp == null) {
            DebugLog.e("create db fail, lost permission--->android.permission.WRITE_EXTERNAL_STORAGE");
            return;
        }
        dbHelp.close();
    }

    /**
     * 获取当前数据库的count
     *
     * @param table 表名
     * @return 表中的数据条数
     */
    public int getCount(String table) {
        if (dbHelp == null) {
            DebugLog.e("create db fail, lost permission--->android.permission.WRITE_EXTERNAL_STORAGE");
            return 0;
        }

        int count = 0;
        Cursor c = null;
        try {
            SQLiteDatabase db = dbHelp.getWritableDatabase();
            c = db.rawQuery("select count(*) from " + table, null);
            if (c.moveToNext())
                count = c.getInt(0);
        } catch (Exception e) {
            DebugLog.e("getCount" + e.getMessage());
            count = 0;
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return count;
    }
}
