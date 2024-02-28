package com.whatscolors.demo.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whatscolors.demo.takephoto.R;
import com.whatscolors.demo.utils.SPUtils;

public class MyBottomSheetDialog extends BottomSheetDialog {


    Spinner spinner1;
    Spinner spinner2;
    private String[] sa_1 = new String[]{"US", "Euro", "Other"};
    private String[] sa_2_1 = new String[]{"DAYLIGHT", "COOL WHITE", "HORIZON", "U35", "U30", "TL84", "INCA", "UV"};
    private String[] sa_2_2 = new String[]{"D65", "TL84", "F", "UV"};
    private String[] sa_2_3 = new String[]{"Other"};

    private Context context;

    private String sp1_str, sp2_str;
    private BottomSheetBehavior behavior;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;


    public MyBottomSheetDialog(@NonNull Context context, BottomSheetBehavior.BottomSheetCallback bottomSheetCallback) {
        super(context);
        this.context = context;
        this.bottomSheetCallback = bottomSheetCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_lb);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initSp();
    }

    private void initSp() {
        spinner1 = findViewById(R.id.spinner_1);
        spinner2 = findViewById(R.id.spinner_2);
        // 建立数据源
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.sp_text, sa_1);//android.R.layout.simple_spinner_item
        adapter.setDropDownViewResource(R.layout.sp_dropdown_text);
        //绑定 Adapter到控件
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.sp_text);

                adapter.setDropDownViewResource(R.layout.sp_dropdown_text);//android.R.layout.simple_spinner_dropdown_item

                switch (pos) {
                    case 0:
                        adapter.addAll(sa_2_1);
                        break;
                    case 1:
                        adapter.addAll(sa_2_2);
                        break;
                    case 2:
                        adapter.addAll(sa_2_3);
                        break;
                }
                //绑定 Adapter到控件
                spinner2.setAdapter(adapter);
                sp1_str = sa_1[pos];
                SPUtils.setSharedStringData(context, "sp1_str", sp1_str);
                SPUtils.setSharedIntData(context, "sp1_str_ps", pos);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sp2_str = (String) spinner2.getAdapter().getItem(position);
                SPUtils.setSharedStringData(context, "sp2_str", sp2_str);
                SPUtils.setSharedIntData(context, "sp2_str_ps", position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner1.setSelection(SPUtils.getSharedIntData(context, "sp1_str_ps"));
        spinner2.setSelection(SPUtils.getSharedIntData(context, "sp2_str_ps"));

        View bottomSheet = findViewById(R.id.sp_scroll);
//        behavior = BottomSheetBehavior.from(bottomSheet);
//        behavior.setBottomSheetCallback(bottomSheetCallback);
    }

}
