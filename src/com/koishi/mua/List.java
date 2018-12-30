package com.koishi.mua;

import java.util.ArrayList;

class List extends Value implements Callable {

	private final boolean isFunction;
	private final ArrayList<String> params;
	private final ArrayList<Value> value;

	ArrayList<Value> getValue() {
		return value;
	}

	Value execute(Facility facility, Context context) throws Exception {
		return facility.processor.parse(facility, new ArrayList<>(value), context);
	}

	boolean isCallable() {
		return isFunction;
	}

	@Override
	public Value execute(Facility facility, ArrayList<Value> params, Context context) throws Exception {
		if (!isFunction) {
			throw new InternalException();
		} else {

			if (params.size() != this.params.size()) {
				throw new InternalException();
			}
			var inner = new Context(context, true);
			for (int i = 0; i != params.size(); ++i) {
				facility.astBuilder.push(facility.astBuilder.top().children.get(i));
				Util.putArg(this.params.get(i), params.get(i), inner);
				facility.astBuilder.pop();
			}

			facility.astBuilder.push(new Tree("function"));		// enter builder

			List executable = (List)value.get(1);
			try {
				executable.execute(facility, inner);
			} catch	(FunctionStop stop) {
				// do nothing
			} catch (EOFException e) {
				throw new Exception(e.getMessage());
			}

			facility.astBuilder.pop();					// leave builder

			return inner.getResult();
		}
	}

	@Override
	public int getParamCount() {
		return params.size();
	}

	@Override
	public String toString() {
		return "list<x" + value.size() + ">";
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

	List(ArrayList<Value> value) {
		boolean isFunction = true;
		var args = new ArrayList<String>();
		if (value.size() != 2) {
			isFunction = false;
		} else {
			if ((value.get(0) instanceof List) && (value.get(1) instanceof List)) {
				List params = (List) value.get(0);
				for (var param: params.getValue()) {
					if (param instanceof List) {
						isFunction = false;
						break;
					} else {
						Word word = (Word) param;
						if (!word.is(Word.Type.word)) {
							isFunction = false;
							break;
						} else {
							args.add(word.getValue());
						}
					}
				}
			} else {
				isFunction = false;
			}
		}
		this.value = value;
		this.isFunction = isFunction;
		if (isFunction) {
			this.params = args;
		} else {
			this.params = null;
		}
	}
}
