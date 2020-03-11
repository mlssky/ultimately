package tv.yixia.testj;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.xcleans.uninstall.Uninstall;

import java.io.UnsupportedEncodingException;

//import org.xwalk.core.XWalkView;

public class MainActivity extends Activity {

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
//        TextView tv = (TextView) findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());
//        callFromJava("一天的几天dd8990777&&&", this);
//        Uninstall.getInstance(this);
        String s = Uninstall.getInstance(this).testString("你好吧12😄🥰");

        try {
            TextView txt = findViewById(R.id.sample_text);
            txt.setText(s);
            Log.d("JNILogJava", "form native :" + new String(s.getBytes(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String unicode2String(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native void callFromJava(String s, Context context);
}
