package com.aide.chenpan.myaide.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/10.
 */
public class NearByMenuBean {


    private List<AllTagsListBean> allTagsList;

    public List<AllTagsListBean> getAllTagsList() {
        return allTagsList;
    }

    public void setAllTagsList(List<AllTagsListBean> allTagsList) {
        this.allTagsList = allTagsList;
    }

    public static class AllTagsListBean {
        /**
         * tagsName : 热
         * tagsId : 1
         * tagInfoList : [{"tagType":"10","tagName":"美食","tagId":"101"},{"tagType":"10","tagName":"酒店","tagId":"102"},{"tagType":"10","tagName":"景点","tagId":"103"},{"tagType":"10","tagName":"停车场","tagId":"104"},{"tagType":"10","tagName":"加油站","tagId":"105"},{"tagType":"10","tagName":"公交站","tagId":"106"},{"tagType":"10","tagName":"地铁站","tagId":"107"},{"tagType":"10","tagName":"银行","tagId":"108"},{"tagType":"10","tagName":"超市","tagId":"109"},{"tagType":"10","tagName":"洗浴","tagId":"110"}]
         */

        private String tagsName;
        private String tagsId;
        private List<TagInfoListBean> tagInfoList;

        public String getTagsName() {
            return tagsName;
        }

        public void setTagsName(String tagsName) {
            this.tagsName = tagsName;
        }

        public int getTagsId() {
            return Integer.valueOf(tagsId);
        }

        public void setTagsId(String tagsId) {
            this.tagsId = tagsId;
        }

        public List<TagInfoListBean> getTagInfoList() {
            return tagInfoList;
        }

        public void setTagInfoList(List<TagInfoListBean> tagInfoList) {
            this.tagInfoList = tagInfoList;
        }

        public static class TagInfoListBean implements Serializable{
            /**
             * tagType : 10
             * tagName : 美食
             * tagId : 101
             */

            private String tagType;
            private String tagName;
            private String tagId;

            public String getTagType() {
                return tagType;
            }

            public void setTagType(String tagType) {
                this.tagType = tagType;
            }

            public String getTagName() {
                return tagName;
            }

            public void setTagName(String tagName) {
                this.tagName = tagName;
            }

            public String getTagId() {
                return tagId;
            }

            public void setTagId(String tagId) {
                this.tagId = tagId;
            }
        }
    }
}
