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
import network.Node;
import network.NodePair;
import resource.ResourceOnLink;
import subgraph.LinearRoute;

public class RegeneratorPlace {
	public int newFS = 0;
	static int totalregNum = 0;
	String OutFileName = MainOfAulixiaryRegenetor.OutFileName;

	public boolean regeneratorplace(double IPflow, double routelength, Boolean LinkOrRoute, LinearRoute newRoute,
			ArrayList<Link> linklistOfRoute, Layer MixLayer, ArrayList<WorkandProtectRoute> wprlist, NodePair nodepair,
			ArrayList<Double> RegLengthList, float threshold, ParameterTransfer ptOftransp) throws IOException {
		// 第二种方法先判断一条路径最少使用的再生器的个数 然后穷尽所有的情况来选择再生器 放置的位置
		// 这里的finalRoute已经换为两个版本 A表示输入的是route 有两个参数 B表示输入的链路集合 有三个参数 多一个常数参数
		file_out_put file_io = new file_out_put();
		int minRegNum = (int) Math.floor(routelength / 4000);// 最少的再生器的个数
		int internode = 0, size = 0;

		if (!LinkOrRoute) {
			// 此时输入的是route
			internode = newRoute.getNodelist().size() - 2;
			size = newRoute.getNodelist().size() - 1;
		} else {
			internode = linklistOfRoute.size() - 1;
			size = linklistOfRoute.size();
		}

		int FStotal = 0, n = 0;
		double length = 0;
		ArrayList<Link> linklist = new ArrayList<>();
		boolean partworkflag = false, RSAflag = false, regflag = false, success = false;
		ArrayList<RouteAndRegPlace> regplaceoption = new ArrayList<>();
		RouteAndRegPlace finalRoute = null;

		// 找到所有可以成功路由的路径 part1
		for (int s = minRegNum; s <= internode; s++) {
			if (partworkflag || regplaceoption.size() != 0)// 如果再生器个数较少的时候已经可以RSA那么就不需要增加再生器的个数
				break;
			Test nOfm = new Test(s, internode); // 在所有中间节点中随机选取m个点来放置再生器
			while (nOfm.hasNext()) {
				RSAflag = false;
				regflag = false;
				partworkflag = false;
				n = 0;
				length = 0;
				FStotal = 0;
				linklist.clear();
				int[] set = nOfm.next(); // 随机产生的再生器放置位置
				ArrayList<Float> RemainRatio = new ArrayList<>();// 记录每段链路上剩余的flow
				float NumRemainFlow = 0;

				for (int i = 0; i < set.length + 1; i++) {// RSA的次数比再生器的个数多1
															// 对某一段链路在某个set再生器下进行RSA
					if (!partworkflag && RSAflag)
						break;
					if (i < set.length) {
						file_io.filewrite2(OutFileName, "****************工作再生器的位置为：" + set[i]);
					} else {
						file_io.filewrite2(OutFileName, "************最后一个再生器与终结点之间的RSA ");
						regflag = true;
					}
					do {// 通过一个
						Link link_loop = new Link(null, 0, null, MixLayer, null, null, 0, 0);
						if (LinkOrRoute) {
							link_loop = linklistOfRoute.get(n);
						} else {
							link_loop = newRoute.getLinklist().get(n);
						}
						// file_io.filewrite2(OutFileName, "n="+n+" size="+
						// size);
						if (link_loop.getnature_IPorOP() == Constant.NATURE_BOUND)
							continue;
						file_io.filewrite2(OutFileName, link_loop.getName());
						length = length + link_loop.getLength();
						linklist.add(link_loop);
						n = n + 1;
						if (!regflag) {// 未到达最后一段路径的RSA
							if (n == set[i]) {
								ParameterTransfer pt = new ParameterTransfer();
								partworkflag = vertify(IPflow, length, linklist, MixLayer, wprlist, nodepair, pt);//
								RemainRatio.add(pt.getRemainFlowRatio());
								NumRemainFlow = NumRemainFlow + pt.getNumremainFlow();
								FStotal = FStotal + newFS;
								length = 0;
								RSAflag = true;
								linklist.clear();
								break;
							}
						}

						if (n == size) {// 最后一段路由
							ParameterTransfer pt = new ParameterTransfer();
							partworkflag = vertify(IPflow, length, linklist, MixLayer, wprlist, nodepair, pt);// 此时在n点放置再生器
							RemainRatio.add(pt.getRemainFlowRatio());
							NumRemainFlow = NumRemainFlow + pt.getNumremainFlow();
							FStotal = FStotal + newFS;
						}
						if (!partworkflag && RSAflag)
							break;
					} while (n < size);
					// 如果路由成功则保存该路由对于再生器的放置
				}
				if (partworkflag) {
					if (LinkOrRoute) {// 此处输入为linklist
						RouteAndRegPlace rarp = new RouteAndRegPlace(linklistOfRoute, 0, 0);
						rarp.setnewFSnum(FStotal);
						ArrayList<Integer> setarray = new ArrayList<>();
						ArrayList<Integer> IPRegarray = new ArrayList<>();

						for (int k = 0; k < set.length; k++) {
							setarray.add(set[k]);
							file_io.filewrite2(OutFileName, "k=" + k + " re size=" + RemainRatio.size());
							if (RemainRatio.get(k) >= threshold || RemainRatio.get(k + 1) >= threshold) {// 只要再生器前面或者后面有一段未充分使用则放置IP再生器
								IPRegarray.add(set[k]);// 存储IP再生器放置节点
							}
						}
						rarp.setIPRegnode(IPRegarray);
						rarp.setregnode(setarray);
						rarp.setregnum(setarray.size());
						rarp.setNumRemainFlow(NumRemainFlow);
						regplaceoption.add(rarp);
						file_io.filewrite2(OutFileName, "该路径成功RSA, 已成功RSA的条数为：" + regplaceoption.size());
					}
					if (!LinkOrRoute) {
						RouteAndRegPlace rarp = new RouteAndRegPlace(newRoute, 0);
						rarp.setnewFSnum(FStotal);
						ArrayList<Integer> setarray = new ArrayList<>();
						ArrayList<Integer> IPRegarray = new ArrayList<>();

						for (int k = 0; k < set.length; k++) {
							setarray.add(set[k]);
							if (RemainRatio.get(k) >= threshold || RemainRatio.get(k + 1) >= threshold) {// 只要再生器前面或者后面有一段未充分使用则放置IP再生器
								IPRegarray.add(set[k]);// 存储IP再生器放置节点
							}
						}
						rarp.setIPRegnode(IPRegarray);
						rarp.setregnode(setarray);
						rarp.setregnum(setarray.size());
						rarp.setNumRemainFlow(NumRemainFlow);
						regplaceoption.add(rarp);
						file_io.filewrite2(OutFileName, "该路径成功RSA, 已成功RSA的条数为：" + regplaceoption.size());
					}
				}
			}
		} // part1 finish
		/*
		 * debug print出每个备选路由上的IP路由器个数 剩余的容量 以及使用的FS数
		 */

		for (RouteAndRegPlace DebugRegRoute : regplaceoption) {
			file_io.filewrite2(OutFileName, " ");
			file_io.filewrite_without(OutFileName, "再生器放置位置： ");
			for (int Reg : DebugRegRoute.getregnode()) {
				file_io.filewrite_without(OutFileName, Reg + "  ");
			}
			file_io.filewrite2(OutFileName, "  ");
			if (DebugRegRoute.getIPRegnode().size() != 0) {
				// System.out.print("IP再生器放置位置 ： ");
				file_io.filewrite_without(OutFileName, "IP再生器放置位置 ： ");
				for (int IPReg : DebugRegRoute.getIPRegnode()) {
					file_io.filewrite_without(OutFileName, IPReg + "  ");
				}
			} else {
				file_io.filewrite2(OutFileName, "再生器均为纯OEO再生");
			}

			file_io.filewrite2(OutFileName, "剩余的流量： " + DebugRegRoute.getNumRemainFlow());
			file_io.filewrite2(OutFileName, "使用的newFS个数： " + DebugRegRoute.getnewFSnum());
		}

		// 以下在备选路径中通过3层选出最终路径
		if (regplaceoption.size() != 0) {
			success = true;
			ArrayList<RouteAndRegPlace> RemoveRoute = new ArrayList<>();
			// 确定final route
			// 第一层选择 选择IP再生器少的路由
			if (regplaceoption.size() == 1) {
				finalRoute = regplaceoption.get(0);
			} else {
				for (int standard = 0; standard < regplaceoption.size() - 1; standard++) {
					RouteAndRegPlace StandardRoute = regplaceoption.get(standard);
					if (RemoveRoute.contains(StandardRoute))
						continue;
					// System.out.print("第一层筛选 标准路由的IP再生器个数为 ：");
					// System.out.println(StandardRoute.getIPRegnode().size());

					for (int k = standard + 1; k < regplaceoption.size(); k++) {
						RouteAndRegPlace CompareRoute = regplaceoption.get(k);
						if (RemoveRoute.contains(CompareRoute))
							continue;
						// System.out.print("第一层筛选 比较路由的IP再生器个数为：");
						// System.out.println(CompareRoute.getIPRegnode().size());
						if (StandardRoute.getIPRegnode().size() > CompareRoute.getIPRegnode().size()) {
							RemoveRoute.add(StandardRoute);// 删去IP再生器多的路径
							break;
						}
						if (StandardRoute.getIPRegnode().size() < CompareRoute.getIPRegnode().size()) {
							RemoveRoute.add(CompareRoute);// 比较的没有标准好
						}
					}
				}
				for (RouteAndRegPlace rag : RemoveRoute) {
					regplaceoption.remove(rag);
				}
				RemoveRoute.clear();
				// 第二层选择 在路由器类型相同的情况下选择链路上剩余容量多的组合
				if (regplaceoption.size() == 1) {
					finalRoute = regplaceoption.get(0);
				} else {
					for (int standard = 0; standard < regplaceoption.size() - 1; standard++) {
						RouteAndRegPlace StandardRoute_2 = regplaceoption.get(standard);
						if (RemoveRoute.contains(StandardRoute_2))
							continue;
						// System.out.print("第二层筛选 标准路由剩余流量为：");
						// System.out.println(StandardRoute_2.getNumRemainFlow());

						for (int k = standard + 1; k < regplaceoption.size(); k++) {
							RouteAndRegPlace CompareRoute_2 = regplaceoption.get(k);
							if (RemoveRoute.contains(CompareRoute_2))
								continue;
							// System.out.print("第二层筛选 比较路由剩余流量为：");
							// System.out.println(CompareRoute_2.getNumRemainFlow());
							if (StandardRoute_2.getNumRemainFlow() < CompareRoute_2.getNumRemainFlow()) {
								RemoveRoute.add(StandardRoute_2);// 删去剩余流量少的路由
								break;
							}
							if (StandardRoute_2.getNumRemainFlow() > CompareRoute_2.getNumRemainFlow()) {
								RemoveRoute.add(CompareRoute_2);// 比较的没有标准好
							}
						}
					}
					for (RouteAndRegPlace rag : RemoveRoute) {
						regplaceoption.remove(rag);
					}
					RemoveRoute.clear();

					// 第三层选择 选择新使用FS较少的路由
					if (regplaceoption.size() == 1) {
						finalRoute = regplaceoption.get(0);
					} else {
						for (int standard = 0; standard < regplaceoption.size() - 1; standard++) {
							RouteAndRegPlace StandardRoute_3 = regplaceoption.get(standard);
							if (RemoveRoute.contains(StandardRoute_3))
								continue;
							// System.out.print("第三层筛选 标准路由使用的FS为：");
							// System.out.println(StandardRoute_3.getnewFSnum());

							for (int k = standard + 1; k < regplaceoption.size(); k++) {
								RouteAndRegPlace CompareRoute_3 = regplaceoption.get(k);
								if (RemoveRoute.contains(CompareRoute_3))
									continue;
								// System.out.print("第三层筛选 比较路由使用的FS为：");
								// System.out.println(CompareRoute_3.getnewFSnum());
								if (StandardRoute_3.getnewFSnum() > CompareRoute_3.getnewFSnum()) {
									RemoveRoute.add(StandardRoute_3);// 删去使用FS较多的路由
									break;
								}
								if (StandardRoute_3.getnewFSnum() < CompareRoute_3.getnewFSnum()) {
									RemoveRoute.add(CompareRoute_3);// 比较的没有标准好
								}
							}
						}
						for (RouteAndRegPlace rag : RemoveRoute) {
							regplaceoption.remove(rag);
						}
						RemoveRoute.clear();
					}
					finalRoute = regplaceoption.get(0);// 最终不管是否只剩一条链路
														// 都选择第一条作为最终链路

				}
			}
			nodepair.setFinalRoute(finalRoute);
			RegeneratorPlace regp = new RegeneratorPlace();
			if (LinkOrRoute)// true输入的是linklist
				regp.FinalRouteRSA(true, finalRoute, MixLayer, IPflow, RegLengthList, ptOftransp);
			else// false输入的是route
				regp.FinalRouteRSA(false, finalRoute, MixLayer, IPflow, RegLengthList, ptOftransp);
		}

		if (regplaceoption.size() == 0) {
			success = false;
		}

		if (success) {
			ptOftransp.setNumOfTransponder(ptOftransp.getNumOfTransponder()+2);
			// System.out.print("再生器放置成功并且RSA,放置的再生器个数为" +
			// finalRoute.getregnum() + " 位置为：");
			file_io.filewrite_without(OutFileName, "再生器放置成功并且RSA,放置的再生器个数为" + finalRoute.getregnum() + "  位置为：");

			for (int p = 0; p < finalRoute.getregnode().size(); p++) {
				// System.out.print(finalRoute.getregnode().get(p) + " ");
				file_io.filewrite_without(OutFileName, finalRoute.getregnode().get(p) + "     ");
			}
			// System.out.println();
			// if (finalRoute.getIPRegnode().size() != 0) {
			// System.out.print("IP再生器放置位置 ： ");
			// file_io.filewrite_without(OutFileName, "IP再生器放置位置 ： ");
			// for (int IPReg : finalRoute.getIPRegnode()) {
			// System.out.print(IPReg + " ");
			// file_io.filewrite_without(OutFileName, IPReg + " ");
			// }
			// }else{
			// System.out.println("再生器均为纯OEO再生");
			// file_io.filewrite2(OutFileName, "再生器均为纯OEO再生");
			// }//工作路径全部采用OEO再生器

			// System.out.println();
			totalregNum = totalregNum + finalRoute.getregnum();
			// System.out.println("*******工作路径一共需要再生器个数：" + totalregNum);
			file_io.filewrite2(OutFileName, "");
			file_io.filewrite2(OutFileName, "******工作路径一共需要再生器个数：" + totalregNum);
		} else {
			// System.out.println("工作路径无法成功放置再生器");
			file_io.filewrite2(OutFileName, "工作路径无法成功放置再生器");
		}
		return success;
		// */
		/*
		 * 第一部分是通过距离来决定在哪里放置再生器 //
		 */
		/*
		 * double length=0; int n=0; boolean
		 * brokeflag=false,opworkflag=false,partworkflag=false,RSAflag=false;
		 * ArrayList<Link> linklist=new ArrayList<Link>();
		 * 
		 * for(Link link:newRoute.getLinklist()){//判断route的每一段链路长度是否超过最长调制距离
		 * if(link.getLength()>4000) { System.out.println(link.getName()+
		 * " 的距离过长 业务堵塞"); brokeflag=true; break; } }
		 * 
		 * if(!brokeflag){ do{ Node nodeA=newRoute.getNodelist().get(n); Node
		 * nodeB=newRoute.getNodelist().get(n+1);
		 * System.out.println(nodeA.getName()+"-"+nodeB.getName());
		 * 
		 * Link link=oplayer.findLink(nodeA, nodeB);
		 * length=length+link.getLength(); if(length<=4000) { n=n+1;
		 * linklist.add(link); if(n==newRoute.getNodelist().size()-1)
		 * partworkflag=modifylinkcapacity(IPflow,length, linklist,
		 * oplayer,ipLayer);//为目的节点前的剩余链路进行RSA totalregNum++; } if(length>4000)
		 * { length=length-link.getLength();
		 * partworkflag=modifylinkcapacity(IPflow,length, linklist,
		 * oplayer,ipLayer);//此时在n点放置再生器 totalregNum++; length=0; RSAflag=true;
		 * linklist.clear(); } if(!partworkflag&&RSAflag) break;
		 * }while(n!=newRoute.getNodelist().size()-1); }
		 * System.out.println("一共需要的再生器个数为："+totalregNum); if(partworkflag)
		 * opworkflag=true; return opworkflag;
		 */

	}

