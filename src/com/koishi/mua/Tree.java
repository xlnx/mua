package com.koishi.mua;

import org.fusesource.jansi.Ansi;

import java.util.ArrayList;

public class Tree {

	private Tree parent;
	final String name;
	final ArrayList<Tree> children;

	public Tree(String name) {
		this.parent = null;
		this.name = name;
		this.children = new ArrayList<>();
	}

	public void add(Tree node) {
		node.parent = this;
		children.add(node);
	}

	public Tree root() {
		if (parent == null) return this;
		return parent.root();
	}

	public void print(Facility facility) {
		System.out.println("<AST>");
		for (int i = 0; i < children.size() - 1; i++) {
			children.get(i).print(facility, "", false);
		}
		if (children.size() > 0) {
			children.get(children.size() - 1)
					.print(facility, "", true);
		}
	}

	private void print(Facility facility, String prefix, boolean isTail) {
		if (this == facility.astBuilder.top()) {
			System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(prefix + (isTail ? " └─ " : " ├─ ") + name).reset());
		} else {
			System.out.println(prefix + (isTail ? " └─ " : " ├─ ") + name);
		}
		for (int i = 0; i < children.size() - 1; i++) {
			children.get(i).print(facility, prefix + (isTail ? "    " : " │  "), false);
		}
		if (children.size() > 0) {
			children.get(children.size() - 1)
					.print(facility, prefix + (isTail ?"    " : " │  "), true);
		}
	}
}
