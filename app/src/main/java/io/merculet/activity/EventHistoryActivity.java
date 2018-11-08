package io.merculet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.merculet.domain.EventDataBean;
import io.merculet.domain.EventHistoryBean;
import io.merculet.example.R;

public class EventHistoryActivity extends BaseActivity {

    private List<EventHistoryBean> events;
    private List<String> events_list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_history);
        initData();
        initView();
    }

    private void initView() {
        ListView lv = (ListView) findViewById(R.id.list);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, events_list));
    }

    private void initData() {
        events_list = new ArrayList<>();
        Intent intent = getIntent();
        EventDataBean datas = (EventDataBean) intent.getSerializableExtra("datas");
        events = datas.datas;
        for (int i = 0; i < events.size(); i++) {
            String data = events.get(i).getEventName() + "============>" + events.get(i).getTime();
            events_list.add(data);
        }
    }
}
