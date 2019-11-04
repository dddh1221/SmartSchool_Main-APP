package com.jungbo.j4android.smartschool_app;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StudentTimetableActivity extends AppCompatActivity {

    private StudentGridAdapter adapter;
    private GridView gridView;
    private TextView tv_StudentTimetableName;

    private SocketManager m_SocketManager;

    private final static int SOCKET_CREATE_SUCCESS = 0;
    private final static int DATA_RECV_SUCCESS = 1;

    private final static String IP = "192.168.0.4";
    private final static int PORT = 8301;
    private ArrayList<StudentTimetable> timetable = new ArrayList<>();

    private Intent main_intent, change_intent; String id;
    private int SOCKET_FLAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_timetable);

        gridView = (GridView)findViewById(R.id.gridview_StudentTimetable);
        tv_StudentTimetableName = (TextView)findViewById(R.id.tv_StudentTimetableName);
        adapter = new StudentGridAdapter(this, timetable);

        m_SocketManager = new SocketManager(IP, PORT, m_Handler);

        main_intent = new Intent(this.getIntent());
        id = main_intent.getStringExtra("ID");
        tv_StudentTimetableName.setText(id);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                StudentTimetable t = (StudentTimetable) adapterView.getAdapter().getItem(i);
                change_intent = new Intent(StudentTimetableActivity.this, StudentTimetableChangeActivity.class);
                change_intent.putExtra("subject", t.getSubject());
                change_intent.putExtra("classroom", t.getClassroom());
                change_intent.putExtra("teacher",  t.getTeacher());
                change_intent.putExtra("position", i);
                change_intent.putExtra("studentName", id);

                startActivity(change_intent);
            }
        });
    }

    private Handler m_Handler = new Handler() {
        JSONArray classname, room, teacher;

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SOCKET_CREATE_SUCCESS:
                    m_SocketManager.sendData("get student classname " + id); SOCKET_FLAG = 1;
                    break;

                case DATA_RECV_SUCCESS:

                    switch(SOCKET_FLAG) {
                        case 1:
                            try {
                                Log.d("CLASSNAME DATA IN", msg.obj.toString());
                                classname = new JSONArray(msg.obj.toString());
                                m_SocketManager.sendData("get student room " + id); SOCKET_FLAG = 2;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;

                        case 2:
                            try {
                                Log.d("ROOM DATA IN", msg.obj.toString());
                                room = new JSONArray(msg.obj.toString());
                                m_SocketManager.sendData("get student teacher " + id); SOCKET_FLAG = 3;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;

                        case 3:
                            try{
                                Log.d("TEACHER DATA IN", msg.obj.toString());
                                teacher = new JSONArray(msg.obj.toString());

                                for(int i = 1 ; i <= 7 ; i++) {
                                    for(int j = 0; j < 5 ; j++ ) {
                                        JSONObject classnameObject = classname.getJSONObject(j);
                                        JSONObject roomObject = room.getJSONObject(j);
                                        JSONObject teacherObject = teacher.getJSONObject(j);

                                        String subject_string = classnameObject.getString("class_" + i);
                                        String room_string = roomObject.getString("room_" + i);
                                        String teacher_string = teacherObject.getString("teacher_" + i);

                                        Log.d("ADD TIMETABLE DATA", subject_string + " " + room_string + " " + teacher_string);

                                        StudentTimetable e = new StudentTimetable(subject_string, room_string, teacher_string);
                                        timetable.add(e);
                                    }
                                }
                                gridView.setAdapter(adapter);
                                SOCKET_FLAG = 0;

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_SocketManager.closeSocket();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        timetable.clear();
        m_SocketManager.sendData("get student classname " + id); SOCKET_FLAG = 1;
    }

    private class StudentTimetable {
        private String subject, classroom, teacher;

        public StudentTimetable(String subject, String classroom, String teacher) {
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

    private class StudentGridAdapter extends BaseAdapter {
        Context context;
        ArrayList<StudentTimetable> studentTimetable = new ArrayList<>();

        public StudentGridAdapter(Context context, ArrayList<StudentTimetable> studentTimetable) {
            this.context = context;
            this.studentTimetable = studentTimetable;
        }

        @Override
        public int getCount() {
            return studentTimetable.size();
        }

        @Override
        public Object getItem(int position) {
            return studentTimetable.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) convertView = new StudentGridItem(context);
            ((StudentGridItem)convertView).setData(studentTimetable.get(position));
            return convertView;
        }
    }

    private class StudentGridItem extends LinearLayout {
        TextView grid_item_subjectName, grid_item_classroom, grid_item_teacher;

        public StudentGridItem(Context context) {
            super(context);
            init(context);
        }

        private void init(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.grid_timetable_item, this);
            grid_item_subjectName = (TextView)findViewById(R.id.grid_item_subjectName);
            grid_item_classroom = (TextView)findViewById(R.id.grid_item_classroom);
            grid_item_teacher = (TextView)findViewById(R.id.grid_item_teacher);
        }

        public void setData(StudentTimetable one) {
            grid_item_subjectName.setText(one.getSubject());
            grid_item_classroom.setText(one.getClassroom());
            grid_item_teacher.setText(one.getTeacher());
        }
    }
}