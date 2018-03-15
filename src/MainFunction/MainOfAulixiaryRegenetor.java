package MainFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import general.Constant;
import general.file_out_put;
import network.Layer;
import network.Link;
import network.NodePair;
import subgraph.LinearRoute;

public class MainOfAulixiaryRegenetor {
	public static String FinalResultFile = "D:\\zyx\\programFile\\RegwithProandTrgro\\6_FinalResult.dat";
	public static String OutFileName = "D:\\zyx\\programFile\\RegwithProandTrgro\\6.dat";

	public static void main(String[] args) throws IOException {
		String TopologyName = "D:/zyx/Topology/cost239.csv";
		// String TopologyName = "F:/zyx/Topology/cost239.csv";
		int DemandNum = 15;
		ParameterTransfer pt = new ParameterTransfer();
		file_out_put file_io = new file_out_put();
		MainOfAulixiaryRegenetor mm = new MainOfAulixiaryRegenetor();
		ArrayList<NodePair> RadomNodepairlist = new ArrayList<NodePair>();

		Layer MixLayer_base = new Layer(null, 0, null, null);
		MixLayer_base.readTopology(TopologyName);
		MixLayer_base.generateNodepairs();

		DemandRadom dr = new DemandRadom();
		RadomNodepairlist = dr.NodePairRadom(DemandNum, MixLayer_base);// �������ָ�������Ľڵ��
		dr.TrafficNumRadom(RadomNodepairlist); // ��������ڵ��ҵ��

		// ���¿��Զ�ȡ����е�ҵ��
		// ReadDemand rd=new ReadDemand();
		// RadomNodepairlist=rd.readDemand(MixLayer_base,"D:\\6traffic.csv");

		/*
		 * ����thresholdѭ��
		 */
		for (float threshold = (float) 0.5; threshold <= 1; threshold = (float) (threshold + 0.8)) {
			double bestResult = 100000;
			int bestshuffle = 1000, NumOfIPreg = 0, NumofOEOreg = 0;
			int bestSingleshuffle = 0, bestAllshuffle = 0;
			int MinSlotofAllShuffleOnSingleLink = 10000;
			int MinSlotofAllShuffleofAllLink = 10000;

			for (int shuffle = 0; shuffle < 1; shuffle++) {// ���Ҵ���100��
				ArrayList<WorkandProtectRoute> wprlist = new ArrayList<>();
				double TotalWorkCost = 0, TotalProCost = 0;
				pt.setNumOfTransponder(0);
				pt.setcost_of_tranp(0);

				file_io.filewrite2(OutFileName, "threshold=" + threshold);
				file_io.filewrite2(FinalResultFile, "threshold=" + threshold);
				file_io.filewrite2(OutFileName, "shuffle=" + shuffle);
				file_io.filewrite2(FinalResultFile, "shuffle=" + shuffle);

				// Collections.shuffle(RadomNodepairlist);// ���Ҳ�����ҵ��100��
				for (NodePair nodepair : RadomNodepairlist) {
					System.out.println("�ڵ��  " + nodepair.getName() + "  ������" + nodepair.getTrafficdemand());
					file_io.filewrite2(FinalResultFile,
							"�ڵ��  " + nodepair.getName() + "  ������" + nodepair.getTrafficdemand());
				}

				// �����Ľڵ��֮�������(int)(Math.random()*(2*Constant.AVER_DEMAND-20));
				// ArrayList<WorkandProtectRoute> wprlist = new ArrayList<>();
				ArrayList<NodePair> SmallNodePairList = new ArrayList<NodePair>();

				Layer MixLayer = new Layer("mixlayer", 0, null, null);
				MixLayer.readTopology(TopologyName);
				MixLayer.generateNodepairs();
				mm.NodepairListset(MixLayer, RadomNodepairlist);// ��IP������nodepairList
				ArrayList<NodePair> demandlist = mm.getDemandList(MixLayer, RadomNodepairlist);// ʹ����ڵ��λ��IP��
				mm.init(MixLayer, pt);

				for (int n = 0; n < demandlist.size(); n++) {
					NodePair nodepair = demandlist.get(n);
					if (nodepair.getTrafficdemand() < 50) {// ҵ��������50G��ҵ�񲻽���ͨ��
						SmallNodePairList.add(nodepair);
						continue;
					}
					file_io.filewrite2(OutFileName, "");
					file_io.filewrite2(OutFileName, "");
					file_io.filewrite2(OutFileName,
							"���ڲ����Ľڵ�ԣ� " + nodepair.getName() + "  �������������ǣ� " + nodepair.getTrafficdemand());
					System.out.println("���ڲ����Ľڵ�ԣ� " + nodepair.getName() + "  �������������ǣ� " + nodepair.getTrafficdemand());
					file_io.filewrite2(OutFileName, "Total numberof transponder " + pt.getNumOfTransponder());

					mm.mainMethod(nodepair, MixLayer, pt, threshold, wprlist);
				}
				if (SmallNodePairList != null && SmallNodePairList.size() != 0) {
					for (NodePair smallnodepair : SmallNodePairList) {

						file_io.filewrite2(OutFileName, "");
						file_io.filewrite2(OutFileName, "");
						System.out.println("���ڲ����Ľڵ�ԣ� " + smallnodepair.getName() + "  �������������ǣ� "
								+ smallnodepair.getTrafficdemand());
						file_io.filewrite2(OutFileName, "���ڲ����Ľڵ�ԣ� " + smallnodepair.getName() + "  �������������ǣ� "
								+ smallnodepair.getTrafficdemand());
						file_io.filewrite2(OutFileName, "Total numberof transponder " + pt.getNumOfTransponder());
						mm.mainMethod(smallnodepair, MixLayer, pt, threshold, wprlist);
					}
				}
				
				
				// ������
				int demandnum = 0, TotalWorkRegNum = 0, TotalWorkIPReg = 0, TotalProRegNum = 0, TotalProIPReg = 0;
				file_io.filewrite2(FinalResultFile, "ҵ�������" + wprlist.size());
				if (wprlist.size() != DemandNum) {
					file_io.filewrite2(FinalResultFile, "�˴�shuffle�޷��������ҵ��");
					continue;
				}
				for (WorkandProtectRoute wpr : wprlist) {
					file_io.filewrite2(FinalResultFile, "");
					file_io.filewrite2(FinalResultFile, "nodepair��" + wpr.getdemand().getName());
					file_io.filewrite2(FinalResultFile, "����·����Ӧ��������·��");
					for (Link link : wpr.getworklinklist()) {
						file_io.filewrite2(FinalResultFile, link.getName());
					}
					file_io.filewrite2(FinalResultFile, " ");
					if (wpr.getdemand().getFinalRoute() != null) {
						RouteAndRegPlace FinalRoute = wpr.getdemand().getFinalRoute();
						file_io.filewrite_without(FinalResultFile, "����·��������������λ��Ϊ��");
						for (int reg : FinalRoute.getregnode()) {
							TotalWorkRegNum++;
							file_io.filewrite_without(FinalResultFile, reg + "  ");
						}
						file_io.filewrite2(FinalResultFile, "");
						if (FinalRoute.getIPRegnode() != null) {
							file_io.filewrite_without(FinalResultFile, "����·������IP��������λ��Ϊ��");
							for (int reg : FinalRoute.getIPRegnode()) {
								TotalWorkIPReg++;
								file_io.filewrite_without(FinalResultFile, reg + "  ");
							}
						} else
							file_io.filewrite2(FinalResultFile, " ��ҵ��ֻ����OEO������  ");
						file_io.filewrite2(FinalResultFile, "  ");
						// ����۸�
						// ������cost
						// /*
						double WorkCost = 0;
						for (int count = 0; count < wpr.getRegWorkLengthList().size() - 1; count++) {
							double cost = 0;
							if (FinalRoute.getIPRegnode().contains(count + 1)) {// ˵���ýڵ��ϵ���IP������
								file_io.filewrite2(FinalResultFile, "����·���ϵ�" + count + "��������(IP)���˵�cost");
								for (int num = count; num <= count + 1; num++) {
									double length = wpr.getRegWorkLengthList().get(num);
									file_io.filewrite2(FinalResultFile, "����Ϊ " + length);
									if (length > 2000 && length <= 4000) {
										cost = Constant.Cost_IP_reg_BPSK;
										file_io.filewrite2(FinalResultFile, "����BPSK,costΪ��" + cost);
									} else if (length > 1000 && length <= 2000) {
										cost = Constant.Cost_IP_reg_QPSK;
										file_io.filewrite2(FinalResultFile, "����QPSK,costΪ��" + cost);
									} else if (length > 500 && length <= 1000) {
										cost = Constant.Cost_IP_reg_8QAM;
										file_io.filewrite2(FinalResultFile, "����8QAM,costΪ��" + cost);
									} else if (length > 0 && length <= 500) {
										cost = Constant.Cost_IP_reg_16QAM;
										file_io.filewrite2(FinalResultFile, "����16QAM,costΪ��" + cost);
									}
									WorkCost = WorkCost + cost;
								}
							} else {
								file_io.filewrite2(FinalResultFile, "����·���ϵ�" + count + "��������(OEO)���˵�cost");
								for (int num = count; num <= count + 1; num++) {
									double length = wpr.getRegWorkLengthList().get(num);
									file_io.filewrite2(FinalResultFile, "����Ϊ " + length);
									if (length > 2000 && length <= 4000) {
										cost = Constant.Cost_OEO_reg_BPSK;
										file_io.filewrite2(FinalResultFile, "����BPSK,costΪ��" + cost);
									} else if (length > 1000 && length <= 2000) {
										cost = Constant.Cost_OEO_reg_QPSK;
										file_io.filewrite2(FinalResultFile, "����QPSK,costΪ��" + cost);
									} else if (length > 500 && length <= 1000) {
										cost = Constant.Cost_OEO_reg_8QAM;
										file_io.filewrite2(FinalResultFile, "����8QAM,costΪ��" + cost);
									} else if (length > 0 && length <= 500) {
										cost = Constant.Cost_OEO_reg_16QAM;
										file_io.filewrite2(FinalResultFile, "����16QAM,costΪ��" + cost);
									}
									WorkCost = WorkCost + cost;
								}
							}
						}
						file_io.filewrite2(FinalResultFile, "�����������ܵ�costΪ��" + WorkCost);
						file_io.filewrite2(FinalResultFile, " ");
						TotalWorkCost = TotalWorkCost + WorkCost;
					} else {
						file_io.filewrite2(FinalResultFile, "�ù�����·����Ҫ����������");
					}
				}
			}
		}
		System.out.println("Finish");
	}

