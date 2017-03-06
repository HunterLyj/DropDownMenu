package com.hunter.dropdownmenulib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hunter.dropdownmenulib.R;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.parseColor;

/**
 * Created by liaoyeji on 2017/2/22.
 * 仿美团菜单
 */
public class DropDownMenuView extends TextView implements PopupWindow.OnDismissListener {

    private int mNormalBg;//正常状态下的背景
    private int mPressBg;//按下状态下的背景
    private int mNormalIcon;//正常状态下的图标
    private int mPressIcon;//按下状态下的图标
    private Drawable mNormalIconDrawable;
    private Drawable mPressIconDrawable;
    private int mNormalColor;//正常状态下的字体颜色
    private int mPressColor;//按下状态下的字体颜色

    private int mItemSelectTypefaceColor;//字体颜色
    private int mItemNormalTypefaceColor;
    private int mItemSelectBgColor;//背景颜色
    private int mItemNormalBgColor;

    private Context mContext;
    private int mScreenWidth;
    private int mScreenHeight;
    private DropDownListener mDropDownListener;
    private PopupListAdapter mParentAdapter;
    private PopupListAdapter mSecondSubAdapter;
    private PopupListAdapter mThirdSubAdapter;
    private View mView;
    private ListView mLvParent;
    private ListView mLvSecondSub;
    private ListView mLvThirdSub;
    private PopupWindow mPopupWindow;
    private Object mCurrentParentObject;
    private Object mCurrentSecondSubObject;
    private boolean mIsReturnUserSelect = false;
    private boolean mIsHaveSecondSub = false;
    private boolean mIsHaveThirdSub = false;


    public DropDownMenuView(Context context) {
        this(context, null);
    }

