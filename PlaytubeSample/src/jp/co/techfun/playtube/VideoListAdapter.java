package jp.co.techfun.playtube;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

// 動画リストを表示するAdapter
public class VideoListAdapter extends ArrayAdapter<YouTubeVideoItem> {

    // LayoutInflater(レイアウトXMLからViewを生成)
    private LayoutInflater inflater;

    // コンストラクタ
    public VideoListAdapter(Context context, int resourceId,
        List<YouTubeVideoItem> items) {
        super(context, resourceId, items);

        // ContextからLayoutInflaterを取得
        inflater =
            (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // getViewメソッド(動画1件分のリスト表示)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView != null) {
            view = convertView;
        } else {
            // convertViewがnullの場合、新規作成
            view = inflater.inflate(R.layout.video_list_row, null);
        }

        // 動画リストのViewを生成
        ImageView ivThumbnail =
            (ImageView) view.findViewById(R.id.iv_thumbnail);

        // 対象のアイテムを取得
        YouTubeVideoItem item = getItem(position);

        // サムネイル画像を取得
        URL url;
        Bitmap thumbnail = null;
        try {
            url = new URL(item.getThumbnailURL());
            thumbnail = BitmapFactory.decodeStream(url.openStream());
        } catch (MalformedURLException e) {
            Log.w(getClass().getSimpleName(), "URLエラー(" + item + ")", e);
        } catch (IOException e) {
            Log.w(getClass().getSimpleName(), "画像取得エラー(" + item + ")", e);
        }

        // サムネイル画像をImageViewに設定
        ivThumbnail.setImageBitmap(thumbnail);

        // 動画タイトルをTextViewに設定
        TextView textView = (TextView) view.findViewById(R.id.tv_video_title);
        textView.setText(item.getTitle());

        return view;
    }
}
