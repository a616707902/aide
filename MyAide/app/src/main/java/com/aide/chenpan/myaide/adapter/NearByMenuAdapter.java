package com.aide.chenpan.myaide.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.bean.NearByMenuBean;
import com.aide.chenpan.myaide.utils.ListUtils;

import java.util.List;


/**
 * 搜周边的菜单适配器
 */

public class NearByMenuAdapter extends SectionedRecyclerViewAdapter<NearByHeaderHolder, NearByDescHolder, RecyclerView.ViewHolder> {


    public List<NearByMenuBean.AllTagsListBean> allTagList;
    private Context mContext;
    private LayoutInflater mInflater;
    ItemListener<NearByMenuBean.AllTagsListBean.TagInfoListBean> listener;
    //  private SparseBooleanArray mBooleanMap;

    public NearByMenuAdapter(Context context,ItemListener listener) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.listener=listener;
//        mBooleanMap = new SparseBooleanArray();
    }

    public void setData(List<NearByMenuBean.AllTagsListBean> allTagList) {
        this.allTagList = allTagList;
        notifyDataSetChanged();
    }

    /**
     * 大项
     * @return
     */
    @Override
    protected int getSectionCount() {
        return ListUtils.isEmpty(allTagList) ? 0 : allTagList.size();
    }

    /**
     * 小项
     * @param section
     * @return
     */
    @Override
    protected int getItemCountForSection(int section) {
        int count = allTagList.get(section).getTagInfoList().size();

        return ListUtils.isEmpty(allTagList.get(section).getTagInfoList()) ? 0 : count;
    }

    //是否有footer布局
    @Override
    protected boolean hasFooterInSection(int section) {
        return false;
    }

    @Override
    protected NearByHeaderHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType) {
        return new NearByHeaderHolder(mInflater.inflate(R.layout.nearby_head_item, parent, false));
    }


    @Override
    protected RecyclerView.ViewHolder onCreateSectionFooterViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    protected NearByDescHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new NearByDescHolder(mInflater.inflate(R.layout.nearby_menu_item, parent, false));
    }


    @Override
    protected void onBindSectionHeaderViewHolder(final NearByHeaderHolder holder, final int section) {

        holder.titleView.setText(allTagList.get(section).getTagsName());
        int tagId = allTagList.get(section).getTagsId();
        int resid = R.drawable.hot_micon;
        switch (tagId) {
            case 1:
                break;
            case 2:
                resid = R.drawable.chi_micon;
                break;
            case 3:
                resid = R.drawable.zhu_micon;
                break;
            case 4:
                resid = R.drawable.xing_micon;
                break;
            case 5:
                resid = R.drawable.wan_micon;
                break;
            case 6:
                resid = R.drawable.you_micon;
                break;
            case 7:
                resid = R.drawable.gou_micon;
                break;
            case 8:
                resid = R.drawable.shenghuo_micon;
                break;
        }
        holder.imageView.setImageResource(resid);

    }


    @Override
    protected void onBindSectionFooterViewHolder(RecyclerView.ViewHolder holder, int section) {

    }

    @Override
    protected void onBindItemViewHolder(final NearByDescHolder holder, final int section, final int position) {
        holder.descView.setText(allTagList.get(section).getTagInfoList().get(position).getTagName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder.itemView,position,allTagList.get(section).getTagInfoList().get(position));
            }
        });
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(String section) {
        for (int i = 0; i < getSectionCount(); i++) {

           if (section.equals(allTagList.get(i).getTagsName())){
               int position =0;
               if (i==0){
                   return position;
               }

               for (int j=i-1;j>=0;j--){
                   position+=getItemCountForSection(j)+1;
               }
               return position;
           }
        }

        return -1;
    }
}
