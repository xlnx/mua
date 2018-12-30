package com.koishi.mua;

import java.util.ArrayList;
import java.util.regex.Pattern;

class Lexer {

	private static class WordDesc {
		Word.Type type;
		Pattern regex;

		WordDesc(Word.Type type, String regex) {
			this.type = type;
			this.regex = Pattern.compile("^\\s*(" + regex + ")");
		}
	}

	ArrayList<Word> parse(String src) {
		var words = new ArrayList<Word>();
		boolean flag;
		do {
			flag = false;
			for (var wordDesc : Lexer.wordDescs) {
				var reg = wordDesc.regex;
				var type = wordDesc.type;
				var m = reg.matcher(src);
				if (m.find()) {
					var raw = m.group(1);
					src = src.substring(m.end(1));
					words.add(new Word(type, raw));
					flag = true;
					break;
				}
			}
		} while (flag);
		return words;
	}

	private static ArrayList<WordDesc> wordDescs;
	static
	{
		var word_r = "[^\\s]+";

		wordDescs = new ArrayList<>();

		wordDescs.add(new WordDesc(Word.Type.literal, "\"" + word_r));
		wordDescs.add(new WordDesc(Word.Type.value, ":" + word_r));
		wordDescs.add(new WordDesc(Word.Type.left, "\\["));
		wordDescs.add(new WordDesc(Word.Type.right, "\\]"));
		wordDescs.add(new WordDesc(Word.Type.word, word_r));
	}
}
