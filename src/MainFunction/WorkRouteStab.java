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
		//需要恢复性质为保护的链路 容量不够的链路（mixGrooming）
		file_out_put file_io=new file_out_put();
		boolean routeFlag=false;
		ArrayList<Double> RegLengthList = new ArrayList<>();
		// 删除剩余流量为0的虚拟链路 不需要在最后恢复 只有这里删除的链路（IP 虚拟）不需要恢复 其他均需要恢复
		////删除性质为保护的虚拟链路（需要恢复）
		ArrayList<Link> DelNoFlowLink = new ArrayList<>();
		ArrayList<Link> DelProLink = new ArrayList<>();
		DelNoFlowLink.clear();
		HashMap<String, Link> linklist = MixLayer.getLinklist();
		Iterator<String> linkitor = linklist.keySet().iterator();
		while (linkitor.hasNext()) {
			Link linkInMixlayer = (Link) (linklist.get(linkitor.next()));
			if(linkInMixlayer.getnature_IPorOP()==Constant.NATURE_BOUND) continue;
			if (linkInMixlayer.getnature_IPorOP() == Constant.NATURE_IP) {// 链路属性为虚拟IP链路
				if (linkInMixlayer.getRestcapacity() == 0) {//删除剩余流量为0的虚拟链路（无需恢复）
					DelNoFlowLink.add(linkInMixlayer);
					continue;
				}
				if(linkInMixlayer.getRestcapacity() == Constant.NATURE_PRO){//删除性质为保护的虚拟链路（需要恢复）
					DelProLink.add(linkInMixlayer);
				}
			}
		}
		for (Link delLink1 : DelNoFlowLink) {
			MixLayer.removeLink(delLink1);// to check
			file_io.filewrite2(OutFileName, "删除没有剩余流量的虚拟链路为：" + delLink1.getName());
		}
		for (Link delLink2 : DelProLink) {
			MixLayer.removeLink(delLink2);// to check
			file_io.filewrite2(OutFileName, "删除性质为保护的虚拟链路为：" + delLink2.getName());
		}
		
		//寻找仅有IP链路的路径 并且可以flowsplitting
		FlowSplitting fsl=new FlowSplitting();
		ArrayList<FlowUseOnLink> fuoList=new ArrayList<>();
		ArrayList<Link> totallink=new ArrayList<>();
		double UnfishFlow=fsl.flowsplitting(MixLayer, nodepair, totallink,fuoList);
		
		//当未完成的流量大于0时 此时尝试mix grooming 如果还不行 则选择在光层新建
		if(UnfishFlow==0) routeFlag=true;
		if(!routeFlag){
			ptoftransp.setNumOfTransponder(ptoftransp.getNumOfTransponder()+2);
			file_io.filewrite2(OutFileName,"纯虚拟链路路由结束，未完成的流量： "+UnfishFlow );
			//mixGrooming
			MixGrooming mg=new MixGrooming();
			routeFlag= mg.MixGrooming(nodepair,MixLayer,UnfishFlow, ptoftransp, RegLengthList, wprlist, threshold,totallink,fuoList);
			
			if(routeFlag){
				WorkandProtectRoute wpr=new WorkandProtectRoute(nodepair);
				Request re=new Request(nodepair);
				wpr.setrequest(re);
				wpr.setworklinklist(totallink);
				wprlist.add(wpr);
				wpr.setRegWorkLengthList(RegLengthList);//这里需要权衡
				file_io.filewrite2(OutFileName,"工作路径路由成功");
			}
			if(!routeFlag){//Mixgrooming 不成功 此时在光层建立物理链路
				PurePhyWorkRouteSta rpwrs=new PurePhyWorkRouteSta();
				routeFlag=rpwrs.purephyworkroutesta(nodepair, MixLayer, wprlist, threshold, ptoftransp, RegLengthList);
			}
		}
	for(Link recoLink:DelProLink){//恢复前面删除的属性为保护的链路
		MixLayer.addLink(recoLink);
	}
	}

}
