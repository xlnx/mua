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
			context.put(args.get(0).asWord(facility, 0).getValue(), args.get(1));
			return null;
		}));
		functions.add(new BuiltinFunction("thing", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			return context.get(args.get(0).asWord(facility, 0).getValue());
		}));
		functions.add(new BuiltinFunction("erase", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			context.erase(args.get(0).asWord(facility, 0).getValue());
			return null;
		}));
		functions.add(new BuiltinFunction("isname", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Bool(context.containsKey(args.get(0).asWord(facility, 0).getValue()));
		}));
		functions.add(new BuiltinFunction("print", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			args.get(0).print();
			System.out.println();
			return null;
		}));
		functions.add(new BuiltinFunction("read", 0, (Facility facility, ArrayList<Value> args, Context context) -> {
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

		functions.add(new BuiltinFunction("add", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Number(args.get(0).asNumber(facility, 0).getValue() + args.get(1).asNumber(facility, 1).getValue());
		}));
		functions.add(new BuiltinFunction("sub", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Number(args.get(0).asNumber(facility, 0).getValue() - args.get(1).asNumber(facility, 1).getValue());
		}));
		functions.add(new BuiltinFunction("mul", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Number(args.get(0).asNumber(facility, 0).getValue() * args.get(1).asNumber(facility, 1).getValue());
		}));
		functions.add(new BuiltinFunction("div", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Number(args.get(0).asNumber(facility, 0).getValue() / args.get(1).asNumber(facility, 1).getValue());
		}));
		functions.add(new BuiltinFunction("mod", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Number(args.get(0).asNumber(facility, 0).getValue() % args.get(1).asNumber(facility, 1).getValue());
		}));
		functions.add(new BuiltinFunction("eq", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			var a = args.get(0);
			var b = args.get(1);
			if ((a instanceof Number) && (b instanceof Number)) {
				return new Bool(a.asNumber(facility, 0).getValue() == b.asNumber(facility, 1).getValue());
			} else if ((a instanceof Word) && (b instanceof Word)) {
				return new Bool(a.asWord(facility, 0).getValue().equals(b.asWord(facility, 1).getValue()));
			} else {
				throw new ExpectedException(facility, 0, "word or number", a);
			}
		}));
		functions.add(new BuiltinFunction("gt", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			var a = args.get(0);
			var b = args.get(1);
			if ((a instanceof Number) && (b instanceof Number)) {
				return new Bool(a.asNumber(facility, 0).getValue() > b.asNumber(facility, 1).getValue());
			} else if ((a instanceof Word) && (b instanceof Word)) {
				return new Bool(a.asWord(facility, 0).getValue().compareTo(b.asWord(facility, 1).getValue()) > 0);
			} else {
				throw new ExpectedException(facility, 0, "word or number", a);
			}
		}));
		functions.add(new BuiltinFunction("lt", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			var a = args.get(0);
			var b = args.get(1);
			if ((a instanceof Number) && (b instanceof Number)) {
				return new Bool(a.asNumber(facility, 0).getValue() < b.asNumber(facility, 1).getValue());
			} else if ((a instanceof Word) && (b instanceof Word)) {
				return new Bool(a.asWord(facility, 0).getValue().compareTo(b.asWord(facility, 1).getValue()) < 0);
			} else {
				throw new ExpectedException(facility, 0, "word or number", a);
			}
		}));
		functions.add(new BuiltinFunction("and", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Bool(args.get(0).asBool(facility, 0).getValue() && args.get(1).asBool(facility, 1).getValue());
		}));
		functions.add(new BuiltinFunction("or", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Bool(args.get(0).asBool(facility, 0).getValue() || args.get(1).asBool(facility, 1).getValue());
		}));
		functions.add(new BuiltinFunction("not", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Bool(!args.get(0).asBool(facility, 0).getValue());
		}));

		functions.add(new BuiltinFunction("readlist", 0, (Facility facility, ArrayList<Value> args, Context context) -> {
			System.out.println("readlist");
			return null;
		}));
		functions.add(new BuiltinFunction("repeat", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			var executable = args.get(1).asList(facility, 1);

			facility.astBuilder.push(new Tree("list"));

			var inner = new Context(context, false);
			try {
				for (int i = 0; i != args.get(0).asNumber(facility, 0).getValue(); ++i) {
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
			return new Bool(args.get(0) instanceof Number);
		}));
		functions.add(new BuiltinFunction("isword", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Bool(args.get(0) instanceof Word);
		}));
		functions.add(new BuiltinFunction("islist", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Bool(args.get(0) instanceof List);
		}));
		functions.add(new BuiltinFunction("isbool", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Bool(args.get(0) instanceof Bool);
		}));
		functions.add(new BuiltinFunction("isempty", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			var a = args.get(0);
			if (a instanceof List) {
				return new Bool(a.asList(facility, 0).getValue().isEmpty());
			} else if (a instanceof Word) {
				return new Bool(!context.containsKey(a.asWord(facility, 0).getValue()));
			} else {
				throw new ExpectedException(facility, 0, "word or list", a);
			}
		}));

		functions.add(new BuiltinFunction("random", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Number(Math.random() * args.get(0).asNumber(facility, 0).getValue());
		}));
		functions.add(new BuiltinFunction("sqrt", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Number(Math.sqrt(args.get(0).asNumber(facility, 0).getValue()));
		}));
		functions.add(new BuiltinFunction("int", 1, (Facility facility, ArrayList<Value> args, Context context) -> {
			return new Number(Math.floor(args.get(0).asNumber(facility, 0).getValue()));
		}));

		functions.add(new BuiltinFunction("word", 2, (Facility facility, ArrayList<Value> args, Context context) -> {
			var a = args.get(0).asWord(facility, 0).getValue();
			var b = args.get(1);
			if (b instanceof Number) {
				a += b.asNumber(facility, 1).getValue();
			} else if (b instanceof Bool) {
				a += b.asBool(facility, 1).getValue();
			} else if (b instanceof Word) {
				a += b.asWord(facility, 1).getValue();
			} else {
				throw new ExpectedException(facility, 0, "word, number or bool", a);
			}
			return new Word(Word.Type.word, a);
		}));
		functions.add(new BuiltinFunction("if", 3, (Facility facility, ArrayList<Value> args, Context context) -> {
			var cond = args.get(0).asBool(facility, 0).getValue();

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
			if (a0 instanceof List) {
				l.addAll(a0.asList(facility, 0).getValue());
			} else {
				l.add(a0);
			}
			if (a1 instanceof List) {
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
			if (a instanceof List) {
				var value = a.asList(facility, 0).getValue();
				if (value.isEmpty()) {
					throw new EmptyException(facility, 0, "list");
				} else {
					return value.get(0);
				}
			} else if (a instanceof Word) {
				var value = a.asWord(facility, 0).getValue();
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
			if (a instanceof List) {
				var value = a.asList(facility, 0).getValue();
				if (value.isEmpty()) {
					throw new EmptyException(facility, 0, "list");
				} else {
					return value.get(value.size() - 1);
				}
			} else if (a instanceof Word) {
				var value = a.asWord(facility, 0).getValue();
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
			if (a instanceof List) {
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
			} else if (a instanceof Word) {
				var value = a.asWord(facility, 0).getValue();
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
			if (a instanceof List) {
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
			} else if (a instanceof Word) {
				var value = a.asWord(facility, 0).getValue();
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
				Thread.sleep((int) args.get(0).asNumber(facility, 0).getValue());
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
