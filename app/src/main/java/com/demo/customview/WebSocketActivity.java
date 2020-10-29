package com.demo.customview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.ByteString;

/**
 * Created by guoxiaodong on 2020/10/29 15:29
 */
public class WebSocketActivity extends Activity {
    public static final int PORT = 9999;
    private static WebSocket mWebSocket;
    private static WebSocket mMockWebSocket;
    private int clientIndex;
    private int serverIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_socket);

        mockWebServer();
        connectWebSocket();
    }

    public void onClientClick(View view) {
        mWebSocket.send("" + clientIndex++);
    }

    public void onServerClick(View view) {
        mMockWebSocket.send("" + serverIndex++);
    }

    public void onCloseServerClick(View view) {
        Log.d("gxd", "关闭服务端..." + mMockWebSocket.hashCode());
        mMockWebSocket.close(1001, null);
    }

    public void onCloseClientClick(View view) {
        Log.d("gxd", "关闭客户端..." + mWebSocket.hashCode());
        mWebSocket.close(1000, null);
    }

    private void connectWebSocket() {
        OkHttpClient client = new OkHttpClient.Builder()
                .pingInterval(30, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url("ws://localhost:" + PORT)
                .build();
        client.newWebSocket(request, new MyWebSocketListener("client"));
    }

    private void mockWebServer() {
        MockWebServer mMockWebServer = new MockWebServer();
        MockResponse mockResponse = new MockResponse();
        mockResponse.withWebSocketUpgrade(new MyWebSocketListener("server"));
        mMockWebServer.enqueue(mockResponse);
        new Thread(() -> {
            try {
                mMockWebServer.start(PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static class MyWebSocketListener extends WebSocketListener {
        private final String tag;

        public MyWebSocketListener(String tag) {
            this.tag = tag;
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            if ("server".equals(tag)) {
                mMockWebSocket = webSocket;
            } else {
                mWebSocket = webSocket;
            }
            Log.d("gxd", tag + "...onOpen..." + webSocket.hashCode());
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            Log.d("gxd", tag + "...收到String消息..." + text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            Log.d("gxd", tag + "...准备关闭..." + code + "..." + webSocket.hashCode());
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Log.d("gxd", tag + "...已关闭连接..." + code + "..." + webSocket.hashCode());
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            Log.d("gxd", tag + "...MyWebSocketListener.onFailure-->", t);
        }
    }
}