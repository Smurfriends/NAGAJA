package com.gachon.nagaja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class SearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        TextView textView = findViewById(R.id.textView);
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.addJavascriptInterface(new BridgeInterface(), "Android");

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                webView.loadUrl("javascript:sample2_execDaumPostCode();");
            }
        });

        // 최초 웹뷰 로드
        webView.setWebViewClient(new SslWebViewConnect());
        webView.loadUrl("http://nagaja-3bb34.web.app");
        textView.setText("검색 후, 마커를 클릭하세요");
    }

    private class BridgeInterface {
        @JavascriptInterface
        public void processDATA(String data){
            // 주소 검색 api의 결과값이 브릿지 통로를 통해 전달받는다. (자바스크립트로부터)
            Intent intent = new Intent();

            intent.putExtra("data", data);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}