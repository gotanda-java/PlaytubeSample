package jp.co.techfun.playtube;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

//データベース関連処理クラス(YouTubeDataAPIを使用して動画検索)
public final class YouTubeDataUtil {

    // YouTubeDataUtilインスタンス
    private static final YouTubeDataUtil INSTANCE = new YouTubeDataUtil();

    // 最後に検索したキーワード
    private static String lastKeyword;

    // 最後に表示した検索結果のページ
    private static Integer lastPageNum;

    // 最後に検索した結果
    private static List<YouTubeVideoItem> lastResultList;

    // コンストラクタ
    private YouTubeDataUtil() {
        // 検索結果を格納するリスト
        lastResultList = new ArrayList<YouTubeVideoItem>();
    }

    // YouTubeDataUtilインスタンスを返すメソッド
    public static YouTubeDataUtil getInstance() {
        return INSTANCE;
    }

    // YouTube検索結果の1ページ目をリスト化して返すメソッド
    public List<YouTubeVideoItem> getSearchResult(String keyword) {
        // 検索結果の1ページ目を取得
        lastKeyword = keyword;
        lastPageNum = 1;
        return getSearchResult(keyword, lastPageNum);
    }

    // YouTube検索結果より、前のページをリスト化して返すメソッド
    public List<YouTubeVideoItem> getPrevPage() {
        // 検索結果の前ページを取得
        lastPageNum--;
        return getSearchResult(lastKeyword, lastPageNum);
    }

    // YouTube検索結果より、次のページをリスト化して返すメソッド
    public List<YouTubeVideoItem> getNextPage() {
        // 検索結果の次ページを取得
        lastPageNum++;
        return getSearchResult(lastKeyword, lastPageNum);
    }

    // 最後に検索した結果を返すメソッド
    public List<YouTubeVideoItem> getLastResutList() {
        return lastResultList;
    }

    // YouTube検索結果をリスト化して返すメソッド
    private List<YouTubeVideoItem> getSearchResult(String keyword,
        Integer pageNum) {

        // 検索結果をクリア
        lastResultList.clear();

        try {
            // 検索キーワードを含むHTTPリクエストURLを取得
            String url = getQueryURL(keyword, pageNum);

            // GETリクエストインスタンス生成
            HttpUriRequest httpGet = new HttpGet(url);

            // クライアントインスタンス生成
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();

            // HTTP通信しレスポンス情報取得
            HttpResponse httpResponse = defaultHttpClient.execute(httpGet);

            // HTTPレスポンスが正常でない場合
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                // 空のリストを返す
                return lastResultList;
            }

            // HTTPレスポンスのコンテンツを取得
            HttpEntity entity = httpResponse.getEntity();

            // JSONフォーマットでHTTPレスポンスを取得
            JSONObject json = new JSONObject(EntityUtils.toString(entity));

            // 検索結果("items"要素)を取得
            JSONArray jsonArray =
                json.getJSONObject("data").getJSONArray("items");

            // 検索結果を1件ずつ繰り返す
            for (int i = 0; i < jsonArray.length(); ++i) {
                // 動画1件の情報を取得
                JSONObject obj = jsonArray.getJSONObject(i);

                // 動画タイトル
                String title = obj.getString("title");

                // 動画URL
                // (モバイル向け動画の再生用 RTSP ストリーミング URL です。
                // MPEG-4 SP 動画（最大 176x144）と AAC 音声です。)
                String mpeg4spURL = obj.getJSONObject("content").getString("6");

                // サムネイル画像
                String thumbnailURL =
                    obj.getJSONObject("thumbnail").getString("hqDefault");

                // 動画リスト１件を生成
                YouTubeVideoItem item =
                    new YouTubeVideoItem(title, mpeg4spURL, thumbnailURL);

                // 動画リストに追加
                lastResultList.add(item);
            }

        } catch (JSONException e) {
            Log.w(getClass().getSimpleName(), "JSONデータの解析に失敗しました。", e);
        } catch (IOException e) {
            Log.w(getClass().getSimpleName(), "HTTPレスポンスの取得に失敗しました。", e);
        }

        // 検索結果を返す
        return lastResultList;
    }

    // HTTP リクエスト URLを生成するメソッド
    private String getQueryURL(String keyword, Integer pageNum) {
        StringBuilder sb = new StringBuilder();

        // YouTube gdata Server URL
        sb.append("http://gdata.youtube.com");

        // Video Feed URL
        sb.append("/feeds/api/videos");

        // APIバージョン指定
        sb.append("?v=2");

        // 返されるフィードのフォーマットを指定
        sb.append("&alt=jsonc");

        // 動画フォーマットを指定
        // (モバイル向け動画の再生用 RTSP ストリーミング URL です。
        // MPEG-4 SP 動画（最大 176x144）と AAC 音声です。)
        sb.append("&format=6");

        // 検索結果1ページ当たりの件数
        sb.append("&max-results=10");

        // 検索結果取得ページ
        sb.append("&start-index=" + pageNum);

        // 検索キーワード
        try {
            sb.append("&q=" + URLEncoder.encode(keyword, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            Log.w(getClass().getSimpleName(), "getQueryURL：検索キーワードの変換に失敗しました。",
                e);
        }
        return sb.toString();
    }
}
