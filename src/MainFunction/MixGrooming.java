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

public class MixGrooming {
	String OutFileName = MainOfAulixiaryRegenetor.OutFileName;

	public boolean MixGrooming(Boolean WorkOrPro, NodePair nodepair, Layer MixLayer, double UnfishFlow,
			ParameterTransfer ptoftransp, ArrayList<Double> RegLengthList, ArrayList<Double> ProLengthList,ArrayList<WorkandProtectRoute> wprlist,
			float threshold, ArrayList<Link> totallink, ArrayList<FlowUseOnLink> fuoList, ArrayList<Link> ProVlinklist,Request ProRequest)
			throws IOException {
		file_out_put file_io = new file_out_put();
		RouteSearching Dijkstra = new RouteSearching();
		boolean routeFlag = false;
		ArrayList<Link> Prototallink = new ArrayList<>();

		file_io.filewrite2(OutFileName, "  ");
		file_io.filewrite2(OutFileName, "��ʼMix Grooming");
		// ����bound��·��������СΪ�����
		// �ҵ���·���б��뺬��������·
		// ��Ϊֻ��ѡȡһ��·�� mixgrooming ���Դ�ʱҪɾ������������������·
		ArrayList<Link> DelLackCapLink = new ArrayList<>(); // ɾ����������·����С��δ�����������·
		HashMap<String, Link> linklist = MixLayer.getLinklist();
		Iterator<String> linkitor2 = linklist.keySet().iterator();
		while (linkitor2.hasNext()) {
			Link linkInMixlayer = (Link) (linklist.get(linkitor2.next()));
			if (linkInMixlayer.getnature_IPorOP() == Constant.NATURE_BOUND)
				continue;
			if (linkInMixlayer.getnature_IPorOP() == Constant.NATURE_IP) {
				if (linkInMixlayer.getRestcapacity() < UnfishFlow) {
					DelLackCapLink.add(linkInMixlayer);
					continue;
				}
				if (WorkOrPro) {// ����ɾȥ����������·
					 if(linkInMixlayer.getnature_WorkOrPro()==Constant.NATURE_PRO)
//							file_io.filewrite2(OutFileName, "ɾ�������Ᵽ����·" +  linkInMixlayer.getName());
						 DelLackCapLink.add(linkInMixlayer);
				}
			}
		}
		for (Link delLink : DelLackCapLink) {
			MixLayer.removeLink(delLink);
		}
		// debug
		HashMap<String, Link> Linklist = MixLayer.getLinklist();
		Iterator<String> iter1 = Linklist.keySet().iterator();
		file_io.filewrite2(OutFileName, "��·����" + Linklist.size());
		while (iter1.hasNext()) {
			Link link = (Link) (Linklist.get(iter1.next()));
			file_io.filewrite2(OutFileName, "��·" + link.getName() + "  ���� " + link.getnature_IPorOP() + "  "
					+ link.getnature_WorkOrPro() + "  ʣ��" + link.getRestcapacity());
		}

		ArrayList<Link> psyLinklist = new ArrayList<>();
		ArrayList<LinearRoute> routeList = new ArrayList<>();
		Dijkstra.Kshortest(nodepair.getSrcNode(), nodepair.getDesNode(), MixLayer, 20, routeList);

		if (routeList.size() != 0 && routeList != null) {// �ҵ�·��
			for (LinearRoute route : routeList) {
				file_io.filewrite2(OutFileName, "Mixgrooming·��");
				route.OutputRoute_node(route, OutFileName);
				file_io.filewrite2(OutFileName, "");

				boolean IPFlag = false;
				for (Link LinkOnRoute : route.getLinklist()) {
					if (LinkOnRoute.getnature_IPorOP() == Constant.NATURE_IP) {// �жϸ�·����·���Ƿ���������·
						IPFlag = true;
						break;
					}
				}

				if (IPFlag) {// ֻ�е�·���к���IP��·ʱ�Ż���з���
					// Ѱ��·�������е�IP��·��������С��ҵ��
					psyLinklist.clear();

					ArrayList<Link> IPLinkOnRoute = new ArrayList<>();
					ArrayList<Link> OPLinkOnRoute = new ArrayList<>();
					int n = 0;
					boolean conFlag = false;
					for (Link LinkOnRoute : route.getLinklist()) {
						LinkOnRoute.setindexInRoute(n);
						n++;
						if (LinkOnRoute.getnature_IPorOP() == Constant.NATURE_IP) {
							IPLinkOnRoute.add(LinkOnRoute);
							for (Link phyLink : LinkOnRoute.getPhysicallink())
								psyLinklist.add(phyLink);
						} else if (LinkOnRoute.getnature_IPorOP() == Constant.NATURE_OP)
							OPLinkOnRoute.add(LinkOnRoute);
					}
					for (int m = 0; m < OPLinkOnRoute.size() - 1; m++) {
						Link link0 = OPLinkOnRoute.get(m);
						Link link1 = OPLinkOnRoute.get(m + 1);
						if (Math.abs(link0.getindexInRoute() - link1.getindexInRoute()) != 1) {
							conFlag = true;
							break;
						}
					}
					if (conFlag)
						continue;
					double ResCapMin = 1000;
					for (Link link : IPLinkOnRoute) {
						if (link.getRestcapacity() < ResCapMin)
							ResCapMin = link.getRestcapacity();
					}
					if (ResCapMin < UnfishFlow)
						continue;

					// IP·���ϵ�ʣ����������δ������� ����Ҫ��������·����FS
					// debug
					file_io.filewrite2(OutFileName, "mixgrooming�е�������·��");
					for (Link link : OPLinkOnRoute) {
						file_io.filewrite2(OutFileName, link.getName());
					}

					MixGrooming mg = new MixGrooming();

					routeFlag = mg.AssignFSforPhyLinkAndIPLinkStab(WorkOrPro, OPLinkOnRoute, UnfishFlow, ptoftransp,
							MixLayer, wprlist, nodepair, RegLengthList, threshold, ProLengthList,ProRequest);

//					n AssignFSforPhyLinkAndIPLinkStab(Boolean WorkOrPro, ArrayList<Link> OPLinkOnRoute, double UnfishFlow,
//							ParameterTransfer ptoftransp, Layer MixLayer, ArrayList<WorkandProtectRoute> wprlist, NodePair nodepair,
//							ArrayList<Double> RegLengthList, float threshold,ArrayList<Double> ProLengthList,Request ProRequest) throws IOException {
						
					if (routeFlag) {// ˵����ʱ������··�ɳɹ� ��Ҫ�ı�������·�ϵ�����
						ProVlinklist.addAll(IPLinkOnRoute);
						totallink.addAll(psyLinklist);
						Prototallink.addAll(OPLinkOnRoute);
						file_io.filewrite2(OutFileName, "MixGrooming·�ɳɹ�");
						route.OutputRoute_node(route, OutFileName);
						for (Link link : OPLinkOnRoute)// ·�ɳɹ�ʱ��Ҫ��������·��Ӧ��·��Ҳ����totallink
							totallink.add(link);
						for (Link ModifyCapLink : IPLinkOnRoute) {
							ModifyCapLink.setRestcapacity(ModifyCapLink.getRestcapacity() - UnfishFlow);
						}
						break;// �ҵ�һ���ɹ���·�ɾ����� first-fit
					}
				}
			}
		}
		if (!routeFlag) {
			file_io.filewrite2(OutFileName, "MixGroomingû���ҵ�·��");
			totallink.clear();
			ProVlinklist.clear();
			for (FlowUseOnLink fuo : fuoList) {// ���mixgrooming���ɹ�
												// ��Ҫ��flowsplitting�м�ȥ�������ָ�
				fuo.getVlink().setRestcapacity(fuo.getVlink().getRestcapacity() + fuo.getFlowUseOnLink());
			}
		}

		for (Link recLink : DelLackCapLink) {// �ָ���ʼɾȥ����·
			MixLayer.addLink(recLink);
		}
		if (routeFlag) {
			for (WorkandProtectRoute wpr : wprlist) {
				if (wpr.getdemand().equals(nodepair)) {
					wpr.setprovlinklist(ProVlinklist);// ����·���õ���������·
					wpr.setprolinklist(Prototallink);// ����·���õ���������·

				}
			}

		}
		return routeFlag;

	}

