package jp.co.techfun.playtube;

// YouTube����1���̏��i�[�p�N���X
public class YouTubeVideoItem {

    // ����^�C�g��
    private String title;

    // ����URL
    private String mpeg4spURL;

    // �T���l�C���摜URL
    private String thumbnailURL;

    // �R���X�g���N�^
    public YouTubeVideoItem(String text, String mpeg4spURL, String thumbnailURL) {
        this.title = text;
        this.mpeg4spURL = mpeg4spURL;
        this.thumbnailURL = thumbnailURL;
        
    }

    // ����^�C�g�����擾���郁�\�b�h
    public String getTitle() {
        return title;
    }

    // ����URL���擾���郁�\�b�h
    public String getMpeg4spURL() {
        return mpeg4spURL;
    }

    // �T���l�C���摜URL���擾���郁�\�b�h
    public String getThumbnailURL() {
        return thumbnailURL;
    }

    // ������\����Ԃ����\�b�h
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
