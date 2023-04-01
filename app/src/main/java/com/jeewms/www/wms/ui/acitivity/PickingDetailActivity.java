package com.jeewms.www.wms.ui.acitivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.View;

import com.android.volley.VolleyError;
import com.jeewms.www.wms.bean.bean.MessageEvent;
import com.jeewms.www.wms.util.StringUtil;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;
import com.jeewms.www.wms.R;
import com.jeewms.www.wms.base.BaseActivity;
import com.jeewms.www.wms.bean.listvm.PickingListVm;
import com.jeewms.www.wms.constance.Constance;
import com.jeewms.www.wms.ui.adapter.GoogleCardsAdapter;
import com.jeewms.www.wms.util.GsonUtils;
import com.jeewms.www.wms.util.LoadingUtil;
import com.jeewms.www.wms.util.Logutil;
import com.jeewms.www.wms.util.SharedPreferencesUtil;
import com.jeewms.www.wms.util.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.jeewms.www.wms.volley.HTTPUtils;
import com.jeewms.www.wms.volley.VolleyListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 13799 on 2018/6/7.
 * <p>
 * 按单拣货
 */

public class PickingDetailActivity extends BaseActivity implements OnDismissCallback {

    GoogleCardsAdapter mAdapter;
    @BindView(R.id.activity_googlecards_listview)
    ListView mListView;
    @BindView(R.id.et_search)
    AutoCompleteTextView etSearch;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.et_search2)
    AutoCompleteTextView etSearch2;
    @BindView(R.id.et_search3)
    AutoCompleteTextView etSearch3;
    public static void show(Context context) {
        Intent intent = new Intent(context, PickingDetailActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        super.initView();
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        etSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int i, KeyEvent keyEvent) {

                if (i == KeyEvent.KEYCODE_ENTER) {
                    getDate(etSearch.getText().toString(),etSearch2.getText().toString(),etSearch3.getText().toString());
                    final EditText et_search3 = (EditText) findViewById(R.id.et_search3);
                    et_search3.requestFocus();
                    return true;
                }
                return false;
            }
        });


        etSearch3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int i, KeyEvent keyEvent) {

                if (i == KeyEvent.KEYCODE_ENTER) {
                    getDate(etSearch.getText().toString(),etSearch2.getText().toString(),etSearch3.getText().toString());

                    if(!StringUtil.isEmpty(etSearch3.getText().toString())){
                        final EditText et_search2 = (EditText) findViewById(R.id.et_search2);
                        et_search2.requestFocus();
                    }

                    return true;
                }
                return false;
            }
        });

        etSearch2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int i, KeyEvent keyEvent) {

                if (i == KeyEvent.KEYCODE_ENTER) {
                    getDate(etSearch.getText().toString(),etSearch2.getText().toString(),etSearch3.getText().toString());
                    return true;
                }
                return false;
            }
        });




                mBtnLeft.setVisibility(View.VISIBLE);
//        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if(actionId == EditorInfo.IME_ACTION_GO){
//                    getDate(etSearch.getText().toString(),etSearch2.getText().toString(),etSearch3.getText().toString());
//                    return true;
//                }
//                return false;
//            }
//        });

        setTitle("拣货下架");
        addAdapter();
        getDate("","","");
        LoadingUtil.showLoading(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEventBus(MessageEvent msg) {
        getDate(etSearch.getText().toString(),etSearch2.getText().toString(),etSearch3.getText().toString());
    }
    private void addAdapter() {
        mAdapter = new GoogleCardsAdapter(this);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new SwipeDismissAdapter(mAdapter, this));
        swingBottomInAnimationAdapter.setInitialDelayMillis(300);
        swingBottomInAnimationAdapter.setAbsListView(mListView);
        mListView.setAdapter(swingBottomInAnimationAdapter);

    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_pickingnew;
    }



    private void getDate(String searchKey,String searchKey2,String searchKey3) {
        Map<String, String> params = new HashMap<>();
        String url=Constance.getGiNoticeURL()+ "/search?username="+ SharedPreferencesUtil.getInstance(this).getKeyValue(Constance.SHAREP.LOGINNAME);
        url+="&searchstr="+searchKey;

        url += "&searchstr2=" + searchKey2;
        url += "&searchstr3=" + searchKey3;

        Logutil.print("url"+url);

        HTTPUtils.get(this,url, new VolleyListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(String response) {
                PickingListVm vm = GsonUtils.parseJSON(response, PickingListVm.class);
                if(vm!=null&&vm.getObj()!=null) {
                    Logutil.print("重新加载数据");
                    mAdapter.setmPickingList(vm.getObj());
                    mAdapter.notifyDataSetChanged();
                    LoadingUtil.hideLoading();
                }
            }
        });
    }

    @Override
    public void onDismiss(@NonNull ViewGroup listView, @NonNull int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            mAdapter.remove(position);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    @OnClick({R.id.et_search, R.id.btn_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.et_search:
                break;
            case R.id.btn_search:
                getDate(etSearch.getText().toString(),etSearch2.getText().toString(),etSearch3.getText().toString());
                break;
        }
    }
}
