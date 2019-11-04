package com.jungbo.j4android.smartschool_app;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TeacherAttendanceActivity extends AppCompatActivity {

    private TextView tv_teacher_attendance_date;
    private ListView listView_studentList;

    private ArrayList<Student> studentArrayList = new ArrayList<>();
    private StudentListAdapter adapter;

    private SocketManager m_SocketManager;

    private final static int SOCKET_CREATE_SUCCESS = 0;
    private final static int DATA_RECV_SUCCESS = 1;

    private final static String IP = "192.168.0.4";
    private final static int PORT = 8301;

    private int SOCKET_FLAG;
    private String date;

    private Intent main_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_attendance);

        tv_teacher_attendance_date = (TextView)findViewById(R.id.tv_teacher_attendance_date);
        listView_studentList = (ListView)findViewById(R.id.listView_studentList);
        adapter = new StudentListAdapter(this, studentArrayList);

        m_SocketManager = new SocketManager(IP, PORT, m_Handler);

        main_intent = new Intent(this.getIntent());
        date = main_intent.getStringExtra("DATE");
        String[] date_split = date.split("-");
        String date_string = date_split[0] + "년 " + date_split[1] + "월 " + date_split[2] + "일 " + date_split[3] + "요일";
        tv_teacher_attendance_date.setText(date_string);

        listView_studentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Student e = (Student)adapterView.getAdapter().getItem(i);

                Intent change_activity = new Intent(TeacherAttendanceActivity.this, TeacherAttendanceChangeActivity.class);
                change_activity.putExtra("NUM", e.getNum());
                change_activity.putExtra("NAME", e.getName());
                startActivity(change_activity);
            }
        });
    }

    private Handler m_Handler = new Handler() {

        private JSONArray student_list;
        int student_length = 0, index = 0;

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SOCKET_CREATE_SUCCESS:
                    m_SocketManager.sendData("get studentlist"); SOCKET_FLAG = 1;
                    studentArrayList.clear();
                    break;

                case DATA_RECV_SUCCESS:
                    switch(SOCKET_FLAG) {
                        case 1:     // get studentlist 응답
                            try {
                                student_list = new JSONArray(msg.obj.toString());

                                student_length = student_list.length();
                                JSONObject student = student_list.getJSONObject(index);
                                String name = student.getString("name");
                                m_SocketManager.sendData("attendance get " + name); SOCKET_FLAG = 2;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;

                        case 2:     // attendance get (이름) 응답
                            try {
                                JSONObject student = student_list.getJSONObject(index);

                                String name = student.getString("name");
                                String num = student.getString("num");
                                String state = msg.obj.toString();

                                int int_num = Integer.parseInt(num);

                                int department = int_num / 10000;
                                int grade = int_num % 10000 / 1000;
                                int classroom = int_num % 1000 / 100;
                                int _num = int_num % 100;

                                String student_info = "";
                                switch (department) {
                                    case 1:
                                        student_info = "전자제어과";
                                        break;

                                    case 2:
                                        student_info = "전자회로설계과";
                                        break;

                                    case 3:
                                        student_info = "정보통신기기과";
                                        break;
                                }

                                student_info += " " + grade + "학년 " + classroom + "반 " + _num + "번";
                                Student e = new Student(student_info, name, state);
                                studentArrayList.add(e);

                                index++;

                                if (index == student_length) {
                                    listView_studentList.setAdapter(adapter);
                                    index = 0;
                                }
                                else {
                                    JSONObject student_obj = student_list.getJSONObject(index);
                                    String student_name = student_obj.getString("name");
                                    m_SocketManager.sendData("attendance get " + student_name);
                                }

                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                            break;
                    }
                    break;
            }
        }
    };

    private class Student {
        private String num, name, state;

        public Student(String num, String name, String state) {
            this.num = num;
            this.name = name;
            this.state = state;
        }

        public String getNum() {
            return num;
        }

        public String getName() {
            return name;
        }

        public String getState() {
            return state;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    private class StudentListAdapter extends BaseAdapter {
        Context context;
        ArrayList<Student> student = new ArrayList<>();

        public StudentListAdapter(Context context, ArrayList<Student> student) {
            this.context = context;
            this.student = student;
        }

        @Override
        public int getCount() {
            return student.size();
        }

        @Override
        public Object getItem(int i) {
            return student.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) convertView = new StudentListItem(context);
            ((StudentListItem)convertView).setData(student.get(position));
            return convertView;
        }
    }

    private class StudentListItem extends LinearLayout {
        TextView list_item_attendance_num, list_item_attendance_name, list_item_attendance_state;

        public StudentListItem(Context context) {
            super(context);
            init(context);
        }

        private void init(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_attendance_studentlist, this);
            list_item_attendance_num = (TextView)findViewById(R.id.list_item_attendance_num);
            list_item_attendance_name = (TextView)findViewById(R.id.list_item_attendance_name);
            list_item_attendance_state = (TextView)findViewById(R.id.list_item_attendance_state);
        }

        String state;

        // (* 0 : 미출석, 1 : 출석, 2 : 외출, 3 : 결석, 4 : 병결 )
        public void setData(Student one) {
            switch(Integer.parseInt(one.getState())) {
                case 0: state = "미출석"; list_item_attendance_state.setTextColor(getColor(R.color.attendanceState0)); break;
                case 1: state = "출석"; list_item_attendance_state.setTextColor(getColor(R.color.attendanceState1)); break;
                case 2: state = "외출"; list_item_attendance_state.setTextColor(getColor(R.color.attendanceState2)); break;
                case 3: state = "결석"; list_item_attendance_state.setTextColor(getColor(R.color.attendanceState3)); break;
                case 4: state = "병결"; list_item_attendance_state.setTextColor(getColor(R.color.attendanceState4)); break;
            }

            list_item_attendance_num.setText(one.getNum());
            list_item_attendance_name.setText(one.getName());
            list_item_attendance_state.setText(state);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_SocketManager.closeSocket();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        m_SocketManager = new SocketManager(IP, PORT, m_Handler);
    }
}
