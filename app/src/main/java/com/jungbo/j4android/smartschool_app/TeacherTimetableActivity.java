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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TeacherTimetableActivity extends AppCompatActivity {

    private SocketManager m_SocketManager;
    private TeacherGridAdapter adapter;

    private ArrayList<TeacherTimetable> timetable = new ArrayList<>();

    private TextView tv_TeacherTimetableName;
    private GridView gridview_TeacherTimetable;

    private final static int SOCKET_CREATE_SUCCESS = 0;
    private final static int DATA_RECV_SUCCESS = 1;

    private final static String IP = "192.168.0.4";
    private final static int PORT = 8301;

    private Intent main_intent;
    private String id;
    private int SOCKET_FLAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_timetable);

        m_SocketManager = new SocketManager(IP, PORT, m_Handler);

        main_intent = new Intent(getIntent());
        id = main_intent.getStringExtra("ID");

        tv_TeacherTimetableName = (TextView)findViewById(R.id.tv_TeacherTimetableName);
        gridview_TeacherTimetable = (GridView)findViewById(R.id.gridview_TeacherTimetable);

        adapter = new TeacherGridAdapter(this, timetable);

        tv_TeacherTimetableName.setText(id);


    }

    private Handler m_Handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SOCKET_CREATE_SUCCESS:
                    m_SocketManager.sendData("get teacher timetable " + id); SOCKET_FLAG = 1;
                    break;

                case DATA_RECV_SUCCESS:
                    switch(SOCKET_FLAG) {
                        case 1:
                            try {
                                JSONArray teacher_timetable = new JSONArray(msg.obj.toString());

                                for(int i = 1 ; i <= 7 ; i++ ){
                                    for(int j = 0 ; j < 5 ; j++ ) {
                                        JSONObject object = teacher_timetable.getJSONObject(j);

                                        String subject = object.getString("class_" + i);
                                        String room = object.getString("room_" + i);

                                        TeacherTimetable e = new TeacherTimetable(subject, room);
                                        timetable.add(e);
                                    }
                                }

                                gridview_TeacherTimetable.setAdapter(adapter);
                            } catch (JSONException e) {

                            }
                            break;
                    }
                    break;
            }
        }
    };

    private class TeacherTimetable {
        private String subject, classroom;

        public TeacherTimetable(String subject, String classroom) {
            this.subject = subject;
            this.classroom = classroom;
        }

        public String getSubject() {
            return subject;
        }

        public String getClassroom() {
            return classroom;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public void setClassroom(String classroom) {
            this.classroom = classroom;
        }
    }

    private class TeacherGridAdapter extends BaseAdapter {
        Context context;
        ArrayList<TeacherTimetable> teacherTimetable = new ArrayList<>();

        public TeacherGridAdapter(Context context, ArrayList<TeacherTimetable> teacherTimetable) {
            this.context = context;
            this.teacherTimetable = teacherTimetable;
        }

        @Override
        public int getCount() {
            return teacherTimetable.size();
        }

        @Override
        public Object getItem(int i) {
            return teacherTimetable.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) convertView = new TeacherGridItem(context);
            ((TeacherGridItem)convertView).setData(teacherTimetable.get(position));
            return convertView;
        }
    }

    private class TeacherGridItem extends LinearLayout {
        TextView grid_item_teacher_subjectName, grid_item_teacher_classroom;

        public TeacherGridItem(Context context) {
            super(context);
            init(context);
        }

        private void init(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.grid_teacher_timetable_item, this);
            grid_item_teacher_subjectName = (TextView)findViewById(R.id.grid_item_teacher_subjectName);
            grid_item_teacher_classroom = (TextView)findViewById(R.id.grid_item_teacher_classroom);
        }

        public void setData(TeacherTimetable one) {
            grid_item_teacher_subjectName.setText(one.getSubject());
            grid_item_teacher_classroom.setText(one.getClassroom());
        }
    }
}
