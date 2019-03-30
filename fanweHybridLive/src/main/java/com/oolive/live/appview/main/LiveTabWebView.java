package com.oolive.live.appview.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fanwe.lib.pulltorefresh.ISDPullToRefreshView;
import com.fanwe.lib.pulltorefresh.SDPullToRefreshView;
import com.oolive.live.R;

/**
 * Created by mac on 2018/12/11.
 */

public class LiveTabWebView extends LiveTabBaseView {
    private String url;
    private WebView webView;
    //Fragment的View加载完毕的标记
    private SDPullToRefreshView pullToRefreshView;

    public void setUrl(String url) {
        this.url = url;

    }

    public LiveTabWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LiveTabWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LiveTabWebView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setContentView(R.layout.live_tab_webview);
        webView = (WebView) findViewById(R.id.webViewLiveTab);
        pullToRefreshView = (SDPullToRefreshView) findViewById(R.id.view_pull_to_refresh);
        pullToRefreshView.setMode(ISDPullToRefreshView.Mode.PULL_FROM_HEADER);
        //声明WebSettings子类
        WebSettings webSettings = webView.getSettings();

//如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);

//设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

//缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

//其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webView.setWebViewClient(new WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });
        pullToRefreshView.setOnRefreshCallback(new ISDPullToRefreshView.OnRefreshCallback() {
            @Override
            public void onRefreshingFromHeader(SDPullToRefreshView sdPullToRefreshView) {
                loadUrl();
                pullToRefreshView.stopRefreshing();
            }

            @Override
            public void onRefreshingFromFooter(SDPullToRefreshView sdPullToRefreshView) {
                pullToRefreshView.stopRefreshing();
            }
        });
        startLoopRunnable();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);

    }

    private void loadUrl() {
        webView.stopLoading();
        webView.clearCache(true);
        webView.loadUrl(url);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        super.onActivityDestroyed(activity);
        webView.destroy();
    }

    @Override
    protected void onRoomClosed(int roomId) {

    }

    @Override
    public void scrollToTop() {
        loadUrl();

    }

    @Override
    protected void onLoopRun() {

        loadUrl();
    }
}
