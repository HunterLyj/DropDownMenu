package com.hunter.dropdownmenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hunter.dropdownmenulib.view.DropDownMenuView;

import java.util.ArrayList;
import java.util.List;

import static com.hunter.dropdownmenulib.view.DropDownMenuView.*;

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
            public String getParentItemName(Object object) {
                return ((OneItem) object).name;
            }

            @Override
            public List getSecondSubList(Object object) {
                if (object == null) {
                    return null;
                }
                return ((OneItem) object).list;
            }

            @Override
            public String getSecondSubItemName(Object object) {
                return ((TwoItem) object).name;
            }

            @Override
            public List getThirdSubList(Object object) {
                if (object == null) {
                    return null;
                }
                return ((TwoItem) object).list;
            }

            @Override
            public String getThirdSubItemName(Object object) {
                return ((ThreeItem) object).name;
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
        mDropDownMenuView.addList(list, 3);
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
