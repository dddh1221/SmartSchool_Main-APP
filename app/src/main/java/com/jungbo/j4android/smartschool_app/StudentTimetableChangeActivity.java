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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentTimetableChangeActivity extends AppCompatActivity {

    private TextView change_time, change_subject, change_room, change_teacher, change_time2;
    private ListView change_subject_list;

    private ChangeSubjectAdapter adapter;

    private Intent timetable_intent;

    private String subject, classroom, teacher, week, time, studentName;
    private int position;

    private ArrayList<ChangeSubjectList> changeSubject = new ArrayList<>();
    private SocketManager m_SocketManager;

    private final static int SOCKET_CREATE_SUCCESS = 0;
    private final static int DATA_RECV_SUCCESS = 1;

    private final static String IP = "192.168.0.4";
    private final static int PORT = 8301;

    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_timetable_change);

        change_time = (TextView)findViewById(R.id.change_time);
        change_subject = (TextView)findViewById(R.id.change_subject);
        change_room = (TextView)findViewById(R.id.change_room);
        change_teacher = (TextView)findViewById(R.id.change_teacher);
        change_time2 = (TextView)findViewById(R.id.change_time2);
        change_subject_list = (ListView)findViewById(R.id.change_subject_list);

        timetable_intent = new Intent(this.getIntent());
        subject = timetable_intent.getStringExtra("subject");
        classroom = timetable_intent.getStringExtra("classroom");
        teacher = timetable_intent.getStringExtra("teacher");
        position = timetable_intent.getIntExtra("position", 0);
        studentName = timetable_intent.getStringExtra("studentName");

        change_subject.setText(subject);
        change_room.setText(classroom);
        change_teacher.setText(teacher);
        change_time.setText(classifySubjectTime(position));
        change_time2.setText(classifySubjectTime(position));

        adapter = new ChangeSubjectAdapter(this, changeSubject);
        m_SocketManager = new SocketManager(IP, PORT, m_Handler);

        change_subject_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChangeSubjectList t = (ChangeSubjectList)adapterView.getAdapter().getItem(i);

                classifySubjectTime(position);
                final String change_subject = t.getSubject();
                final String change_classroom = t.getClassroom();
                final String change_teacher = t.getTeacher();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("과목 변경");
                alertDialogBuilder
                        .setMessage(subject + " 과목을 " + change_subject + " 과목으로 변경하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String send = "change subject " + studentName + " " + week + "&" + time + "&" + subject + "&" + classroom + "&" + teacher + " "
                                        + week + "&" + time + "&" + change_subject + "&" + change_classroom + "&" + change_teacher;
                                // change subject 이름 원래(요일&교시&과목명&강의실&선생님) 변경(요일&교시&과목명&강의실&선생님)

                                m_SocketManager.sendData(send);
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }

    private String classifySubjectTime(int position) {
        int week = position % 5;
        int time = (int)(position / 5);
        String day = "";

        switch(week) {
            case 0: day += "월"; this.week = "월"; break;
            case 1: day += "화"; this.week = "화"; break;
            case 2: day += "수"; this.week = "수"; break;
            case 3: day += "목"; this.week = "목"; break;
            case 4: day += "금"; this.week = "금"; break;
        }

        day += "요일 ";

        switch(time) {
            case 0: day += "1"; this.time = "1"; break;
            case 1: day += "2"; this.time = "2"; break;
            case 2: day += "3"; this.time = "3"; break;
            case 3: day += "4"; this.time = "4"; break;
            case 4: day += "5"; this.time = "5"; break;
            case 5: day += "6"; this.time = "6"; break;
            case 6: day += "7"; this.time = "7"; break;
        }

        day += "교시";

        return day;
    }

    private Handler m_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SOCKET_CREATE_SUCCESS:
                    String day = classifySubjectTime(position);
                    m_SocketManager.sendData("get subject " + week + " " + time);
                    Log.d("REQUEST", day);
                break;

                case DATA_RECV_SUCCESS:
                    if(msg.obj.toString().equals("true")) {
                        Toast.makeText(context, "과목을 성공적으로 변경했습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        try {
                            JSONArray subject = new JSONArray(msg.obj.toString());

                            for (int i = 0; i < subject.length(); i++) {
                                JSONObject object = subject.getJSONObject(i);

                                String subject_string = object.getString("class_" + time);
                                String classroom_string = object.getString("name");
                                String teacher_string = object.getString("teacher_" + time);

                                Log.d("ADD LIST", subject_string + " " + classroom_string + " " + teacher_string);

                                if (!(subject_string.equals("수업없음") | teacher_string.equals("수업없음"))) {
                                    ChangeSubjectList e = new ChangeSubjectList(subject_string, classroom_string, teacher_string);
                                    changeSubject.add(e);
                                }
                            }
                            change_subject_list.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_SocketManager.closeSocket();
    }

    private class ChangeSubjectAdapter extends BaseAdapter {
        Context context;
        ArrayList<ChangeSubjectList> changeSubjectList = new ArrayList<>();

        public ChangeSubjectAdapter(Context context, ArrayList<ChangeSubjectList> changeSubjectList) {
            this.context = context;
            this.changeSubjectList = changeSubjectList;
        }

        @Override
        public int getCount() {
            return changeSubjectList.size();
        }

        @Override
        public Object getItem(int position) {
            return changeSubjectList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) convertView = new ChangeSubjectListItem(context);
            ((ChangeSubjectListItem)convertView).setData(changeSubjectList.get(position));
            return convertView;
        }
    }

    private class ChangeSubjectList {
        private String subject, classroom, teacher;

        public ChangeSubjectList(String subject, String classroom, String teacher) {
            this.subject = subject;
            this.classroom = classroom;
            this.teacher = teacher;
        }

        public String getSubject() {
            return subject;
        }

        public String getClassroom() {
            return classroom;
        }

        public String getTeacher() {
            return teacher;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public void setClassroom(String classroom) {
            this.classroom = classroom;
        }

        public void setTeacher(String teacher) {
            this.teacher = teacher;
        }
    }

    private class ChangeSubjectListItem extends LinearLayout {
        TextView list_change_subject, list_change_room, list_change_teacher;

        public ChangeSubjectListItem (Context context) {
            super(context);
            init(context);
        }

        private void init(Context context){
            View view = LayoutInflater.from(context).inflate(R.layout.list_subject_item, this);
            list_change_subject = (TextView)findViewById(R.id.list_change_subject);
            list_change_room = (TextView)findViewById(R.id.list_change_room);
            list_change_teacher = (TextView)findViewById(R.id.list_change_teacher);
        }

        public void setData(ChangeSubjectList one) {
            list_change_subject.setText(one.getSubject());
            list_change_room.setText(one.getClassroom());
            list_change_teacher.setText(one.getTeacher());
        }

    }
}
