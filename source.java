
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetWebView extends AppCompatActivity {


    private WebView webView;
    private String URL_SESSION;
    private ProgressDialog progressDialog;

    private MaterialButton salir_btn;

    String session_id;


    @Override
    public void onBackPressed() {
        Log.e ("ajá", "Entra a onBackPressed");
        Toast.makeText(getApplicationContext(), "right click:", Toast.LENGTH_LONG).show();

        KeyEvent kd = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_F10, 0, KeyEvent.KEYCODE_SHIFT_RIGHT);
        KeyEvent ku = new KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_F10, 0, KeyEvent.KEYCODE_SHIFT_RIGHT);
        webView.dispatchKeyEvent(kd);
        webView.dispatchKeyEvent(ku);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setwebview);
        retriveValues();
        setWebView();
        webView.setFocusable(true);


        salir_btn = findViewById(R.id.salir_btn);


        setupDestroySession();



        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);



/*
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
*/


    }



    private void retriveValues() {
        Intent intent = getIntent();
        if (null != intent) { //Null Checking
            URL_SESSION = intent.getStringExtra(Values.VALUE_URL_SESSION);
            session_id = intent.getStringExtra("session_id");
           // Toast.makeText(getApplicationContext(), URL_SESSION, Toast.LENGTH_LONG).show();
        }
    }


    private void endSessionByUserData(String user_id, String SetwebView_id, String SetwebView_session) {

        Call<StatusAnalytics> call = RetrofitClient.getInstance().getEndpoints().startSessionByUser(user_id, SetwebView_id, SetwebView_session);


        call.enqueue(new Callback<StatusAnalytics>() {
            @Override
            public void onResponse(Call<StatusAnalytics> call, Response<StatusAnalytics> response) {
                if (response.isSuccessful()) {

                }
            }

            @Override
            public void onFailure(Call<StatusAnalytics> call, Throwable t) {

            }
        });

    }


    private void setWebView() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("¿Preparado para ver magia?");
        progressDialog.setMessage(getResources().getString(R.string.message_web));
        progressDialog.show();

        webView = findViewById(R.id.webview);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);


        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }


        webView.loadUrl(URL_SESSION);
        webView.setWebViewClient(new HelloWebViewClient());


    }

    private  class HelloWebViewClient extends WebViewClient {


        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            Log.e("url MAIN", url);
            String url_data = url;
            if(url.equals("https://demo.example.co/#/")){
                startActivity(new Intent(SetwebView.this, LoginActivity.class));
                finish();
                Toast.makeText(SetwebView.this, "Su sesión ha finalizado, gracias por usar", Toast.LENGTH_LONG).show();
            }

            super.doUpdateVisitedHistory(view, url, isReload);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("url MAIN", url);
            view.loadUrl(url);
            return true;
        }




        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressDialog.dismiss();


        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
            onReceivedError(view, rerr.getErrorCode(),         rerr.getDescription().toString(),req.getUrl().toString());
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            handleError(errorCode,view);
        }
    }



    public static void handleError(int errorCode, WebView view) {

        String message = null;
        if (errorCode == WebViewClient.ERROR_AUTHENTICATION) {
            message = "User authentication failed on server";
        } else if (errorCode == WebViewClient.ERROR_TIMEOUT) {
            message = "The server is taking too much time to communicate. Try again later.";
        } else if (errorCode == WebViewClient.ERROR_TOO_MANY_REQUESTS) {
            message = "Too many requests during this load";
        } else if (errorCode == WebViewClient.ERROR_UNKNOWN) {
            message = "Generic error";
        } else if (errorCode == WebViewClient.ERROR_BAD_URL) {
            message = "Check entered URL..";
        } else if (errorCode == WebViewClient.ERROR_CONNECT) {
            message = "Failed to connect to the server";
        } else if (errorCode == WebViewClient.ERROR_FAILED_SSL_HANDSHAKE) {
            message = "Failed to perform SSL handshake";
        } else if (errorCode == WebViewClient.ERROR_HOST_LOOKUP) {
            message = "Server or proxy hostname lookup failed";
        } else if (errorCode == WebViewClient.ERROR_PROXY_AUTHENTICATION) {
            message = "User authentication failed on proxy";
        } else if (errorCode == WebViewClient.ERROR_REDIRECT_LOOP) {
            message = "Too many redirects";
        } else if (errorCode == WebViewClient.ERROR_UNSUPPORTED_AUTH_SCHEME) {
            message = "Unsupported authentication scheme (not basic or digest)";
        } else if (errorCode == WebViewClient.ERROR_UNSUPPORTED_SCHEME) {
            message = "unsupported scheme";
        } else if (errorCode == WebViewClient.ERROR_FILE) {
            message = "Generic file error";
        } else if (errorCode == WebViewClient.ERROR_FILE_NOT_FOUND) {
            message = "File not found";
        } else if (errorCode == WebViewClient.ERROR_IO) {
            message = "The server failed to communicate. Try again later.";
        }
        if (message != null) {
            Log.e("url MAIN", message);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
        webView = null;
    }



    private void setupDestroySession() {

        salir_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destroySessionDialog();
            }
        });


    }

    private void destroySessionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("app")
                .setMessage("¿Quieres realmente terminar la sesión?")
                .setIcon(R.drawable.ic_round_warning_24)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        startActivity(new Intent(SetwebView.this, LoginActivity.class));
                        finish();
                        Toast.makeText(SetwebView.this, "ended", Toast.LENGTH_LONG).show();
                    }})
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
