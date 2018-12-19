package com.koishi.mua;

import java.util.Vector;

public class Processor {

	static class FunctionStop extends Exception {
		FunctionStop() {
			super("'stop' in global scope is not allowed");
		}
	}

	static void stop() throws FunctionStop {
		throw new FunctionStop();
	}

	private static Value getComponent(Vector<Value> code, Context context) throws Exception {
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
					return callable.execute(args, context);
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

	static Value parse(Vector<Value> code, Context context) throws Exception {
		Value result = null;
		while (!code.isEmpty()) {
			result = getComponent(code, context);		// each getComponent call consumes a statement
		}
		return result;
	}
}
