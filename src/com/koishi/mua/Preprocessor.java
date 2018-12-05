package com.koishi.mua;

public class Preprocessor {
	String parse(String src) {
		var lines = src.split("\n");
		for (int i = 0; i != lines.length; ++i) {
			var idx = lines[i].indexOf("//");
			if (idx != -1) {
				lines[i] = lines[i].substring(0, idx);
			}
		}
		return String.join(" ", lines).replace('\r', ' ');
	}
}