	public Boolean vertify(double IPflow, double routelength, ArrayList<Link> linklist, Layer MixLayer,
			ArrayList<WorkandProtectRoute> wprlist, NodePair nodepair, ParameterTransfer RemainRatio) {
		// 判断某一段transparent链路是否能够成功RSA 并且记录新使用的FS数量和该链路上的剩余容量

		double X = 1;
		int slotnum = 0;
		file_out_put file_io = new file_out_put();
		boolean opworkflag = false;
		if (routelength > 4000) {
			// System.out.println("链路过长无法RSA");
			file_io.filewrite2(OutFileName, "链路过长无法RSA");
		}
		if (routelength < 4000) {
			if (routelength > 2000 && routelength <= 4000) {
				X = 12.5;
			} else if (routelength > 1000 && routelength <= 2000) {
				X = 25.0;
			} else if (routelength > 500 && routelength <= 1000) {
				X = 37.5;
			} else if (routelength > 0 && routelength <= 500) {
				X = 50.0;
			}
			slotnum = (int) Math.ceil(IPflow / X);// 向上取整
			// System.out.println("该链路所需slot数： " + slotnum);
			if (slotnum < Constant.MinSlotinLightpath) {
				slotnum = Constant.MinSlotinLightpath;
			}
			newFS = slotnum * linklist.size();

			ArrayList<Integer> index_wave = new ArrayList<Integer>();
			MainOfAulixiaryRegenetor spa = new MainOfAulixiaryRegenetor();
			index_wave = spa.spectrumallocationOneRoute(false, null, linklist, slotnum);
			if (index_wave.size() == 0) {
				// System.out.println("路径堵塞 ，不分配频谱资源");
				file_io.filewrite2(OutFileName, "路径堵塞 ，不分配频谱资源");
			} else {
				RemainRatio.setRemainFlowRatio((float) ((slotnum * X - IPflow) / (slotnum * X)));
				RemainRatio.setNumremainFlow((float) (slotnum * X - IPflow));
				file_io.filewrite2(OutFileName,
						"建立通道的总容量 " + slotnum * X + "   业务容量 " + IPflow + "   剩余的容量比例 "
								+ RemainRatio.getRemainFlowRatio() + "   剩余的业务量：" + RemainRatio.getNumremainFlow()
								+ "  需要的FS数量：" + slotnum + "  FS起始：" + index_wave.get(0));
				opworkflag = true;
				// System.out.println("可以进行RSA ");
				file_io.filewrite2(OutFileName, "可以进行RSA");
			}
		}
		return opworkflag;
	}

