package com.koishi.mua;

import java.util.*;

public class Builtin {

	private interface BuiltinFunctionBody {
		Value execute(Context context) throws Exception;
	}

	private static class BuiltinFunction implements Callable {

		private int params;
		private BuiltinFunctionBody body;

		@Override
		public int getParamCount() {
			return params;
		}

		@Override
		public Value execute(Vector<Value> params, Context context) throws Exception {
			if (params.size() != this.params) {
				throw new Exception("internal error arg mismatch");
			}
			var inner = context.derive();
			for (int i = 0; i != params.size(); ++i) {
				inner.getArgs().add(params.get(i));
			}
			return body.execute(inner);
		}

		BuiltinFunction(int params, BuiltinFunctionBody body) {
			this.params = params;
			this.body = body;
		}
	}

	static Callable getFunction(String name) throws Exception {
		if (!functions.containsKey(name)) {
			throw new Exception("internal error builtin");
		}
		return functions.get(name);
	}

	static boolean isResolved(String name) {
		return functions.containsKey(name);
	}

	private static Map<String, BuiltinFunction> functions;
	static
	{
		functions = new HashMap<>();

		functions.put("add", new BuiltinFunction(2, (Context context) -> {
			return new Number(context.getArgs().get(0).asNumber().getValue() + context.getArgs().get(1).asNumber().getValue());
		}));
		functions.put("sub", new BuiltinFunction(2, (Context context) -> {
			return new Number(context.getArgs().get(0).asNumber().getValue() - context.getArgs().get(1).asNumber().getValue());
		}));
		functions.put("mul", new BuiltinFunction(2, (Context context) -> {
			return new Number(context.getArgs().get(0).asNumber().getValue() * context.getArgs().get(1).asNumber().getValue());
		}));
		functions.put("div", new BuiltinFunction(2, (Context context) -> {
			return new Number(context.getArgs().get(0).asNumber().getValue() / context.getArgs().get(1).asNumber().getValue());
		}));
		functions.put("mod", new BuiltinFunction(2, (Context context) -> {
			return new Number(context.getArgs().get(0).asNumber().getValue() % context.getArgs().get(1).asNumber().getValue());
		}));
		functions.put("and", new BuiltinFunction(2, (Context context) -> {
			return new Bool(context.getArgs().get(0).asBool().getValue() && context.getArgs().get(1).asBool().getValue());
		}));
		functions.put("or", new BuiltinFunction(2, (Context context) -> {
			return new Bool(context.getArgs().get(0).asBool().getValue() || context.getArgs().get(1).asBool().getValue());
		}));
		functions.put("not", new BuiltinFunction(1, (Context context) -> {
			return new Bool(!context.getArgs().get(0).asBool().getValue());
		}));
		functions.put("eq", new BuiltinFunction(2, (Context context) -> {
			var a = context.getArgs().get(0);
			var b = context.getArgs().get(1);
			if ((a instanceof Number) && (b instanceof Number)) {
				return new Bool(a.asNumber().getValue() == b.asNumber().getValue());
			} else if ((a instanceof Word) && (b instanceof Word)) {
				return new Bool(a.asWord().getValue().equals(b.asWord().getValue()));
			} else {
				throw new Exception("expected word or number");
			}
		}));
		functions.put("lt", new BuiltinFunction(2, (Context context) -> {
			var a = context.getArgs().get(0);
			var b = context.getArgs().get(1);
			if ((a instanceof Number) && (b instanceof Number)) {
				return new Bool(a.asNumber().getValue() < b.asNumber().getValue());
			} else if ((a instanceof Word) && (b instanceof Word)) {
				return new Bool(a.asWord().getValue().compareTo(b.asWord().getValue()) < 0);
			} else {
				throw new Exception("expected word or number");
			}
		}));
		functions.put("gt", new BuiltinFunction(2, (Context context) -> {
			var a = context.getArgs().get(0);
			var b = context.getArgs().get(1);
			if ((a instanceof Number) && (b instanceof Number)) {
				return new Bool(a.asNumber().getValue() > b.asNumber().getValue());
			} else if ((a instanceof Word) && (b instanceof Word)) {
				return new Bool(a.asWord().getValue().compareTo(b.asWord().getValue()) > 0);
			} else {
				throw new Exception("expected word or number");
			}
		}));

		functions.put("make", new BuiltinFunction (2, (Context context) -> {
			context.getParent().put(context.getArgs().get(0).asWord().getValue(), context.getArgs().get(1));
			return null;
		}));
		functions.put("print", new BuiltinFunction (1, (Context context) -> {
			context.getArgs().get(0).print();
			System.out.println();
			return null;
		}));
		functions.put("isname", new BuiltinFunction (1, (Context context) -> {
			return new Bool(context.getParent().containsKey(context.getArgs().get(0).asWord().getValue()));
		}));
		functions.put("thing", new BuiltinFunction (1, (Context context) -> {
			return context.getParent().get(context.getArgs().get(0).asWord().getValue());
		}));
		functions.put("erase", new BuiltinFunction (1, (Context context) -> {
			context.getParent().erase(context.getArgs().get(0).asWord().getValue());
			return null;
		}));
		functions.put("read", new BuiltinFunction (0, (Context context) -> {
			var scanner = new Scanner(System.in);
			var lexer = new Lexer();
			var word = lexer.parse(scanner.next()).get(0);
			switch (word.getType()) {
				case wordValue:
					throw new Exception("can't read :<word> from stdin");
				case wordLiteral:
					return new Word(Word.Type.word, word.getValue().substring(1));
				case number:
					return new Number(Double.parseDouble(word.getValue()));
				default:
					throw new Exception("read function definitions from standard input is not supported");
			}
		}));
		functions.put("readlist", new BuiltinFunction (0, (Context context) -> {
			System.out.println("readlist");
			return null;
		}));

		functions.put("repeat", new BuiltinFunction(2, (Context context) -> {
			var executable = context.getArgs().get(1).asList();
			for (int i = 0; i != context.getArgs().get(0).asNumber().getValue(); ++i) {
				executable.execute(context);
			}
			return null;
		}));
		functions.put("output", new BuiltinFunction(1, (Context context) -> {
			context.setResult(context.getArgs().get(0));
			return null;
		}));
		functions.put("stop", new BuiltinFunction(0, (Context context) -> {
			Processor.stop();
			return null;
		}));
		functions.put("export", new BuiltinFunction(0, (Context context) -> {
			context.export();
			return null;
		}));
		functions.put("isnumber", new BuiltinFunction (1, (Context context) -> {
			return new Bool(context.getArgs().get(0) instanceof Number);
		}));
		functions.put("isword", new BuiltinFunction (1, (Context context) -> {
			return new Bool(context.getArgs().get(0) instanceof Word);
		}));
		functions.put("islist", new BuiltinFunction (1, (Context context) -> {
			return new Bool(context.getArgs().get(0) instanceof List);
		}));
		functions.put("isbool", new BuiltinFunction (1, (Context context) -> {
			return new Bool(context.getArgs().get(0) instanceof Bool);
		}));
		functions.put("isempty", new BuiltinFunction (1, (Context context) -> {
			var a = context.getArgs().get(0);
			if (a instanceof List) {
				return new Bool(a.asList().getValue().isEmpty());
			} else if (a instanceof Word) {
				return new Bool(!context.getParent().containsKey(a.asWord().getValue()));
			} else {
				throw new Exception("expected list or word");
			}
		}));

		functions.put("random", new BuiltinFunction(1, (Context context) -> {
			return new Number(Math.random() * context.getArgs().get(0).asNumber().getValue());
		}));
		functions.put("sqrt", new BuiltinFunction(1, (Context context) -> {
			return new Number(Math.sqrt(context.getArgs().get(0).asNumber().getValue()));
		}));
		functions.put("int", new BuiltinFunction(1, (Context context) -> {
			return new Number(Math.floor(context.getArgs().get(0).asNumber().getValue()));
		}));

		functions.put("word", new BuiltinFunction(2, (Context context) -> {
			var a = context.getArgs().get(0).asWord().getValue();
			var b = context.getArgs().get(1);
			if (b instanceof Number) {
				a += b.asNumber().getValue();
			} else if (b instanceof Bool) {
				a += b.asBool().getValue();
			} else if (b instanceof Word) {
				a += b.asWord().getValue();
			} else {
				throw new Exception("expected word, number or bool");
			}
			return new Word(Word.Type.word, a);
		}));
		functions.put("if", new BuiltinFunction(3, (Context context) -> {
			var cond = context.getArgs().get(0).asBool().getValue();
			if (cond) {
				context.getArgs().get(1).asList().execute(context);
			} else {
				context.getArgs().get(2).asList().execute(context);
			}
			return null;
		}));
		functions.put("sentence", new BuiltinFunction(2, (Context context) -> {
			var l = new Vector<Value>();
			var a0 = context.getArgs().get(0);
			var a1 = context.getArgs().get(1);
			if (a0 instanceof List) {
				l.addAll(a0.asList().getValue());
			} else {
				l.add(a0);
			}
			if (a1 instanceof List) {
				l.addAll(a1.asList().getValue());
			} else {
				l.add(a1);
			}
			return new List(l);			// buggy maybe
		}));
		functions.put("list", new BuiltinFunction(2, (Context context) -> {
			var l = new Vector<Value>();
			l.add(context.getArgs().get(0));
			l.add(context.getArgs().get(1));
			return new List(l);			// buggy maybe
		}));
		functions.put("join", new BuiltinFunction(2, (Context context) -> {
			var l = new Vector<Value>();
			l.addAll(context.getArgs().get(0).asList().getValue());
			l.add(context.getArgs().get(1));
			return new List(l);			// buggy maybe
		}));
		functions.put("first", new BuiltinFunction(1, (Context context) -> {
			var a = context.getArgs().get(0);
			if (a instanceof List) {
				var value = a.asList().getValue();
				if (value.isEmpty()) {
					throw new Exception("target list is empty");
				} else {
					return value.get(0);
				}
			} else if (a instanceof Word) {
				var value = a.asWord().getValue();
				if (value.equals("")) {
					throw new Exception("target word is empty");
				} else {
					return new Word(Word.Type.word, value.substring(0, 1));
				}
			} else {
				throw new Exception("expected word or list");
			}
		}));
		functions.put("last", new BuiltinFunction(1, (Context context) -> {
			var a = context.getArgs().get(0);
			if (a instanceof List) {
				var value = a.asList().getValue();
				if (value.isEmpty()) {
					throw new Exception("target list is empty");
				} else {
					return value.get(value.size() - 1);
				}
			} else if (a instanceof Word) {
				var value = a.asWord().getValue();
				if (value.equals("")) {
					throw new Exception("target word is empty");
				} else {
					return new Word(Word.Type.word, value.substring(value.length() - 1));
				}
			} else {
				throw new Exception("expected word or list");
			}
		}));
		functions.put("butfirst", new BuiltinFunction(1, (Context context) -> {
			var a = context.getArgs().get(0);
			if (a instanceof List) {
				var value = a.asList().getValue();
				if (value.isEmpty()) {
					throw new Exception("target list is empty");
				} else {
					var list = new Vector<Value>();
					for (var i = 1; i < value.size(); ++i) {
						list.add(value.get(i));
					}
					return new List(list);
				}
			} else if (a instanceof Word) {
				var value = a.asWord().getValue();
				if (value.equals("")) {
					throw new Exception("target word is empty");
				} else {
					return new Word(Word.Type.word, value.substring(1));
				}
			} else {
				throw new Exception("expected word or list");
			}
		}));
		functions.put("butlast", new BuiltinFunction(1, (Context context) -> {
			var a = context.getArgs().get(0);
			if (a instanceof List) {
				var value = a.asList().getValue();
				if (value.isEmpty()) {
					throw new Exception("target list is empty");
				} else {
					var list = new Vector<Value>();
					for (var i = 0; i < value.size() - 1; ++i) {
						list.add(value.get(i));
					}
					return new List(list);
				}
			} else if (a instanceof Word) {
				var value = a.asWord().getValue();
				if (value.equals("")) {
					throw new Exception("target word is empty");
				} else {
					return new Word(Word.Type.word, value.substring(0, value.length() - 1));
				}
			} else {
				throw new Exception("expected word or list");
			}
		}));

		functions.put("wait", new BuiltinFunction(1, (Context context) -> {
			try {
				Thread.sleep((int) context.getArgs().get(0).asNumber().getValue());
			} catch (InterruptedException e) {
				; // do nothing
			}
			return null;
		}));
		functions.put("save", new BuiltinFunction(1, (Context context) -> {
			throw new Exception("save function not implemented");
		}));
		functions.put("load", new BuiltinFunction(1, (Context context) -> {
			throw new Exception("load function not implemented");
		}));
		functions.put("erall", new BuiltinFunction(0, (Context context) -> {
			context.getParent().clear();
			return null;
		}));
		functions.put("poall", new BuiltinFunction(0, (Context context) -> {
			context.getParent().list();
			return null;
		}));
	}
}