    public DropDownMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropDownMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        initView();
        initAttrs(context, attrs);
    }

    //初始化各种自定义参数
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.popupbtn);

        mNormalBg = typedArray.getResourceId(R.styleable.popupbtn_normalBg, -1);
        mPressBg = typedArray.getResourceId(R.styleable.popupbtn_pressBg, -1);
        mNormalIcon = typedArray.getResourceId(R.styleable.popupbtn_normalIcon, -1);
        mPressIcon = typedArray.getResourceId(R.styleable.popupbtn_pressIcon, -1);
        mNormalColor = typedArray.getColor(R.styleable.popupbtn_normalColor, parseColor("#2d2d37"));
        mPressColor = typedArray.getColor(R.styleable.popupbtn_pressColor, parseColor("#ed4d4d"));

        mPressIconDrawable = getResources().getDrawable(mPressIcon);
        mNormalIconDrawable = getResources().getDrawable(mNormalIcon);

        setNormal();
    }

    private void initView() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mScreenHeight = wm.getDefaultDisplay().getHeight();

        mItemSelectTypefaceColor = Color.parseColor("#ed4d4d");
        mItemNormalTypefaceColor = Color.parseColor("#2d2d37");
        mItemSelectBgColor = Color.parseColor("#f9f9f9");
        mItemNormalBgColor = Color.parseColor("#ffffff");
    }

    /**
     * 设置选中时候的按钮状态
     */
    private void setPress() {
        this.setTextColor(mPressColor);
        if (mPressBg != -1) {
            this.setBackgroundResource(mPressBg);
        }
        if (mPressIcon != -1) {
            this.setCompoundDrawablesWithIntrinsicBounds(null, null, mPressIconDrawable, null);
        }
    }

    /**
     * 设置正常模式下的按钮状态
     */
    private void setNormal() {
        this.setTextColor(mNormalColor);
        if (mNormalBg != -1) {
            this.setBackgroundResource(mNormalBg);
        }
        if (mNormalIcon != -1) {
            this.setCompoundDrawablesWithIntrinsicBounds(null, null, mNormalIconDrawable, null);
        }
    }

    /**
     * 添加数据前要先设置回调
     * <p>
     * subTotal 关联级数
     * parentPosition、secondPosition、thirdPosition 默认选中0位置
     */
    public void addList(List list, int subTotal) {
        addList(list, subTotal, 0, 0, 0);
    }

    /**
     * 添加数据前要先设置回调
     * <p>
     * subTotal 关联级数
     * parentPosition、secondPosition、thirdPosition 默认选中位置
     */
    public void addList(List list, int subTotal, int parentPosition, int secondPosition, int thirdPosition) {
        if (mDropDownListener == null) {
            throw new IllegalArgumentException("mDropDownListener is null");
        }
        switch (subTotal) {
            case 1:
                mIsHaveSecondSub = false;
                mIsHaveThirdSub = false;
                break;
            case 2:
                mIsHaveSecondSub = true;
                mIsHaveThirdSub = false;
                break;
            case 3:
                mIsHaveSecondSub = true;
                mIsHaveThirdSub = true;
                break;
            default:
                throw new IllegalArgumentException("0 < subTotal < 4");
        }
        mView = LayoutInflater.from(mContext).inflate(R.layout.popup_list, null);
        mLvParent = (ListView) mView.findViewById(R.id.list_view_parent);
        mLvSecondSub = (ListView) mView.findViewById(R.id.list_view_second_sub);
        mLvThirdSub = (ListView) mView.findViewById(R.id.list_view_third_sub);

        mParentAdapter = new PopupListAdapter();
        mParentAdapter.addList(list);
        mParentAdapter.setLastPosition(parentPosition);
        mParentAdapter.setCurrentPosition(parentPosition);
        mLvParent.setAdapter(mParentAdapter);
        mLvParent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectParentSub(position);
            }
        });

        if (mIsHaveSecondSub) {
            //显示第二级子选项
            mSecondSubAdapter = new PopupListAdapter();
            //设置是二级sub
            mSecondSubAdapter.setIsSecondSub();
            //获取一级列表第一个内容对应的list
            mCurrentParentObject = mParentAdapter.getItem(parentPosition);
            List itemList = mDropDownListener.getSecondSubList(mCurrentParentObject);
            if (itemList != null) {
                mSecondSubAdapter.clearList();
                mSecondSubAdapter.addList(itemList);
            }
            mSecondSubAdapter.setLastPosition(secondPosition);
            mSecondSubAdapter.setCurrentPosition(secondPosition);
            mLvSecondSub.setAdapter(mSecondSubAdapter);
            mLvSecondSub.setVisibility(VISIBLE);

            mLvSecondSub.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectSecondSub(position, true);
                }
            });
        }

        if (mIsHaveThirdSub) {
            //显示第三级子选项
            mThirdSubAdapter = new PopupListAdapter();
            //设置是三级sub
            mThirdSubAdapter.setIsThirdSub();
            //获取二级列表第一个内容对应的list
            mCurrentSecondSubObject = mSecondSubAdapter.getItem(secondPosition);
            List itemList = mDropDownListener.getThirdSubList(mCurrentSecondSubObject);
            if (itemList != null) {
                mThirdSubAdapter.clearList();
                mThirdSubAdapter.addList(itemList);
            }
            mThirdSubAdapter.setLastPosition(thirdPosition);
            mThirdSubAdapter.setCurrentPosition(thirdPosition);
            mLvThirdSub.setAdapter(mThirdSubAdapter);
            mLvThirdSub.setVisibility(VISIBLE);

            mLvThirdSub.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mDropDownListener != null) {
                        mIsReturnUserSelect = true;
                        mThirdSubAdapter.setCurrentPosition(position);
                        mDropDownListener.selectData(mCurrentParentObject, mCurrentSecondSubObject, mThirdSubAdapter.getItem(position));
                        mPopupWindow.dismiss();
                    }
                }
            });
        }

        //点击内容外的区域PopupWindow消失
        mView.findViewById(R.id.view_content).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        //点击ListView控件的区域PopupWindow不消失
        mView.findViewById(R.id.list_view_content).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        //构建PopupWindow
        mPopupWindow = new PopupWindow(mView, mScreenWidth, mScreenHeight);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        mPopupWindow.setOnDismissListener(this);

        //点击展示PopupWindow
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setPress();
                //将ListView移动到上次选择的位置,如果用户进行操作，又不选择，下次进来会看不到已经选择的内容
                mLvParent.setSelection(mParentAdapter.getCurrentPosition());
                if (mIsHaveSecondSub) {
                    mLvSecondSub.setSelection(mSecondSubAdapter.getCurrentPosition());
                }
                if (mIsHaveThirdSub) {
                    mLvThirdSub.setSelection(mThirdSubAdapter.getCurrentPosition());
                }
                mPopupWindow.showAsDropDown(DropDownMenuView.this);
            }
        });
    }

    /**
     * 选择一级列表处理
     */
    private void selectParentSub(int position) {
        if (mDropDownListener == null) {
            return;
        }

        mParentAdapter.setCurrentPosition(position);
        mParentAdapter.notifyDataSetChanged();
        mCurrentParentObject = mParentAdapter.getItem(position);
        if (mIsHaveSecondSub) {
            //有二级列表，继续进行相关数据操作
            List itemList = mDropDownListener.getSecondSubList(mCurrentParentObject);
            //判空处理，如一级列表第一个是“全部”情况，这时选择第一个就直接返回
            if (itemList == null) {
                //该项特殊情况，没有下一级选择
                mIsReturnUserSelect = true;
                //清空二级列表
                mSecondSubAdapter.setCurrentPosition(-1);
                mSecondSubAdapter.clearList();
                mSecondSubAdapter.notifyDataSetChanged();

                //清空三级列表
                if (mIsHaveThirdSub) {
                    mThirdSubAdapter.setCurrentPosition(-1);
                    mThirdSubAdapter.clearList();
                    mThirdSubAdapter.notifyDataSetChanged();
                }

                //返回数据
                mDropDownListener.selectData(mCurrentParentObject, null, null);
                mPopupWindow.dismiss();
            } else {
                //有二级列表数据，添加数据，并将ListView定位到0
                mSecondSubAdapter.setCurrentPosition(-1);
                mSecondSubAdapter.clearList();
                mSecondSubAdapter.addList(itemList);
                mSecondSubAdapter.notifyDataSetChanged();
                mLvSecondSub.setSelection(0);


                //有三级列表，同时应该也将三级列表进行数据同步
                if (mIsHaveThirdSub) {
                    selectSecondSub(0, false);
                }
            }
        } else {
            //没有二级列表，直接返回
            mIsReturnUserSelect = true;
            mDropDownListener.selectData(mCurrentParentObject, null, null);
            mPopupWindow.dismiss();
        }
    }

    /**
     * 二级列表处理
     * isUserClick 判断是用户选择的二级列表操作，还是一级列表操作时带动的二级列表变化
     * isUserClick true 用户直接操作二级列表
     */
    private void selectSecondSub(int position, boolean isUserClick) {
        if (mDropDownListener == null) {
            return;
        }

        mSecondSubAdapter.setCurrentPosition(position);
        mSecondSubAdapter.notifyDataSetChanged();
        mCurrentSecondSubObject = mSecondSubAdapter.getItem(position);
        if (mIsHaveThirdSub) {
            //有三级列表，继续进行相关数据操作
            List itemList = mDropDownListener.getThirdSubList(mCurrentSecondSubObject);
            //判空处理，如二级列表第一个是“全部”情况，此时不能像一级列表一样直接返回。如果是用户自己选择的，则返回数据；
            // 如果是操作一级列表带动的二级列表变化，此时将不默认选择二级列表的第一个内容
            if (itemList == null) {
                //该项特殊情况，没有下一级选择
                if (isUserClick) {
                    //用户操作二级列表，清空三级列表内容，返回数据
                    mIsReturnUserSelect = true;
                    mThirdSubAdapter.setCurrentPosition(-1);
                    mThirdSubAdapter.clearList();
                    mThirdSubAdapter.notifyDataSetChanged();
                    mDropDownListener.selectData(mCurrentParentObject, mCurrentSecondSubObject, null);
                    mPopupWindow.dismiss();
                } else {
                    //用户操作一级列表，此时将不默认选择第一个内容，清空三级列表内容
                    mSecondSubAdapter.setCurrentPosition(-1);
                    mSecondSubAdapter.notifyDataSetChanged();
                    mThirdSubAdapter.setCurrentPosition(-1);
                    mThirdSubAdapter.clearList();
                    mThirdSubAdapter.notifyDataSetChanged();
                }

            } else {
                //有三级数据，添加数据，并将ListView定位到0
                mThirdSubAdapter.setCurrentPosition(-1);
                mThirdSubAdapter.clearList();
                mThirdSubAdapter.addList(itemList);
                mThirdSubAdapter.notifyDataSetChanged();
                mLvThirdSub.setSelection(0);

                //有三级列表时，由于马上又继续操作二级列表，此时二级列表的定位到0位置失效，这里进行重新定位
                if (!isUserClick) {
                    mLvSecondSub.setSelection(0);//移动到最上
                }
            }
        } else {
            //没有三级列表，直接返回
            mIsReturnUserSelect = true;
            mDropDownListener.selectData(mCurrentParentObject, mCurrentSecondSubObject, null);
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void onDismiss() {
        setNormal();
        if (mIsReturnUserSelect) {
            //有数据返回，更新上一次保存的位置为最新位置
            mIsReturnUserSelect = false;
            mParentAdapter.setLastPosition(mParentAdapter.getCurrentPosition());
            if (mIsHaveSecondSub) {
                mSecondSubAdapter.setLastPosition(mSecondSubAdapter.getCurrentPosition());
            }
            if (mIsHaveThirdSub) {
                mThirdSubAdapter.setLastPosition(mThirdSubAdapter.getCurrentPosition());
            }
        } else {
            //无数据返回，重置之前选择的数据
            mParentAdapter.setCurrentPosition(mParentAdapter.getLastPosition());
            if (mIsHaveSecondSub) {
                mSecondSubAdapter.setCurrentPosition(mSecondSubAdapter.getLastPosition());
                mCurrentParentObject = mParentAdapter.getItem(mParentAdapter.getCurrentPosition());
                List itemList = mDropDownListener.getSecondSubList(mCurrentParentObject);
                mSecondSubAdapter.clearList();
                if (itemList != null) {
                    mSecondSubAdapter.addList(itemList);
                }
                mSecondSubAdapter.notifyDataSetChanged();
            }
            if (mIsHaveThirdSub) {
                mThirdSubAdapter.setCurrentPosition(mThirdSubAdapter.getLastPosition());
                mCurrentSecondSubObject = mSecondSubAdapter.getItem(mSecondSubAdapter.getCurrentPosition());
                List itemList = mDropDownListener.getThirdSubList(mCurrentSecondSubObject);
                mThirdSubAdapter.clearList();
                if (itemList != null) {
                    mThirdSubAdapter.addList(itemList);
                }
                mThirdSubAdapter.notifyDataSetChanged();
            }
        }
    }

    private class PopupListAdapter extends BaseAdapter {
        private boolean mIsSecondSub = false;
        private boolean mIsThirdSub = false;
        private List mData = new ArrayList<>();
        private int mCurrentPosition = 0;//当前选择位置
        private int mLastPosition = 0;//上一次确定选择的位置


        public void clearList() {
            mData.clear();
        }

        public void addList(List list) {
            mData.addAll(list);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            if (mData.size() == 0) {
                return null;
            }
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (null == convertView) {
                textView = new TextView(mContext);
                textView.setTextSize(15);
                textView.setPadding(dip2px(mContext, 20), 0, dip2px(mContext, 10), 0);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setSingleLine();
                ListView.LayoutParams layoutParams = new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(mContext, 44));
                textView.setLayoutParams(layoutParams);
                convertView = textView;
            } else {
                textView = (TextView) convertView;
            }

            if (mDropDownListener != null) {
                if (mIsSecondSub) {
                    //有二级列表
                    if (mIsThirdSub) {
                        //三级列表
                        textView.setText(mDropDownListener.getThirdSubItemName(getItem(position)));
                    } else {
                        //二级列表
                        textView.setText(mDropDownListener.getSecondSubItemName(getItem(position)));
                    }
                } else {
                    //一级列表
                    textView.setText(mDropDownListener.getParentItemName(getItem(position)));
                }
            } else {
                textView.setText("null");
            }

            if (mCurrentPosition == position) {
                textView.setTextColor(mItemSelectTypefaceColor);
                textView.setBackgroundColor(mItemSelectBgColor);
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_choose_red, 0);
            } else {
                textView.setTextColor(mItemNormalTypefaceColor);
                textView.setBackgroundColor(mItemNormalBgColor);
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            return convertView;
        }

        public void setCurrentPosition(int positon) {
            mCurrentPosition = positon;
        }

        public int getCurrentPosition() {
            return mCurrentPosition;
        }

        public void setLastPosition(int positon) {
            mLastPosition = positon;
        }

        public int getLastPosition() {
            return mLastPosition;
        }

        public void setIsSecondSub() {
            mIsSecondSub = true;
        }

        public void setIsThirdSub() {
            mIsSecondSub = true;
            mIsThirdSub = true;
        }
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }

    /**
     * 设置弹窗显示高度
     *
     * @param heightDp
     */
    public void setPopupWindowHeight(int heightDp) {
        LinearLayout linearLayout = (LinearLayout) mView.findViewById(R.id.list_view_content);
        ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
        layoutParams.height = dip2px(mContext, heightDp);
        linearLayout.setLayoutParams(layoutParams);
    }

    /**
     * 设置各级weight
     *
     * @param parentWeight
     * @param secondWeight
     * @param thirdWeight
     */
    public void setListViewWeight(int parentWeight, int secondWeight, int thirdWeight) {
        LinearLayout.LayoutParams parentLayoutParams = (LinearLayout.LayoutParams) mLvParent.getLayoutParams();
        parentLayoutParams.weight = parentWeight;
        mLvParent.setLayoutParams(parentLayoutParams);

        LinearLayout.LayoutParams secondLayoutParams = (LinearLayout.LayoutParams) mLvSecondSub.getLayoutParams();
        secondLayoutParams.weight = secondWeight;
        mLvSecondSub.setLayoutParams(secondLayoutParams);

        LinearLayout.LayoutParams thirdLayoutParams = (LinearLayout.LayoutParams) mLvThirdSub.getLayoutParams();
        thirdLayoutParams.weight = thirdWeight;
        mLvThirdSub.setLayoutParams(thirdLayoutParams);
    }

    public void setDropDownListener(DropDownListener dropDownListener) {
        mDropDownListener = dropDownListener;
    }

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
}