	public boolean AssignFSforPhyLinkAndIPLinkStab(Boolean WorkOrPro, ArrayList<Link> OPLinkOnRoute, double UnfishFlow,
			ParameterTransfer ptoftransp, Layer MixLayer, ArrayList<WorkandProtectRoute> wprlist, NodePair nodepair,
			ArrayList<Double> RegLengthList, float threshold, ArrayList<Double> ProLengthList,Request ProRequest) throws IOException {
		boolean routeFlag = false;
		file_out_put file_io = new file_out_put();
		int routelength = 0, slotnum = 0;
		ArrayList<FSshareOnlink> FSuseOnlink = new ArrayList<FSshareOnlink>();
		file_io.filewrite2(OutFileName, "����������·����FS");
		double X = 1;
		for (Link link : OPLinkOnRoute) {
			routelength = (int) (routelength + link.getLength());
		}
		if (routelength <= 4000) {// �ҵ���·������Ҫ������
			double costOftransp = 0;
			if (routelength > 2000 && routelength <= 4000) {
				costOftransp = Constant.Cost_IP_reg_BPSK;
				X = 12.5;
			} else if (routelength > 1000 && routelength <= 2000) {
				costOftransp = Constant.Cost_IP_reg_QPSK;
				X = 25.0;
			} else if (routelength > 500 && routelength <= 1000) {
				costOftransp = Constant.Cost_IP_reg_8QAM;
				X = 37.5;
			} else if (routelength > 0 && routelength <= 500) {
				costOftransp = Constant.Cost_IP_reg_16QAM;
				X = 50.0;
			}
			slotnum = (int) Math.ceil(UnfishFlow / X);// ����ȡ��
			ptoftransp.setcost_of_tranp(ptoftransp.getcost_of_tranp() + costOftransp * 2);
			file_io.filewrite2(OutFileName, "");
			file_io.filewrite2(OutFileName, "Mixgrooming ����·������Ҫ������  cost of transponder" + costOftransp * 2
					+ "  transponder cost=" + ptoftransp.getcost_of_tranp());

			if (slotnum < Constant.MinSlotinLightpath) {
				slotnum = Constant.MinSlotinLightpath;
			}
			file_io.filewrite2(OutFileName, "������·����slot���� " + slotnum);
			ArrayList<Integer> index_wave = new ArrayList<Integer>();
			MainOfAulixiaryRegenetor spa = new MainOfAulixiaryRegenetor();
			if (WorkOrPro)
				index_wave = spa.spectrumallocationOneRoute(false, null, OPLinkOnRoute, slotnum);
			if (!WorkOrPro) {// ����
				ProRouteStab prs = new ProRouteStab();
				index_wave = prs.FSassignOnlink(OPLinkOnRoute, wprlist, nodepair, slotnum, MixLayer);
			}

			if (index_wave.size() == 0) {
				routeFlag = false;
				file_io.filewrite2(OutFileName, "Mixgrooming ����·������ ���޷�����Ƶ����Դ");
			} else {
				routeFlag = true;
				file_io.filewrite2(OutFileName, "");
				file_io.filewrite2(OutFileName, "MixGrooming ����·��·��FS��");
				file_io.filewrite2(OutFileName, "FS��ʼֵ��" + index_wave.get(0) + "  ����" + slotnum);

				double length1 = 0;
				double cost1 = 0;
				
				for (Link link : OPLinkOnRoute) {// ��¼����·����·��ʹ�õ�FS
					length1 = length1 + link.getLength();
					cost1 = cost1 + link.getCost();
					if(WorkOrPro){
						Request request = null;
						ResourceOnLink ro = new ResourceOnLink(request, link, index_wave.get(0), slotnum);
					}
					else{
						ResourceOnLink ro = new ResourceOnLink(ProRequest, link, index_wave.get(0), slotnum);
						FSshareOnlink fsonLink = new FSshareOnlink(link, index_wave);
						FSuseOnlink.add(fsonLink);
					}
					link.setMaxslot(slotnum + link.getMaxslot());
				} // �ı�������ϵ���·FS���� �Ա�����һ���½�ʱ����slot

				// Ѱ������·�ɵ������յ�
				int index = ptoftransp.getNumOfLink() + 1;
				ptoftransp.setNumOfLink(index);
				String srcnode_name = null;
				String desnode_name = null;
				String index_inName = String.valueOf(index);
				Node helpNode = new Node(null, index, null, MixLayer, 0, 0); // ���ｫhelpNode����Ϊ�м丨���ڵ�

				if (OPLinkOnRoute.size() == 1) {
					helpNode.setName(OPLinkOnRoute.get(0).getNodeA().getName() + "(" + index_inName + ")");
					srcnode_name = OPLinkOnRoute.get(0).getNodeA().getName();
					desnode_name = OPLinkOnRoute.get(0).getNodeB().getName();
				} else {
					Link link0 = OPLinkOnRoute.get(0);
					Link link1 = OPLinkOnRoute.get(1);
					Link link3 = OPLinkOnRoute.get(OPLinkOnRoute.size() - 2);
					Link link4 = OPLinkOnRoute.get(OPLinkOnRoute.size() - 1);
					if (link0.getNodeA().equals(link1.getNodeA()) || link0.getNodeA().equals(link1.getNodeB())) {
						srcnode_name = link0.getNodeB().getName();
						helpNode.setName(srcnode_name + "(" + index_inName + ")");
					} else {
						srcnode_name = link0.getNodeA().getName();
						helpNode.setName(srcnode_name + "(" + index_inName + ")");
					}

					if (link4.getNodeA().equals(link3.getNodeA()) || link4.getNodeA().equals(link3.getNodeB())) {
						desnode_name = link4.getNodeB().getName();
					} else
						desnode_name = link4.getNodeA().getName();
				}
				MixLayer.addNode(helpNode);

				Node srcnode = MixLayer.getNodelist().get(srcnode_name);
				Node desnode = MixLayer.getNodelist().get(desnode_name);
				length1 = length1 / 1000;
				cost1 = cost1 / 1000;
				String name = null;
				Link createlink = new Link(null, 0, null, null, null, null, 0, 0);
				if (desnode.getIndex() < helpNode.getIndex()) {
					// ȷ����ӵ�����·��������
					name = desnode.getName() + "-" + helpNode.getName();
					createlink = new Link(name, index, null, MixLayer, desnode, helpNode, length1, cost1);
				} else {
					name = helpNode.getName() + "-" + desnode.getName();
					createlink = new Link(name, index, null, MixLayer, helpNode, desnode, length1, cost1);
				}
				createlink.setnature_IPorOP(Constant.NATURE_IP);
				if (WorkOrPro)
					createlink.setnature_WorkOrPro(Constant.NATURE_WORK);
				else if (!WorkOrPro)
					createlink.setnature_WorkOrPro(Constant.NATURE_PRO);

				createlink.setFullcapacity(slotnum * X);
				createlink.setRestcapacity(createlink.getFullcapacity() - UnfishFlow);
				createlink.setPhysicallink(OPLinkOnRoute);
				MixLayer.addLink(createlink);

				String boundLink_name = null;
				Link boundlink = new Link(null, 0, null, null, null, null, 0, 0);
				if (srcnode.getIndex() < helpNode.getIndex()) {
					// ȷ����ӵ�����·��������
					boundLink_name = srcnode.getName() + "-" + helpNode.getName();
					boundlink = new Link(boundLink_name, index, null, MixLayer, srcnode, helpNode, 0, 0);
				} else {
					boundLink_name = helpNode.getName() + "-" + srcnode.getName();
					boundlink = new Link(boundLink_name, index, null, MixLayer, helpNode, srcnode, 0, 0);
				}

				boundlink.setnature_IPorOP(Constant.NATURE_BOUND);
				boundlink.setnature_WorkOrPro(Constant.NATURE_BOUND);
				boundlink.setRestcapacity(0);
				MixLayer.addLink(boundlink);

				ArrayList<Link> IPlinkStaInWork = new ArrayList<>();
				IPlinkStaInWork.add(createlink);
				ptoftransp.setIPlinkStaInWork(IPlinkStaInWork);// ���湤��ʱ������������·
				file_io.filewrite2(OutFileName, "������������ʱ �½�������· " + createlink.getName() + "  index= "
						+ createlink.getIndex() + "  ʣ��������" + createlink.getRestcapacity());
			}

		} else if (routelength > 4000) {
			// ��ʱ��Ҫ����������
			RegeneratorPlace rp = new RegeneratorPlace();
			ProregeneratorPlace prp=new ProregeneratorPlace();
			if(WorkOrPro)
			routeFlag = rp.regeneratorplace(UnfishFlow, routelength, true, null, OPLinkOnRoute, MixLayer, wprlist,
					nodepair, RegLengthList, threshold, ptoftransp);
			else if(!WorkOrPro)
				routeFlag =prp.ProRegeneratorPlace(true, nodepair, null, OPLinkOnRoute, wprlist, routelength, MixLayer, UnfishFlow
						, ProRequest, threshold, ptoftransp,FSuseOnlink);
		 
		}
		if(routeFlag&&!WorkOrPro){
			for(WorkandProtectRoute wpr: wprlist ){
				if(wpr.getdemand().equals(nodepair)){
					wpr.setFSoneachLink(FSuseOnlink);
				}
			}
		}
		
		return routeFlag;
	}
}
