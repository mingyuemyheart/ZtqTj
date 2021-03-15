package com.pcs.ztqtj.view.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import com.pcs.ztqtj.control.inter.OnMyScrollChanged;
import com.pcs.ztqtj.control.listener.ObserverScrollSync;

import java.util.HashMap;
import java.util.Map;

/*
 * 自定义的 滚动控件
 * 重载了 onScrollChanged（滚动条变化）,监听每次的变化通知给 观察(此变化的)观察者
 * 可使用 AddOnScrollChangedListener 来订阅本控件的 滚动条变化
 * */
public class MyHScrollView extends HorizontalScrollView {
	ObserverScrollSync mObserver = null;
    private Map<Integer, OnMyScrollChanged> listenerMap = new HashMap<>();

	public MyHScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyHScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyHScrollView(Context context) {
		super(context);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		/*
		 * 当滚动条移动后，引发 滚动事件。通知给观察者，观察者会传达给其他的。
		 */
		if (mObserver != null && (l != oldl || t != oldt)) {
			mObserver.callAllScroll(this.hashCode(), l, t);
		}
		super.onScrollChanged(l, t, oldl, oldt);
        for(Map.Entry<Integer, OnMyScrollChanged> entry : listenerMap.entrySet()) {
            if(entry.getValue() != null) {
                entry.getValue().onScrollChanged(l, t, oldl, oldt);
            }
        }
	}

	public void callScroll(int hashCode, int x, int y) {
		if (hashCode != this.hashCode()) {
			this.scrollTo(x, y);
		}
	}

    public void addScrollChangedListener(Object object, OnMyScrollChanged listener) {
        listenerMap.put(object.hashCode(), listener);
    }

    public void removeScrollChangedListener(Object object) {
        listenerMap.remove(object.hashCode());
    }

	public void setObserver(ObserverScrollSync observer) {
		mObserver = observer;
	}
}
