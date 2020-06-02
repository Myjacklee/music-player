package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public ImageView backIv,nextIv,playIv;
    public TextView singerTv,songTv;
    public RecyclerView musicRv;
    List<localMusicBean> musicDatas; //数据源
    localMusicAdapter adapter;
    MediaPlayer mediaPlayer;
    // 当前音乐文件的播放位置
    int currentPlayPosition=-1;
    // 记录暂停音乐时的位置
    int currentPausePosition=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mediaPlayer=new MediaPlayer();
        musicDatas= new ArrayList<>();
        // 创建适配器对象,recyclerView的适配器是由recyclerViewAdapter来完成的
        adapter=new localMusicAdapter(this,musicDatas);
        musicRv.setAdapter(adapter);
        // 设置布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        musicRv.setLayoutManager(manager);
        // 加载本地数据源
        loadLocalMusicDatas();
        // 设置每一项的点击事件
        setEventListener();
        setMediaPlayerFinishListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
        mediaPlayer.release();
        mediaPlayer=null;
    }

    private void setEventListener() {
        adapter.setOnItemClickListener(new localMusicAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                currentPlayPosition=position;
                localMusicBean musicBean = musicDatas.get(position);
                playMusicInBean(musicBean);
            }
        });
    }

    private void setMediaPlayerFinishListener(){
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopMusic();
            }
        });
    }

    public void playMusicInBean(localMusicBean musicBean) {
        /*
        * 播放指定的musicBean
        * */
        // 设置底部的歌手名和歌曲名
        songTv.setText(musicBean.getSong());
        singerTv.setText(musicBean.getSinger());
        stopMusic();
        // 重置多媒体播放器
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(musicBean.getPath());
            mediaPlayer.prepare();
            playMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playMusic() {
        // 播放音乐
        if(mediaPlayer!=null&&!mediaPlayer.isPlaying()){
            // 从暂停位置开始播放音乐
            mediaPlayer.seekTo(currentPausePosition);
            mediaPlayer.start();
            playIv.setImageResource(R.mipmap.player_pause_circle);
        }
    }

    private void stopMusic() {
        // 停止音乐
        if(mediaPlayer!=null){
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            currentPausePosition=0;
            mediaPlayer.stop();
            playIv.setImageResource(R.mipmap.player_play);
        }
    }

    private void loadLocalMusicDatas() {
        // 获取contentResolver对象
        ContentResolver resolver=getContentResolver();
        // 获取本地音乐uri地址
        Uri url= MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        // 查询地址
        Cursor cursor = resolver.query(url, null, null, null, null);
        // 遍历查询对象
        int i=0;
        while(cursor.moveToNext()){
            String song = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            i++;
            String id=String.valueOf(i);
            String path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            long duration=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            SimpleDateFormat sdf=new SimpleDateFormat("mm:ss");
            String time=sdf.format(new Date(duration));
            // 将一行中的数据封装到对象中
            localMusicBean bean = new localMusicBean(id, song, singer, album, time, path);
            musicDatas.add(bean);
        }
        // 数据变化，提示adapter更新
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        backIv=findViewById(R.id.local_music_bottom_iv_icon_back);
        nextIv=findViewById(R.id.local_music_bottom_iv_icon_next);
        playIv=findViewById(R.id.local_music_bottom_iv_icon_play);
        singerTv=findViewById(R.id.local_music_bottom_tv_singer);
        songTv=findViewById(R.id.local_music_bottom_tv_song);
        musicRv=findViewById(R.id.local_music_rv);
        backIv.setOnClickListener(this);
        playIv.setOnClickListener(this);
        nextIv.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.local_music_bottom_iv_icon_back:
                if(isFirstLoad()){
                    return;
                }
                if(currentPlayPosition==0){
                    Toast.makeText(this,"当前为第一首歌曲",Toast.LENGTH_SHORT).show();
                    return;
                }
                currentPlayPosition=currentPlayPosition-1;
                localMusicBean lastBean = musicDatas.get(currentPlayPosition);
                playMusicInBean(lastBean);
                break;
            case R.id.local_music_bottom_iv_icon_play:
                if (isFirstLoad()) return;
                if(mediaPlayer.isPlaying()){
                    // 音乐正在播放
                    musicPause();
                }else{
                    // 音乐没有播放
                    playMusic();
                }
                break;
            case R.id.local_music_bottom_iv_icon_next:
                if (isFirstLoad()){
                    return;
                }
                if(currentPlayPosition==musicDatas.size()-1){
                    Toast.makeText(this,"当前为最后一首歌曲",Toast.LENGTH_SHORT).show();
                    return;
                }
                currentPlayPosition=currentPlayPosition+1;
                localMusicBean nextBean = musicDatas.get(currentPlayPosition);
                playMusicInBean(nextBean);
                break;
        }
    }

    public boolean isFirstLoad() {
        if(currentPlayPosition==-1){
            Toast.makeText(this,"请选择想要播放的音乐",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void musicPause() {
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            currentPausePosition=mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            playIv.setImageResource(R.mipmap.player_play);
        }
    }
}
