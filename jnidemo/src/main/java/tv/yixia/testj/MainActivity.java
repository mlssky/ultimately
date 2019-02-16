package tv.yixia.testj;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

//import org.xwalk.core.XWalkView;

public class MainActivity extends Activity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        XWalkView xWalkView = findViewById(R.id.webview_id);
//        xWalkView.loadUrl("https://www.iqiyi.com/v_19rqrd6790.html");
        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
//
        callFromJava("一天的几天dd8990777&&&", this);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native void callFromJava(String s, Context context);
}
