package com.koishi.mua;

import java.util.ArrayList;

class Parser {

	private interface EOFHandler {
		void apply() throws Exception;
	}

	private ArrayList<Word> oldWords;

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
		this.oldWords.addAll(words);
		var old = new ArrayList<>(this.oldWords);
		try {
			var result = parseRecursively(this.oldWords, () -> {});
			if (!this.oldWords.isEmpty()) {
				throw new UnexpectedRightBracketException();
			}
			this.oldWords.clear();
			return result;
		} catch (EOFException e) {
			this.oldWords = old;
			throw e;
		} catch (Exception e) {
			this.oldWords.clear();
			throw e;
		}
	}

	Parser() {
		this.oldWords = new ArrayList<>();
	}
}
