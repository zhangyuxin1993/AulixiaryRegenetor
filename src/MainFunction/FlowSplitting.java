package MainFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import general.Constant;
import general.file_out_put;
import graphalgorithms.RouteSearching;
import graphalgorithms.SearchConstraint;
import network.Layer;
import network.Link;
import network.Node;
import network.NodePair;
import subgraph.LinearRoute;

public class FlowSplitting {
	String OutFileName = MainOfAulixiaryRegenetor.OutFileName;

	public double flowsplitting(Boolean ProFlag,Layer MixLayer, NodePair nodepair,ArrayList<Link> totallink,ArrayList<FlowUseOnLink> fuoList,ArrayList<Link> ProVlinklist) throws IOException {
		//�������н��ڵ��֮���ҵ�񾡿��ܵķ���������·������
//		ɾ����������·�Ѿ�ȫ���ָ� ����û�лָ��õ�������
		RouteSearching Dijkstra = new RouteSearching();
		file_out_put file_io = new file_out_put();
		ProVlinklist.clear();
		double UnfinishFLow = nodepair.getTrafficdemand();
		Node srcnode = nodepair.getSrcNode();
		Node desnode = nodepair.getDesNode();
		file_io.filewrite2(OutFileName, "��ʼflow splitting");
		ArrayList<Link> CannotUseLink=new ArrayList<>();
		
		if(!ProFlag){
			//����ɾ����������ΪOP�Լ���������·
			HashMap<String, Link> linklisttest = MixLayer.getLinklist();
			Iterator<String> linkitortest = linklisttest.keySet().iterator();
			while (linkitortest.hasNext()) {
				Link Mlink = (Link) (linklisttest.get(linkitortest.next()));
				if(Mlink.getnature_IPorOP()==Constant.NATURE_BOUND) {
					Mlink.setRestcapacity(Constant.MaxNum);
					continue;
				}
				if(Mlink.getnature_IPorOP()==Constant.NATURE_OP||Mlink.getnature_WorkOrPro()==Constant.NATURE_PRO)
					CannotUseLink.add(Mlink);
			}
			for(Link delLink: CannotUseLink){
				MixLayer.removeLink(delLink);
			}
		}
		
		ArrayList<Link> DelNoFlowLinkToReco = new ArrayList<>();// ÿ��ѭ��Ҫɾ����������·
		ArrayList<Link> AllDelNoFlowLinkToReco = new ArrayList<>();// һ����Ҫɾ����������·
		while (true) {
			LinearRoute newRoute = new LinearRoute(null, 0, null);
			SearchConstraint sc = new SearchConstraint(100);// ��·������󳤶�����Ϊ100  
															// ������̵�����·��Ҫ����100
															// ʹ���ҵ���·��ȫ��������·
			Dijkstra.Dijkstras(srcnode, desnode, MixLayer, newRoute, sc);
			if (newRoute.getLinklist().size() == 0) {// ˵��Դ���֮��û��·��
				file_io.filewrite2(OutFileName, "��������·�޷�·��");
				break;
			} else {
				file_io.filewrite2(OutFileName, " ");
				DelNoFlowLinkToReco.clear();
				ProVlinklist.addAll(newRoute.getLinklist());
				// ���Ȳ���·��������������·����Сʣ������
				file_io.filewrite2(OutFileName, "ѭ���У��ҵ�������··��");
				newRoute.OutputRoute_node(newRoute, OutFileName);

				// �ҳ����·���ϵ���С����
				double MinFlowOnRoute = 10000;
				for (Link LinkOnRoute : newRoute.getLinklist()) {
					if (LinkOnRoute.getRestcapacity() < MinFlowOnRoute) {
						MinFlowOnRoute = LinkOnRoute.getRestcapacity();
					}
				}

				// �ҵ���·���ϵ���С������������������
				if (UnfinishFLow <= MinFlowOnRoute) {// δ��ɵ�ҵ��С����·��ʣ���������ʱ�������ҵ��
					file_io.filewrite2(OutFileName, "δ��ɵ�ҵ��С����·��ʣ������� flowSplitting �ɹ�");
					for (Link LinkOnRoute : newRoute.getLinklist()) {// �ı�ÿ��������·�ϵ�ʣ������
						FlowUseOnLink fuo =new FlowUseOnLink(LinkOnRoute, UnfinishFLow);
						fuoList.add(fuo);
						LinkOnRoute.setRestcapacity(LinkOnRoute.getRestcapacity() - UnfinishFLow);
						if(LinkOnRoute.getPhysicallink()!=null){
							for (Link phlink : LinkOnRoute.getPhysicallink()) {// ��¼�����߹���������·��Ӧ��������·
								totallink.add(phlink);
							}
						}
					}
					UnfinishFLow = 0;
					break;
				}

				// δ��ɵ�ҵ�������·��ʣ������� ��ʱ���������ҵ��
				else if (UnfinishFLow > MinFlowOnRoute) {
					file_io.filewrite2(OutFileName, "δ��ɵ�ҵ�������·��ʣ������� ��Ҫ�´�ѭ��");
					for (Link LinkOnRoute : newRoute.getLinklist()) {// �ı�ÿ��������·�ϵ�ʣ������
						FlowUseOnLink fuo =new FlowUseOnLink(LinkOnRoute, UnfinishFLow);
						fuoList.add(fuo);
						LinkOnRoute.setRestcapacity(LinkOnRoute.getRestcapacity() - MinFlowOnRoute);
						file_io.filewrite2(OutFileName, "��·"+LinkOnRoute.getName()+"��ʣ������Ϊ��"+LinkOnRoute.getRestcapacity());
						if(LinkOnRoute.getPhysicallink()!=null){
							for (Link phlink : LinkOnRoute.getPhysicallink()) {// ��¼�����߹���������·��Ӧ��������·
								totallink.add(phlink);
							}
						}
					}
					UnfinishFLow = UnfinishFLow - MinFlowOnRoute;
				}

				// �޸�������������·��ʣ������ʱ�۲��Ƿ���������·��ʣ������Ϊ0 ɾ����������·
				file_io.filewrite2(OutFileName, "ѭ��һ�ν������۲�������·�仯����ɾ��ʣ������Ϊ0 ��������·");
				HashMap<String, Link> linklist2 = MixLayer.getLinklist();
				Iterator<String> linkitor2 = linklist2.keySet().iterator();
				while (linkitor2.hasNext()) {
					Link IPlink = (Link) (linklist2.get(linkitor2.next()));
					if(IPlink.getnature_IPorOP()==Constant.NATURE_BOUND) continue;
					if (IPlink.getRestcapacity() == 0) {
						DelNoFlowLinkToReco.add(IPlink); // ɾ������Ϊ0 ��������·
						file_io.filewrite2(OutFileName, "ɾ��ʣ������Ϊ0��������·��" + IPlink.getName());
					}
				}
				for (Link link : DelNoFlowLinkToReco) {
					MixLayer.removeLink(link); // �����IP��·ɾ����������·�ɲ��ɹ�ʱ��Ҫ�ָ�
					AllDelNoFlowLinkToReco.add(link);
				}
			}
		}

		// ����ѭ�� �ָ���ɾ������·
		if (AllDelNoFlowLinkToReco.size() != 0 && AllDelNoFlowLinkToReco != null) {// ��ѭ������������·��ɾ��
			for (Link nowlink : AllDelNoFlowLinkToReco) {
				file_io.filewrite2(OutFileName, "�ָ���������·Ϊ��" + nowlink.getName());
				MixLayer.addLink(nowlink);
			}
			AllDelNoFlowLinkToReco.clear();
		}
		if(!ProFlag){
			for(Link link:CannotUseLink){//�ָ�OP�Լ�������·
				MixLayer.addLink(link);
			}
		}
		return UnfinishFLow;
	}
}
