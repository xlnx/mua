package com.koishi.mua;

import java.util.ArrayList;

class Processor {

	void stop() throws FunctionStop {
		throw new FunctionStop();
	}

	private Value getComponent(Facility facility, ArrayList<Value> code, Context context) throws Exception {
		if (code.isEmpty()) {
			throw new EOFException("expected function parameter before eof");
		}
		var token = code.remove(0);
		if (token instanceof List) {
			List list = token.as();
			facility.astBuilder.top().add(new Tree(list.toString()));

			return list;
		} else {
			Word word = token.as();
			switch (word.getType()) {
				case word: {
					var ast = new Tree(word.getValue());
					facility.astBuilder.top().add(ast);
					facility.astBuilder.push(ast);	// enter builder

					Callable callable;
					var function = context.get(word.getValue());
					if (function instanceof BuiltinFunction) {
						callable = function.as();
					} else if ((function instanceof List) && ((List) function).isCallable()) {
						callable = function.as();
					} else {
						throw new ExpectedException("callable", function);
					}

					var args = new ArrayList<Value>();
					try {
						for (int i = 0; i != callable.getParamCount(); ++i) {
							args.add(getComponent(facility, code, context));
						}
					} catch (FunctionStop e) {
						facility.astBuilder.pop();	// leave builder
						throw e;
					}

					var result = callable.execute(facility, args, context);

					facility.astBuilder.pop();		// leave builder

					return result;
				}
				default: {
					Value result;
					var raw = word.getValue();
					var ast = new Tree(raw);
					facility.astBuilder.top().add(ast);
					facility.astBuilder.push(ast);

					switch (word.getType()) {
						case wordLiteral: {
							result = new Word(Word.Type.word, raw.substring(1));
						} break;
						case wordValue: {
							result = context.get(raw.substring(1));
						} break;
						case number: {
							result = new Number(Double.parseDouble(raw));
						} break;
						case bool: {
							result = new Bool(Boolean.parseBoolean(raw));
						} break;
						default: {
							throw new InternalException();
						}
					}

					facility.astBuilder.pop();

					return result;
				}
			}
		}
	}

	Value parse(Facility facility, ArrayList<Value> code, Context context) throws Exception {
		Value result = null;
		while (!code.isEmpty()) {
			result = getComponent(facility, code, context);		// each getComponent call consumes a statement
		}
		return result;
	}
}
