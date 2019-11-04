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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StudentBoardActivity extends AppCompatActivity {

    Spinner searchSpinner;
    EditText edit_search;
    Button btn_search;
    ListView list_board_search;

    private SocketManager m_SocketManager;
    private ArrayList<BoardItem> boardItemArrayList;
    private ListviewAdapter listviewAdapter;

    private final static int SOCKET_CREATE_SUCCESS = 0;
    private final static int DATA_RECV_SUCCESS = 1;

    private final static String IP = "192.168.0.4";
    private final static int PORT = 8301;

    private int SOCKET_FLAG;

    Intent view_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_board);

        edit_search = (EditText)findViewById(R.id.edit_search);
        btn_search = (Button)findViewById(R.id.btn_search);
        list_board_search = (ListView)findViewById(R.id.list_board_search);
        searchSpinner = (Spinner)findViewById(R.id.searchSpinner);

        String[] spinnerStr = getResources().getStringArray(R.array.searchArray);
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerStr);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        searchSpinner.setAdapter(spinnerAdapter);

        boardItemArrayList = new ArrayList<>();
        m_SocketManager = new SocketManager(IP, PORT, m_Handler);

        listviewAdapter = new ListviewAdapter(getApplicationContext(), boardItemArrayList);

        list_board_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BoardItem e = (BoardItem)adapterView.getAdapter().getItem(i);

                String num = e.getNum();
                String title = e.getTitle();
                String date = e.getDate();
                String editor = e.getEditor();

                view_intent = new Intent(getApplicationContext(), StudentBoardViewActivity.class);
                view_intent.putExtra("num", num);
                view_intent.putExtra("title", title);
                view_intent.putExtra("date", date);
                view_intent.putExtra("editor", editor);

                startActivity(view_intent);
            }
        });
    }

    private Handler m_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case SOCKET_CREATE_SUCCESS:
                    m_SocketManager.sendData("board get list"); SOCKET_FLAG = 1;
                    break;

                case DATA_RECV_SUCCESS:
                    switch(SOCKET_FLAG) {
                        case 1:     // board get list 명령
                            try {
                                JSONArray board_array = new JSONArray(msg.obj.toString());

                                for(int i = 0 ; i < board_array.length() ; i++ ) {
                                    JSONObject board_object = board_array.getJSONObject(i);

                                    String num = board_object.getString("num");
                                    String title = board_object.getString("title");
                                    String date = board_object.getString("date");
                                    String editor = board_object.getString("editor");

                                    BoardItem e = new BoardItem(num, title, date, editor);
                                    boardItemArrayList.add(e);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            list_board_search.setAdapter(listviewAdapter);

                            break;

                        case 2:

                            break;
                    }
                    break;
            }
        }
    };

    private class ListviewAdapter extends BaseAdapter {
        Context context;
        ArrayList<BoardItem> boardItemArrayList = new ArrayList<>();

        public ListviewAdapter(Context context, ArrayList<BoardItem> boardItemArrayList) {
            this.context = context;
            this.boardItemArrayList = boardItemArrayList;
        }

        @Override
        public int getCount() {
            return boardItemArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return boardItemArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View converView, ViewGroup parent) {
            if(converView == null) converView = new ListviewItem(context);
            ((ListviewItem)converView).setData(boardItemArrayList.get(position));
            return converView;
        }
    }

    private class ListviewItem extends LinearLayout {
        TextView list_item_num, list_item_title, list_item_date, list_item_editor;

        public ListviewItem(Context context) {
            super(context);
            init(context);
        }

        private void init(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_board_item, this);
            list_item_num = (TextView)findViewById(R.id.list_item_num);
            list_item_title = (TextView)findViewById(R.id.list_item_title);
            list_item_date = (TextView)findViewById(R.id.list_item_date);
            list_item_editor = (TextView)findViewById(R.id.list_item_editor);
        }

        public void setData(BoardItem one) {
            list_item_num.setText(one.getNum());
            list_item_title.setText(one.getTitle());
            list_item_date.setText(one.getDate());
            list_item_editor.setText(one.getEditor());
        }
    }

    private class BoardItem {
        private String num, title, date, editor;

        public BoardItem(String num, String title, String date, String editor) {
            this.num = num;
            this.title = title;
            this.date = date;
            this.editor = editor;
        }

        public String getNum() {
            return num;
        }

        public String getTitle() {
            return title;
        }

        public String getDate() {
            return date;
        }

        public String getEditor() {
            return editor;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setEditor(String editor) {
            this.editor = editor;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_SocketManager.closeSocket();
    }
}
