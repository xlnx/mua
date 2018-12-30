package com.koishi.mua;

import java.util.*;

interface BuiltinFunctionBody {
	Value execute(Facility facility, ArrayList<Value> args, Context context) throws Exception;
}

class BuiltinFunction extends Value implements Callable {

	private final String name;
	private final int params;
	private final BuiltinFunctionBody body;

	@Override
	public int getParamCount() {
		return params;
	}

	@Override
	public Value execute(Facility facility, ArrayList<Value> params, Context context) throws Exception {
		if (params.size() != this.params) {
			throw new InternalException();
		}
		for (var arg: params) {
			if (arg == null) {
				throw new NullArgException();
			}
		}
		return body.execute(facility, params, context);
	}

	@Override
	public String toString() {
		return "operation<" + name + ">";
	}

	@Override
	public void print() {
		System.out.print(this);
	}

	String getName() {
		return name;
	}

	BuiltinFunction(String name, int params, BuiltinFunctionBody body) {
		this.name = name;
		this.params = params;
		this.body = body;
	}
}

class Builtin {

	static void dump(Context context) {
		for (var fn: functions) {
			context.put(fn.getName(), fn);
		}
	}

	private static final ArrayList<BuiltinFunction> functions;
	static
	{
		functions = new ArrayList<>();

		functions.add(new BuiltinFunction("make", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			context.put(args.get(0).asWord(facility, 0), args.get(1));
			return null;
		}));
		functions.add(new BuiltinFunction("thing", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			return context.get(args.get(0).asWord(facility, 0));
		}));
		functions.add(new BuiltinFunction("erase", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			context.erase(args.get(0).asWord(facility, 0));
			return null;
		}));
		functions.add(new BuiltinFunction("isname", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Word(context.containsKey(args.get(0).asWord(facility, 0)));
		}));
		functions.add(new BuiltinFunction("print", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			args.get(0).print();
			System.out.println();
			return null;
		}));
		functions.add(new BuiltinFunction("read", 0, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Parser().parseWord(new Lexer().parse(new Preprocessor().parse(new Scanner(System.in).nextLine())));
		}));

		functions.add(new BuiltinFunction("add", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Word(args.get(0).asNumber(facility, 0) + args.get(1).asNumber(facility, 1));
		}));
		functions.add(new BuiltinFunction("sub", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Word(args.get(0).asNumber(facility, 0) - args.get(1).asNumber(facility, 1));
		}));
		functions.add(new BuiltinFunction("mul", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Word(args.get(0).asNumber(facility, 0) * args.get(1).asNumber(facility, 1));
		}));
		functions.add(new BuiltinFunction("div", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			var a = args.get(0).asNumber(facility, 0);
			var b = args.get(1).asNumber(facility, 1);
			if (b == 0) {
				throw new DivideByZeroException(facility, 1);
			}
			return new Word(a/b);
		}));
		functions.add(new BuiltinFunction("mod", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			var a = args.get(0).asNumber(facility, 0);
			var b = args.get(1).asNumber(facility, 1);
			if (b == 0) {
				throw new DivideByZeroException(facility, 1);
			}
			return new Word(a/b);
		}));
		functions.add(new BuiltinFunction("eq", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			var a = args.get(0);
			var b = args.get(1);
			if (a.isNumber() && b.isNumber()) {
				return new Word(a.asNumber(facility, 0) == b.asNumber(facility, 1));
			} else if (a.isWord() && b.isWord()) {
				return new Word(a.asWord(facility, 0).equals(b.asWord(facility, 1)));
			} else {
				throw new ExpectedException(facility, 0, "word or number", a);
			}
		}));
		functions.add(new BuiltinFunction("gt", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			var a = args.get(0);
			var b = args.get(1);
			if (a.isNumber() && b.isNumber()) {
				return new Word(a.asNumber(facility, 0) > b.asNumber(facility, 1));
			} else if (a.isWord() && b.isWord()) {
				return new Word(a.asWord(facility, 0).compareTo(b.asWord(facility, 1)) > 0);
			} else {
				throw new ExpectedException(facility, 0, "word or number", a);
			}
		}));
		functions.add(new BuiltinFunction("lt", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			var a = args.get(0);
			var b = args.get(1);
			if (a.isNumber() && b.isNumber()) {
				return new Word(a.asNumber(facility, 0) < b.asNumber(facility, 1));
			} else if (a.isWord() && b.isWord()) {
				return new Word(a.asWord(facility, 0).compareTo(b.asWord(facility, 1)) < 0);
			} else {
				throw new ExpectedException(facility, 0, "word or number", a);
			}
		}));
		functions.add(new BuiltinFunction("and", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Word(args.get(0).asBool(facility, 0) && args.get(1).asBool(facility, 1));
		}));
		functions.add(new BuiltinFunction("or", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Word(args.get(0).asBool(facility, 0) || args.get(1).asBool(facility, 1));
		}));
		functions.add(new BuiltinFunction("not", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Word(!args.get(0).asBool(facility, 0));
		}));

		functions.add(new BuiltinFunction("readlist", 0, (Facility facility, ArrayList<Value> args, Context context) -> {
			var preprocessor = new Preprocessor();
			var lexer = new Lexer();
			var parser = new Parser();
			var scanner = new Scanner(System.in);
			var olds = new ArrayList<Word>();
			while (true) {
				var line = scanner.nextLine();
				var words = new ArrayList<>(olds);
				words.addAll(lexer.parse(preprocessor.parse(line)));
				try {
					var tokens = parser.parse(words);
					if (tokens.isEmpty()) {
						throw new EOFException("");
					}
					var list = tokens.remove(0);
					if (!(list instanceof List)) {
						throw new ExpectedException("list", list);
					} else if (!tokens.isEmpty()) {
						throw new Exception("redundant input");
					}
					return list;
				} catch (EOFException e) {
					olds = words;
				}
			}
		}));
		functions.add(new BuiltinFunction("repeat", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			var executable = args.get(1).asList(facility, 1);

			facility.astBuilder.push(new Tree("list"));

//			var inner = new Context(context, false);
			try {
				for (int i = 0; i != args.get(0).asNumber(facility, 0); ++i) {
					executable.execute(facility, context);
				}
			} catch (FunctionStop e) {
				facility.astBuilder.pop();
				throw e;
			}

			facility.astBuilder.pop();

			return null;
		}));

		functions.add(new BuiltinFunction("output", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			context.setResult(args.get(0));
			return null;
		}));
		functions.add(new BuiltinFunction("stop", 0, (Facility facility, ArrayList<Value> args, Context context) -> {
			facility.processor.stop();
			return null;
		}));
		functions.add(new BuiltinFunction("export", 0, (Facility facility, ArrayList<Value> args, Context context) -> {
			context.export();
			return null;
		}));

		functions.add(new BuiltinFunction("isnumber", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Word(args.get(0).isNumber());
		}));
		functions.add(new BuiltinFunction("isword", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Word(args.get(0).isWord());
		}));
		functions.add(new BuiltinFunction("islist", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Word(args.get(0).isList());
		}));
		functions.add(new BuiltinFunction("isbool", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Word(args.get(0).isBool());
		}));
		functions.add(new BuiltinFunction("isempty", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			var a = args.get(0);
			if (a.isList()) {
				return new Word(a.asList(facility, 0).getValue().isEmpty());
			} else if (a.isWord()) {
				return new Word(!context.containsKey(a.asWord(facility, 0)));
			} else {
				throw new ExpectedException(facility, 0, "word or list", a);
			}
		}));

		functions.add(new BuiltinFunction("random", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Word(Math.random() * args.get(0).asNumber(facility, 0));
		}));
		functions.add(new BuiltinFunction("sqrt", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			var value = args.get(0).asNumber(facility, 0);
			if (value < 0) {
				throw new SqrtNegativeException(facility, 0, value);
			}
			return new Word(Math.sqrt(value));
		}));
		functions.add(new BuiltinFunction("int", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Word(Math.floor(args.get(0).asNumber(facility, 0)));
		}));

		functions.add(new BuiltinFunction("word", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			var a = args.get(0).asWord(facility, 0);
			var b = args.get(1);
			if (b.isNumber()) {
				a += b.asNumber(facility, 1);
			} else if (b.isBool()) {
				a += b.asBool(facility, 1);
			} else if (b.isWord()) {
				a += b.asWord(facility, 1);
			} else {
				throw new ExpectedException(facility, 0, "word, number or bool", a);
			}
			return new Word(Word.Type.word, a);
		}));
		functions.add(new BuiltinFunction("if", 3, (Facility facility, ArrayList<Value> args, Context context) -> {
			var cond = args.get(0).asBool(facility, 0);

			facility.astBuilder.push(new Tree("list"));

			try {
				if (cond) {
					args.get(1).asList(facility, 1).execute(facility, context);
				} else {
					args.get(2).asList(facility, 2).execute(facility, context);
				}
			} catch (FunctionStop e) {
				facility.astBuilder.pop();
				throw e;
			}

			facility.astBuilder.pop();

			return null;
		}));
		functions.add(new BuiltinFunction("sentence", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			var l = new ArrayList<Value>();
			var a0 = args.get(0);
			var a1 = args.get(1);
			if (a0.isList()) {
				l.addAll(a0.asList(facility, 0).getValue());
			} else {
				l.add(a0);
			}
			if (a1.isList()) {
				l.addAll(a1.asList(facility, 1).getValue());
			} else {
				l.add(a1);
			}
			return new List(l);			// buggy maybe
		}));
		functions.add(new BuiltinFunction("list", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			var l = new ArrayList<Value>();
			l.add(args.get(0));
			l.add(args.get(1));
			return new List(l);			// buggy maybe
		}));
		functions.add(new BuiltinFunction("join", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			var l = new ArrayList<>(args.get(0).asList(facility, 0).getValue());
			l.add(args.get(1));
			return new List(l);			// buggy maybe
		}));
		functions.add(new BuiltinFunction("first", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			var a = args.get(0);
			if (a.isList()) {
				var value = a.asList(facility, 0).getValue();
				if (value.isEmpty()) {
					throw new EmptyException(facility, 0, "list");
				} else {
					return value.get(0);
				}
			} else if (a.isWord()) {
				var value = a.asWord(facility, 0);
				if (value.equals("")) {
					throw new EmptyException(facility, 0, "word");
				} else {
					return new Word(Word.Type.word, value.substring(0, 1));
				}
			} else {
				throw new ExpectedException(facility, 0, "word or list", a);
			}
		}));
		functions.add(new BuiltinFunction("last", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			var a = args.get(0);
			if (a.isList()) {
				var value = a.asList(facility, 0).getValue();
				if (value.isEmpty()) {
					throw new EmptyException(facility, 0, "list");
				} else {
					return value.get(value.size() - 1);
				}
			} else if (a.isWord()) {
				var value = a.asWord(facility, 0);
				if (value.equals("")) {
					throw new EmptyException(facility, 0, "word");
				} else {
					return new Word(Word.Type.word, value.substring(value.length() - 1));
				}
			} else {
				throw new ExpectedException(facility, 0, "word or list", a);
			}
		}));
		functions.add(new BuiltinFunction("butfirst", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			var a = args.get(0);
			if (a.isList()) {
				var value = a.asList(facility, 0).getValue();
				if (value.isEmpty()) {
					throw new EmptyException(facility, 0, "list");
				} else {
					var list = new ArrayList<Value>();
					for (var i = 1; i < value.size(); ++i) {
						list.add(value.get(i));
					}
					return new List(list);
				}
			} else if (a.isWord()) {
				var value = a.asWord(facility, 0);
				if (value.equals("")) {
					throw new EmptyException(facility, 0, "word");
				} else {
					return new Word(Word.Type.word, value.substring(1));
				}
			} else {
				throw new ExpectedException(facility, 0, "word or list", a);
			}
		}));
		functions.add(new BuiltinFunction("butlast", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			var a = args.get(0);
			if (a.isList()) {
				var value = a.asList(facility, 0).getValue();
				if (value.isEmpty()) {
					throw new EmptyException(facility, 0, "list");
				} else {
					var list = new ArrayList<Value>();
					for (var i = 0; i < value.size() - 1; ++i) {
						list.add(value.get(i));
					}
					return new List(list);
				}
			} else if (a.isWord()) {
				var value = a.asWord(facility, 0);
				if (value.equals("")) {
					throw new EmptyException(facility, 0, "word");
				} else {
					return new Word(Word.Type.word, value.substring(0, value.length() - 1));
				}
			} else {
				throw new ExpectedException(facility, 0, "word or list", a);
			}
		}));

		functions.add(new BuiltinFunction("wait", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			try {
				Thread.sleep((int) args.get(0).asNumber(facility, 0));
			} catch (InterruptedException e) {
				// do nothing
			}
			return null;
		}));
		functions.add(new BuiltinFunction("save", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			throw new Exception("save function not implemented");
		}));
		functions.add(new BuiltinFunction("load", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			throw new Exception("load function not implemented");
		}));
		functions.add(new BuiltinFunction("erall", 0, (Facility facility, ArrayList<Value> args, Context context) -> {
			context.clear();
			return null;
		}));
		functions.add(new BuiltinFunction("poall", 0, (Facility facility, ArrayList<Value> args, Context context) -> {
			context.list();
			return null;
		}));
	}
}
