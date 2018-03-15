package MainFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import demand.Request;
import general.Constant;
import general.file_out_put;
import network.Layer;
import network.Link;
import network.NodePair;

public class WorkRouteStab {
	String OutFileName =MainOfAulixiaryRegenetor.OutFileName;
	public void WorkRouteStab(NodePair nodepair, Layer MixLayer,ArrayList<WorkandProtectRoute> wprlist,ParameterTransfer ptoftransp,
			 float threshold) throws IOException {
		//��Ҫ�ָ�����Ϊ��������· ������������·��mixGrooming��
		file_out_put file_io=new file_out_put();
		boolean routeFlag=false;
		ArrayList<Double> RegLengthList = new ArrayList<>();
		// ɾ��ʣ������Ϊ0��������· ����Ҫ�����ָ� ֻ������ɾ������·��IP ���⣩����Ҫ�ָ� ��������Ҫ�ָ�
		////ɾ������Ϊ������������·����Ҫ�ָ���
		ArrayList<Link> DelNoFlowLink = new ArrayList<>();
		ArrayList<Link> DelProLink = new ArrayList<>();
		DelNoFlowLink.clear();
		HashMap<String, Link> linklist = MixLayer.getLinklist();
		Iterator<String> linkitor = linklist.keySet().iterator();
		while (linkitor.hasNext()) {
			Link linkInMixlayer = (Link) (linklist.get(linkitor.next()));
			if(linkInMixlayer.getnature_IPorOP()==Constant.NATURE_BOUND) continue;
			if (linkInMixlayer.getnature_IPorOP() == Constant.NATURE_IP) {// ��·����Ϊ����IP��·
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
			file_io.filewrite2(OutFileName, "ɾ��û��ʣ��������������·Ϊ��" + delLink1.getName());
		}
		for (Link delLink2 : DelProLink) {
			MixLayer.removeLink(delLink2);// to check
			file_io.filewrite2(OutFileName, "ɾ������Ϊ������������·Ϊ��" + delLink2.getName());
		}
		
		//Ѱ�ҽ���IP��·��·�� ���ҿ���flowsplitting
		FlowSplitting fsl=new FlowSplitting();
		ArrayList<FlowUseOnLink> fuoList=new ArrayList<>();
		ArrayList<Link> totallink=new ArrayList<>();
		double UnfishFlow=fsl.flowsplitting(MixLayer, nodepair, totallink,fuoList);
		
		//��δ��ɵ���������0ʱ ��ʱ����mix grooming ��������� ��ѡ���ڹ���½�
		if(UnfishFlow==0) routeFlag=true;
		if(!routeFlag){
			ptoftransp.setNumOfTransponder(ptoftransp.getNumOfTransponder()+2);
			file_io.filewrite2(OutFileName,"��������··�ɽ�����δ��ɵ������� "+UnfishFlow );
			//mixGrooming
			MixGrooming mg=new MixGrooming();
			routeFlag= mg.MixGrooming(nodepair,MixLayer,UnfishFlow, ptoftransp, RegLengthList, wprlist, threshold,totallink,fuoList);
			
			if(routeFlag){
				WorkandProtectRoute wpr=new WorkandProtectRoute(nodepair);
				Request re=new Request(nodepair);
				wpr.setrequest(re);
				wpr.setworklinklist(totallink);
				wprlist.add(wpr);
				wpr.setRegWorkLengthList(RegLengthList);//������ҪȨ��
				file_io.filewrite2(OutFileName,"����·��·�ɳɹ�");
			}
			if(!routeFlag){//Mixgrooming ���ɹ� ��ʱ�ڹ�㽨��������·
				PurePhyWorkRouteSta rpwrs=new PurePhyWorkRouteSta();
				routeFlag=rpwrs.purephyworkroutesta(nodepair, MixLayer, wprlist, threshold, ptoftransp, RegLengthList);
			}
		}
	for(Link recoLink:DelProLink){//�ָ�ǰ��ɾ��������Ϊ��������·
		MixLayer.addLink(recoLink);
	}
	}

}
