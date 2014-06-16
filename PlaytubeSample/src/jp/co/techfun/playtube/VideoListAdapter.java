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

// ���惊�X�g��\������Adapter
public class VideoListAdapter extends ArrayAdapter<YouTubeVideoItem> {

    // LayoutInflater(���C�A�E�gXML����View�𐶐�)
    private LayoutInflater inflater;

    // �R���X�g���N�^
    public VideoListAdapter(Context context, int resourceId,
        List<YouTubeVideoItem> items) {
        super(context, resourceId, items);

        // Context����LayoutInflater���擾
        inflater =
            (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // getView���\�b�h(����1�����̃��X�g�\��)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView != null) {
            view = convertView;
        } else {
            // convertView��null�̏ꍇ�A�V�K�쐬
            view = inflater.inflate(R.layout.video_list_row, null);
        }

        // ���惊�X�g��View�𐶐�
        ImageView ivThumbnail =
            (ImageView) view.findViewById(R.id.iv_thumbnail);

        // �Ώۂ̃A�C�e�����擾
        YouTubeVideoItem item = getItem(position);

        // �T���l�C���摜���擾
        URL url;
        Bitmap thumbnail = null;
        try {
            url = new URL(item.getThumbnailURL());
            thumbnail = BitmapFactory.decodeStream(url.openStream());
        } catch (MalformedURLException e) {
            Log.w(getClass().getSimpleName(), "URL�G���[(" + item + ")", e);
        } catch (IOException e) {
            Log.w(getClass().getSimpleName(), "�摜�擾�G���[(" + item + ")", e);
        }

        // �T���l�C���摜��ImageView�ɐݒ�
        ivThumbnail.setImageBitmap(thumbnail);

        // ����^�C�g����TextView�ɐݒ�
        TextView textView = (TextView) view.findViewById(R.id.tv_video_title);
        textView.setText(item.getTitle());

        return view;
    }
}
