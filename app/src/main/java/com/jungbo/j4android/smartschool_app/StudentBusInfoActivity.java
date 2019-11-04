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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StudentBusInfoActivity extends AppCompatActivity{

    private TextView tv_bus_station, tv_bus_nextStation, tv_bus_rest;
    private ListView listView_bus;
    private ListviewAdapter listviewAdapter;

    private ImageView im_stop1, im_stop2, im_stop3, im_stop4;
    private ImageView im_loading1, im_loading2, im_loading3;

    private ArrayList<BusInfo> busInfoArrayList;

    private SocketManager m_SocketManager;

    private final static int SOCKET_CREATE_SUCCESS = 0;
    private final static int DATA_RECV_SUCCESS = 1;

    private final static String IP = "192.168.0.4";
    private final static int PORT = 8301;

    private int SOCKET_FLAG;
    private boolean THREAD = true;
    private int BUS_ID = 1;
    private final double BUS_LIMIT_DISTANCE = 1.0;

    private String BUS_STOP1 = "통신과";
    private String BUS_STOP2 = "제어과";
    private String BUS_STOP3 = "회로과";

    private String now_station = "";
    private String next_station = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_bus_info);

        tv_bus_station = (TextView)findViewById(R.id.tv_bus_station);
        tv_bus_nextStation = (TextView)findViewById(R.id.tv_bus_nextStation);
        tv_bus_rest = (TextView)findViewById(R.id.tv_bus_rest);
        listView_bus = (ListView)findViewById(R.id.listView_bus);

        im_stop1 = (ImageView)findViewById(R.id.im_stop1);
        im_stop2 = (ImageView)findViewById(R.id.im_stop2);
        im_stop3 = (ImageView)findViewById(R.id.im_stop3);
        im_stop4 = (ImageView)findViewById(R.id.im_stop4);
        im_loading1 = (ImageView)findViewById(R.id.im_loading1);
        im_loading2 = (ImageView)findViewById(R.id.im_loading2);
        im_loading3 = (ImageView)findViewById(R.id.im_loading3);

        busInfoArrayList = new ArrayList<BusInfo>();
        listviewAdapter = new ListviewAdapter(getApplicationContext(), busInfoArrayList);

        m_SocketManager = new SocketManager(IP, PORT, m_handler);
    }

    private Handler m_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case SOCKET_CREATE_SUCCESS:
                    ReloadThread thread = new ReloadThread();
                    thread.start();
                    break;

                case DATA_RECV_SUCCESS:
                    switch(SOCKET_FLAG) {
                        case 1:         // bus get nextstation (ID) 명령 데이터 처리
                            try {
                                JSONArray bus_array = new JSONArray(msg.obj.toString());
                                JSONObject bus_object = bus_array.getJSONObject(0);

                                now_station = bus_object.getString("now_station");
                                next_station = bus_object.getString("next_station");
                                String rest_seat = bus_object.getString("rest_seat");

                                tv_bus_station.setText(now_station);
                                tv_bus_nextStation.setText(next_station);
                                tv_bus_rest.setText(rest_seat);
                            } catch (JSONException e) {

                            }
                            m_SocketManager.sendData("bus get distance"); SOCKET_FLAG = 2;
                            break;

                        case 2:         // bus get distance 명령 데이터 처리
                            busInfoArrayList.clear();

                            try {
                                JSONArray bus_array = new JSONArray(msg.obj.toString());
                                for (int i = 0 ; i < 3 ; i++ ) {
                                    JSONObject bus_object = bus_array.getJSONObject(i);

                                    String station = bus_object.getString("station");
                                    String distance = bus_object.getString("distance");

                                    BusInfo e = new BusInfo(station + " 정류장", distance + "m");
                                    busInfoArrayList.add(e);

                                    if(station.equals(now_station) && station.equals(BUS_STOP1) && Double.parseDouble(distance) < BUS_LIMIT_DISTANCE) {
                                        im_stop1.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_loading1.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_stop2.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_loading2.setImageDrawable(getDrawable(R.drawable.bus_route_wait));
                                        im_stop3.setImageDrawable(getDrawable(R.drawable.bus_route_wait));
                                        im_loading3.setImageDrawable(getDrawable(R.drawable.bus_route_wait));
                                        im_stop4.setImageDrawable(getDrawable(R.drawable.bus_route_wait));
                                    }
                                    else if(station.equals(now_station) && station.equals(BUS_STOP1) && Double.parseDouble(distance) >= BUS_LIMIT_DISTANCE) {
                                        im_stop1.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_loading1.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_stop2.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_loading2.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_stop3.setImageDrawable(getDrawable(R.drawable.bus_route_wait));
                                        im_loading3.setImageDrawable(getDrawable(R.drawable.bus_route_wait));
                                        im_stop4.setImageDrawable(getDrawable(R.drawable.bus_route_wait));
                                    }
                                    else if(station.equals(now_station) && station.equals(BUS_STOP2) && Double.parseDouble(distance) < BUS_LIMIT_DISTANCE) {
                                        im_stop1.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_loading1.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_stop2.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_loading2.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_stop3.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_loading3.setImageDrawable(getDrawable(R.drawable.bus_route_wait));
                                        im_stop4.setImageDrawable(getDrawable(R.drawable.bus_route_wait));
                                    }
                                    else if(station.equals(now_station) && station.equals(BUS_STOP2) && Double.parseDouble(distance) >= BUS_LIMIT_DISTANCE) {
                                        im_stop1.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_loading1.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_stop2.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_loading2.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_stop3.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_loading3.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_stop4.setImageDrawable(getDrawable(R.drawable.bus_route_wait));
                                    }
                                    else if(station.equals(now_station) && station.equals(BUS_STOP3) && Double.parseDouble(distance) < BUS_LIMIT_DISTANCE) {
                                        im_stop1.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_loading1.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_stop2.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_loading2.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_stop3.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_loading3.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_stop4.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                    }
                                    else if(station.equals(now_station) && station.equals(BUS_STOP3) && Double.parseDouble(distance) >= BUS_LIMIT_DISTANCE) {
                                        im_stop1.setImageDrawable(getDrawable(R.drawable.bus_route_wait2));
                                        im_loading1.setImageDrawable(getDrawable(R.drawable.bus_route_wait));
                                        im_stop2.setImageDrawable(getDrawable(R.drawable.bus_route_wait));
                                        im_loading2.setImageDrawable(getDrawable(R.drawable.bus_route_wait));
                                        im_stop3.setImageDrawable(getDrawable(R.drawable.bus_route_wait));
                                        im_loading3.setImageDrawable(getDrawable(R.drawable.bus_route_wait));
                                        im_stop4.setImageDrawable(getDrawable(R.drawable.bus_route_wait));
                                    }
                                }

                                listView_bus.setAdapter(listviewAdapter);
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                    break;
            }
        }
    };

    private class ReloadThread extends Thread {
        @Override
        public void run() {
            try {
                while (THREAD) {
                    if (m_SocketManager != null) {
                        m_SocketManager.sendData("bus get bus_info " + BUS_ID); SOCKET_FLAG = 1;
                    } else {
                        Toast.makeText(StudentBusInfoActivity.this, "서버와 연결할 수 없습니다.", Toast.LENGTH_SHORT).show(); SOCKET_FLAG = 0;
                        THREAD = false;
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Toast.makeText(StudentBusInfoActivity.this, "서버와 연결하는데 문제가 발생했습니다.", Toast.LENGTH_SHORT).show(); SOCKET_FLAG = 0;
                e.printStackTrace();
            }
        }
    }

    private class ListviewAdapter extends BaseAdapter {
        Context context;
        ArrayList<BusInfo> busInfoArrayList = new ArrayList<>();

        public ListviewAdapter(Context context, ArrayList<BusInfo> busInfoArrayList) {
            this.context = context;
            this.busInfoArrayList = busInfoArrayList;
        }

        @Override
        public int getCount() {
            return busInfoArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return busInfoArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) convertView = new ListviewItem(context);
            ((ListviewItem)convertView).setData(busInfoArrayList.get(position));
            return convertView;
        }
    }

    private class ListviewItem extends LinearLayout {
        TextView list_tv_bus_station_name, list_tv_bus_distance;

        public ListviewItem(Context context) {
            super(context);
            init(context);
        }

        private void init(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_bus_item, this);
            list_tv_bus_station_name = (TextView)findViewById(R.id.list_tv_bus_station_name);
            list_tv_bus_distance = (TextView)findViewById(R.id.list_tv_bus_distance);
        }

        public void setData(BusInfo one) {
            list_tv_bus_station_name.setText(one.getName());
            list_tv_bus_distance.setText(one.getDistance());
        }
    }

    private class BusInfo {
        private String name, distance;

        public BusInfo(String name, String distance) {
            this.name = name;
            this.distance = distance;
        }

        public String getName() {
            return name;
        }

        public String getDistance() {
            return distance;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_SocketManager.closeSocket();
        THREAD = false;
    }
}
