package com.example.videokonference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.vidyo.VidyoClient.Connector.ConnectorPkg;
import com.vidyo.VidyoClient.Connector.Connector;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity implements Connector.IConnect{
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 0;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    private Connector vc = null;
    private FrameLayout videoFrame;
    private Button StartBtn, ConnectBtn, DisconnectBtn;
    private Boolean viewShown = false;
    private Boolean connected = false;
    private Boolean disconnected = false;
    //private ProgressBar ProgressSpinner;
    public static String t;
    private static MainActivity currentInstance;
    public ProgressDialog p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectorPkg.setApplicationUIContext(this);
        ConnectorPkg.initialize();
        videoFrame = (FrameLayout)findViewById(R.id.videoFrame);
        StartBtn = (Button)findViewById(R.id.StartBtn);
        ConnectBtn = (Button)findViewById(R.id.ConnectBtn);
        DisconnectBtn = (Button)findViewById(R.id.DisconnectBtn);

        ConnectBtn.setEnabled(false);
        DisconnectBtn.setEnabled(false);
        //ProgressSpinner = (ProgressBar)findViewById(R.id.ProgressSpinner);
        currentInstance = this;
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        if(viewShown && !connected) {
            ConnectBtn.setEnabled(true);
            StartBtn.setEnabled(false);
        }
        if(connected) {
            ConnectBtn.setEnabled(false);
        }
        if(disconnected) {
            DisconnectBtn.setEnabled(false);
        }
        if(t != null) {
            String token = t;
            showProgressDialog();
            connected = vc.connect("prod.vidyo.io", token, "Koustav", "CSE_Class", this);
            if(connected) {
                DisconnectBtn.setEnabled(true);
                ConnectBtn.setEnabled(false);
                hideProgressDialog();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(vc != null) {
            vc.setMode(Connector.ConnectorMode.VIDYO_CONNECTORMODE_Foreground);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static MainActivity getInstance() {
        return currentInstance;
    }

    public void showProgressDialog() {
        currentInstance.p = new ProgressDialog(currentInstance);
        currentInstance.p.setMessage("Establishing Connection");
        currentInstance.p.setIndeterminate(false);
        currentInstance.p.setCancelable(false);
        currentInstance.p.show();
    }

    public void hideProgressDialog() {
        currentInstance.p.hide();
    }

    public void Start(View v) {
        vc = new Connector(videoFrame, Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Default, 16, "", "", 0);
        viewShown = vc.showViewAt(videoFrame, 0, 0, videoFrame.getWidth(), videoFrame.getHeight());
        if(viewShown)
        {
            ConnectBtn.setEnabled(true);
            StartBtn.setEnabled(false);
        }
    }

    public void Connect(View v) {
        GetTokenFromServer getToken = new GetTokenFromServer();
        onPause();
        getToken.execute("http://videod.ddns.net:6002/generateRandomToken/");
    }

    public void Disconnect(View v) {
        disconnected = vc.disconnect();
        if(disconnected)
        {
            connected = false;
            DisconnectBtn.setEnabled(false);
            ConnectBtn.setEnabled(true);
        }
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure(Connector.ConnectorFailReason connectorFailReason) {

    }

    @Override
    public void onDisconnected(Connector.ConnectorDisconnectReason connectorDisconnectReason) {

    }
}
