package com.koishi.mua;

import java.util.Vector;

public class Interpreter {

	private Context context;
	private Preprocessor preprocessor;
	private Lexer lexer;
	private Parser parser;
	private Processor processor;

	public Value interpret(String src) throws Exception {
		var executable = new List(parser.parse(lexer.parse(preprocessor.parse(src))));
		return executable.execute(processor, context);
	}

	public Interpreter() {
		this.context = new Context();
		this.context.put("pi", new Number(3.14159));
		var list = new Vector<Value>();
		var args = new Vector<Value>();
		var body = new Vector<Value>();
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
		this.processor = new Processor();
	}
}
