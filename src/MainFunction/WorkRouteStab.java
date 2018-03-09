package MainFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import demand.Request;
import general.Constant;
import general.file_out_put;
import graphalgorithms.RouteSearching;
import graphalgorithms.SearchConstraint;
import network.Layer;
import network.Link;
import network.Node;
import network.NodePair;
import network.VirtualLink;
import subgraph.LinearRoute;

public class WorkRouteStab {
	String OutFileName =MainOfAulixiaryRegenetor.OutFileName;
	public void WorkRouteStab(NodePair nodepair, Layer MixLayer,ArrayList<FlowUseOnLink> flowuseonlink,ArrayList<SlotUseOnWorkPhyLink> rowList,
			ArrayList<WorkandProtectRoute> wprlist,ParameterTransfer ptoftransp) throws IOException {
		//��Ҫ�ָ�����Ϊ��������· ������������·��mixGrooming��
		file_out_put file_io=new file_out_put();
		RouteSearching Dijkstra = new RouteSearching();
		
		// ɾ��ʣ������Ϊ0��������· ����Ҫ�����ָ� ֻ������ɾ������·��IP ���⣩����Ҫ�ָ� ��������Ҫ�ָ�
		////ɾ������Ϊ������������·����Ҫ�ָ���
		ArrayList<Link> DelNoFlowLink = new ArrayList<>();
		ArrayList<Link> DelProLink = new ArrayList<>();
		HashMap<String, Link> linklist = MixLayer.getLinklist();
		Iterator<String> linkitor = linklist.keySet().iterator();
		while (linkitor.hasNext()) {
			Link linkInMixlayer = (Link) (linklist.get(linkitor.next()));
			if (linkInMixlayer.getnature_IPorOP() == Constant.NATURE_IP) {// ��·����Ϊ����IP��·
				DelNoFlowLink.clear();
				if (linkInMixlayer.getRestcapacity() == 0) {//ɾ��ʣ������Ϊ0��������·������ָ���
					DelNoFlowLink.add(linkInMixlayer);
					continue;
				}
				if(linkInMixlayer.getRestcapacity() == Constant.NATURE_PRO){//ɾ������Ϊ������������·����Ҫ�ָ���
					DelProLink.add(linkInMixlayer);
				}
			}
		}
		for (Link delLink1 : DelNoFlowLink) {
			MixLayer.removeLink(delLink1);// to check
			file_io.filewrite2(OutFileName, "ɾ��û��ʣ��������IP����·Ϊ��" + delLink1.getName());
		}
		for (Link delLink2 : DelProLink) {
			MixLayer.removeLink(delLink2);// to check
			file_io.filewrite2(OutFileName, "ɾ������Ϊ������IP����·Ϊ��" + delLink2.getName());
		}
		
		//Ѱ�ҽ���IP��·��·�� ���ҿ���flowsplitting
		FlowSplitting fsl=new FlowSplitting();
		ArrayList<Link> totallink=new ArrayList<>();
		double UnfishFlow=fsl.flowsplitting(MixLayer, nodepair, flowuseonlink, totallink);
		
		//��δ��ɵ���������0ʱ ��ʱ����mix grooming ��������� ��ѡ���ڹ���½�
		if(UnfishFlow!=0){
			file_io.filewrite2(OutFileName,"��������··�ɽ�����ʣ������ "+UnfishFlow );
			//mixGrooming
			MixGrooming mg=new MixGrooming();
			mg.MixGrooming(nodepair,MixLayer,UnfishFlow,flowuseonlink,rowList, ptoftransp);
			
			
		}
		if(UnfishFlow==0){
			WorkandProtectRoute wpr=new WorkandProtectRoute(nodepair);
			Request re=new Request(nodepair);
			wpr.setrequest(re);
			wpr.setworklinklist(totallink);
			wprlist.add(wpr);
			file_io.filewrite2(OutFileName,"����·��·�ɳɹ�");
		}
		
		


	}

}
