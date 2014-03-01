package com.example.giambiserver;

public class RC4 {
	
	public static String key(){
		return "beef";
	}
	/**
	 * Key String have to be hex String
	 */
	public static String encrypt(String message, String keyString) {
		StringBuilder ciphertext = new StringBuilder();
		if (message == null || message.length() == 0) {
			throw new RuntimeException("Your message is empty!!");
		} else if(keyString == null || keyString.length() == 0) {
			throw new RuntimeException("Your key is empty!!");
		} else {
			int[] key = keyGenerator(keyString);
			int[] s = scheduleGenerator(key);
			char[] m = message.toCharArray();
			/*------ Conver char array into ASII integer array---*/
			int[] im = new int[m.length];
			for (int i = 0; i < m.length; i++) {
				im[i] = m[i];
			}
			/*----Finally encoding and XOR the message with S values---*/
			int x = 0, y = 0 , z = 0;
			for (int i = 0; i < im.length; i++) {
				x = (x + 1) % 256;
				y = (y + s[x]) % 256; 
				int temp = s[x];
				s[x] = s[y];
				s[y] = temp;
				z = (s[x] + s[y]) % 256;
				im[i] = im[i] ^ s[z];
 			}
 			for (int a: im) {
 				if (a < 16) {
 					ciphertext.append("0");
 				}
 				ciphertext.append(Integer.toHexString(a));
 			}
		}
		return ciphertext.toString();
	}
	/**
	 * ciphertext has to be hex String
	 */
	public static String decrypt(String ciphertext, String keyString) {
		String message = null;
		if (ciphertext == null || ciphertext.length() == 0) {
			throw new RuntimeException("Your ciphertext is empty!!");
		} else if(keyString == null || keyString.length() == 0) {
			throw new RuntimeException("Your key is empty!!");
		} else {
			int[] key = keyGenerator(keyString);
			int[] im = keyGenerator(ciphertext);
			int[] s = scheduleGenerator(key);
			int x = 0, y = 0 , z = 0;
			for (int i = 0; i < im.length; i++) {
				x = (x + 1) % 256;
				y = (y + s[x]) % 256;
				int temp = s[x];
				s[x] = s[y];
				s[y] = temp;
				z = (s[x] + s[y]) % 256;
				im[i] = im[i] ^ s[z];
 			}
 			char[] m = new char[im.length];
 			for (int i = 0; i < m.length; i++) {
				m[i] = (char) im[i];
			}
			message = new String(m);
		}
		return message;
	}
	private static int[] keyGenerator(String keyString) {
		String[] key = keyString.split("");
		String[] tempKey = new String[key.length - 1];
		for (int i = 0; i < tempKey.length; i++) {
			tempKey[i] = key[i+1];
		}
		key = tempKey;
		int keyLength = keyString.length();
		if (keyLength % 2 == 1) {
			String[] temp = new String[keyLength + 1];
			temp[0] = "0";
			for (int i = 0; i < keyLength; i++) {
				temp[i + 1] = key[i];
			}
			key = temp;
			keyLength = (keyLength + 1) / 2;
			
		} else {
			keyLength = keyLength / 2;
		}
		int[] newKey = new int[keyLength];
		for (int i = 0; i < keyLength; i++) {
			StringBuilder sb = new StringBuilder("0x");
			newKey[i] = Integer.decode(sb.append(key[i * 2]).append(key[i * 2 + 1]).toString());
		}
		return newKey;
	}

	private static int[] scheduleGenerator(int[] key) {
		int keyLength = key.length;
		int[] schedule = new int[256];
		for (int i = 0; i < 256; i++) {
			schedule[i] = i;
		}
		int j = 0;
		for (int i = 0; i < 256; i++) {
			j = (j + schedule[i] + key[i % keyLength]) % 256;
			int temp = schedule[i];
			schedule[i] = schedule[j];
			schedule[j] = temp;
		}
		return schedule;
	}
}