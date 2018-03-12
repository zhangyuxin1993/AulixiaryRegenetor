package MainFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import demand.Request;
import general.Constant;
import general.file_out_put;
import graphalgorithms.RouteSearching;
import network.Layer;
import network.Link;
import network.Node;
import network.NodePair;
import resource.ResourceOnLink;
import subgraph.LinearRoute;

public class PurePhyWorkRouteSta {
	String OutFileName = MainOfAulixiaryRegenetor.OutFileName;

	public boolean purephyworkroutesta(NodePair nodepair, Layer MixLayer,ArrayList<WorkandProtectRoute> wprlist, float threshold,
			ParameterTransfer ptoftransp,ArrayList<Double> RegLengthList) throws IOException {
		RouteSearching Dijkstra = new RouteSearching();
		boolean opworkflag = false;
		Node srcnode = nodepair.getSrcNode();
		Node desnode = nodepair.getDesNode();
		double routelength = 0;
		LinearRoute route_out = new LinearRoute(null, 0, null);
		file_out_put file_io = new file_out_put();
	 
		file_io.filewrite2(OutFileName, " ");
		ArrayList<LinearRoute> routeList = new ArrayList<>();
		file_io.filewrite2(OutFileName, "��������·����·�ɽ���");

		//��Ҫʹ�ô�����·�� ���ʱӦ��ɾ������Ϊ����
		int index= ptoftransp.getNumOfLink() + 1;
		ptoftransp.setNumOfLink(index);
		ArrayList<Link> DelAllIPLink=new ArrayList<>();
		HashMap<String,Link> linklist=MixLayer.getLinklist();
	     Iterator<String> iter1=linklist.keySet().iterator();
	     while(iter1.hasNext()){
	    	 Link link=(Link)(linklist.get(iter1.next()));
	    	 if(link.getnature_IPorOP()==Constant.NATURE_IP){
	    		 DelAllIPLink.add(link);
	    	 }
	     }
			for (Link delLink : DelAllIPLink) {
				MixLayer.removeLink(delLink);
			}
	     
		// �ڹ���½���·��ʱ����Ҫ��������������
		LinearRoute opnewRoute=new LinearRoute(null, 0, null);
		Dijkstra.Kshortest(srcnode, desnode, MixLayer, 10, routeList);

		for (int count = 0; count < routeList.size(); count++) {
			opnewRoute = routeList.get(count);
			file_io.filewrite_without(OutFileName, "count=" + count + "  ����·��·�ɣ�");
			opnewRoute.OutputRoute_node(opnewRoute, OutFileName);
			file_io.filewrite2(OutFileName, "");

			if (opnewRoute.getLinklist().size() == 0) {
				file_io.filewrite2(OutFileName, "������·��");
			} else {
				file_io.filewrite_without(OutFileName, "��������··��Ϊ��");
				route_out.OutputRoute_node(opnewRoute, OutFileName);

				int slotnum = 0;
				int IPflow = nodepair.getTrafficdemand();
				double X = 1;// 2000-4000 BPSK,1000-2000
								// QBSK,500-1000��8QAM,0-500 16QAM

				for (Link link : opnewRoute.getLinklist()) {
					routelength = routelength + link.getLength();
				}
				// ͨ��·���ĳ������仯���Ƹ�ʽ �����ж������� ��ʹ��

				if (routelength < 4000) {// �ҵ���·������Ҫ�������Ϳ���ֱ��ʹ��
					double costOftransp=0;
					if (routelength > 2000 && routelength <= 4000) {
						costOftransp=Constant.Cost_IP_reg_BPSK;
						X = 12.5;
					} else if (routelength > 1000 && routelength <= 2000) {
						costOftransp=Constant.Cost_IP_reg_QPSK;
						X = 25.0;
					} else if (routelength > 500 && routelength <= 1000) {
						costOftransp=Constant.Cost_IP_reg_8QAM;
						X = 37.5;
					} else if (routelength > 0 && routelength <= 500) {
						costOftransp=Constant.Cost_IP_reg_16QAM;
						X = 50.0;
					}
					slotnum = (int) Math.ceil(IPflow / X);// ����ȡ��
					ptoftransp.setcost_of_tranp(ptoftransp.getcost_of_tranp()+costOftransp*2);
					file_io.filewrite2(OutFileName, "");
					file_io.filewrite2(OutFileName, "��������·����·������Ҫ������ʱ cost of transponder" + costOftransp*2
							+"transponder cost="+ ptoftransp.getcost_of_tranp());
					
					if (slotnum < Constant.MinSlotinLightpath) {
						slotnum = Constant.MinSlotinLightpath;
					}
					opnewRoute.setSlotsnum(slotnum);
					// System.out.println("����Ҫ������ ����·����slot���� " + slotnum);
					file_io.filewrite2(OutFileName, "����Ҫ������ ����·����slot���� " + slotnum);
					ArrayList<Integer> index_wave = new ArrayList<Integer>();
					MainOfAulixiaryRegenetor spa = new MainOfAulixiaryRegenetor();
					index_wave = spa.spectrumallocationOneRoute(true, opnewRoute, null, slotnum);
					if (index_wave.size() == 0) {
						file_io.filewrite2(OutFileName, "·������ ��������Ƶ����Դ");
					} else {
						file_io.filewrite2(OutFileName, "");
						file_io.filewrite2(OutFileName, "������·����Ҫ������ʱ�ڹ�����Ƶ�ף�");
						file_io.filewrite2(OutFileName, "FS��ʼֵ��" + index_wave.get(0) + "  ����" + slotnum);
						opworkflag = true;
						int length1 = 0;
						double cost = 0;

						for (Link link : opnewRoute.getLinklist()) {// �������link
							length1 = length1 + link.getLength();
							cost = cost + link.getCost();
							Request request = null;
							ResourceOnLink ro = new ResourceOnLink(request, link, index_wave.get(0), slotnum);
							// ֮���ڹ���·����Ҫ�������ĵط�Ҳ�������
							link.setMaxslot(slotnum + link.getMaxslot());
						} // �ı��������ϵ���·���� �Ա�����һ���½�ʱ����slot

						String name = srcnode.getName() + "-" + desnode.getName();

						Link createlink = new Link(name, index, null, MixLayer, srcnode, desnode, length1, cost);
						createlink.setnature_IPorOP(Constant.NATURE_IP);
						createlink.setnature_WorkOrPro(Constant.NATURE_WORK);
						createlink.setFullcapacity(slotnum * X);// �������flow�Ǵ����������
						createlink.setRestcapacity(createlink.getFullcapacity() - IPflow );
						createlink.setPhysicallink(opnewRoute.getLinklist());
						MixLayer.addLink(createlink);
					}
				}
				if (routelength > 4000) {
					RegeneratorPlace regplace = new RegeneratorPlace();
					opworkflag = regplace.regeneratorplace(IPflow, routelength, false,opnewRoute, null,MixLayer, wprlist,
							nodepair, RegLengthList,threshold,ptoftransp);
				}
			}
			
			if (opworkflag) {
				ptoftransp.setNumOfTransponder(ptoftransp.getNumOfTransponder()+2);//������·�����ɹ�
				file_io.filewrite2(OutFileName, "����·���ڹ��ɹ�·�ɲ���RSA");
				WorkandProtectRoute wpr = new WorkandProtectRoute(nodepair);
				Request re = new Request(nodepair);
				ArrayList<Link> totallink = new ArrayList<>();
				totallink = opnewRoute.getLinklist();
				wpr.setrequest(re);
				wpr.setworklinklist(totallink);
				wpr.setRegWorkLengthList(RegLengthList);
				wprlist.add(wpr);
				break;

			}
			if (!opworkflag) {
				file_io.filewrite2(OutFileName, "����·���ڹ���޷�����");
			}
		}
		for(Link link:DelAllIPLink){//�ָ����е�������·
			MixLayer.addLink(link);
		}
		return opworkflag;
	}
}