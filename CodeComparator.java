import java.util.Comparator;

public class CodeComparator implements Comparator <MachineCode>
{

	@Override
	public int compare(MachineCode arg0, MachineCode arg1) {
		// TODO Auto-generated method stub
		return arg0.lc-arg1.lc;
	}

	
}
