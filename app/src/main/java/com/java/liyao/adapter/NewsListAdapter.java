package com.java.liyao.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
// import android.util.Log;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.java.liyao.NewsInfo;
import com.java.liyao.R;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.java.liyao.db.HistoryDbHelper;
import com.java.liyao.db.LikeDbHelper;
import com.java.liyao.entity.UserInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.MyHold> {

    private List<NewsInfo.DataDTO> mDataDTOList = new ArrayList<>();
    private Context mContext;

    UserInfo userInfo = UserInfo.getUserinfo();
    String eml = userInfo == null ? null : userInfo.getUser_email();

    public NewsListAdapter(Context context) {
        this.mContext = context;
    }

    public void setListData(List<NewsInfo.DataDTO> list) {
        this.mDataDTOList = list;
        notifyDataSetChanged();
    }

    public boolean isViewed(String unique_id) {
        return HistoryDbHelper.getInstance(mContext).searchHistory(unique_id, eml);
    }

    public boolean isLiked(String unique_id) {
        return LikeDbHelper.getInstance(mContext).searchLike(unique_id, eml);
    }

    @NonNull
    @Override
    public MyHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // GitHub Copilot 配享太庙！！！！！！！
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_item, parent, false);
        return new MyHold(view);
    }

    @SuppressLint("CheckResult") // 断无此疏
    @Override
    public void onBindViewHolder(@NonNull MyHold holder, int position) {
        NewsInfo.DataDTO dataDTO = mDataDTOList.get(position);

        holder.author_name.setText(dataDTO.getPublisher());
        holder.title.setText(dataDTO.getTitle());
        holder.date.setText(dataDTO.getPublishTime());

        if (isViewed(dataDTO.getUniqueID())) {
            // Log.d("Viewed", "onBindViewHolder: 已浏览");
            // 为标题设置特殊颜色
            holder.title.setTextColor(mContext.getResources().getColor(R.color.grey));
            // Log.d("AlreadyViewed", "onBindViewHolder: " + dataDTO.getTitle() + "已浏览");
        }
        else {
            holder.title.setTextColor(mContext.getResources().getColor(R.color.black));
            // 权宜之计
        }

        // 使用Glide加载图片，确保了图片URL的正确性
        // 这里似乎出现了问题，图片的请求都失败了？
        // 行，解决了。这个 image 的格式实在是太智障了。
        if (dataDTO.getImage() != null && !dataDTO.getImage().isEmpty()) {
            Glide.with(mContext)
                    .load(dataDTO.getThumbnail())
                    .override(300, 300) // 限制图片的尺寸
                    .error(R.drawable.default_holder)
                    .into(holder.thumbnail_pic_s);
        } else {
            holder.thumbnail_pic_s.setImageResource(R.drawable.default_holder);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(dataDTO, position);
                }
            }
        });

        Animation animation = android.view.animation.AnimationUtils.loadAnimation(mContext, R.anim.item_animation_float_up);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return mDataDTOList.size();
    }

    static class MyHold extends RecyclerView.ViewHolder {
        ImageView thumbnail_pic_s;
        TextView title;
        TextView author_name;
        TextView date;

        public MyHold(@NonNull View itemView) {
            super(itemView);
            thumbnail_pic_s = itemView.findViewById(R.id.thumbnail_pic_s);
            title = itemView.findViewById(R.id.title);
            author_name = itemView.findViewById(R.id.author_name);
            date = itemView.findViewById(R.id.date); // 确保这里的ID与布局文件中的ID匹配
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(NewsInfo.DataDTO dataDTO, int position);
    }

    public void addListData(List<NewsInfo.DataDTO> newData) {
        int startPosition = this.mDataDTOList.size();
        this.mDataDTOList.addAll(newData);
        notifyItemRangeInserted(startPosition, newData.size());
    }

    public void clearData() {
        this.mDataDTOList.clear();
        notifyDataSetChanged();
    }
}