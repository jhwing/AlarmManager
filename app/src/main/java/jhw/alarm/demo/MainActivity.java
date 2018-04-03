package jhw.alarm.demo;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.List;

import jhw.alarm.AlarmUtil;
import jhw.alarm.data.AlarmItem;
import jhw.alarm.data.AlarmRepository;

public class MainActivity extends AppCompatActivity {

    Activity mActivity;
    AlarmRepository alarmRepository;
    RecyclerView alarmListView;
    AlarmListAdapter alarmListAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmRepository = new AlarmRepository(this);
        alarmListView = findViewById(R.id.alarmListView);
        alarmListView.setLayoutManager(new LinearLayoutManager(this));
        alarmListAdapter = new AlarmListAdapter();

        alarmListView.setAdapter(alarmListAdapter);
        AlarmUtil.requestService(MyAlarmService.class);
        alarmRepository.isExist(new AlarmRepository.IsExistCallback() {
            @Override
            public void onExist(boolean isExist) {
                Log.d("jihongwen", "onExist::" + isExist);
                if (isExist) {
                    AlarmUtil.resume(mActivity);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getList();
    }

    private void getList() {
        alarmRepository.getAlarmList(new AlarmRepository.LoadTasksCallback() {
            @Override
            public void onItemsLoaded(List<AlarmItem> items) {
                Log.d("jihongwen", "onItemsLoaded");
                if (items.isEmpty()) {
                    // 没有提醒，停止服务
                    AlarmUtil.stopAlarmService(mActivity);
                }
                alarmListAdapter.setItemList(items);
            }

            @Override
            public void onDataNotAvailable() {
                Log.d("jihongwen", "onDataNotAvailable");
            }
        });
    }

    public void addAlarm(View view) {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                AlarmItem alarmItem = new AlarmItem((int) (calendar.getTimeInMillis() / 1000), calendar.getTimeInMillis(), AlarmUtil.INTERVAL, true);
                AlarmUtil.addAlarm(mActivity, alarmItem);
                alarmRepository.saveAlarm(alarmItem);
                getList();
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void cancelAlarm(View view) {
        alarmRepository.getAlarmList(new AlarmRepository.LoadTasksCallback() {
            @Override
            public void onItemsLoaded(List<AlarmItem> items) {
                for (AlarmItem item : items) {
                    AlarmUtil.cancel(mActivity, item.id);
                }
            }

            @Override
            public void onDataNotAvailable() {

            }
        });

    }

    class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.AlarmViewHolder> {

        LayoutInflater inflater = LayoutInflater.from(mActivity);

        List<AlarmItem> itemList;

        public void setItemList(List<AlarmItem> itemList) {
            this.itemList = itemList;
            notifyDataSetChanged();
        }

        @Override
        public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AlarmViewHolder(inflater.inflate(R.layout.alarm_item_view, parent, false));
        }

        @Override
        public void onBindViewHolder(AlarmViewHolder holder, int position) {
            holder.bind(itemList.get(position));
        }

        @Override
        public int getItemCount() {
            return itemList == null ? 0 : itemList.size();
        }

        class AlarmViewHolder extends RecyclerView.ViewHolder {

            TextView alarmIdView;
            TextView alarmDateView;
            View deleteBtn;

            public AlarmViewHolder(View itemView) {
                super(itemView);
                alarmIdView = itemView.findViewById(R.id.alarm_id);
                alarmDateView = itemView.findViewById(R.id.alarm_date);
                deleteBtn = itemView.findViewById(R.id.delete_btn);
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlarmItem alarmItem = itemList.get(getLayoutPosition());
                        alarmRepository.delete(alarmItem);
                        AlarmUtil.cancel(mActivity, alarmItem.id);
                        getList();

                    }
                });
            }

            public void bind(AlarmItem alarmItem) {
                alarmIdView.setText(alarmItem.getId());
                alarmDateView.setText(alarmItem.getDate());
            }
        }
    }
}
