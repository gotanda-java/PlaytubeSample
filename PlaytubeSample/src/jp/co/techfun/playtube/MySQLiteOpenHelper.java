package jp.co.techfun.playtube;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
	static final String DB = "sqlite_favorit.db";
	static final int DB_VERSION = 1;
	static final String CREATE_TABLE = "create table favorite ( _id integer primary key autoincrement, title text not null, content text not null, thumbnail text not null );";
	static final String DROP_TABLE = "drop table mytable;";

	public MySQLiteOpenHelper(Context context) {
		super(context, DB, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL(DROP_TABLE);
		onCreate(db);
	}

}
