package ir.mjahanbazi.dragdrop;

import android.content.ClipData;
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
        view.startDrag(data
                , dragshadow
                , view
                , 0
        );
        return true;
    }
}
