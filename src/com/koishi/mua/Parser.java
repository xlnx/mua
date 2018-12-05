package com.koishi.mua;

import java.util.Vector;

public class Parser {

	private interface EOFHandler {
		void apply() throws Exception;
	}

	public static class EOFException extends Exception {
		EOFException(String what) {
			super(what);
		}
	}

	private Vector<Word> oldWords;

	private Vector<Value> parseRecursively(Vector<Word> words, EOFHandler handler) throws Exception {
		var values = new Vector<Value>();
		while (!words.isEmpty()) {
			var word = words.remove(0);
			if (word.is(Word.Type.bracket)) {
				if (word.is(Word.Type.lbracket)) {
					values.add(new List(parseRecursively(words, () -> {
						throw new EOFException("expected ']' before end of file");
					})));
				} else {
					return values;
				}
			} else {
				values.add(word);
			}
		}
		handler.apply();
		return values;
	}

	Vector<Value> parse(Vector<Word> words) throws Exception {
		this.oldWords.addAll(words);
		var old = new Vector<>(this.oldWords);
		try {
			var result = parseRecursively(this.oldWords, () -> {});
			this.oldWords = new Vector<>();
			return result;
		} catch (EOFException e) {
			this.oldWords = old;
			throw e;
		} catch (Exception e) {
			this.oldWords = new Vector<>();
			throw e;
		}
	}

	Parser() {
		this.oldWords = new Vector<>();
	}
}
