package com.jungbo.j4android.smartschool_app;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class TeacherClassroomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_classroom);
    }

    private class Classroom {
        private String name, code, front_light, back_light, fan, air_conditioner;

        public Classroom(String name, String code, String front_light, String back_light, String fan, String air_conditioner) {
            this.name = name;
            this.code = code;
            this.front_light = front_light;
            this.back_light = back_light;
            this.fan = fan;
            this.air_conditioner = air_conditioner;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }

        public String getFront_light() {
            return front_light;
        }

        public String getBack_light() {
            return back_light;
        }

        public String getFan() {
            return fan;
        }

        public String getAir_conditioner() {
            return air_conditioner;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setFront_light(String front_light) {
            this.front_light = front_light;
        }

        public void setBack_light(String back_light) {
            this.back_light = back_light;
        }

        public void setFan(String fan) {
            this.fan = fan;
        }

        public void setAir_conditioner(String air_conditioner) {
            this.air_conditioner = air_conditioner;
        }

        private class ClassroomListAdapter extends BaseAdapter {
            Context context;
            ArrayList<Classroom> classrooms = new ArrayList<>();

            public ClassroomListAdapter(Context context, ArrayList<Classroom> classrooms) {
                this.context = context;
                this.classrooms = classrooms;
            }

            @Override
            public int getCount() {
                return classrooms.size();
            }

            @Override
            public Object getItem(int i) {
                return classrooms.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return null;
            }
        }

        private class ClassroomListItem extends LinearLayout {
            ImageView imageView;
            TextView name, teacher, front_light, back_light, fan, air_conditioner;

            public ClassroomListItem(Context context) {
                super(context);
                init(context);
            }

            private void init(Context context) {
                View view = LayoutInflater.from(context).inflate(R.layout.list_classroom_item, this);

                imageView = (ImageView)findViewById(R.id.item_classroom_view);
                name = (TextView)findViewById(R.id.item_classroom_name);
                teacher = (TextView)findViewById(R.id.item_classroom_teacher);

                front_light = (TextView)findViewById(R.id.item_classroom_front_light);
                back_light = (TextView)findViewById(R.id.item_classroom_back_light);
                fan = (TextView)findViewById(R.id.item_classroom_fan);
                air_conditioner = (TextView)findViewById(R.id.item_classroom_air_conditioner);
            }
        }
    }
}
