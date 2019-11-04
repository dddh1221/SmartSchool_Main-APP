package com.jungbo.j4android.smartschool_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private final static int SOCKET_CREATE_SUCCESS = 0;
    private final static int DATA_RECV_SUCCESS = 1;

    private LinearLayout lay_logo, lay_menu, lay_toStudent, lay_toTeacher;
    private Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        lay_logo = (LinearLayout)findViewById(R.id.logo);
        lay_menu = (LinearLayout)findViewById(R.id.menu);
        lay_toStudent = (LinearLayout)findViewById(R.id.toStudent);
        lay_toTeacher = (LinearLayout)findViewById(R.id.toTeacher);
        btn_register = (Button)findViewById(R.id.btnRegister);

        Animation ani_logo = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.login_logo);
        Animation ani_menu = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.login_menu);
        Animation ani_button = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.login_button);

        ani_logo.setInterpolator(getApplicationContext(), android.R.anim.accelerate_decelerate_interpolator);
        ani_menu.setInterpolator(getApplicationContext(), android.R.anim.accelerate_decelerate_interpolator);

        lay_logo.startAnimation(ani_logo);
        lay_menu.startAnimation(ani_menu);
        btn_register.startAnimation(ani_button);

        lay_toStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), StudentLoginActivity.class);
                startActivity(intent);
            }
        });

        lay_toTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TeacherLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
