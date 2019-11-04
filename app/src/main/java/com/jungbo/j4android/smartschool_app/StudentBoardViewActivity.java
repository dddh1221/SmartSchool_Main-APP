package com.jungbo.j4android.smartschool_app;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class StudentBoardViewActivity extends AppCompatActivity {

    TextView tv_board_title, tv_board_date, tv_board_teacher, tv_board_text;

    private SocketManager m_SocketManager;

    private final static int SOCKET_CREATE_SUCCESS = 0;
    private final static int DATA_RECV_SUCCESS = 1;

    private final static String IP = "192.168.0.4";
    private final static int PORT = 8301;

    private int SOCKET_FLAG;

    Intent board_intent;
    String num, title, date, editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_board_view);

        tv_board_title = (TextView)findViewById(R.id.tv_board_title);
        tv_board_date = (TextView)findViewById(R.id.tv_board_date);
        tv_board_teacher = (TextView)findViewById(R.id.tv_board_teacher);
        tv_board_text = (TextView)findViewById(R.id.tv_board_text);

        m_SocketManager = new SocketManager(IP, PORT, m_Handler);
        board_intent = new Intent(this.getIntent());

        num = board_intent.getStringExtra("num");
        title = board_intent.getStringExtra("title");
        date = board_intent.getStringExtra("date");
        editor = board_intent.getStringExtra("editor");

        tv_board_title.setText(title);
        tv_board_date.setText(date);
        tv_board_teacher.setText(editor + " 선생님");
    }

    private Handler m_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SOCKET_CREATE_SUCCESS:
                    m_SocketManager.sendData("board get text " + num); SOCKET_FLAG = 1;
                    break;

                case DATA_RECV_SUCCESS:
                    switch(SOCKET_FLAG) {
                        case 1:
                            String text = msg.obj.toString();
                            tv_board_text.setText(text);
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
}
