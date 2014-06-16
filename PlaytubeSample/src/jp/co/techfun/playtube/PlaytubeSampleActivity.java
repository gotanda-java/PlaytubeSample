package jp.co.techfun.playtube;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

// YouTube動画検索画面Activity
public class PlaytubeSampleActivity extends ListActivity {
	// インテントのデータ受け渡しキー定義
	// YouTube動画URL
	static enum IntentKey {
		MEDIA_URL, MEDIA_TITLE, THUMBNAIL_URL
	};

	// 状態保持用キー定義
	private static enum BundleKey {
		SEARCH_KEYWORD
	};

	// データベース取得
	SQLiteDatabase fav_db;

	// お気に入りリストフラグ
	boolean flag = false;

	// onCreateメソッド(画面初期表示イベント)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// レイアウト設定ファイル指定
		setContentView(R.layout.playtube_main);

		// 検索キーワード入力用EditTextにリスナー設定
		EditText etKeyword = (EditText) findViewById(R.id.et_keyword);
		etKeyword.setOnFocusChangeListener(keywordInputOnFocusChangeListener);

		// 検索ボタンにリスナー設定
		ImageButton ibtnSearch = (ImageButton) findViewById(R.id.ibtn_search);
		ibtnSearch.setOnClickListener(searchBtnOnClickListener);

		// 前ページ( < )ボタンにリスナー設定
		ImageButton ibtnPrev = (ImageButton) findViewById(R.id.ibtn_prev);
		ibtnPrev.setOnClickListener(prevBtnOnClickListener);

		// 次ページ( > )ボタンにリスナー設定
		ImageButton ibtnNext = (ImageButton) findViewById(R.id.ibtn_next);
		ibtnNext.setOnClickListener(nextBtnOnClickListener);

		// お気に入り表示
		List<YouTubeVideoItem> items = getFavoritList();

		// お気に入りがあれば
		if (flag)
			setSearchResult(items);
	}

	// onSaveInstanceStateメソッド（状態保持処理)
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// 検索キーワードEditText取得
		EditText etKeyword = (EditText) findViewById(R.id.et_keyword);

		// 検索キーワードを保存
		outState.putString(BundleKey.SEARCH_KEYWORD.name(), etKeyword.getText().toString());
	}

	// onRestoreInstanceStateメソッド（状態復帰処理）
	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);

		// 検索キーワードを取得
		String keyword = state.getString(BundleKey.SEARCH_KEYWORD.name());

		// 検索キーワードをEditTextに設定
		EditText etKeyword = (EditText) findViewById(R.id.et_keyword);
		etKeyword.setText(keyword);

		// 検索結果を取得
		List<YouTubeVideoItem> resultList = YouTubeDataUtil.getInstance().getLastResutList();

		// 動画リストを表示
		setSearchResult(resultList);
	}

	// お気に入り動画のリスト取得
	private List<YouTubeVideoItem> getFavoritList() {
		List<YouTubeVideoItem> favList = new ArrayList<YouTubeVideoItem>();

		// データベースインスタンス取得
		MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this);
		fav_db = helper.getReadableDatabase();
		String[] columns = { "title", "content", "thumbnail" };

		// データ読み出し
		Cursor cs = fav_db.query("favorite", columns, null, null, null, null, null);
		if (cs.moveToFirst()) {
			do {
				String title = cs.getString(0);
				String contentURL = cs.getString(1);
				String thumbnailURL = cs.getString(2);

				Toast.makeText(this, title, Toast.LENGTH_SHORT).show();

				YouTubeVideoItem item = new YouTubeVideoItem(title, contentURL, thumbnailURL);
				favList.add(item);
			} while (cs.moveToNext());
		} else {
			fav_db.close();
			return favList;
		}

		// フラグ変更
		flag = true;

		fav_db.close();
		return favList;
	}

	// setSearchResultメソッド（検索結果のリスト設定処理)
	private void setSearchResult(List<YouTubeVideoItem> items) {

		// アダプタクラスのインスタンス生成
		ListAdapter adapter = new VideoListAdapter(this, R.layout.playtube_main, items);

		// アダプタ設定
		setListAdapter(adapter);

		// 動画リストへフォーカス
		getListView().requestFocus();
	}

	// onListItemClickメソッド(動画リストより1件選択処理)
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		// クリックした動画のアイテムを取得
		YouTubeVideoItem item = (YouTubeVideoItem) l.getItemAtPosition(position);

		// YouTube動画再生Activityを起動
		Intent intent = new Intent(this, VideoPlayerActivity.class);

		// インテントパラメータに再生対象動画のURLを設定
		intent.putExtra(IntentKey.MEDIA_URL.name(), item.getMpeg4spURL());

		// インテントパラメータに動画タイトルを設定
		intent.putExtra(IntentKey.MEDIA_TITLE.name(), item.getTitle());

		// インテントパラメータにサムネイルURLを設定
		intent.putExtra(IntentKey.THUMBNAIL_URL.name(), item.getThumbnailURL());

		// Activityを表示
		startActivity(intent);
	}

	// 検索キーワードフォーカス変更時リスナー定義
	private OnFocusChangeListener keywordInputOnFocusChangeListener = new OnFocusChangeListener() {
		// onFocusChangeメソッド(フォーカス変更時イベント)
		@Override
		public void onFocusChange(View v, boolean isFocused) {
			// 前ページ( < )ボタンと次ページ( > )ボタンオブジェクト取得
			ImageButton ibtnPrev = (ImageButton) findViewById(R.id.ibtn_prev);
			ImageButton ibtnNext = (ImageButton) findViewById(R.id.ibtn_next);
			// 検索キーワードにフォーカスされた場合
			if (isFocused) {
				// Prev・Nextボタンを非表示
				ibtnPrev.setVisibility(View.GONE);
				ibtnNext.setVisibility(View.GONE);
			} else {
				// Prev・Nextボタンを表示
				ibtnPrev.setVisibility(View.VISIBLE);
				ibtnNext.setVisibility(View.VISIBLE);
			}
		}
	};

	// 検索ボタンクリックリスナー定義
	private OnClickListener searchBtnOnClickListener = new OnClickListener() {
		// onClickメソッド(ボタンクリック時イベント)
		@Override
		public void onClick(View v) {
			// 検索キーワードを取得
			EditText etKeyword = (EditText) findViewById(R.id.et_keyword);

			// 検索結果を取得
			List<YouTubeVideoItem> items = YouTubeDataUtil.getInstance().getSearchResult(etKeyword.getText().toString());

			// 検索結果を表示
			setSearchResult(items);

			// リストラベル変更
			TextView tvLabel = (TextView) findViewById(R.id.tv_listlabel);
			tvLabel.setText("検索結果一覧");
		}
	};

	// 前ページ( < )ボタンボタンクリックリスナー定義
	private OnClickListener prevBtnOnClickListener = new OnClickListener() {
		// onClickメソッド(ボタンクリック時イベント)
		@Override
		public void onClick(View v) {

			// 検索結果の前のページを取得
			List<YouTubeVideoItem> items = YouTubeDataUtil.getInstance().getPrevPage();

			// 検索結果を表示
			setSearchResult(items);
		}
	};

	// 次ページ( > )ボタンボタンクリックリスナー定義
	private OnClickListener nextBtnOnClickListener = new OnClickListener() {
		// onClickメソッド(ボタンクリック時イベント)
		@Override
		public void onClick(View v) {

			// 検索結果の次のページを取得
			List<YouTubeVideoItem> items = YouTubeDataUtil.getInstance().getNextPage();

			// 検索結果を表示
			setSearchResult(items);
		}
	};
}
