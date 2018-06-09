package jhw.alarm.demo;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.List;

import jhw.alarm.AlarmCompat;
import jhw.alarm.AlarmUtil;
import jhw.alarm.data.AlarmItem;
import jhw.alarm.data.AlarmRepository;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    Activity mActivity;
    AlarmRepository alarmRepository;
    SwipeRefreshLayout swipeRefresh;
    RecyclerView alarmListView;
    FloatingActionButton addBtn;
    AlarmListAdapter alarmListAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmRepository = new AlarmRepository(this);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        alarmListView = findViewById(R.id.alarmListView);
        alarmListView.setLayoutManager(new LinearLayoutManager(this));
        alarmListAdapter = new AlarmListAdapter();
        alarmListView.setAdapter(alarmListAdapter);

        addBtn = findViewById(R.id.addClock);
        addBtn.setOnClickListener(this);

        alarmRepository.isExist(new AlarmRepository.IsExistCallback() {
            @Override
            public void onExist(boolean isExist) {
                Log.d("jihongwen", "onExist::" + isExist);
                if (isExist) {
                    alarmRepository.getAlarmList(new AlarmRepository.LoadTasksCallback() {
                        @Override
                        public void onItemsLoaded(List<AlarmItem> items) {
                            for (AlarmItem item : items) {
                                AlarmCompat.addAlarm(MainActivity.this, item);
                            }
                        }

                        @Override
                        public void onDataNotAvailable() {
                        }
                    });
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
                swipeRefresh.setRefreshing(false);
                alarmListAdapter.setItemList(items);
            }

            @Override
            public void onDataNotAvailable() {
                Log.d("jihongwen", "onDataNotAvailable");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addClock:
                // 添加提醒
                add();
                break;
        }
    }

    @Override
    public void onRefresh() {
        getList();
    }

    private void add() {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                AlarmItem alarmItem = new AlarmItem((int) (calendar.getTimeInMillis() / 1000), calendar.getTimeInMillis(), AlarmUtil.MINUTE, true);
                AlarmCompat.addAlarm(mActivity, alarmItem);
                alarmRepository.saveAlarm(alarmItem);
                getList();
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void del(AlarmItem alarmItem) {
        alarmRepository.delete(alarmItem);
        AlarmCompat.cancel(mActivity, alarmItem.id);
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
            SwitchCompat switchBtn;

            public AlarmViewHolder(final View itemView) {
                super(itemView);
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(MainActivity.this, itemView);
                        popupMenu.inflate(R.menu.item_action);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                int position = getLayoutPosition();
                                del(itemList.remove(position));
                                notifyItemRemoved(position);
                                return true;
                            }
                        });

                        popupMenu.show();
                        return true;
                    }
                });
                alarmIdView = itemView.findViewById(R.id.alarm_id);
                alarmDateView = itemView.findViewById(R.id.alarm_date);
                switchBtn = itemView.findViewById(R.id.switchBtn);
                switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            // 打开闹钟
                        } else {
                            // 关闭闹钟
                        }
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
