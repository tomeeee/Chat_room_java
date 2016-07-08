package com.example.tome.chatsoba;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View.OnKeyListener;
import android.view.View;
import android.widget.*;

public class MainActivity extends AppCompatActivity {
    public TextView msg;
    public EditText inputmsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        msg = (TextView) findViewById(R.id.gl);
        inputmsg = (EditText) findViewById(R.id.inputmsg);
        final Client client = new Client(this);
        new Thread(client).start();

        inputmsg.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String a=inputmsg.getText().toString();
                    System.out.println(a);
                    inputmsg.getText().clear();
                    MainActivity.this.ispis(a);
                    client.posalji(a);
                    return true;
                }
                return false;
            }
        });
    }

    void ispis(final String t) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msg.append("\n" + t);
            }
        });
    }

}


