package com.koishi.mua;

import java.util.ArrayList;

class Interpreter {

	private final Context context;
	private final Preprocessor preprocessor;
	private final Lexer lexer;
	private final Parser parser;
	private final Facility facility;
	private ArrayList<Word> olds;

	Value interpret(String src) throws Exception {
		var words = new ArrayList<>(olds);
		words.addAll(lexer.parse(preprocessor.parse(src)));
		var executable = new List(parser.parse(words));
		facility.astBuilder.reset();
		try {
			var res = executable.execute(facility, context);
			olds.clear();
			return res;
		} catch (EOFException e) {
			olds = words;
			throw e;
		} catch (Exception e) {
			facility.astBuilder.top().root().print(facility);
			olds.clear();
			throw e;
		}
	}

	Interpreter() {
		this.context = new Context();
		Builtin.dump(context);
		this.context.put("pi", new Number(3.14159));
		var list = new ArrayList<Value>();
		var args = new ArrayList<Value>();
		var body = new ArrayList<Value>();
		args.add(new Word(Word.Type.word, "a0"));
		body.add(new Word(Word.Type.word, "repeat"));
		body.add(new Word(Word.Type.number, "1"));
		body.add(new Word(Word.Type.wordValue, ":a0"));
		list.add(new List(args));
		list.add(new List(body));
		this.context.put("run", new List(list));
		this.preprocessor = new Preprocessor();
		this.lexer = new Lexer();
		this.parser = new Parser();
		this.olds = new ArrayList<>();
		this.facility = new Facility();
	}
}