	private void init(Layer MixLayer, ParameterTransfer ptoftransp) {
		HashMap<String, Link> LinkList = MixLayer.getLinklist();
		Iterator<String> iter1 = LinkList.keySet().iterator();
		while (iter1.hasNext()) {
			Link link = (Link) (LinkList.get(iter1.next()));
			link.setnature_IPorOP(Constant.NATURE_OP);
			link.setnature_WorkOrPro(Constant.NATURE_PHY);
			link.setRestcapacity(0);
		}
		ptoftransp.setNumOfLink(MixLayer.getLinklist().size());

	}

	public void mainMethod(NodePair nodepair, Layer MixLayer, ParameterTransfer ptoftransp, float threshold,
			ArrayList<WorkandProtectRoute> wprlist) throws IOException {
		// debug
		file_out_put file_io = new file_out_put();
		HashMap<String, Link> Linklist = MixLayer.getLinklist();
		Iterator<String> iter1 = Linklist.keySet().iterator();
		file_io.filewrite2(OutFileName, "��·����" + Linklist.size());
		while (iter1.hasNext()) {
			Link link = (Link) (Linklist.get(iter1.next()));
			file_io.filewrite2(OutFileName,
					"��·" + link.getName() + "  ���� " + link.getnature_IPorOP() + "  " + link.getnature_WorkOrPro()
							+ "  ʣ��" + link.getRestcapacity() + "   ����" + link.getLength() + "  cost:"
							+ link.getCost());
			if(link.getnature_IPorOP()==Constant.NATURE_IP){
				file_io.filewrite2(OutFileName,"��IP��·��Ӧ��������·��");
				for(Link psylink:link.getPhysicallink()){
					file_io.filewrite(OutFileName,psylink.getName()+"   ");
				}
				file_io.filewrite2(OutFileName,"");
			}
		}

		WorkRouteStab wrs = new WorkRouteStab();
		wrs.WorkRouteStab(nodepair, MixLayer, wprlist, ptoftransp, threshold);

	}

