package com.jungbo.j4android.smartschool_app;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TeacherMainActivity extends AppCompatActivity {

    private Button btn_teacher_timetable, btn_teacher_attendance, btn_teacher_classroom, btn_teacher_megaphone;
    private TextView tv_teacher_data, tv_teacher_main_date, tv_teahcer_main_time, tv_teacher_main_time, tv_teacher_main_subject, tv_teacher_main_state;

    private SocketManager m_SocketManager;

    private final static int SOCKET_CREATE_SUCCESS = 0;
    private final static int DATA_RECV_SUCCESS = 1;

    private final static String IP = "192.168.0.4";
    private final static int PORT = 8301;

    private String date;

    private Intent login_intent, timetable_intent, attendance_intent, bus_intent, megaphone_intent;
    private String id;
    private int SOCKET_FLAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        tv_teacher_data = (TextView)findViewById(R.id.tv_teacher_data);

        tv_teacher_main_date = (TextView)findViewById(R.id.tv_teacher_main_date);
        tv_teacher_main_time = (TextView)findViewById(R.id.tv_teacher_main_time);
        tv_teacher_main_subject = (TextView)findViewById(R.id.tv_teacher_main_subject);

        btn_teacher_timetable = (Button)findViewById(R.id.btn_teacher_timetable);
        btn_teacher_attendance = (Button)findViewById(R.id.btn_teacher_attendance);
        btn_teacher_classroom = (Button)findViewById(R.id.btn_teacher_classroom);
        btn_teacher_megaphone = (Button)findViewById(R.id.btn_teacher_megaphone);

        m_SocketManager = new SocketManager(IP, PORT, m_Handler);

        login_intent = new Intent(this.getIntent());
        id = login_intent.getStringExtra("ID");

        tv_teacher_data.setText(id + " 선생님");

        btn_teacher_timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timetable_intent = new Intent(TeacherMainActivity.this, TeacherTimetableActivity.class);
                timetable_intent.putExtra("ID", id);
                startActivity(timetable_intent);
                m_SocketManager.closeSocket();
            }
        });

        btn_teacher_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attendance_intent = new Intent(TeacherMainActivity.this, TeacherAttendanceActivity.class);
                attendance_intent.putExtra("DATE", date);
                startActivity(attendance_intent);
                m_SocketManager.closeSocket();
            }
        });

        btn_teacher_classroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bus_intent = new Intent(TeacherMainActivity.this, StudentBusInfoActivity.class);
                startActivity(bus_intent);
                m_SocketManager.closeSocket();
            }
        });

        btn_teacher_megaphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                megaphone_intent = new Intent(TeacherMainActivity.this, StudentBoardActivity.class);
                startActivity(megaphone_intent);
                m_SocketManager.closeSocket();
            }
        });
    }

    private Handler m_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String time = "";

            switch(msg.what) {
                case SOCKET_CREATE_SUCCESS:
                    // 소켓 연결에 성공했을 때
                    m_SocketManager.sendData("get date"); SOCKET_FLAG = 1;
                    break;

                case DATA_RECV_SUCCESS:
                    // 데이터를 성공적으로 받았을 경우
                    Log.d("RECV", "SUCCESS");
                    switch(SOCKET_FLAG){
                        case 1:
                            // get date에 대한 처리
                            date = msg.obj.toString();

                            String[] date_split = date.split("-");
                            String date_string = date_split[0] + "년 " + date_split[1] + "월 " + date_split[2] + "일 " + date_split[3] + "요일";
                            tv_teacher_main_date.setText(date_string);
                            SOCKET_FLAG = 2;
                            m_SocketManager.sendData("get time");
                            break;

                        case 2:
                            // get time에 대한 처리
                            time = msg.obj.toString();
                            SOCKET_FLAG = 3;
                            m_SocketManager.sendData("get classTime");
                            break;

                        case 3:
                            // get classTime에 대한 처리
                            tv_teacher_main_time.setText(time + " " + msg.obj.toString() + "교시");
                            m_SocketManager.sendData("get teacher permission " + id); SOCKET_FLAG = 4;
                            break;

                        case 4:
                            // get teacher permission (이름)
                            tv_teacher_main_subject.setText(msg.obj.toString());
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
        m_SocketManager = new SocketManager(IP, PORT, m_Handler);
    }
}
