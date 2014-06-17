package jp.co.techfun.playtube;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

// YouTube���挟�����Activity
@SuppressLint("NewApi")
public class PlaytubeSampleActivity extends ListActivity {
	// �C���e���g�̃f�[�^�󂯓n���L�[��`
	// YouTube����URL
	static enum IntentKey {
		MEDIA_URL, MEDIA_TITLE, THUMBNAIL_URL
	};

	// ��ԕێ��p�L�[��`
	private static enum BundleKey {
		SEARCH_KEYWORD
	};

	// �f�[�^�x�[�X�擾
	SQLiteDatabase fav_db;

	// ���C�ɓ��胊�X�g�t���O
	boolean flag = false;

	// menu�萔
	private static final int MENU_ITEM_DISPLAY = 0;

	// TextView�萔
	private static final String RESULTS = "�������ʈꗗ";

	// onCreate���\�b�h(��ʏ����\���C�x���g)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ���C�A�E�g�ݒ�t�@�C���w��
		setContentView(R.layout.playtube_main);

		// �����L�[���[�h���͗pEditText�Ƀ��X�i�[�ݒ�
		EditText etKeyword = (EditText) findViewById(R.id.et_keyword);
		etKeyword.setOnFocusChangeListener(keywordInputOnFocusChangeListener);

		// �����{�^���Ƀ��X�i�[�ݒ�
		ImageButton ibtnSearch = (ImageButton) findViewById(R.id.ibtn_search);
		ibtnSearch.setOnClickListener(searchBtnOnClickListener);

		// �O�y�[�W( < )�{�^���Ƀ��X�i�[�ݒ�
		ImageButton ibtnPrev = (ImageButton) findViewById(R.id.ibtn_prev);
		ibtnPrev.setOnClickListener(prevBtnOnClickListener);

		// ���y�[�W( > )�{�^���Ƀ��X�i�[�ݒ�
		ImageButton ibtnNext = (ImageButton) findViewById(R.id.ibtn_next);
		ibtnNext.setOnClickListener(nextBtnOnClickListener);

		// ���C�ɓ��蓮�惊�X�g�擾
		List<YouTubeVideoItem> items = getFavoritList();

