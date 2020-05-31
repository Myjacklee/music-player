package com.example.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class localMusicAdapter extends RecyclerView.Adapter<localMusicAdapter.localMusicViewHolder> {
    Context context;
    List<localMusicBean> musicDatas;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        public void OnItemClick(View view,int position);
    }
    public localMusicAdapter(Context context, List<localMusicBean> musicDatas) {
        this.context = context;
        this.musicDatas = musicDatas;
    }

    @NonNull
    @Override
    public localMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_local_music,parent,false);
        localMusicViewHolder holder=new localMusicViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull localMusicViewHolder holder, final int position) {
        localMusicBean musicBean=musicDatas.get(position);
        holder.idTv.setText(musicBean.getId());
        holder.durationTv.setText((musicBean.getDuration()));
        holder.albumTv.setText(musicBean.getAlbum());
        holder.singerTv.setText(musicBean.getSinger());
        holder.songTv.setText(musicBean.getSong());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicDatas.size();
    }

    class localMusicViewHolder extends RecyclerView.ViewHolder{
        TextView idTv,songTv,singerTv,albumTv,durationTv;
        public localMusicViewHolder(View itemHolder){
            super(itemHolder);
            idTv=itemHolder.findViewById(R.id.item_local_music_num);
            songTv=itemHolder.findViewById(R.id.item_local_music_song);
            singerTv=itemHolder.findViewById(R.id.item_local_music_singer);
            albumTv=itemHolder.findViewById(R.id.item_local_music_album);
            durationTv=itemHolder.findViewById(R.id.item_local_music_duration);
        }

    }
}
