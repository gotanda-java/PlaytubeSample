package jp.co.techfun.playtube;

import java.io.IOException;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

// 動画再生画面Activity
public class VideoPlayerActivity extends Activity {
	// db取得
	static SQLiteDatabase fav_db;

	// menuアイテムID
	private static final int MENU_ITEM_ADDFAV = 0;

	// onCreateメソッド(画面初期表示イベント)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// レイアウト設定ファイル指定
		setContentView(R.layout.video_player);

		// ※以下のどちらかをコメントアウトして使用してください。
		// ***************************************
		// MediaPlayerクラスを使用した動画再生
		// useMediaPlayer();
		// ***************************************
		// VideoPlayerクラスを使用した動画再生
		useVideoPlayer();
		// ***************************************

	}

	// useVideoPlayerメソッド（VideoPlayerクラスを使用した動画再生処理)
	private void useVideoPlayer() {
		VideoView vvPlayer = (VideoView) findViewById(R.id.vv_player);

		// 動画のURIを取得
		String urlString = getIntent().getStringExtra(PlaytubeSampleActivity.IntentKey.MEDIA_URL.name());

		// 動画のURIを設定
		vvPlayer.setVideoURI(Uri.parse(urlString));

		// 動画タイトルを取得
		String title = getIntent().getStringExtra(PlaytubeSampleActivity.IntentKey.MEDIA_TITLE.name());

		// タイトルバーに動画タイトルを設定
		setTitle(title);

		// メディアコントローラを設定
		vvPlayer.setMediaController(new MediaController(this));

		// 動画を再生
		vvPlayer.start();
	}

	// useMediaPlayerメソッド（MediaPlayerクラスを使用した動画再生処理）
	@SuppressWarnings("unused")
	private void useMediaPlayer() {
		// メディアプレーヤーインタンス生成
		final MediaPlayer mp = new MediaPlayer();

		// 動画のURLを取得
		final String urlString = getIntent().getStringExtra(PlaytubeSampleActivity.IntentKey.MEDIA_URL.name());

		// サーフェイスビュー取得
		final SurfaceView sv = (SurfaceView) findViewById(R.id.vv_player);

		// サーフェイスホルダ取得
		final SurfaceHolder sh = sv.getHolder();
		sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// メディアプレーヤーにサーフェイスホルダ設定
		mp.setDisplay(sh);

		sh.addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// 動画停止
				mp.stop();
				mp.release();
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				try {

					// 動画のURLを設定
					mp.setDataSource(urlString);

					// 動画再生準備
					mp.prepare();

					// 横幅を基準に16:9に変換
					int w = getWindowManager().getDefaultDisplay().getWidth();
					int h = w / 16 * 9;
					sv.layout(0, 0, w, h);

					// 動画再生
					mp.start();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ITEM_ADDFAV, Menu.NONE, "お気に入りに追加");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_ADDFAV:
			// 動画タイトルを取得
			String title = getIntent().getStringExtra(PlaytubeSampleActivity.IntentKey.MEDIA_TITLE.name());
			// 動画のURIを取得
			String urlString = getIntent().getStringExtra(PlaytubeSampleActivity.IntentKey.MEDIA_URL.name());
			// 動画のURIを取得
			String thumbnail = getIntent().getStringExtra(PlaytubeSampleActivity.IntentKey.THUMBNAIL_URL.name());
			
			// データベースオープン
			MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this);
			fav_db = helper.getWritableDatabase();
			
			// データ追加
			ContentValues values = new ContentValues();
			values.put("title", title);
			values.put("content", urlString);
			values.put("thumbnail", thumbnail);
			fav_db.insert("favorite", null, values);
			values.clear();
			
			fav_db.close();
			
			Toast.makeText(VideoPlayerActivity.this, "お気に入りに追加しました", Toast.LENGTH_SHORT).show();
			break;
		}
		return false;
	}
}
