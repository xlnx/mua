import com.koishi.mua.VM;

public class Mua {

	public static void main(String[] args) {
		var vm = new VM();
		vm.interpret(System.in);
	}
}