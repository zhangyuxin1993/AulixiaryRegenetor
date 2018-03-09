package MainFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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

	public double flowsplitting(Layer MixLayer, NodePair nodepair, ArrayList<FlowUseOnLink> flowuseonlink,
			ArrayList<Link> totallink) throws IOException {
		//本方法中将节点对之间的业务尽可能的分流在虚拟路径上面
//		删除的虚拟链路已经全部恢复 但是没有恢复用掉的流量
		
		RouteSearching Dijkstra = new RouteSearching();
		file_out_put file_io = new file_out_put();

		double UnfinishFLow = nodepair.getTrafficdemand();
		Node srcnode = nodepair.getSrcNode();
		Node desnode = nodepair.getDesNode();
		file_io.filewrite2(OutFileName, "开始grooming");
		HashMap<String, Link> linklisttest = MixLayer.getLinklist();
		Iterator<String> linkitortest = linklisttest.keySet().iterator();
		while (linkitortest.hasNext()) {
			Link Mlink = (Link) (linklisttest.get(linkitortest.next()));
			file_io.filewrite2(OutFileName, "虚拟链路为：" + Mlink.getName() + " 上剩余的流量为：" + Mlink.getRestcapacity());
		}

		ArrayList<Link> DelNoFlowLinkToReco = new ArrayList<>();// 每次循环要删除的虚拟链路
		ArrayList<Link> AllDelNoFlowLinkToReco = new ArrayList<>();// 一共需要删除的虚拟链路
		while (true) {
			LinearRoute newRoute = new LinearRoute(null, 0, null);
			SearchConstraint sc = new SearchConstraint(100);// 将路径的最大长度限制为100
															// 设置最短的物理路径要大于100
															// 使得找到的路径全是虚拟链路
			Dijkstra.Dijkstras(srcnode, desnode, MixLayer, newRoute, sc);
			if (newRoute.getLinklist().size() == 0) {// 说明源结点之间没有路由
				file_io.filewrite2(OutFileName, "虚拟链路无法路由");
				break;
			} else {
				file_io.filewrite2(OutFileName, " ");
				DelNoFlowLinkToReco.clear();

				// 首先查找路由上所有虚拟链路的最小剩余流量
				file_io.filewrite2(OutFileName, "循环中：找到虚拟链路路由");
				newRoute.OutputRoute_node(newRoute, OutFileName);

				// 找出这段路由上的最小流量
				double MinFlowOnRoute = 10000;
				for (Link LinkOnRoute : newRoute.getLinklist()) {
					if (LinkOnRoute.getRestcapacity() < MinFlowOnRoute) {
						MinFlowOnRoute = LinkOnRoute.getRestcapacity();
					}
				}

				// 找到该路由上的最小流量后分两种情况讨论
				if (UnfinishFLow <= MinFlowOnRoute) {// 未完成的业务小于链路上剩余的流量此时可以完成业务
					file_io.filewrite2(OutFileName, "未完成的业务小于链路上剩余的流量 此时可以完成业务");
					for (Link LinkOnRoute : newRoute.getLinklist()) {// 改变每段虚拟链路上的剩余流量
						boolean findFlag = false;

						for (FlowUseOnLink fuo : flowuseonlink) {
							if (fuo.getLink().equals(LinkOnRoute)) {// 在集合中已经存在该虚拟链路
								fuo.setFlowUseOnLink(fuo.getFlowUseOnLink() + UnfinishFLow);
								file_io.filewrite2(OutFileName, "集合中已存在该虚拟链路" + fuo.getLink().getNodeA().getName() + "-"
										+ fuo.getLink().getNodeB().getName() + " 虚拟链路使用的流量" + fuo.getFlowUseOnLink());
								findFlag = true;
								break;
							}
						}
						if (!findFlag) {// 在原来的集合中没有该虚拟链路
							FlowUseOnLink fuo = new FlowUseOnLink(LinkOnRoute, UnfinishFLow);// debug这里如果vlink一致的情况下会不会多次加入
							flowuseonlink.add(fuo);
							file_io.filewrite2(OutFileName, "集合中不存在该虚拟链路" + fuo.getLink().getNodeA().getName() + "-"
									+ fuo.getLink().getNodeB().getName() + " 使用的流量为 " + fuo.getFlowUseOnLink());
						}
						LinkOnRoute.setRestcapacity(LinkOnRoute.getRestcapacity() - UnfinishFLow);
						for (Link phlink : LinkOnRoute.getPhysicallink()) {// 记录工作走过的虚拟链路对应的物理链路
							totallink.add(phlink);
						}
					}
					UnfinishFLow = 0;
					break;
				}

				// 未完成的业务大于链路上剩余的流量 此时不可以完成业务
				else if (UnfinishFLow > MinFlowOnRoute) {
					file_io.filewrite2(OutFileName, "未完成的业务大于链路上剩余的流量 需要下次循环");

					for (Link LinkOnRoute : newRoute.getLinklist()) {// 改变每段虚拟链路上的剩余流量
						boolean findFlag = false;

						for (FlowUseOnLink fuo : flowuseonlink) {
							if (fuo.getLink().equals(LinkOnRoute)) {// 在集合中已经存在该虚拟链路
								fuo.setFlowUseOnLink(fuo.getFlowUseOnLink() + MinFlowOnRoute);
								file_io.filewrite2(OutFileName, "集合中已存在该虚拟链路" + fuo.getLink().getNodeA().getName() + "-"
										+ fuo.getLink().getNodeB().getName() + " 虚拟链路使用的流量" + fuo.getFlowUseOnLink());
								findFlag = true;
								break;
							}
						}
						if (!findFlag) {// 在原来的集合中没有该虚拟链路
							FlowUseOnLink fuo = new FlowUseOnLink(LinkOnRoute, MinFlowOnRoute);// debug这里如果vlink一致的情况下会不会多次加入
							flowuseonlink.add(fuo);
							file_io.filewrite2(OutFileName, "集合中不存在该虚拟链路" + fuo.getLink().getNodeA().getName() + "-"
									+ fuo.getLink().getNodeB().getName() + " 使用的流量为 " + fuo.getFlowUseOnLink());
						}
						LinkOnRoute.setRestcapacity(LinkOnRoute.getRestcapacity() - MinFlowOnRoute);
						for (Link phlink : LinkOnRoute.getPhysicallink()) {// 记录工作走过的虚拟链路对应的物理链路
							totallink.add(phlink);
						}
					}
					UnfinishFLow = UnfinishFLow - MinFlowOnRoute;
				}

				// 修改完所有虚拟链路的剩余流量时观察是否有虚拟链路的剩余流量为0 删除该虚拟链路
				file_io.filewrite2(OutFileName, "循环一次结束，观察虚拟链路变化，并删除剩余流量为0 的虚拟链路");
				HashMap<String, Link> linklist2 = MixLayer.getLinklist();
				Iterator<String> linkitor2 = linklist2.keySet().iterator();
				while (linkitor2.hasNext()) {
					Link IPlink = (Link) (linklist2.get(linkitor2.next()));
					if (IPlink.getRestcapacity() == 0) {
						DelNoFlowLinkToReco.add(IPlink); // 删除流量为0 的虚拟链路
						file_io.filewrite2(OutFileName, "IP链路：" + IPlink.getName() + " 剩余流量为0的虚拟链路为："
								+ IPlink.getNodeA().getName() + "-" + IPlink.getNodeB().getName());
					}
				}

				for (Link link : DelNoFlowLinkToReco) {
					file_io.filewrite2(OutFileName, "删除的虚拟层链路为：" + link.getName());
					MixLayer.removeLink(link); // 这里的IP链路删除如果在最后路由不成功时需要恢复
					AllDelNoFlowLinkToReco.add(link);
				}
			}
		}

		// 结束循环 恢复被删除的链路
		if (AllDelNoFlowLinkToReco.size() != 0 && AllDelNoFlowLinkToReco != null) {// 再循环过程中有链路被删除
			for (Link nowlink : AllDelNoFlowLinkToReco) {
				file_io.filewrite2(OutFileName, "恢复的虚拟链路为：" + nowlink.getName());
				MixLayer.addLink(nowlink);
			}
			AllDelNoFlowLinkToReco.clear();
		}

		return UnfinishFLow;
	}

}
