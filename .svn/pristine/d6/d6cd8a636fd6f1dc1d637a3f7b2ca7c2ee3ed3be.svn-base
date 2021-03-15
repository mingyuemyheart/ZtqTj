package com.pcs.ztqtj.control.tool;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.pcs.ztqtj.R;

public class Sound {

	private SoundPool sp;
	private HashMap<Integer, Integer> spMap;
	private Context content;

	public Sound(Context content) {
		this.content = content;
		sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		spMap = new HashMap<Integer, Integer>();
		spMap.put(1, sp.load(content, R.raw.speed, 1)); // 多个声音添加
	}
	public void play() {
		playSounds(1);
	}
	public void playSounds(int sound) {
		AudioManager am = (AudioManager) content.getSystemService(content.AUDIO_SERVICE);
		float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		float volumnRatio = audioCurrentVolumn / audioMaxVolumn;
		sp.play(spMap.get(sound), volumnRatio, volumnRatio, 0, 0, 1);
	}
}
