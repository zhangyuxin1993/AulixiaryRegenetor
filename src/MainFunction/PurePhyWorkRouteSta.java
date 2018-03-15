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

	public LinearRoute purephyworkroutesta(NodePair nodepair, Layer MixLayer,ArrayList<WorkandProtectRoute> wprlist, float threshold,
			ParameterTransfer ptoftransp,ArrayList<Double> RegLengthList,ArrayList<Link> totallink) throws IOException {
		RouteSearching Dijkstra = new RouteSearching();
		boolean opworkflag = false;
		Node srcnode = nodepair.getSrcNode();
		Node desnode = nodepair.getDesNode();
		double routelength = 0;
		LinearRoute route_out = new LinearRoute(null, 0, null);
		file_out_put file_io = new file_out_put();
	 
		file_io.filewrite2(OutFileName, " ");
		ArrayList<LinearRoute> routeList = new ArrayList<>();
		file_io.filewrite2(OutFileName, "��������·����·�ɳ��Խ���");

		//��Ҫʹ�ô�����·�� ���ʱӦ��ɾ������Ϊ����
		int index= ptoftransp.getNumOfLink() + 1;
		ptoftransp.setNumOfLink(index);
		String index_inName=String.valueOf(index);
		ArrayList<Link> DelAllIPLink=new ArrayList<>();
		HashMap<String,Link> linklist=MixLayer.getLinklist();
	     Iterator<String> iter1=linklist.keySet().iterator();
	     while(iter1.hasNext()){
	    	 Link link=(Link)(linklist.get(iter1.next()));
	    	 if(link.getnature_IPorOP()==Constant.NATURE_BOUND) continue;
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
							+"   total transponder cost="+ ptoftransp.getcost_of_tranp());
					
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
						double length1 = 0;
						double cost = 0;

						for (Link link : opnewRoute.getLinklist()) {// ������link
							length1 = length1 + link.getLength();
							cost = cost + link.getCost();
							Request request = null;
							ResourceOnLink ro = new ResourceOnLink(request, link, index_wave.get(0), slotnum);
							// ֮���ڹ���·����Ҫ�������ĵط�Ҳ�������
							link.setMaxslot(slotnum + link.getMaxslot());
						} // �ı�������ϵ���·���� �Ա�����һ���½�ʱ����slot

						Node helpNode=new Node(null, index, null, MixLayer, 0, 0); // ���ｫhelpNode����Ϊ�м丨���ڵ�
						helpNode.setName(srcnode.getName()+"("+index_inName+")");
						MixLayer.addNode(helpNode);
						length1=length1/1000;
						cost=cost/1000;
						
						String name = null;
						Link createlink=new Link(null, 0, null, null, null, null, 0, 0);
						if (desnode.getIndex() < helpNode.getIndex()){
							// ȷ����ӵ�����·��������
							name = desnode.getName() +"-"+ helpNode.getName();
							createlink = new Link(name, index, null, MixLayer, desnode, helpNode, length1, cost);
						}
						else{
							name = helpNode.getName() +"-"+ desnode.getName() ;
							createlink = new Link(name, index, null, MixLayer,helpNode, desnode,  length1, cost);
						}
						
						createlink.setnature_IPorOP(Constant.NATURE_IP);
						createlink.setnature_WorkOrPro(Constant.NATURE_WORK);
						createlink.setFullcapacity(slotnum * X);// �������flow�Ǵ����������
						createlink.setRestcapacity(createlink.getFullcapacity() - IPflow );
						createlink.setPhysicallink(opnewRoute.getLinklist());
						MixLayer.addLink(createlink);
						file_io.filewrite2(OutFileName, "����������·" +createlink.getName()+" ʣ������"+ createlink.getRestcapacity());
						
						String boundLink_name = null;
						Link boundlink=new Link(null, 0, null, null, null, null, 0, 0);
						if (srcnode.getIndex() < helpNode.getIndex()){
							// ȷ����ӵ�����·��������
							boundLink_name = srcnode.getName() +"-"+ helpNode.getName();
							boundlink = new Link(boundLink_name, index, null, MixLayer, srcnode, helpNode, 0, 0);
						}
						else{
							boundLink_name = helpNode.getName() +"-"+ srcnode.getName() ;
							boundlink = new Link(boundLink_name, index, null, MixLayer,helpNode, srcnode, 0, 0);
						}
						
						boundlink.setnature_IPorOP(Constant.NATURE_BOUND);
						boundlink.setnature_WorkOrPro(Constant.NATURE_BOUND);
						boundlink.setRestcapacity(0);
						MixLayer.addLink(boundlink);
					}
				}
				if (routelength > 4000) {
					RegeneratorPlace regplace = new RegeneratorPlace();
					opworkflag = regplace.regeneratorplace(IPflow, routelength, false,opnewRoute, null,MixLayer, wprlist,
							nodepair, RegLengthList,threshold,ptoftransp);
				}
			}
			
			if (opworkflag) 
				break;
		}
		if (opworkflag) {
			file_io.filewrite2(OutFileName, "����·��������·��·�ɳɹ�����RSA");
			
		}
		if (!opworkflag) {
			file_io.filewrite2(OutFileName, "����·����������··��ʧ��");
		}
		for(Link link:DelAllIPLink){//�ָ����е�������·
			MixLayer.addLink(link);
		}
		return opnewRoute;
	}
}
