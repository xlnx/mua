package com.koishi.mua;

import java.util.Stack;

public class ASTBuilder {
	final Stack<Tree> asts = new Stack<>();

	void push(Tree tree) {
		asts.push(tree);
	}

	void pop() {
		asts.pop();
	}

	Tree top() {
		return asts.peek();
	}

	void reset() {
		asts.clear();
		asts.push(new Tree("global"));
	}
}
