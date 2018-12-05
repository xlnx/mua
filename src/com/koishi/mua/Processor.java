package com.koishi.mua;

import java.util.Vector;

public class Processor {

	private Vector<Value> oldCode;

	private Value getComponent(Vector<Value> code, Context context) throws Exception {
		if (code.isEmpty()) {
			throw new Parser.EOFException("expected function parameter before eof");
		}
		var token = code.remove(0);
		if (token instanceof List) {
			List list = token.as();
			return list;
		} else {
			Word word = token.as();
			switch (word.getType()) {
				case wordValue: {
					return context.get(word.getValue().substring(1));
				}
				case wordLiteral: {
					return new Word(Word.Type.word, word.getValue().substring(1));
				}
				case word: {
					Callable callable = null;
					if (Builtin.isResolved(word.getValue())) {
						callable = Builtin.getFunction(word.getValue());
					} else {
						var function = context.get(word.getValue());
						if ((function instanceof List) && ((List) function).isCallable()) {
							callable = function.as();
						} else {
							throw new Exception("expected a function, got " + function);
						}
					}
					var args = new Vector<Value>();
					for (int i = 0; i != callable.getParamCount(); ++i) {
						args.add(getComponent(code, context));
					}
					return callable.execute(this, args, context);
				}
				case number: {
					return new Number(Double.parseDouble(word.getValue()));
				}
				default: {
					throw new Exception("internal error on '[/]'");
				}
			}
		}
	}

	private Value parseRecursively(Vector<Value> code, Context context) throws Exception {
		Value result = null;
		while (!code.isEmpty()) {
			var oldCode = new Vector<>(code);		// preserve the code and content before each statement
			Context oldContext = null;
			if (context.getParent() == null) {
				oldContext = context.copy();
			}
			try {
				result = getComponent(code, context);		// each getComponent call consumes a statement
				this.oldCode = null;
			} catch (Parser.EOFException e) {		// this statement can't finish because eof exception, keep the previous tokens and restore the context.
				this.oldCode = oldCode;
				if (context.getParent() == null) {
					context.assign(oldContext);
				}
				throw e;
			}
		}
		return result;
	}

	Value parse(Vector<Value> code, Context context) throws Exception {
		this.oldCode.addAll(code);
		var old = new Vector<>(this.oldCode);
		try {
			var result = parseRecursively(this.oldCode, context);
			this.oldCode = new Vector<>();
			return result;
		} catch (Parser.EOFException e) {
			this.oldCode = old;
			throw e;
		} catch (Exception e) {
			this.oldCode = new Vector<>();
			throw e;
		}
	}

	Processor() {
		this.oldCode = new Vector<>();
	}
}
