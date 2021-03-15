package com.pcs.ztqtj.view.fragment.warning.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pcs.ztqtj.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/13 0013.
 */

public class VideoListActivity extends Activity {

    private ListView mListView;
    private TextView noData;
    private Button btnBack;
    ArrayList<VideoInfo> vList;
    private Intent lastIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        setResult(Activity.RESULT_OK);
        mListView = (ListView) findViewById(R.id.lv_video);
        noData = (TextView) findViewById(R.id.tv_nodata);
        btnBack = (Button) findViewById(R.id.btn_back);

        lastIntent = getIntent();
        initData();
        mListView.setOnItemClickListener(new ItemClick());
    }

    private void initData() {
        vList = new ArrayList<VideoInfo>();
        String[] mediaColumns = new String[]{MediaStore.MediaColumns.DATA,
                BaseColumns._ID, MediaStore.MediaColumns.TITLE, MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.Video.VideoColumns.DURATION, MediaStore.MediaColumns.SIZE};
        Cursor cursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns,
                null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor
                        .getLong(cursor
                                .getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))<(400*1024*1024l)){
                    VideoInfo info = new VideoInfo();
                    info.setFilePath(cursor.getString(cursor
                            .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)));
                    info.setMimeType(cursor.getString(cursor
                            .getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)));
                    info.setTitle(cursor.getString(cursor
                            .getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE)));
                    info.setTime(CommTools.LongToHms(cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION))));
                    info.setSize(CommTools.LongToPoint(cursor
                            .getLong(cursor
                                    .getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))));
                    int id = cursor.getInt(cursor
                            .getColumnIndexOrThrow(BaseColumns._ID));
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inDither = false;
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    info.setB(MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(), id,
                            MediaStore.Images.Thumbnails.MICRO_KIND, options));
                    vList.add(info);
                }


            } while (cursor.moveToNext());
        }

        if (vList.size() == 0) {
            mListView.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        } else {
            VideoListAdapter adapter = new VideoListAdapter(VideoListActivity.this);
            mListView.setAdapter(adapter);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastIntent.putExtra("path", "");
                setResult(Activity.RESULT_OK, lastIntent);
                finish();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // Do something.
            lastIntent.putExtra("path", "");
            setResult(Activity.RESULT_OK, lastIntent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class ItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mListView.getItemAtPosition(position);
            String filePath = vList.get(position).getFilePath();
            lastIntent.putExtra("path", filePath);
            setResult(Activity.RESULT_OK, lastIntent);
            finish();
        }

    }

    class VideoListAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public VideoListAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return vList.size();
        }

        @Override
        public Object getItem(int p) {
            return vList.get(p);
        }

        @Override
        public long getItemId(int p) {
            return p;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_video_list, null);
                holder.vImage = (ImageView) convertView.findViewById(R.id.video_img);
                holder.vTitle = (TextView) convertView.findViewById(R.id.video_title);
                holder.vSize = (TextView) convertView.findViewById(R.id.video_size);
                holder.vTime = (TextView) convertView.findViewById(R.id.video_time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.vImage.setImageBitmap(vList.get(position).getB());
            holder.vTitle.setText(vList.get(position).getTitle()); // + "." + (videoList.get(position).getMimeType()).substring(6))
            holder.vSize.setText(vList.get(position).getSize());
            holder.vTime.setText(vList.get(position).getTime());

//            holder.vImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    String bpath = "file://" + vList.get(position).getFilePath();
//                    intent.setDataAndType(Uri.parse(bpath), "video/*");
//                    startActivity(intent);
//                }
//            });
            return convertView;
        }

        class ViewHolder {
            ImageView vImage;
            TextView vTitle;
            TextView vSize;
            TextView vTime;
        }
    }

}
