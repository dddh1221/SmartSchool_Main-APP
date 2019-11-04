package com.jungbo.j4android.smartschool_app;

import android.os.Message;
import android.os.Handler;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;

public class SocketManager implements Serializable {

    private String IP;
    private int PORT;

    private SocketChannel m_SocketChannel;
    private Selector m_Selector;

    private readDataThread m_readData;
    private sendDataThread m_sendData;

    private Handler m_handler;

    private final static int SOCKET_CREATE_SUCCESS = 0;
    private final static int DATA_RECV_SUCCESS = 1;

    public SocketManager(String ip, int port, Handler h) {
        this.IP = ip;
        this.PORT = port;
        this.m_handler = h;

        // Thread Objects의 작업 할당 및 초기화
        m_readData = new readDataThread();
        m_readData.start();
    }

    private void setSocket(String ip, int port) throws IOException {

        // selector 생성
        m_Selector = Selector.open();
        // 채널 생성
        m_SocketChannel = SocketChannel.open(new InetSocketAddress(ip, port));
        // 논블로킹 모드 설정
        m_SocketChannel.configureBlocking(false);
        // 소켓 채널을 selector에 등록
        m_SocketChannel.register(m_Selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }

    public void sendData(String data){
        m_sendData = new sendDataThread(m_SocketChannel, data);
        m_sendData.start();
    }

    private void read(SelectionKey key) throws Exception {
        // SelectionKey로부터 소켓채널을 얻어온다.
        SocketChannel sc = (SocketChannel) key.channel();
        // ByteBuffer를 생성한다.
        ByteBuffer buffer = ByteBuffer.allocate(2048);

        long read = 0;

        // 요청한 소켓채널로부터 데이터를 읽어들인다.
        read = sc.read(buffer);
        buffer.flip();

        String data = new String();
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        data = decoder.decode(buffer).toString();

        // 메세지 얻어오기
        Message msg = m_handler.obtainMessage();
        // 메시지 ID 설정
        msg.what = DATA_RECV_SUCCESS;
        // 메시지 정보 설정 (Object 형식)
        msg.obj = data;
        m_handler.sendMessage(msg);

        // 버퍼 메모리를 해제한다.
        clearBuffer(buffer);
    }

    private void clearBuffer(ByteBuffer buffer){
        if (buffer != null) {
            buffer.clear();
            buffer = null;
        }
    }

    void closeSocket(){
        try {
            if (m_SocketChannel.isConnected()) {
                // 소켓 종료
                m_SocketChannel.close();
                // 셀렉터 종료
                m_Selector.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /* *-----------* 내부 쓰레드 클래스 *-----------* */
    public class sendDataThread extends Thread {
        private SocketChannel sdt_SocketChannel;
        private String data;

        public sendDataThread(SocketChannel sc, String d){
            sdt_SocketChannel = sc;
            data = d;
        }

        @Override
        public void run() {
            try{
                // 데이터 전송
                sdt_SocketChannel.write(ByteBuffer.wrap(data.getBytes()));
            } catch (Exception e1) {

            }
        }
    }

    public class readDataThread extends Thread {
        @Override
        public void run() {
            try{
                setSocket(IP, PORT);
            } catch (IOException e){
                e.printStackTrace();
            }

            // 소켓 생성 완료를 메인 UI 스레드에 알림.
            m_handler.obtainMessage();
            m_handler.sendEmptyMessage(SOCKET_CREATE_SUCCESS);

            // 데이터 읽기 시작.
            try {
                while(true) {
                    // 셀렉터의 select() 메소드로 준비된 이벤트가 있는지 확인한다.
                    m_Selector.select();

                    // 셀렉터에 저장된 이벤트들(SelectionKey)을 하나씩 처리한다.
                    Iterator it = m_Selector.selectedKeys().iterator();

                    while (it.hasNext()) {
                        SelectionKey key = (SelectionKey) it.next();

                        if (key.isReadable()) {
                            // 이미 연결된 클라이언트가 메시지를 보낸 경우
                            try {
                                read(key);
                            } catch (Exception e) {

                            }
                        }

                        // 이미 처리한 이벤트이므로 삭제
                        it.remove();
                    }
                }
            } catch (Exception e) {

            }
        }
    }
}