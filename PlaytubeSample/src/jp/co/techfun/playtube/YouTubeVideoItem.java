package jp.co.techfun.playtube;

// YouTube動画1件の情報格納用クラス
public class YouTubeVideoItem {

    // 動画タイトル
    private String title;

    // 動画URL
    private String mpeg4spURL;

    // サムネイル画像URL
    private String thumbnailURL;

    // コンストラクタ
    public YouTubeVideoItem(String text, String mpeg4spURL, String thumbnailURL) {
        this.title = text;
        this.mpeg4spURL = mpeg4spURL;
        this.thumbnailURL = thumbnailURL;
        
    }

    // 動画タイトルを取得するメソッド
    public String getTitle() {
        return title;
    }

    // 動画URLを取得するメソッド
    public String getMpeg4spURL() {
        return mpeg4spURL;
    }

    // サムネイル画像URLを取得するメソッド
    public String getThumbnailURL() {
        return thumbnailURL;
    }

    // 文字列表現を返すメソッド
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("YouTubeVideoItem={");
        sb.append("title=" + title + ", ");
        sb.append("mpeg4spURL=" + mpeg4spURL + ", ");
        sb.append("thumbnailURL=" + thumbnailURL);
        sb.append("}");
        return sb.toString();
    }
}