	public void FinalRouteRSA(Boolean LinkOrRoute, RouteAndRegPlace finalRoute, Layer MixLayer, double IPflow,
			ArrayList<Double> RegLengthList, ParameterTransfer ptOftransp) throws IOException {
		// 这里需要将不同的再生器 构造不同的IP虚拟链路加入IP层
		// IP再生器 两端需要加入两段虚拟链路 oeo再生器只需要加入一段虚拟链路
		ArrayList<Link> phyLinklist = new ArrayList<>();
		ParameterTransfer pt = new ParameterTransfer();
		RegeneratorPlace rp = new RegeneratorPlace();
		int count = 0;
		double length2 = 0;
		boolean regflag2 = false;
		ArrayList<Link> linklist2 = new ArrayList<>();
		file_out_put file_io = new file_out_put();
		ArrayList<Double> ResFlowOnlinks = new ArrayList<Double>();

		if (!LinkOrRoute)
			pt.setStartNode(finalRoute.getRoute().getNodelist().get(0));// 首先设置该链路的起始节点

		// 分析出linklist的nodelist
		ArrayList<Node> nodelistInLinkRoute = new ArrayList<>();
		if (LinkOrRoute) {// 输入的是ArrayList
			ArrayList<Link> linkListInput = new ArrayList<>();
			linkListInput = finalRoute.getLinklistOnRoute();
			for (int n = 0; n < linkListInput.size() - 1; n++) {
				Link link0 = linkListInput.get(n);
				Link link1 = linkListInput.get(n + 1);
				if (link0.getNodeA().equals(link1.getNodeA().getName())
						|| link0.getNodeA().getName().equals(link1.getNodeB().getName())) {// 取出没有重复的节点
					nodelistInLinkRoute.add(link0.getNodeB());
					if (n == linkListInput.size() - 2) {
						Node NodeofLastlink = link0.getNodeA();
						nodelistInLinkRoute.add(NodeofLastlink);
						if (link1.getNodeA().equals(NodeofLastlink))
							nodelistInLinkRoute.add(link1.getNodeB());
						else if (link1.getNodeB().equals(NodeofLastlink))
							nodelistInLinkRoute.add(link1.getNodeA());
					}
				} else {
					nodelistInLinkRoute.add(link0.getNodeA());
					if (n == linkListInput.size() - 2) {
						Node NodeofLastlink = link0.getNodeB();
						nodelistInLinkRoute.add(NodeofLastlink);
						if (link1.getNodeA().equals(NodeofLastlink))
							nodelistInLinkRoute.add(link1.getNodeB());
						else if (link1.getNodeB().equals(NodeofLastlink))
							nodelistInLinkRoute.add(link1.getNodeA());
					}
				}
			}
			pt.setStartNode(nodelistInLinkRoute.get(0));
			// for(Node node:nodelistInLinkRoute){
			// file_io.filewrite(OutFileName, "linklist上的节点"+node.getName()+"
			// ");
			// }

		}

		pt.setMinRemainFlowRSA(10000);// 首先初始化
		file_io.filewrite2(OutFileName, "");
		int size = 0;
		if (LinkOrRoute)
			size = nodelistInLinkRoute.size() - 1;
		else
			size = finalRoute.getRoute().getNodelist().size() - 1;

		for (int i = 0; i < finalRoute.getregnum() + 1; i++) {
			if (i >= finalRoute.getregnum())
				regflag2 = true;
			do {
				Link link_loop = new Link(null, 0, null, MixLayer, null, null, 0, 0);
				if (LinkOrRoute)
					link_loop = finalRoute.getLinklistOnRoute().get(count);
				else {
					link_loop = finalRoute.getRoute().getLinklist().get(count);
				}
				file_io.filewrite2(OutFileName, "工作链路RSA：" + link_loop.getName());
				length2 = length2 + link_loop.getLength();
				linklist2.add(link_loop);
				count = count + 1;
				if (!regflag2) {// 未到达最后一段路径的RSA
					if (count == finalRoute.getregnode().get(i)) {// 首先该点放置了再生器
						if (!LinkOrRoute)// route
							pt.setEndNode(finalRoute.getRoute().getNodelist().get(count));// 设置终止节点
						else
							pt.setEndNode(nodelistInLinkRoute.get(count));// 设置终止节点
						if (count == 1) {// 此时为transponder的发出链路
							double costOfStart = rp.transpCostCal(length2);
							ptOftransp.setcost_of_tranp(ptOftransp.getcost_of_tranp() + costOfStart);
							file_io.filewrite2(OutFileName, "transponder起点cost" + costOfStart + "   此时transponder cost="
									+ ptOftransp.getcost_of_tranp());
						}
						// 该点放置了IP再生器
						if (finalRoute.getIPRegnode().contains(count)) {
							// 这里用count计算transponder的cost
							modifylinkcapacity(true, IPflow, length2, linklist2, MixLayer, ptOftransp, ResFlowOnlinks,
									phyLinklist, pt);
							file_io.filewrite2(OutFileName, "本次RSA长度为：" + length2);
							RegLengthList.add(length2);
							length2 = 0;
							linklist2.clear();
							break;
						}
						// 该点放置纯OEO再生器
						else {
							modifylinkcapacity(false, IPflow, length2, linklist2, MixLayer, ptOftransp, ResFlowOnlinks,
									phyLinklist, pt);
							RegLengthList.add(length2);
							file_io.filewrite2(OutFileName, "本次RSA长度为：" + length2);
							length2 = 0;
							linklist2.clear();
							break;
						}
					}
				}

				if (count == size) {// 最后一个再生器和终点之间的RSA
					double costOfEnd = rp.transpCostCal(length2);
					ptOftransp.setcost_of_tranp(ptOftransp.getcost_of_tranp() + costOfEnd);
					file_io.filewrite2(OutFileName,
							"transponder终点cost" + costOfEnd + "   此时transponder cost=" + ptOftransp.getcost_of_tranp());
					if (!LinkOrRoute)
						pt.setEndNode(finalRoute.getRoute().getNodelist().get(count));// 设置终止节点
					else
						pt.setEndNode(nodelistInLinkRoute.get(count));// 设置终止节点
					modifylinkcapacity(true, IPflow, length2, linklist2, MixLayer, ptOftransp, ResFlowOnlinks,
							phyLinklist, pt);// 此时在n点放置再生器
					RegLengthList.add(length2);
					file_io.filewrite2(OutFileName, "本次RSA长度为：" + length2);
					linklist2.clear();
				}
			} while (count < size);
		}
	}

