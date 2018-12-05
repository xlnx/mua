package com.koishi.mua;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

	private static class WordDesc {
		Word.Type type;
		Pattern regex;

		WordDesc(Word.Type type, String regex) {
			this.type = type;
			this.regex = Pattern.compile("^\\s*(" + regex + ")");
		}
	}

	Vector<Word> parse(String src) {
		var words = new Vector<Word>();
		boolean flag = false;
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

	private static Vector<WordDesc> wordDescs;
	static
	{
		wordDescs = new Vector<>();

		wordDescs.add(new WordDesc(Word.Type.wordLiteral, "\"[^\\s]+"));
		wordDescs.add(new WordDesc(Word.Type.wordValue, ":[^\\s]+"));
		wordDescs.add(new WordDesc(Word.Type.number, "-?\\s*[0-9]+(?:\\.[0-9]+)?\\b"));
		wordDescs.add(new WordDesc(Word.Type.lbracket, "\\["));
		wordDescs.add(new WordDesc(Word.Type.rbracket, "\\]"));
		wordDescs.add(new WordDesc(Word.Type.word, "[^\\s]+"));
	}
}
