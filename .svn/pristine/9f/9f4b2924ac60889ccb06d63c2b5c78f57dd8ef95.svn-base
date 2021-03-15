package com.pcs.ztqtj.control.lifecycle;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;

/**
 * 控制器：生命周期
 * 
 * @author JiangZY
 * 
 */
public class ControlLifeCycle implements InterLifeCycleInformer,
		InterLifeCycleReceiver {
	// 接收者列表
	private List<InterLifeCycleReceiver> mListReceiver = new ArrayList<InterLifeCycleReceiver>();

	@Override
	public void addReceiver(InterLifeCycleReceiver receiver) {
		if (mListReceiver.contains(receiver)) {
			return;
		}
		mListReceiver.add(receiver);
	}

	@Override
	public void removeReceiver(InterLifeCycleReceiver receiver) {
		mListReceiver.remove(receiver);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		for (int i = 0; i < mListReceiver.size(); i++) {
			mListReceiver.get(i).onSaveInstanceState(outState);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		for (int i = 0; i < mListReceiver.size(); i++) {
			mListReceiver.get(i).onCreate(savedInstanceState);
		}
	}

	@Override
	public void onResume() {
		for (int i = 0; i < mListReceiver.size(); i++) {
			mListReceiver.get(i).onResume();
		}
	}

	@Override
	public void onPause() {
		for (int i = 0; i < mListReceiver.size(); i++) {
			mListReceiver.get(i).onPause();
		}
	}

	@Override
	public void onDestroy() {
		for (int i = 0; i < mListReceiver.size(); i++) {
			mListReceiver.get(i).onDestroy();
		}
		mListReceiver.clear();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		for (int i = 0; i < mListReceiver.size(); i++) {
			mListReceiver.get(i)
					.onActivityResult(requestCode, resultCode, data);
		}
	}
}