	public boolean modifylinkcapacity(Boolean IPorOEO, double IPflow, double routelength, ArrayList<Link> linklist,
			Layer MixLayer, ParameterTransfer ptoftransp, ArrayList<Double> ResFlowOnlinks, ArrayList<Link> phyLinklist,
			ParameterTransfer pt) {// true表示IP再生器
		// false表示纯OEO再生器
		double X = 1;
		int slotnum = 0;
		boolean opworkflag = false;
		file_out_put file_io = new file_out_put();
		Node srcnode = new Node(null, 0, null, MixLayer, 0, 0);
		Node desnode = new Node(null, 0, null, MixLayer, 0, 0);
		double resflow = 0;
		if (routelength > 4000) {
			file_io.filewrite2(OutFileName, "链路过长无法RSA");
		}
		if (routelength < 4000) {
			if (routelength > 2000 && routelength <= 4000) {
				X = 12.5;
			} else if (routelength > 1000 && routelength <= 2000) {
				X = 25.0;
			} else if (routelength > 500 && routelength <= 1000) {
				X = 37.5;
			} else if (routelength > 0 && routelength <= 500) {
				X = 50.0;
			}
			slotnum = (int) Math.ceil(IPflow / X);// 向上取整
			if (slotnum < Constant.MinSlotinLightpath) {
				slotnum = Constant.MinSlotinLightpath;
			}
			float RemainFlow = (float) (slotnum * X - IPflow);
			resflow = slotnum * X - IPflow;
			ResFlowOnlinks.add(resflow);// 存储由OEO再生器衔接的链路上剩余的流量
			if (RemainFlow < pt.getMinRemainFlowRSA()) {// 若中间经过OEO再生器那么存储剩余较小的flow
				pt.setMinRemainFlowRSA(RemainFlow);
			}

			// 计算所需要的FS数 并且观察每段链路上可用的频谱窗
			file_io.filewrite2(OutFileName, "该链路所需slot数： " + slotnum);
			ArrayList<Integer> index_wave = new ArrayList<Integer>();
			MainOfAulixiaryRegenetor spa = new MainOfAulixiaryRegenetor();
			index_wave = spa.spectrumallocationOneRoute(false, null, linklist, slotnum);
			if (index_wave.size() == 0) {
				file_io.filewrite2(OutFileName, "路径堵塞 ，不分配频谱资源");
			} else {
				opworkflag = true;
				float length1 = 0;
				double cost = 0;
				file_io.filewrite_without(OutFileName, "光层分配频谱：");
				file_io.filewrite2(OutFileName, "FS起始值：" + index_wave.get(0) + "  长度" + slotnum);
				// 物理层的link 改变物理层link上面剩余的FS数
				for (Link link : linklist) {
					length1 = length1 + link.getLength();
					cost = cost + link.getCost();
					Request request = null;
					ResourceOnLink ro = new ResourceOnLink(request, link, index_wave.get(0), slotnum);
					link.setMaxslot(slotnum + link.getMaxslot());
					phyLinklist.add(link);// 记录建立的虚拟链路对应的物理链路
				}

				if (IPorOEO) {// true的时候表示放置的是IP再生器 需要在IP层建立链路
					// 改变起始节点 剩余容量
					// 首先寻找需要构建IP链路的起始节点和终止节点
					Node startnode = pt.getStartNode();
					Node endnode = pt.getEndNode();

					// 在IP层中寻找transparent链路的两端
					for (int num = 0; num < MixLayer.getNodelist().size() - 1; num++) {
						boolean srcflag = false, desflag = false;
						HashMap<String, Node> map = MixLayer.getNodelist();
						Iterator<String> iter = map.keySet().iterator();
						while (iter.hasNext()) {
							Node node = (Node) (map.get(iter.next()));
							if (node.getName().equals(startnode.getName())) {
								srcnode = node;
								srcflag = true;
							}
							if (node.getName().equals(endnode.getName())) {
								desnode = node;
								desflag = true;
							}
						}
						if (srcflag && desflag)
							break;
					}
					pt.setStartNode(desnode);
					if (srcnode.getIndex() > desnode.getIndex()) {
						Node internode = srcnode;
						srcnode = desnode;
						desnode = internode;
					}
					double minflow = 10000;
					for (double resflow2 : ResFlowOnlinks) {// 寻找不同链路上剩余流量最小的链路
						if (minflow > resflow2) {
							minflow = resflow2;
						}
					}
					ResFlowOnlinks.clear();

					int index = ptoftransp.getNumOfLink() + 1;
					ptoftransp.setNumOfLink(index);
					Node helpNode = new Node(null, index, null, MixLayer, 0, 0); // 这里将helpNode设置为中间辅助节点
					String index_inName = String.valueOf(index);
					helpNode.setName(srcnode.getName() + "(" + index_inName + ")");
					MixLayer.addNode(helpNode);

					length1 = length1 / 1000;
					cost = cost / 1000;
					String name = null;
					Link createlink = new Link(null, 0, null, null, null, null, 0, 0);
					if (desnode.getIndex() < helpNode.getIndex()) {
						// 确定添加的虚拟路径的名字
						name = desnode.getName() + "-" + helpNode.getName();
						createlink = new Link(name, index, null, MixLayer, desnode, helpNode, length1, cost);
					} else {
						name = helpNode.getName() + "-" + desnode.getName();
						createlink = new Link(name, index, null, MixLayer, helpNode, desnode, length1, cost);
					}

					createlink.setnature_IPorOP(Constant.NATURE_IP);
					createlink.setnature_WorkOrPro(Constant.NATURE_WORK);
					createlink.setPhysicallink(phyLinklist);
					createlink.setRestcapacity(minflow);
					phyLinklist.clear();
					MixLayer.addLink(createlink);

					String boundLink_name = null;
					Link boundlink = new Link(null, 0, null, null, null, null, 0, 0);
					if (srcnode.getIndex() < helpNode.getIndex()) {
						// 确定添加的虚拟路径的名字
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

					// 以上已经成功建立IP虚拟链路 要更改此次的终止节点为下次的起始节点 并且初始化链路上最小剩余容量
					pt.setMinRemainFlowRSA(10000);
				}
			}
		}
		return opworkflag;
	}

	public double transpCostCal(double routelength) {
		double costOftransp = 0;
		if (routelength > 2000 && routelength <= 4000) {
			costOftransp = Constant.Cost_IP_reg_BPSK;
		} else if (routelength > 1000 && routelength <= 2000) {
			costOftransp = Constant.Cost_IP_reg_QPSK;
		} else if (routelength > 500 && routelength <= 1000) {
			costOftransp = Constant.Cost_IP_reg_8QAM;
		} else if (routelength > 0 && routelength <= 500) {
			costOftransp = Constant.Cost_IP_reg_16QAM;
		}
		return costOftransp;
	}
}