		// ���C�ɓ��肪����Ε\��
		if (flag)
			setSearchResult(items);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ITEM_DISPLAY, Menu.NONE, "���C�ɓ���\��");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_DISPLAY:
			Intent intent = new Intent();
			intent.setClass(PlaytubeSampleActivity.this, getClass());
			startActivity(intent);
			break;
		}
		return false;
	}

	// onSaveInstanceState���\�b�h�i��ԕێ�����)
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// �����L�[���[�hEditText�擾
		EditText etKeyword = (EditText) findViewById(R.id.et_keyword);

		// �����L�[���[�h��ۑ�
		outState.putString(BundleKey.SEARCH_KEYWORD.name(), etKeyword.getText().toString());
	}

	// onRestoreInstanceState���\�b�h�i��ԕ��A�����j
	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);

		// �����L�[���[�h���擾
		String keyword = state.getString(BundleKey.SEARCH_KEYWORD.name());

		// �����L�[���[�h��EditText�ɐݒ�
		EditText etKeyword = (EditText) findViewById(R.id.et_keyword);
		etKeyword.setText(keyword);

		// �������ʂ��擾
		List<YouTubeVideoItem> resultList = YouTubeDataUtil.getInstance().getLastResutList();

		// ���惊�X�g��\��
		setSearchResult(resultList);
	}

	// ���C�ɓ��蓮��̃��X�g�擾
	private List<YouTubeVideoItem> getFavoritList() {
		List<YouTubeVideoItem> favList = new ArrayList<YouTubeVideoItem>();

		// �f�[�^�x�[�X�C���X�^���X�擾
		MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this);
		fav_db = helper.getReadableDatabase();
		String[] columns = { "title", "content", "thumbnail" };

		// �f�[�^�ǂݏo��
		Cursor cs = fav_db.query("favorite", columns, null, null, null, null, null);
		if (cs.moveToFirst()) {
			do {
				String title = cs.getString(0);
				String contentURL = cs.getString(1);
				String thumbnailURL = cs.getString(2);

				YouTubeVideoItem item = new YouTubeVideoItem(title, contentURL, thumbnailURL);
				favList.add(item);
			} while (cs.moveToNext());
		} else {
			fav_db.close();
			return favList;
		}

		// �t���O�ύX
		flag = true;

		fav_db.close();
		return favList;
	}

	// setSearchResult���\�b�h�i�������ʂ̃��X�g�ݒ菈��)
	private void setSearchResult(List<YouTubeVideoItem> items) {

		// �A�_�v�^�N���X�̃C���X�^���X����
		ListAdapter adapter = new VideoListAdapter(this, R.layout.playtube_main, items);

		// �A�_�v�^�ݒ�
		setListAdapter(adapter);

		// �������ʂȂ瓮��փt�H�[�J�X
		TextView tvLabel = (TextView) findViewById(R.id.tv_listlabel);
		if (tvLabel.getText().toString().equals(RESULTS))
			// ���惊�X�g�փt�H�[�J�X
			getListView().requestFocus();
	}

	// onListItemClick���\�b�h(���惊�X�g���1���I������)
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		// �N���b�N��������̃A�C�e�����擾
		YouTubeVideoItem item = (YouTubeVideoItem) l.getItemAtPosition(position);

		// YouTube����Đ�Activity���N��
		Intent intent = new Intent(this, VideoPlayerActivity.class);

		// �C���e���g�p�����[�^�ɍĐ��Ώۓ����URL��ݒ�
		intent.putExtra(IntentKey.MEDIA_URL.name(), item.getMpeg4spURL());

		// �C���e���g�p�����[�^�ɓ���^�C�g����ݒ�
		intent.putExtra(IntentKey.MEDIA_TITLE.name(), item.getTitle());

		// �C���e���g�p�����[�^�ɃT���l�C��URL��ݒ�
		intent.putExtra(IntentKey.THUMBNAIL_URL.name(), item.getThumbnailURL());

		// Activity��\��
		startActivity(intent);
	}

	// �����L�[���[�h�t�H�[�J�X�ύX�����X�i�[��`
	private OnFocusChangeListener keywordInputOnFocusChangeListener = new OnFocusChangeListener() {
		// onFocusChange���\�b�h(�t�H�[�J�X�ύX���C�x���g)
		@Override
		public void onFocusChange(View v, boolean isFocused) {
			// �O�y�[�W( < )�{�^���Ǝ��y�[�W( > )�{�^���I�u�W�F�N�g�擾
			ImageButton ibtnPrev = (ImageButton) findViewById(R.id.ibtn_prev);
			ImageButton ibtnNext = (ImageButton) findViewById(R.id.ibtn_next);
			// �����L�[���[�h�Ƀt�H�[�J�X���ꂽ�ꍇ
			if (isFocused) {
				// Prev�ENext�{�^�����\��
				ibtnPrev.setVisibility(View.GONE);
				ibtnNext.setVisibility(View.GONE);
			} else {
				// Prev�ENext�{�^����\��
				ibtnPrev.setVisibility(View.VISIBLE);
				ibtnNext.setVisibility(View.VISIBLE);
				// �\�t�g�E�F�A�L�[�{�[�h���\��
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	};

	// �����{�^���N���b�N���X�i�[��`
	@SuppressLint("NewApi")
	private OnClickListener searchBtnOnClickListener = new OnClickListener() {
		// onClick���\�b�h(�{�^���N���b�N���C�x���g)
		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) {
			// �����L�[���[�h���擾
			EditText etKeyword = (EditText) findViewById(R.id.et_keyword);

			// �������ʂ��擾
			List<YouTubeVideoItem> items = YouTubeDataUtil.getInstance().getSearchResult(etKeyword.getText().toString());

			// �������ʂ�\��
			setSearchResult(items);

			// ���X�g���x���ύX
			TextView tvLabel = (TextView) findViewById(R.id.tv_listlabel);
			tvLabel.setText(RESULTS);
			
			// �t�H�[�J�X�����ړ�
			ImageButton ibtnSearch = (ImageButton) findViewById(R.id.ibtn_search);
			ibtnSearch.setFocusable(true);
			ibtnSearch.setFocusableInTouchMode(true);
			ibtnSearch.requestFocus();
		}
	};

	// �O�y�[�W( < )�{�^���{�^���N���b�N���X�i�[��`
	private OnClickListener prevBtnOnClickListener = new OnClickListener() {
		// onClick���\�b�h(�{�^���N���b�N���C�x���g)
		@Override
		public void onClick(View v) {

			// �������ʂ̑O�̃y�[�W���擾
			List<YouTubeVideoItem> items = YouTubeDataUtil.getInstance().getPrevPage();

			// �������ʂ�\��
			setSearchResult(items);
		}
	};

	// ���y�[�W( > )�{�^���{�^���N���b�N���X�i�[��`
	private OnClickListener nextBtnOnClickListener = new OnClickListener() {
		// onClick���\�b�h(�{�^���N���b�N���C�x���g)
		@Override
		public void onClick(View v) {

			// �������ʂ̎��̃y�[�W���擾
			List<YouTubeVideoItem> items = YouTubeDataUtil.getInstance().getNextPage();

			// �������ʂ�\��
			setSearchResult(items);
		}
	};
}