	public void NodepairListset(Layer ipLayer, ArrayList<NodePair> nodepairlist) {
		// ����������Ľڵ�Ե��������õ�iplayer��Ӧ�Ľڵ������
		HashMap<String, NodePair> IPnodePairList = new HashMap<String, NodePair>();
		HashMap<String, NodePair> map3 = ipLayer.getNodepairlist();
		Iterator<String> iter3 = map3.keySet().iterator();
		while (iter3.hasNext()) {
			NodePair NodePair = (NodePair) (map3.get(iter3.next()));
			for (int n = 0; n < nodepairlist.size(); n++) {
				NodePair nodePairinList = nodepairlist.get(n);
				if (nodePairinList.getName().equals(NodePair.getName())) {
					NodePair.setTrafficdemand(nodePairinList.getTrafficdemand());
					IPnodePairList.put(NodePair.getName(), NodePair);
					break;
				}
			}
		}
		ipLayer.setNodepairlist(IPnodePairList);
	}

	public ArrayList<NodePair> getDemandList(Layer ipLayer, ArrayList<NodePair> RadomNodepairlist) {
		ArrayList<NodePair> demandList = new ArrayList<NodePair>();

		ArrayList<NodePair> NewFormatnodePairList = new ArrayList<NodePair>();
		HashMap<String, NodePair> nodepairlist = ipLayer.getNodepairlist();
		Iterator<String> iter1 = nodepairlist.keySet().iterator();
		while (iter1.hasNext()) {
			NodePair nodepair = (NodePair) (nodepairlist.get(iter1.next()));
			NewFormatnodePairList.add(nodepair);
		}
		for (NodePair np : RadomNodepairlist) {
			for (NodePair nodepairInIP : NewFormatnodePairList) {
				if (np.getName().equals(nodepairInIP.getName())) {
					demandList.add(nodepairInIP);
					break;
				}
			}
		}
		return demandList;
	}

