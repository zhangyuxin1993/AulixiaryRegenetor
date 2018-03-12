package MainFunction;

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

	public boolean MixGrooming(NodePair nodepair, Layer MixLayer, double UnfishFlow,ParameterTransfer ptoftransp,ArrayList<Double>RegLengthList,
			ArrayList<WorkandProtectRoute> wprlist,float threshold,ArrayList<Link> totallink,ArrayList<FlowUseOnLink> fuoList) {
		file_out_put file_io = new file_out_put();
		RouteSearching Dijkstra = new RouteSearching();
		boolean routeFlag=false;

		// 找到的路由中必须含有虚拟链路
		// 因为只能选取一条路由 mixgrooming 所以此时要删除容量不够的虚拟链路
		ArrayList<Link> DelLackCapLink = new ArrayList<>(); // 删除了虚拟链路容量小于未完成容量的链路
		HashMap<String, Link> linklist = MixLayer.getLinklist();
		Iterator<String> linkitor2 = linklist.keySet().iterator();
		while (linkitor2.hasNext()) {
			Link linkInMixlayer = (Link) (linklist.get(linkitor2.next()));
			if(linkInMixlayer.getnature_IPorOP()==Constant.NATURE_IP){
				if (linkInMixlayer.getRestcapacity() < UnfishFlow) {
					DelLackCapLink.add(linkInMixlayer);
				}
			}
		}
		for (Link delLink : DelLackCapLink) {
			MixLayer.removeLink(delLink);
		}
		ArrayList<LinearRoute> routeList = new ArrayList<>();
		Dijkstra.Kshortest(nodepair.getSrcNode(), nodepair.getDesNode(), MixLayer, 20, routeList);

		if (routeList.size() != 0 && routeList != null) {// 找到路由
			for (LinearRoute route : routeList) {
				boolean IPFlag = false;
				for (Link LinkOnRoute : route.getLinklist()) {
					if (LinkOnRoute.getnature_IPorOP() == Constant.NATURE_IP) {// 判断该路由链路中是否有虚拟链路
						IPFlag = true;
						break;
					}
				}
				if (IPFlag) {// 只有当路由中含有IP链路时才会进行分配
					// 寻找路由上所有的IP链路中流量最小的业务
					totallink.clear();
					ArrayList<Link> IPLinkOnRoute = new ArrayList<>();
					ArrayList<Link> OPLinkOnRoute = new ArrayList<>();
					for (Link LinkOnRoute : route.getLinklist()) {
						if (LinkOnRoute.getnature_IPorOP() == Constant.NATURE_IP){
							IPLinkOnRoute.add(LinkOnRoute);
							for(Link phyLink: LinkOnRoute.getPhysicallink())
								totallink.add(phyLink);
						}
						else
							OPLinkOnRoute.add(LinkOnRoute);
					}
					double ResCapMin = 1000;
					for (Link link : IPLinkOnRoute) {
						if (link.getRestcapacity() < ResCapMin)
							ResCapMin = link.getRestcapacity();
					}
					if (ResCapMin < UnfishFlow)
						continue;

					// IP路由上的剩余容量大于未完成流量 下面要给物理链路分配FS
					MixGrooming mg = new MixGrooming();
					routeFlag=mg.AssignFSforPhyLinkAndIPLinkStab(OPLinkOnRoute, UnfishFlow, ptoftransp,MixLayer, wprlist, nodepair, RegLengthList, threshold);
					
					if(routeFlag){//说明此时物理链路路由成功 则要改变虚拟链路上的容量
						for(Link link: OPLinkOnRoute)//路由成功时需要将物理链路对应的路由也加入totallink
							totallink.add(link);
						for(Link ModifyCapLink: IPLinkOnRoute){
							ModifyCapLink.setRestcapacity(ModifyCapLink.getRestcapacity()-UnfishFlow);
						}
						break;//找到一条成功的路由就跳出 first-fit
					}
				}
			}
		} else {
			System.out.println("MixGrooming没有找到路由");
		}
		for(Link recLink:DelLackCapLink){//恢复开始删去的链路
			MixLayer.addLink(recLink);
		}
		return routeFlag;
 
	}

	public boolean AssignFSforPhyLinkAndIPLinkStab(ArrayList<Link> OPLinkOnRoute, double UnfishFlow, ParameterTransfer ptoftransp, 
			Layer MixLayer,ArrayList<WorkandProtectRoute> wprlist,NodePair nodepair,ArrayList<Double>RegLengthList,float threshold) {
		boolean routeFlag = false;
		file_out_put file_io = new file_out_put();
		int routelength = 0, slotnum = 0,cost=0;
		double X = 1;
		for (Link link : OPLinkOnRoute) {
			routelength = routelength + link.getLength();
			cost=(int) (cost+link.getCost());
		}
		if (routelength <=4000) {// 找到的路径不需要再生器
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
			slotnum = (int) Math.ceil(UnfishFlow / X);// 向上取整
			ptoftransp.setcost_of_tranp(ptoftransp.getcost_of_tranp() + costOftransp * 2);
			file_io.filewrite2(OutFileName, "");
			file_io.filewrite2(OutFileName, "Mixgorrming 工作路径不需要再生器  cost of transponder" + costOftransp * 2
					+ "transponder cost=" + ptoftransp.getcost_of_tranp());

			if (slotnum < Constant.MinSlotinLightpath) {
				slotnum = Constant.MinSlotinLightpath;
			}
			file_io.filewrite2(OutFileName, "物理链路所需slot数： " + slotnum);
			ArrayList<Integer> index_wave = new ArrayList<Integer>();
			MainOfAulixiaryRegenetor spa = new MainOfAulixiaryRegenetor();
			index_wave = spa.spectrumallocationOneRoute(false, null, OPLinkOnRoute, slotnum);

			if (index_wave.size() == 0) {
				routeFlag=false;
				file_io.filewrite2(OutFileName, "Mixgrooming 物理路径堵塞 ，无法分配频谱资源");
			} else {
				routeFlag=true;
				file_io.filewrite2(OutFileName, "");
				file_io.filewrite2(OutFileName, "MixGrooming 物理路由路径FS：");
				file_io.filewrite2(OutFileName, "FS起始值：" + index_wave.get(0) + "  长度" + slotnum);

				int length1 = 0;
				// double cost1 = 0;
				for (Link link : OPLinkOnRoute) {// 记录物理路由链路上使用的FS
					length1 = length1 + link.getLength();
					Request request = null;
					ResourceOnLink ro = new ResourceOnLink(request, link, index_wave.get(0), slotnum);
					link.setMaxslot(slotnum + link.getMaxslot());
				} // 改变物理层上的链路FS分配 以便于下一次新建时分配slot

				// 寻找物理路由的起点和终点
				Link link0 = OPLinkOnRoute.get(0);
				Link link1 = OPLinkOnRoute.get(1);
				Link link3 = OPLinkOnRoute.get(OPLinkOnRoute.size() - 2);
				Link link4 = OPLinkOnRoute.get(OPLinkOnRoute.size() - 1);
				String srcnode_name = null;
				String desnode_name = null;
				if (link0.getNodeA().equals(link1.getNodeA()) || link0.getNodeA().equals(link1.getNodeB())) {
					srcnode_name = link0.getNodeB().getName();
				} else
					srcnode_name = link0.getNodeA().getName();

				if (link4.getNodeA().equals(link3.getNodeA()) || link4.getNodeA().equals(link3.getNodeB())) {
					desnode_name = link4.getNodeB().getName();
				} else
					desnode_name = link4.getNodeA().getName();

				String name = srcnode_name + desnode_name;
				int index = ptoftransp.getNumOfLink() + 1;
				ptoftransp.setNumOfLink(index);

				Node srcnode = MixLayer.getNodelist().get(srcnode_name);
				Node desnode = MixLayer.getNodelist().get(desnode_name);

				Link createlink = new Link(name, index, null, MixLayer, srcnode, desnode, length1, cost);
				createlink.setnature_IPorOP(Constant.NATURE_IP);
				createlink.setnature_WorkOrPro(Constant.NATURE_WORK);
				createlink.setFullcapacity(slotnum * X);
				createlink.setRestcapacity(createlink.getFullcapacity() - UnfishFlow);
				createlink.setPhysicallink(OPLinkOnRoute);
				MixLayer.addLink(createlink);
				ArrayList<Link> IPlinkStaInWork = new ArrayList<>();
				IPlinkStaInWork.add(createlink);
				ptoftransp.setIPlinkStaInWork(IPlinkStaInWork);// 保存工作时建立的虚拟链路
				file_io.filewrite2(OutFileName, "不放置再生器时 新建虚拟链路 " + createlink.getName() + "  index= " + createlink.getIndex());
			}

		}
		else if(routelength>4000){
			//此时需要放置再生器
			RegeneratorPlace rp=new RegeneratorPlace();
			routeFlag = rp.regeneratorplace(UnfishFlow, routelength, true, null, OPLinkOnRoute, MixLayer, wprlist, nodepair, RegLengthList, threshold, ptoftransp);
			
		}
		return routeFlag;
	}
}
