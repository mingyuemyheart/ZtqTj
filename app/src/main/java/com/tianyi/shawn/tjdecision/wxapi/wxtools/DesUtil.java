package com.tianyi.shawn.tjdecision.wxapi.wxtools;

import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


public class DesUtil {
	
	private static final String DES = "DES";
	private static final String DESNP = "DES/ECB/NoPadding";
	
	// 加密
	public static String encrypt(String src, String password) throws Exception {
		try {
			// 将数据转成byte
			byte[] datasource = src.getBytes();
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(password.getBytes());
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance(DES);
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			// 现在，获取数据并加密
			// 正式执行加密操作
			byte[] result = cipher.doFinal(datasource);
			// byte转base64
			return Base64.encodeToString(result, Base64.DEFAULT).trim();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return "";
	}
	
	// 解密
	public static String decrypt(String src, String password) throws Exception {
		byte[] datasource = Base64.decode(src.getBytes(), Base64.DEFAULT);
		// DES算法要求有一个可信任的随机数源
		SecureRandom random = new SecureRandom();
		// 创建一个DESKeySpec对象
		DESKeySpec desKey = new DESKeySpec(password.getBytes("UTF-8"));
		// 创建一个密匙工厂
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		// 将DESKeySpec对象转换成SecretKey对象
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance(DESNP);
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		// 真正开始解密操作
		byte[] result = cipher.doFinal(datasource);
		return new String(result).trim();
	}
}
