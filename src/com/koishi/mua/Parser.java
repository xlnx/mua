package com.koishi.mua;

import java.util.ArrayList;

class Parser {

	private interface EOFHandler {
		void apply() throws Exception;
	}

	private ArrayList<Value> parseRecursively(ArrayList<Word> words, EOFHandler handler) throws Exception {
		var values = new ArrayList<Value>();
		while (!words.isEmpty()) {
			var word = words.get(0);
			if (word.is(Word.Type.bracket)) {
				if (word.is(Word.Type.lbracket)) {
					words.remove(0);
					values.add(new List(parseRecursively(words, () -> {
						throw new EOFException("expected ']' before end of file");
					})));
					if (!words.remove(0).is(Word.Type.rbracket)) {
						throw new InternalException();
					}
				} else {
					return values;
				}
			} else {
				values.add(word);
				words.remove(0);
			}
		}
		handler.apply();
		return values;
	}

	ArrayList<Value> parse(ArrayList<Word> words) throws Exception {
		var newWords = new ArrayList<>(words);
		var result = parseRecursively(newWords, () -> {});
		if (!newWords.isEmpty()) {
			throw new UnexpectedRightBracketException();
		}
		return result;
	}
}
