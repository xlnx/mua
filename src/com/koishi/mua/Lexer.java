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
		wordDescs = new ArrayList<>();

		wordDescs.add(new WordDesc(Word.Type.wordLiteral, "\"[^\\s]+"));
		wordDescs.add(new WordDesc(Word.Type.wordValue, ":[^\\s]+"));
		wordDescs.add(new WordDesc(Word.Type.number, "-?[0-9]+(?:\\.[0-9]+)?\\b"));
		wordDescs.add(new WordDesc(Word.Type.bool, "true|false"));
		wordDescs.add(new WordDesc(Word.Type.lbracket, "\\["));
		wordDescs.add(new WordDesc(Word.Type.rbracket, "\\]"));
		wordDescs.add(new WordDesc(Word.Type.word, "[^\\s]+"));
	}
}
