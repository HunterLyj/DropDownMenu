# DropDownMenu


1、mDropDownMenuView.setDropDownListener 设置数据获取方式

2、mDropDownMenuView.addList 添加数据


各接口方法作用

public interface DropDownListener {

        /**
         * 返回一级Item内容
         */
        public String getParentItemName(Object object);

        /**
         * 返回一级Item内容对应的二级列表
         */
        public List getSecondSubList(Object object);

        /**
         * 返回二级列表对应的Item内容
         */
        public String getSecondSubItemName(Object object);

        /**
         * 返回二级Item内容对应的三级列表
         */
        public List getThirdSubList(Object object);

        /**
         * 返回三级列表对应的Item内容
         */
        public String getThirdSubItemName(Object object);

        /**
         * 返回选择内容，后两级内容可能为空，需自行判断
         */
        public void selectData(Object parentObject, Object secondSubObject, Object thirdSubObject);

    }

    ![image](https://github.com/DropDownMenu/screenshots/device-2017-03-06-115054.png)
