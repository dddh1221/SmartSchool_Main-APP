package com.jungbo.j4android.smartschool_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StudentAttendanceActivity extends AppCompatActivity {

    private TextView attendance_today_tv, attendance_state_tv;
    private GridView gridView_calendar;
    private ListView listView_attendance;

    private Intent main_intent;

    private GridAdapter gridAdapter;
    private ListviewAdapter listAdapter;
    private ArrayList<String> dayList;
    private ArrayList<Attendance> attendanceList;
    private Calendar mCal;

    private SocketManager m_SocketManager;

    private final static int SOCKET_CREATE_SUCCESS = 0;
    private final static int DATA_RECV_SUCCESS = 1;

    private final static String IP = "192.168.0.4";
    private final static int PORT = 8301;

    private int SOCKET_FLAG;

    private int year, month, day, cursor;
    private String week, id;

    private Context context = this;

    private String change_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        attendance_today_tv = (TextView)findViewById(R.id.attendance_today_tv);
        attendance_state_tv = (TextView)findViewById(R.id.attendance_state_tv);
        gridView_calendar = (GridView)findViewById(R.id.gridView_calendar);
        listView_attendance = (ListView)findViewById(R.id.listView_attendance);

        attendanceList = new ArrayList<Attendance>();

        main_intent = new Intent(this.getIntent());
        id = main_intent.getStringExtra("ID");

        listAdapter = new ListviewAdapter(getApplicationContext(), attendanceList);
        m_SocketManager = new SocketManager(IP, PORT, m_Handler);

        gridView_calendar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String click_day = adapterView.getAdapter().getItem(i).toString();
                String click_date = year + "." + month + "." + click_day;
                m_SocketManager.sendData("get attendance " + click_date + " " + id ); SOCKET_FLAG = 2;

                Log.d("POSITION", "" + cursor);
                adapterView.getChildAt(cursor).setBackgroundColor(getResources().getColor(R.color.colorWhite));
                view.setBackgroundColor(getResources().getColor(R.color.weekYellow));
                //newest.tvItemGridView.setBackgroundColor(getResources().getColor(R.color.weekYellow));
                cursor = i;
            }
        });

        listView_attendance.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Attendance a = (Attendance)adapterView.getAdapter().getItem(i);
                final String date = a.getDate();
                final String subject = a.getSubject();
                final String time = a.getTime().replaceAll("[^0-9]", "");
                final String state = a.getState();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(time + " " + subject + " 과목의 출석정보를 변경하시겠습니까?")
                        .setItems(R.array.attendance_array, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String send = "change attendance " + id + " " + date + "&" + time + "&" + subject + " " + (i + 1);
                                change_date = date;
                                m_SocketManager.sendData(send); SOCKET_FLAG = 3;
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
    }

    private Handler m_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String today_date = null;
            switch(msg.what) {
                case SOCKET_CREATE_SUCCESS:
                    m_SocketManager.sendData("get date"); SOCKET_FLAG = 1;
                    break;

                case DATA_RECV_SUCCESS:
                    switch(SOCKET_FLAG) {
                        case 1:         // get date 명령
                            String[] date = msg.obj.toString().split("-");
                            year = Integer.parseInt(date[0]);
                            month = Integer.parseInt(date[1]);
                            day = Integer.parseInt(date[2]);
                            week = date[3];

                            String today = year + "년 " + month + "월 " + day +"일 " + week + "요일";
                            attendance_today_tv.setText(today);

                            dayList = new ArrayList<String>();

                            mCal = Calendar.getInstance();

                            mCal.set(year, month - 1, 1);

                            int dayNum = mCal.get(Calendar.DAY_OF_WEEK);
                            for(int i = 1; i < dayNum; i++) {
                                dayList.add("");
                            }
                            setCalendarDate(mCal.get(Calendar.MONTH) + 1);

                            gridAdapter = new GridAdapter(getApplicationContext(), dayList);
                            gridView_calendar.setAdapter(gridAdapter);

                            today_date = year + "." + month + "." + day;
                            m_SocketManager.sendData("get attendance " + today_date + " " + id ); SOCKET_FLAG = 2;
                            break;

                        case 2:
                            attendanceList.clear();
                            if(!msg.obj.toString().equals("false")) {
                                try {
                                    JSONArray attendance_array = new JSONArray(msg.obj.toString());

                                    for (int j = 0; j < attendance_array.length(); j++) {
                                        JSONObject attendance_object = attendance_array.getJSONObject(j);

                                        String date_string = attendance_object.getString("date");
                                        String time_string = attendance_object.getString("time");
                                        String state_string = attendance_object.getString("state");
                                        String subject_string = attendance_object.getString("subject");

                                        time_string += "교시";

                                        Attendance e = new Attendance(date_string, time_string, state_string, subject_string);
                                        attendanceList.add(e);

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                String error_date = "정보없음";
                                String error_time = "정보없음";
                                String error_state = "0";
                                String error_subject = "정보없음";

                                Attendance e = new Attendance(error_date, error_time, error_state, error_subject);
                                attendanceList.add(e);
                            }
                            listView_attendance.setAdapter(listAdapter);
                            SOCKET_FLAG = 0;
                            break;

                        case 3:
                            if(msg.obj.toString().equals("true")){
                                Toast.makeText(context, "출석부를 성공적으로 수정했습니다", Toast.LENGTH_SHORT).show();
                                m_SocketManager.sendData("get attendance " + change_date + " " + id ); SOCKET_FLAG = 2;
                            }
                            else {
                                Toast.makeText(context, "출석부를 수정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                    break;
            }
        }
    };

    private void setCalendarDate(int month) {
        mCal.set(Calendar.MONTH, month - 1);

        for(int i = 0 ; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList.add("" + (i + 1));
        }
    }

    private class GridAdapter extends BaseAdapter {
        private final List<String> list;
        private final LayoutInflater inflater;

        public GridAdapter(Context context, List<String> list) {
            this.list = list;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, android.view.View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if(convertView == null) {
                convertView = inflater.inflate(R.layout.grid_calendar_item, parent, false);
                holder = new ViewHolder();
                holder.tvItemGridView = (TextView)convertView.findViewById(R.id.tv_calendar_date);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder)convertView.getTag();
            }
            if(!getItem(position).equals("")) {
                holder.tvItemGridView.setText("" + getItem(position));

                mCal.set(Calendar.DATE, Integer.parseInt(getItem(position)));
                Integer today = mCal.get(Calendar.DAY_OF_MONTH);
                int week = mCal.get(Calendar.DAY_OF_WEEK);

                Log.d("TODAY", String.valueOf(getItem(position)));
                Log.d("TODAY WEEK", String.valueOf(week));

                String sToday = String.valueOf(today);

                if (sToday.equals(String.valueOf(day))) {
                    convertView.setBackgroundColor(getResources().getColor(R.color.weekYellow));
                    holder.tvItemGridView.setTextColor(getResources().getColor(R.color.dateAccent3));
                    cursor = position;
                }

                if (week == 1) {
                    holder.tvItemGridView.setTextColor(getResources().getColor(R.color.dateAccent1));
                } else if (week == 7) {
                    holder.tvItemGridView.setTextColor(getResources().getColor(R.color.dateAccent2));
                }
            }

            return convertView;
        }
    }

    private class ViewHolder {
        TextView tvItemGridView;
    }

    private class ListviewAdapter extends BaseAdapter {
        Context context;
        ArrayList<Attendance> attendanceList = new ArrayList<>();

        public ListviewAdapter(Context context, ArrayList<Attendance> attendanceList) {
            this.context = context;
            this.attendanceList = attendanceList;
        }

        @Override
        public int getCount() {
            return attendanceList.size();
        }

        @Override
        public Object getItem(int position) {
            return attendanceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) convertView = new ListviewItem(context);
            ((ListviewItem)convertView).setData(attendanceList.get(position));
            return convertView;
        }
    }

    private class ListviewItem extends LinearLayout {
        TextView list_attendance_date, list_attendance_time, list_attendance_state, list_attendance_subject;

        public ListviewItem (Context context) {
            super(context);
            init(context);
        }

        private void init(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_attendance_item, this);
            list_attendance_date = (TextView)findViewById(R.id.list_attendance_date);
            list_attendance_time = (TextView)findViewById(R.id.list_attendance_time);
            list_attendance_state = (TextView)findViewById(R.id.list_attendance_state);
            list_attendance_subject = (TextView)findViewById(R.id.list_attendance_subject);
        }

        public void setData(Attendance one) {
            switch(Integer.parseInt(one.getState())) {
                case 0: list_attendance_state.setTextColor(getResources().getColor(R.color.attendanceState0)); break;
                case 1: list_attendance_state.setTextColor(getResources().getColor(R.color.attendanceState1)); break;
                case 2: list_attendance_state.setTextColor(getResources().getColor(R.color.attendanceState2)); break;
                case 3: list_attendance_state.setTextColor(getResources().getColor(R.color.attendanceState3)); break;
                case 4: list_attendance_state.setTextColor(getResources().getColor(R.color.attendanceState4)); break;
            }
            String state_string = one.getState();

            if (state_string.equals("0")) state_string = "미출석";
            else if (state_string.equals("1")) state_string = "출석";
            else if (state_string.equals("2")) state_string = "외출";
            else if (state_string.equals("3")) state_string = "결석";
            else if (state_string.equals("4")) state_string = "병결";

            list_attendance_date.setText(one.getDate());
            list_attendance_time.setText(one.getTime());
            list_attendance_state.setText(state_string);
            list_attendance_subject.setText(one.getSubject());
        }
    }

    private class Attendance {
        private String date, time, state, subject;

        public Attendance(String date, String time, String state, String subject) {
            this.date = date;
            this.time = time;
            this.state = state;
            this.subject = subject;
        }

        public String getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }

        public String getState() {
            return state;
        }

        public String getSubject() {
            return subject;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public void setState(String state) {
            this.state = state;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_SocketManager.closeSocket();
    }
}
