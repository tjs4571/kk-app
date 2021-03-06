package cn.kkmofang.app;

import android.app.Dialog;
import android.content.*;
import android.graphics.Point;
import android.view.Window;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

import cn.kkmofang.observer.IObserver;
import cn.kkmofang.observer.Listener;
import cn.kkmofang.observer.Observer;
import cn.kkmofang.view.DocumentView;

/**
 * Created by zhanghailong on 2018/4/8.
 */

public class WindowController extends Dialog {

    private final Controller _controller;

    public WindowController(android.content.Context context,Controller controller) {
        super(context);
        _controller = controller;
        setContentView(R.layout.kk_document);

        final WeakReference<Controller> v = new WeakReference<Controller>(_controller);

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Controller vv = v.get();
                if(vv != null) {
                    vv.recycle();
                }
            }
        });

        if(_controller != null) {
            _controller.page().on(new String[]{"action", "close"}, new Listener<WindowController>() {
                @Override
                public void onChanged(IObserver observer, String[] changedKeys, Object value, WindowController weakObject) {
                    if(weakObject != null && value != null) {
                        weakObject.dismiss();
                    }
                }
            },this, Observer.PRIORITY_NORMAL,false);
        }

        final DocumentView documentView = (DocumentView) findViewById(R.id.kk_documentView);

        if(_controller != null) {
            _controller.application().post(new Runnable() {
                @Override
                public void run() {
                    if(_controller instanceof ViewController) {
                        ((ViewController) _controller).run(documentView);
                    } else {
                        _controller.run();
                    }
                }
            });
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        Window window = getWindow();

        if(window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            Point size = new Point();
            window.getWindowManager().getDefaultDisplay().getSize(size);
            params.width = size.x;
            params.height = size.y;
            window.setAttributes(params);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        if(_controller != null) {
            _controller.willAppear();
            _controller.didAppear();
        }
    }

    @Override
    protected void onStop() {


        if(_controller != null) {
            _controller.willDisappear();
            _controller.didDisappear();
        }

        super.onStop();
    }


}
