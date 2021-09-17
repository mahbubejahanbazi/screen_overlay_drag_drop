# Drag And Drop In Screen Overlay Application
This program shows how to lunch an overlay application drag and drop a view in.

## Tech Stack

Java

<p align="center">
  <img src="https://github.com/peymanjahanbazi/ScreenOverlayDragDrop/blob/main/images/img_1.jpg" />
</p>



<p align="center">
  <img src="https://github.com/peymanjahanbazi/ScreenOverlayDragDrop/blob/main/images/img_2.jpg" />
</p>

## Source code

AppUtils.java
```java
import android.content.Context;
import android.view.WindowManager;


public class AppUtils {
    public static WindowManager windowManager;
    public static AppView mainWindow;
    public static WindowManager.LayoutParams mainWindowParam;

    public static int dpi2Pixel(int dpi, Context context) {
        return (int) (dpi * context.getResources().getDisplayMetrics().density);
    }
}
```

CloseButton.java
```java
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;


public class CloseButton extends AppCompatImageButton implements View.OnClickListener {
    private Context context;

    public CloseButton(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CloseButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CloseButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setBackground(createSelector());
        int widthHeight = AppUtils.dpi2Pixel(35);
        setMaxWidth(widthHeight);
        setMinimumWidth(widthHeight);
        setMaxHeight(widthHeight);
        setMinimumHeight(widthHeight);
        setScaleType(ScaleType.FIT_CENTER);
        setOnClickListener(this);
    }

    private StateListDrawable createSelector() {
        StateListDrawable selector = new StateListDrawable();
        selector.setExitFadeDuration(200);
        selector.addState(new int[]{android.R.attr.state_pressed},
                getResources().getDrawable(R.drawable.bg_close_pressed, null));
        selector.addState(new int[]{},
                getResources().getDrawable(R.drawable.bg_close_default, null));
        return selector;
    }

    @Override
    public void onClick(View view) {
        AppUtils.windowManager.removeView(AppUtils.mainWindow);
        context.stopService(new Intent(context, AppService.class));
    }

}
```

#### In  Body class, drag and drop are implemented.
Body.java
```java
mport android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class Body extends RelativeLayout
        implements View.OnDragListener, View.OnLongClickListener {
    private Button button;

    public Body(Context context) {
        super(context);
        init(context);
    }

    public Body(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Body(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public Body(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflator.inflate(R.layout.body, this);
        button = findViewById(R.id.button_1);
        button.setTag("ANDROID ICON");
        button.setOnLongClickListener(this);
        findViewById(R.id.area_1).setOnDragListener(this);
        findViewById(R.id.area_2).setOnDragListener(this);
        findViewById(R.id.area_3).setOnDragListener(this);
    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        int action = dragEvent.getAction();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                if (dragEvent.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    return true;
                }
                return false;
            case DragEvent.ACTION_DRAG_ENTERED:
                ViewGroup v1 = (ViewGroup) view;
                if (v1.indexOfChild(button) == -1) {
                    view.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                    view.invalidate();
                }
                return true;
            case DragEvent.ACTION_DRAG_LOCATION:
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                view.getBackground().clearColorFilter();
                view.invalidate();
                return true;
            case DragEvent.ACTION_DROP:
                view.getBackground().clearColorFilter();
                view.invalidate();
                View v = (View) dragEvent.getLocalState();
                ViewGroup owner = (ViewGroup) v.getParent();
                owner.removeView(v);
                LinearLayout container = (LinearLayout) view;
                container.addView(v);
                v.setVisibility(View.VISIBLE);
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                view.getBackground().clearColorFilter();
                view.invalidate();
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onLongClick(View view) {
        ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        ClipData data = new ClipData(view.getTag().toString(), mimeTypes, item);
        View.DragShadowBuilder dragshadow = new View.DragShadowBuilder(view);
        view.startDrag(data, dragshadow, view, 0);
        return true;
    }
}
```

