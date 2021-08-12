package com.pcs.ztqtj.view.fragment.warning.emergency_responsibility;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.pcs.lib.lib_pcs_v3.control.tool.Util;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataBrocastReceiver;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataDownload;
import com.pcs.lib.lib_pcs_v3.model.data.PcsDataManager;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackYjDepDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackYjDepUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackYjZqInfoAreaDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackYjZqInfoAreaUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackYjZqUserDown;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.PackYjZqUserUp;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.YjUserInfo;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.YjZqInfoArea;
import com.pcs.lib_ztqfj_v2.model.pack.net.warn.sh_warn.YjZqInfoAreaDown;
import com.pcs.ztqtj.MyApplication;
import com.pcs.ztqtj.R;
import com.pcs.ztqtj.control.adapter.livequery.AdapterData;
import com.pcs.ztqtj.view.activity.FragmentActivityZtqWithPhoneListAndHelp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/11 0011.
 * chen_jx
 */

public class Activity_respon extends FragmentActivityZtqWithPhoneListAndHelp implements View.OnClickListener {

    private ListView list_respon;
    private TextView tv_company, tv_name,tv_respon_list,tv_respon_street,tv_respon_search;
    private EditText et_phone;
    private ImageView iv_respon_search;
    private List<YjUserInfo> list=new ArrayList<>();
    private List<YjZqInfoAreaDown> list_town=new ArrayList<>();
    private AdatperRespon adapter;
    protected ImageButton btnHelp = null;
    private List<String> mlist_province, mlist_county,mlist_down;
    // 每页条目数量
    private static final String COUNT = "30";
    // 是否在加载中
    private boolean isLoading = false;
    // 是否全部请求完成
    private boolean isReqFinish = false;
    //当前页数
    private int currentPage = 1,pos_are;
    private String p_id,sub_id,p_type,town_id="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleText("应急责任人名单");
        setContentView(R.layout.activity_respon);
        Intent intent =getIntent();
        p_id=intent.getStringExtra("p_id");
        sub_id=intent.getStringExtra("sub_id");
        p_type=intent.getStringExtra("p_type");
        initView();
        showProgressDialog();
        Check_Person();
    }

    private PackYjZqUserUp packYjZqUserUp = new PackYjZqUserUp();

    private PackYjZqInfoAreaUp packYjZqInfoAreaUp = new PackYjZqInfoAreaUp();

    private PackYjDepUp packYjDepUp = new PackYjDepUp();

    public void CheckDep(String type, String id) {
        town_id="";
        packYjDepUp.type=type;
        packYjDepUp.s_id=id;
        PcsDataDownload.addDownload(packYjDepUp);
    }

    private void CheckList(){
        packYjZqUserUp.page = String.valueOf(currentPage);
        packYjZqUserUp.p_id = p_id;
        packYjZqUserUp.key_str = et_phone.getText().toString();
        packYjZqUserUp.p_type=p_type;
        packYjZqUserUp.sub_id = sub_id;
        packYjZqUserUp.town_id=town_id;
        PcsDataBrocastReceiver.registerReceiver(Activity_respon.this, mReceiver);
        packYjZqUserUp.mobile = MyApplication.MOBILE;
        //下载
        PcsDataDownload.addDownload(packYjZqUserUp);
    }

    private void Check_Person() {
        //请求
        packYjZqUserUp.page = String.valueOf(currentPage);
        packYjZqUserUp.p_id = p_id;
        packYjZqUserUp.key_str = et_phone.getText().toString();
        packYjZqUserUp.sub_id = sub_id;
        packYjZqUserUp.p_type=p_type;
        packYjZqUserUp.town_id=town_id;
        packYjZqUserUp.mobile = MyApplication.MOBILE;
        PcsDataBrocastReceiver.registerReceiver(Activity_respon.this, mReceiver);
        //下载
        PcsDataDownload.addDownload(packYjZqInfoAreaUp);
        PcsDataDownload.addDownload(packYjZqUserUp);
    }

    private List<YjUserInfo> lists = new ArrayList<>();
    private List<YjZqInfoArea> mlists = new ArrayList<>();
    private List<YjZqInfoAreaDown> list_dowm = new ArrayList<>();
    private PcsDataBrocastReceiver mReceiver = new PcsDataBrocastReceiver() {
        @Override
        public void onReceive(String nameStr, String errorStr) {
            if (TextUtils.isEmpty(nameStr)) {
                return;
            }

            if (nameStr.equals(packYjZqUserUp.getName())) {
                //等待框
                dismissProgressDialog();
                PackYjZqUserDown packDown = (PackYjZqUserDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (packDown == null) {
                    //PcsDataDownload.addDownload(packYjZqUserUp);
                    return;
                }
                if (currentPage==1){
                    lists.clear();
                }
                if (packDown.list_2 != null && packDown.list_2.size() > 0) {
                    tv_respon_list.setVisibility(View.GONE);
                    lists.addAll(packDown.list_2);

                    int count = Integer.parseInt(COUNT);
                    // 当请求回来的数据列表小于count时则表示已无更多数据了，所以不需要再请求并隐藏加载更多
                    if(packDown.list_2.size() < count) {
                        adapter.setLoadingVisibility(false);
                        isReqFinish = true;
                    } else {
                        isReqFinish = false;
                        adapter.setLoadingVisibility(true);
                    }

                    adapter.notifyDataSetChanged();
                }else{
                    tv_respon_list.setVisibility(View.VISIBLE);
                }
//                if(packDown.list_2 != null) {
//
//                    adapter.notifyDataSetChanged();
//                }
            }
            if (nameStr.equals(packYjZqInfoAreaUp.getName())) {
                //等待框
                dismissProgressDialog();
                PackYjZqInfoAreaDown packDown = (PackYjZqInfoAreaDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (packDown == null) {
                    //PcsDataDownload.addDownload(packYjZqInfoAreaUp);
                    return;
                }
                if (packDown.list_2.size()>0) {
                    mlists.addAll(packDown.list_2);
                    list_dowm.addAll(mlists.get(0).list);
                    initData(0);
                    mlist_down.add("全部");
                }
            }

            if (nameStr.equals(packYjDepUp.getName())){
                dismissProgressDialog();
                PackYjDepDown down= (PackYjDepDown) PcsDataManager.getInstance().getNetPack(nameStr);
                if (down==null){
                    return;
                }
                list_town.clear();
                mlist_down.clear();
                if (down.list_2!=null){
                    list_town.addAll(down.list_2);
                    for (int i=0;i<down.list_2.size();i++){
                        mlist_down.add(down.list_2.get(i).s_name);
                    }
                }
                tv_respon_street.setText(mlist_down.get(0));
            }
        }
    };

    private AbsListView.OnScrollListener myOnScrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // 当不滚动时
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                Log.e("position", view.getLastVisiblePosition() + "");
                // 判断是否滚动到底部
                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                    // 加载更多功能的代码
                    Log.e("jzy", "到了底部，加载更多");

                    if (!isLoading && !isReqFinish) {
                        reqCenterDataMore();
                    }
                }
            }
        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i1, int i2) {

        }
    };

    TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub
            temp = s;

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            //key_str=et_phone.getText().toString();
            currentPage=1;
            CheckList();
        }
    };

    private void reqCenterDataMore() {
       // isLoading = true;
        currentPage++;
        packYjZqUserUp.page = String.valueOf(currentPage);
        packYjZqUserUp.p_id = p_id;
        packYjZqUserUp.key_str = et_phone.getText().toString();
        packYjZqUserUp.sub_id = sub_id;
        packYjZqUserUp.p_type=p_type;
        packYjZqUserUp.town_id=town_id;
        packYjZqUserUp.mobile = MyApplication.MOBILE;
        PcsDataDownload.addDownload(packYjZqUserUp);
        Log.e("page:" , currentPage + "");
    }
    private void initData(int flag) {

        mlist_province = new ArrayList<>();
        mlist_county = new ArrayList<>();
        mlist_down=new ArrayList<>();

        if (mlist_province != null) {
            mlist_province.clear();
        }
        if (mlist_county != null) {
            mlist_county.clear();
        }
        for (int i = 0; i < mlists.size(); i++) {
            mlist_province.add(mlists.get(i).name);
        }
        for (int i = 0; i < mlists.get(flag).list.size(); i++) {
            mlist_county.add(mlists.get(flag).list.get(i).s_name);
        }
        tv_name.setText(mlist_county.get(0));
        tv_respon_street.setText("全部");
        //sub_id = list_dowm.get(0).s_id;
//        showProgressDialog();
//        CheckList();

    }

    private void initView() {
        list_respon = (ListView) findViewById(R.id.lv_respon_info);
        list_respon.setOnScrollListener(myOnScrollListener);
        tv_company = (TextView) findViewById(R.id.tv_respon_province);
        tv_company.setOnClickListener(this);
        tv_name = (TextView) findViewById(R.id.tv_respon_county);
        tv_name.setOnClickListener(this);
        tv_respon_street= (TextView) findViewById(R.id.tv_respon_street);
        tv_respon_street.setOnClickListener(this);
        tv_respon_search= (TextView) findViewById(R.id.tv_respon_search);
        tv_respon_search.setOnClickListener(this);
        et_phone = (EditText) findViewById(R.id.et_respon_phone);
//        et_phone.addTextChangedListener(mTextWatcher);
        iv_respon_search= (ImageView) findViewById(R.id.iv_respon_search);
        iv_respon_search.setOnClickListener(this);
        btnHelp = (ImageButton) findViewById(R.id.btn_right);
        btnHelp.setVisibility(View.GONE);
        tv_respon_list= (TextView) findViewById(R.id.tv_respon_list);

        adapter = new AdatperRespon(Activity_respon.this, lists);
        list_respon.setAdapter(adapter);
        list_respon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(Activity_respon.this,ActivityOtherself.class);
                intent.putExtra("name",lists.get(i).fullname);
                intent.putExtra("phone",lists.get(i).mobile);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_respon_province:
                createTimePopupWindow(tv_company, mlist_province,"1")
                        .showAsDropDown(tv_company);

                break;
            case R.id.tv_respon_county:
                createTimePopupWindow(tv_name, mlist_county,"2")
                        .showAsDropDown(tv_name);
                break;
            case R.id.tv_respon_street:
                createTimePopupWindow(tv_respon_street, mlist_down,"3")
                        .showAsDropDown(tv_respon_street);

                break;
            case R.id.tv_respon_search:
                showProgressDialog();
                currentPage=1;
                CheckList();
                break;
            case R.id.iv_respon_search:
                showProgressDialog();
                currentPage=1;
                CheckList();
                break;
        }
    }

    private int screenHight;

    /**
     * 创建下拉选择列表
     */
    public PopupWindow createTimePopupWindow(final TextView dropDownView, final List<String> dataeaum,final String flag) {
        AdapterData dataAdapter = new AdapterData(Activity_respon.this, dataeaum);
        View popcontent = LayoutInflater.from(Activity_respon.this).inflate(R.layout.pop_list_layout, null);
        ListView lv = (ListView) popcontent.findViewById(R.id.mylistviw);
        lv.setAdapter(dataAdapter);
        final PopupWindow pop = new PopupWindow(Activity_respon.this);
        pop.setContentView(popcontent);
        pop.setOutsideTouchable(false);
        pop.setWidth((int) (dropDownView.getWidth() * 2));
        // 调整下拉框长度
        screenHight = Util.getScreenHeight(Activity_respon.this);
        if (dataeaum.size() < 9) {
            pop.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            pop.setHeight((int) (screenHight * 0.6));
        }
        pop.setFocusable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pop.dismiss();
                String showTimeStr = dataeaum.get(position);
                dropDownView.setText(showTimeStr);
                et_phone.setText("");
                currentPage=1;
                //showProgressDialog();
                if (flag.equals("1")) {
                    p_id = mlists.get(position).id;
                    p_type = mlists.get(position).type;
                    sub_id = mlists.get(position).list.get(0).s_id;
                    pos_are=position;
                    list_dowm.clear();
                    initData(position);
                    town_id="";
//                    showProgressDialog();
//                    CheckList();
                } else if (flag.equals("2")){
                    sub_id = mlists.get(pos_are).list.get(position).s_id;
                    showProgressDialog();
                    //CheckList();
                    showProgressDialog();
                    if (position==0){
                        CheckDep("0",sub_id);
                    }else if (position==1){
                        CheckDep("1",sub_id);
                    }else {
                        CheckDep("2",sub_id);
                    }
                }else {
                    town_id=list_town.get(position).s_id;
                }
                list.clear();
            }
        });
        return pop;
    }
}
