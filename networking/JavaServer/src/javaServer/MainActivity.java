package javaServer;

/**
 * @author Taylor Hoss
 * Date: 10/26/2017
 **/

public class MainActivity {

    Server server;
    TextView infoip, msg;
    boolean started;

    // sets initial ContentView, grabs TextViews for input & output.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);
        msg.setMovementMethod(new ScrollingMovementMethod());
        started = false;
    }

    // When a button is tapped start the server unless it has already been started.
    public void onButtonTap(View v){
        if(started == true){
            Toast myToast = Toast.makeText(getApplicationContext(), "Server already started.", Toast.LENGTH_LONG);
            myToast.show();
        }else{
            server = new Server(this);
            started = true;
            infoip.setText(server.getIpAddress() + ":" + server.getPort());

            Toast myToast = Toast.makeText(getApplicationContext(), "Server started.", Toast.LENGTH_LONG);
            myToast.show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.onDestroy();
    }
}
