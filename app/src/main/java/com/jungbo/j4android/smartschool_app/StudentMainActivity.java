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

import org.json.JSONArray;
import org.json.JSONObject;

public class StudentMainActivity extends AppCompatActivity {

    private Button btn_timetable, btn_attendance, btn_schoolbus, btn_megaphone;
    private TextView tv_student_schooldata, tv_student_data, tv_main_date, tv_main_time, tv_main_subject, tv_main_state;

    private SocketManager m_SocketManager;

    private final static int SOCKET_CREATE_SUCCESS = 0;
    private final static int DATA_RECV_SUCCESS = 1;

    private final static String IP = "192.168.0.4";
    private final static int PORT = 8301;

    private Intent login_intent, timetable_intent, attendance_intent, bus_intent, megaphone_intent;
    private String id, date;
    private int SOCKET_FLAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        tv_student_schooldata = (TextView)findViewById(R.id.tv_student_schooldata);
        tv_student_data = (TextView)findViewById(R.id.tv_student_data);

        tv_main_date = (TextView)findViewById(R.id.tv_main_date);
        tv_main_time = (TextView)findViewById(R.id.tv_main_time);
        tv_main_subject = (TextView)findViewById(R.id.tv_main_subject);
        tv_main_state = (TextView)findViewById(R.id.tv_main_state);

        btn_timetable = (Button)findViewById(R.id.btn_timetable);
        btn_attendance = (Button)findViewById(R.id.btn_attendance);
        btn_schoolbus = (Button)findViewById(R.id.btn_schoolBus);
        btn_megaphone = (Button)findViewById(R.id.btn_megaphone);

        m_SocketManager = new SocketManager(IP, PORT, m_Handler);

        login_intent = new Intent(this.getIntent());
        id = login_intent.getStringExtra("ID");

        btn_timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timetable_intent = new Intent(StudentMainActivity.this, StudentTimetableActivity.class);
                timetable_intent.putExtra("ID", id);
                startActivity(timetable_intent);
                m_SocketManager.closeSocket();
            }
        });

        btn_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attendance_intent = new Intent(StudentMainActivity.this, StudentAttendanceActivity.class);
                attendance_intent.putExtra("ID", id);
                startActivity(attendance_intent);
                m_SocketManager.closeSocket();
            }
        });

        btn_schoolbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bus_intent = new Intent(StudentMainActivity.this, StudentBusInfoActivity.class);
                startActivity(bus_intent);
                m_SocketManager.closeSocket();
            }
        });

        btn_megaphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                megaphone_intent = new Intent(StudentMainActivity.this, StudentBoardActivity.class);
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
                            tv_main_date.setText(date_string);

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
                            tv_main_time.setText(time + " " + msg.obj.toString() + "교시");
                            m_SocketManager.sendData("subject " + id); SOCKET_FLAG = 4;
                            break;

                        case 4:
                            // subject (id)에 대한 처리
                            tv_main_subject.setText(msg.obj.toString());
                            m_SocketManager.sendData("attendance get " + id); SOCKET_FLAG = 5;
                            break;

                        case 5:
                            // atendance get (id)에 대한 처리
                            String state = msg.obj.toString();
                            String string_state = "";
                            //(* 0 : 미출석, 1 : 출석, 2 : 외출, 3 : 결석, 4 : 병결 )

                            if(state.equals("0")) { string_state = "미출석"; tv_main_state.setTextColor(getResources().getColor(R.color.attendanceState0)); }
                            else if(state.equals("1")) { string_state = "출석"; tv_main_state.setTextColor(getResources().getColor(R.color.attendanceState1));}
                            else if(state.equals("2")) { string_state = "외출"; tv_main_state.setTextColor(getResources().getColor(R.color.attendanceState2));}
                            else if(state.equals("3")) { string_state = "결석"; tv_main_state.setTextColor(getResources().getColor(R.color.attendanceState3));}
                            else if(state.equals("4")) { string_state = "병결"; tv_main_state.setTextColor(getResources().getColor(R.color.attendanceState4));}
                            tv_main_state.setText(string_state);

                            m_SocketManager.sendData("get student num " + id); SOCKET_FLAG = 6;

                            break;

                        case 6:
                            String student_num = msg.obj.toString();
                            int num = Integer.parseInt(student_num);

                            int department = num / 10000;
                            int grade = num % 10000 / 1000;
                            int classroom = num % 1000 / 100;
                            int int_num = num % 100;

                            String string_department = "";
                            switch(department) {
                                case 1: string_department = "전자제어과"; break;
                                case 2: string_department = "전자회로설계과"; break;
                                case 3: string_department = "정보통신기기과"; break;
                            }

                            tv_student_schooldata.setText("인천전자마이스터고등학교 " + string_department);
                            tv_student_data.setText("" + grade + "학년 " + classroom +"반 " + int_num + "번 " + id);

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
