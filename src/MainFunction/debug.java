package MainFunction;

import network.Layer;

public class debug {
	String OutFileName = MainOfAulixiaryRegenetor.OutFileName;
	public static void main(String[] args) {
		String TopologyName = "D:/zyx/Topology/6.csv";
		Layer MixLayer_base = new Layer(null, 0, null, null);
		MixLayer_base.readTopology(TopologyName);
		MixLayer_base.generateNodepairs();

	}

}