	public ArrayList<Integer> spectrumallocationOneRoute(Boolean routeflag, LinearRoute route, ArrayList<Link> linklist,
			int slotnum) {
		// true ��route false��linklist
		ArrayList<Link> linklistOnroute = new ArrayList<Link>();
		if (routeflag) {
			linklistOnroute = route.getLinklist();
		} else {
			linklistOnroute = linklist;
		}
		ArrayList<Link> NoAssignLink = new ArrayList<>();
		for (Link link : linklistOnroute) {
			if (link.getnature_IPorOP() == Constant.NATURE_BOUND) {
				NoAssignLink.add(link);
			}
		}
		for (Link delLink : NoAssignLink) {
			linklistOnroute.remove(delLink);
		} // check �۲��ܲ����Ƴ�����·
		for (Link link : linklistOnroute) {
			link.getSlotsindex().clear();

			for (int start = 0; start < link.getSlotsarray().size() - slotnum; start++) {
				int flag = 0;
				for (int num = start; num < slotnum + start; num++) {// �����FS������������
					if (link.getSlotsarray().get(num).getoccupiedreqlist().size() != 0) {// �ò����Ѿ���ռ��
						flag = 1;
						break;
					}
				}
				if (flag == 0) {
					link.getSlotsindex().add(start);// ���ҿ���slot�����
				}
			}
		} // �������е�link������

		Link firstlink = linklistOnroute.get(0);
		ArrayList<Integer> sameindex = new ArrayList<Integer>();
		sameindex.clear();

		for (int s = 0; s < firstlink.getSlotsindex().size(); s++) {
			int index = firstlink.getSlotsindex().get(s);
			int flag = 1;

			for (Link otherlink : linklistOnroute) {
				if (otherlink.getName().equals(firstlink.getName()))
					continue;
				if (!otherlink.getSlotsindex().contains(index)) {
					flag = 0;
					break;
				}
			}
			if (flag == 1) {
				sameindex.add(index); // ��ѡ����·��������link��ͬ��slot start��
			}
		}
		return sameindex;
	}

	public void FinalResultOut(ArrayList<WorkandProtectRoute> wprlist, int DemandNum) {
		file_out_put file_io = new file_out_put();

	}
}
