package com.hunter.dropdownmenu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.hunter.dropdownmenulib.view.DropDownMenuView;

import java.util.ArrayList;
import java.util.List;

import static com.hunter.dropdownmenulib.view.DropDownMenuView.DropDownListener;
import static com.hunter.dropdownmenulib.view.DropDownMenuView.dip2px;

public class MainActivity extends AppCompatActivity {

    private DropDownMenuView mDropDownMenuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDropDownMenuView = (DropDownMenuView) findViewById(R.id.drop_view);

        List<OneItem> list = new ArrayList<>();
        OneItem oneItem;
        TwoItem twoItem;
        ThreeItem threeItem;
        List<TwoItem> listItem;
        List<ThreeItem> threeListItem;
        for (int i = 0; i < 10; i++) {
            oneItem = new OneItem();
            oneItem.name = "one " + i;

            listItem = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                twoItem = new TwoItem();
                twoItem.name = "one " + i + " " + j;

                threeListItem = new ArrayList<>();
                for (int k = 0; k < 10; k++) {
                    threeItem = new ThreeItem();
                    threeItem.name = "one " + i + " " + j + " " + k;
                    threeListItem.add(threeItem);
                }
                twoItem.list = threeListItem;
                listItem.add(twoItem);
            }
            oneItem.list = listItem;
            list.add(oneItem);
        }

        list.get(0).list = null;
        list.get(0).name = "全部";
        list.get(1).list.get(0).list = null;
        list.get(1).list.get(0).name = "全部";

        mDropDownMenuView.setDropDownListener(new DropDownListener() {
            @Override
            public String getParentItemName(Object parentObject) {
                return ((OneItem) parentObject).name;
            }

            @Override
            public List getSecondSubList(Object parentObject) {
                if (parentObject == null) {
                    return null;
                }
                return ((OneItem) parentObject).list;
            }

            @Override
            public String getSecondSubItemName(Object secondSubObject) {
                return ((TwoItem) secondSubObject).name;
            }

            @Override
            public List getThirdSubList(Object secondSubObject) {
                if (secondSubObject == null) {
                    return null;
                }
                return ((TwoItem) secondSubObject).list;
            }

            @Override
            public String getThirdSubItemName(Object thirdSubObject) {
                return ((ThreeItem) thirdSubObject).name;
            }

            @Override
            public void selectData(Object parentObject, Object secondSubObject, Object thirdSubObject) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(((OneItem) parentObject).name);
                stringBuffer.append("  ");
                if (secondSubObject != null) {
                    stringBuffer.append(((TwoItem) secondSubObject).name);
                    stringBuffer.append("  ");
                }
                if (thirdSubObject != null) {
                    stringBuffer.append(((ThreeItem) thirdSubObject).name);
                    stringBuffer.append("  ");
                }
                mDropDownMenuView.setText(stringBuffer.toString());
            }
        });
        mDropDownMenuView.addList(list, DropDownMenuView.THREE_LIST);
        mDropDownMenuView.setDropDownCustomViewListener(new DropDownMenuView.DropDownCustomViewListener() {
            @Override
            public View getParentView(Object parentObject, int currentPosition, int position, View convertView, ViewGroup parent) {
                TextView textView;
                if (null == convertView) {
                    textView = new TextView(MainActivity.this);
                    textView.setTextSize(15);
                    textView.setPadding(dip2px(MainActivity.this, 20), 0, dip2px(MainActivity.this, 10), 0);
                    textView.setGravity(Gravity.CENTER_VERTICAL);
                    textView.setSingleLine();
                    ListView.LayoutParams layoutParams = new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(MainActivity.this, 44));
                    textView.setLayoutParams(layoutParams);
                    convertView = textView;
                } else {
                    textView = (TextView) convertView;
                }

                textView.setText(((OneItem) parentObject).name);

                if (currentPosition == position) {
                    textView.setTextColor(Color.BLUE);
                } else {
                    textView.setTextColor(Color.GREEN);
                }
                return convertView;
            }

            @Override
            public View getSecondSubView(Object secondSubObject, int currentPosition, int position, View convertView, ViewGroup parent) {
                return null;
            }

            @Override
            public View getThirdSubView(Object thirdSubObject, int currentPosition, int position, View convertView, ViewGroup parent) {
                return null;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    class OneItem {
        String name;
        List<TwoItem> list;
    }

    class TwoItem {
        String name;
        List<ThreeItem> list;
    }

    class ThreeItem {
        String name;
    }
}