AppView.java
```java
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AppView extends RelativeLayout implements View.OnTouchListener {
    private int initialTouchX = 0;
    private int initialTouchY = 0;
    private int initialX = 0;
    private int initialY = 0;
    private boolean flagDrag = false;

    public AppView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AppView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AppView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOnTouchListener(this);
        setBackgroundResource(R.drawable.bg_app_view);
        LinearLayout buttonPanel = new LinearLayout(context);
        buttonPanel.setId(generateViewId());
        buttonPanel.setId(View.generateViewId());
        addView(buttonPanel, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) {{
            addRule(RelativeLayout.ALIGN_PARENT_TOP);
            addRule(RelativeLayout.ALIGN_PARENT_END);
        }});
        buttonPanel.addView(new CloseButton(context), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT) {{
            int margin = AppUtils.dpi2Pixel(3, getContext());
            setMargins(margin, margin, margin, margin);
        }});
        addView(new Body(getContext()), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) {{
            addRule(RelativeLayout.BELOW, buttonPanel.getId());
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }});

    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        WindowManager.LayoutParams customViewParam = AppUtils.mainWindowParam;
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            initialTouchX = (int) motionEvent.getRawX();
            initialTouchY = (int) motionEvent.getRawY();
            initialX = customViewParam.x;
            initialY = customViewParam.y;
            flagDrag = true;
            return true;
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            flagDrag = false;
            return true;
        }
        if (flagDrag) {
            int newx = (int) motionEvent.getRawX();
            int newy = (int) motionEvent.getRawY();
            customViewParam.y = initialY + newy - initialTouchY;
            customViewParam.x = initialX + newx - initialTouchX;
            AppUtils.windowManager.updateViewLayout(this, customViewParam);
        }
        return false;
    }

}
```

AppService.java
```java
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.annotation.Nullable;


public class AppService extends Service {
    private WindowManager windowManager;
    private AppView appView;
    private WindowManager.LayoutParams param;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final int width = AppUtils.dpi2Pixel(250, this);
        final int height = AppUtils.dpi2Pixel(400, this);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        AppUtils.windowManager = windowManager;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            param = new WindowManager.LayoutParams(
                    width,
                    height,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT) {{
                gravity = Gravity.CENTER;
            }};
        } else {
            param = new WindowManager.LayoutParams(
                    width,
                    height,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT) {{
                gravity = Gravity.CENTER;
            }};
        }
        appView = new AppView(this);
        AppUtils.mainWindow = appView;
        AppUtils.mainWindowParam = param;
        windowManager.addView(appView, param);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
```

#### Check permission for overlay application in MainActivity class
MainActivity.java
```java
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                attachOverlayScreen();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (requestCode == REQUEST_CODE) {
                    if (Settings.canDrawOverlays(getApplicationContext())) {
                        attachOverlayScreen();
                    } else {
                        finish();
                    }
                }
            }
        }, 1000);
    }

    private void attachOverlayScreen() {
        Intent in = new Intent(this, AppService.class);
        startService(in);
        finish();
    }
}
```


-  #### Register permission for overlay screen application and your service in AndroidManifest.xml file.
- #### Use transparent theme for your MainActivity

Androidmanifest.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="ir.mjahanbazi.dragdrop">
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.DragDrop">
        <activity
            android:name="ir.mjahanbazi.dragdrop.MainActivity"
            android:theme="@style/TransparentTheme">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <service
            android:name="ir.mjahanbazi.dragdrop.AppService"
            android:enabled="true"
            android:exported="false" />
    </application>
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
</manifest>
```
activity_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@android:color/transparent">

</androidx.constraintlayout.widget.ConstraintLayout>
```
body.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <LinearLayout
        android:id="@+id/area_1"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="1"
        android:background="#7CD1DC"
        android:gravity="center"
        android:orientation="vertical">

        <Button
            android:id="@+id/button_1"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:background="@drawable/ic_drag"
            android:src="@mipmap/ic_launcher"
            android:text="Drag me" />

    </LinearLayout>

    <LinearLayout
        android:id="@+id/area_2"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="1"
        android:background="#E8DD7E"
        android:gravity="center"
        android:orientation="vertical" />

    <LinearLayout
        android:id="@+id/area_3"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="1"
        android:background="#E4749A"
        android:gravity="center"
        android:orientation="vertical" />
</LinearLayout>
```
#### Declare transparent theme in styles.xml

styles.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="TransparentTheme" parent="@style/Theme.AppCompat.NoActionBar">
        <item name="android:background">#00000000</item>
        <item name="android:windowNoTitle">true</item>
        <item name="android:windowBackground">@android:color/transparent</item>
        <item name="android:colorBackgroundCacheHint">@null</item>
        <item name="android:windowIsTranslucent">true</item>
        <item name="android:windowAnimationStyle">@android:style/Animation</item>
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:backgroundDimEnabled">false</item>
    </style>
 </resources>
```
## Contact

mjahanbazi@protonmail.com