package com.jungbo.j4android.smartschool_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

public class StudentLoginActivity extends AppCompatActivity {

    private EditText inputID, inputPW;
    private Button btnLogin;
    private ProgressDialog m_Dialog;

    private SocketManager m_SocketManager;

    private final static int SOCKET_CREATE_SUCCESS = 0;
    private final static int DATA_RECV_SUCCESS = 1;

    private final static String IP = "192.168.0.4";
    private final static int PORT = 8301;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        inputID = (EditText)findViewById(R.id.inputID);
        inputPW = (EditText)findViewById(R.id.inputPW);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        m_Dialog = new ProgressDialog(this);

        m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_Dialog.setMessage("서버와 연결하는 중...");
        m_Dialog.show();

        m_SocketManager = new SocketManager(IP, PORT, m_Handler);
        intent = new Intent(StudentLoginActivity.this, StudentMainActivity.class);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(inputID.getText()) && !TextUtils.isEmpty(inputPW.getText())){
                    // 입력이 들어왔다면
                    String id = inputID.getText().toString();
                    String pw = inputPW.getText().toString();
                    String send = "login student " + id + " " + pw;

                    intent.putExtra("ID", id);
                    inputID.setText(""); inputPW.setText("");
                    m_SocketManager.sendData(send);
                }
                else {
                    Toast.makeText(StudentLoginActivity.this, "아이디나 비밀번호가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private Handler m_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SOCKET_CREATE_SUCCESS:
                    // 소켓 연결에 성공했을 때
                    m_Dialog.dismiss();
                    break;

                case DATA_RECV_SUCCESS:
                    // 데이터 수신에 성공했을 때
                    if(msg.obj.equals("true")){
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(StudentLoginActivity.this, "아이디와 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
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
