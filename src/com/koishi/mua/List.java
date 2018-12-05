package com.koishi.mua;

import java.util.Vector;

public class List extends Value implements Callable {

	private boolean isFunction;
	private Vector<String> params;
	private final Vector<Value> value;

	Vector<Value> getValue() {
		return value;
	}

	Value execute(Processor processor, Context context) throws Exception {
		return processor.parse(value, context);
	}

	boolean isCallable() {
		return isFunction;
	}

	@Override
	public Value execute(Processor processor, Vector<Value> params, Context context) throws Exception {
		if (!isFunction) {
			throw new Exception("expected a function, got " + this);
		} else {
			if (params.size() != this.params.size()) {
				throw new Exception("internal error args mismatch");
			}
			var inner = context.derive();
			inner.acceptResult();
			for (int i = 0; i != params.size(); ++i) {
				Util.putArg(this.params.get(i), params.get(i), inner);
			}
			List executable = value.get(1).as();
			executable.execute(processor, inner);
			return inner.getResult();
		}
	}

	@Override
	public int getParamCount() {
		return params.size();
	}

	@Override
	public void print() {
		System.out.print("[");
		for (var elem: value) {
			System.out.print(" ");
			elem.print();
		}
		System.out.print(" ]");
	}

	List(Vector<Value> value) {
		isFunction = true;
		this.params = new Vector<>();
		if (value.size() != 2) {
			isFunction = false;
		} else {
			if ((value.get(0) instanceof List) && (value.get(1) instanceof List)) {
				List params = value.get(0).as();
				for (var param: params.getValue()) {
					if (param instanceof List) {
						isFunction = false;
						break;
					} else {
						Word word = param.as();
						if (!word.is(Word.Type.word)) {
							isFunction = false;
							break;
						} else {
							this.params.add(word.getValue());
						}
					}
				}
			} else {
				isFunction = false;
			}
		}
		this.value = value;
		if (!isFunction) {
			this.params = null;
		}
	}
}
