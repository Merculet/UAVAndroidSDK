package io.merculet.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import io.merculet.config.BaseAdapter;
import io.merculet.config.BaseViewHolder;
import io.merculet.config.RequestUtil;
import io.merculet.domain.ListBean;
import io.merculet.domain.UavListResponse;
import io.merculet.example.R;
import cn.magicwindow.uav.sdk.http.ResponseListener;

/**
 * @Description
 * @Author sean
 * @Email xiao.lu@magicwindow.cn
 * @Date 18/09/2018 2:16 PM
 * @Version
 */
public class UavHistoryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uav_history);
        initView();
        initData();
    }

    private void initView() {
        String userId = getIntent().getStringExtra("userId");
        ((TextView)findViewById(R.id.tv_user_id)).setText("userId: "+userId);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, OrientationHelper.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.shape_list_divider_gray_05dp));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private BaseAdapter<ListBean> adapter = new BaseAdapter<ListBean>() {
        @Override
        public void onBindViewHolderImpl(BaseViewHolder holder, int position, ListBean listBean) {
            holder.<TextView>getView(R.id.tv_name).setText("rule name: " + listBean.rule_name);
            holder.<TextView>getView(R.id.tv_quantity).setText("change quantity: " + listBean.change_quantity);
        }

        @Override
        public int getLayoutId(int viewType) {
            return R.layout.cell_uav_history;
        }
    };

    private void initData() {
        RequestUtil.getUavList(new ResponseListener<UavListResponse>() {

            @Override
            public void onSuccess(UavListResponse uavListResponse) {
                adapter.addData(uavListResponse.data.list);
            }

            @Override
            public void onFail(Exception e) {
                Toast.makeText(UavHistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
