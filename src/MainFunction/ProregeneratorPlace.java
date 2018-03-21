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

public class ProregeneratorPlace {
	String OutFileName = MainOfAulixiaryRegenetor.OutFileName;
	static int totalregNum = 0;

	// 在RSAunderSet 里面控制阈值
	public boolean ProRegeneratorPlace(Boolean LinkOrRoute, NodePair nodepair, LinearRoute Route,
			ArrayList<Link> LinkOnRoute, ArrayList<WorkandProtectRoute> wprlist, double routelength, Layer MixLayer,
			double IPflow, Request request, float threshold, ParameterTransfer ptoftransp,ArrayList<FSshareOnlink> FSuseOnlink) throws IOException {
		WorkandProtectRoute nowdemand = new WorkandProtectRoute(null);
		ProregeneratorPlace prp = new ProregeneratorPlace();
		ProregeneratorPlace rgp2 = new ProregeneratorPlace();
		Test t = new Test();
		ArrayList<Integer> ShareReg = new ArrayList<>();
		ArrayList<Node> comnodelist = new ArrayList<>();
		ArrayList<Regenerator> sharereglist = new ArrayList<>();
		ArrayList<Regenerator> removereglist = new ArrayList<>();
		ArrayList<Regenerator> addreglist = new ArrayList<>();
		ArrayList<RouteAndRegPlace> regplaceoption = new ArrayList<>();
		ProregeneratorPlace rgp = new ProregeneratorPlace();
		file_out_put file_io = new file_out_put();

		// 如果输入的是linklist (Mixgrooming) 或者route（纯物理链路建立）
		ArrayList<Node> nodelistInLinkRoute = new ArrayList<>();
		if (LinkOrRoute) {// 输入的是ArrayList
			ArrayList<Link> linkListInput = new ArrayList<>();
			linkListInput = LinkOnRoute;
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
		}
		int size = 0;
		if (!LinkOrRoute)
			// 此时输入的是route
			size = Route.getNodelist().size() - 1;
		else
			size = LinkOnRoute.size();

		// part1 找到该保护链路上面已存在的共享再生器
		for (WorkandProtectRoute nowwpr : wprlist) {// 首先找本次nodepair对应的 wpr
			if (nowwpr.getdemand().equals(nodepair)) {
				nowdemand = nowwpr;
				break;
			}
		}

		for (WorkandProtectRoute wpr : wprlist) {// 在已存在的业务中 找出新业务上已存在的共享再生器
			int cross = t.linklistcompare(nowdemand.getworklinklist(), wpr.getworklinklist());
			if (cross == 0) {// 首先判断了这个新产生的再生器是否可以共享

				for (Regenerator newreg : wpr.getnewreglist()) {// 只看该链路上有没有新建的再生器
					Node node = newreg.getnode();
					if ((LinkOrRoute && nodelistInLinkRoute.contains(node))
							|| (!LinkOrRoute && Route.getNodelist().contains(node))) {// 输入link
						// 如果之前的业务在某一节点上已经放置了再生器
						// 判断该业务与新业务可否共享再生器（两个业务的工作链路对应的物理链路是否交叉）
						int already = 0, newregg = 0;
						boolean noshareFlag = false;
						for (WorkandProtectRoute comwpr : wprlist) {
							if (wpr.getdemand().equals(comwpr.getdemand()))
								continue;
							for (Regenerator haveshareReg : comwpr.getsharereglist()) {
								if (haveshareReg.equals(newreg)) {// 其他业务上曾经共享该再生器
									int cross_second = t.linklistcompare(nowdemand.getworklinklist(),
											comwpr.getworklinklist());
									if (cross_second == 1) {
										noshareFlag = true;
										break;
									}
								}
							}
						} // 以上判断之前业务某一节点的再生器可否在本次业务节点上共享

						if (!noshareFlag) {// 表示该节点上再生器在本业务上也可以共享
							/*
							 * 在以下修改 优先选择IP再生器 有多个IP再生器时选择保护路径多的再生器 OEO再生器同理
							 */
							int po = 0;
							if (!LinkOrRoute)
								po = t.nodeindexofroute(node, Route);// 保存新链路上可以共享的再生器的位置
							if (LinkOrRoute)
								po = prp.getIndexFromArr(nodelistInLinkRoute, node);

							if (po != 0 && po != size) {// 判断新链路上已存在的再生器是否在链路的两端
								/*
								 * comnodelist存储的是已经存在可共享再生器的节点 sharereglist
								 * 存储的是经判断可以共享的再生器 一下进行一个节点上多个共享再生器的筛选
								 */
								if (comnodelist.contains(node)) {// 说明该节点上已存在可共享的再生器
																	// 此时需要选择用哪个共享再生器
									for (Regenerator alreadyReg : sharereglist) {
										// sharereglist里面放置已经判断可以共享的再生器
										// sharelist里面每个节点上只有一个最优的共享再生器
										if (alreadyReg.getnode().equals(node)) {// 此时alreadyReg表示共享列表中已存在的reg

											if (alreadyReg.getNature() == 0 && newreg.getNature() == 1) {// 新来的再生器是IP再生器
																											// 原来的再生器是纯OEO再生
																											// 此时选择新到再生器
												removereglist.add(alreadyReg);
												addreglist.add(newreg);
											} else if (alreadyReg.getNature() == 1 && newreg.getNature() == 0) {// 新来的再生器是OEO再生器
																												// 原来的再生器是IP再生
																												// 此时选择原来的再生器
											} else {// 表示原来的再生器和新到的再生器的属性一致
													// 此时需要比较他们保护路径条数

												for (WorkandProtectRoute comwpr : wprlist) {// 一下比较哪个再生器使用的多
													if (comwpr.getRegeneratorlist().contains(alreadyReg)) {
														already++;
													}
													if (comwpr.getRegeneratorlist().contains(newreg)) {
														newregg++;
													}
												}
												if (already < newregg) {// 说明新增加的reg共享的保护链路比较多
													removereglist.add(alreadyReg);
													addreglist.add(newreg);
												}
												newregg = 0;
												already = 0;
												break;
											}
										}
									}
									for (Regenerator remoReg : removereglist) {
										sharereglist.remove(remoReg);
									}
									removereglist.clear();// 原来不需要吗？？？
									for (Regenerator addReg : addreglist) {
										if (!sharereglist.contains(addReg))
											sharereglist.add(addReg);
									}
									addreglist.clear();// 原来不需要吗？？？

								} else {// 新产生的再生器
									comnodelist.add(node);
									sharereglist.add(newreg);
								}
								// System.out.println("再生器的个数："+sharereglist.size());
								// for(Regenerator reg:sharereglist){
								// System.out.println(reg.getnode().getName());
								// }
								if (!ShareReg.contains(po))
									ShareReg.add(po); // 保存了新的业务上哪些节点上面有再生器
							}
						}
					}
				}
			}
		}
		// part1 finish 存储了所有该链路上可共享再生器的位置

		boolean success = false, passflag = false;
		int minRegNum = (int) Math.floor(routelength / 4000);
		int internode = 0;
		if (!LinkOrRoute) {
			// 此时输入的是route
			internode = Route.getNodelist().size() - 2;
		} else {
			internode = LinkOnRoute.size() - 1;
		}
		// debug
		file_io.filewrite2(OutFileName, "");
		file_io.filewrite2(OutFileName, "可共享再生器的个数：" + ShareReg.size() + "需要的最少再生器个数：" + minRegNum);

		for (Regenerator reg : sharereglist) {
			file_io.filewrite_without(OutFileName, "可共享再生器在 " + reg.getnode().getName() + "节点上,  ");
			if (reg.getNature() == 0) {
				file_io.filewrite2(OutFileName, "他是纯OEO再生器 ");
			}
			if (reg.getNature() == 1) {
				file_io.filewrite2(OutFileName, "他是IP再生器 ");
			}
		}

		/*
		 * // part2 当路由上共享再生器的个数小于所需再生器的最小个数时 给定set进行RSA 产生regplaceoption
		 * 如果可共享再生器的个数小于最少需要再生的个数时
		 */

		if (ShareReg.size() <= minRegNum) {
			for (int s = minRegNum; s <= internode; s++) {
				if (regplaceoption.size() != 0)
					break;
				Test nOfm = new Test(s, internode); // 在所有中间节点中随机选取m个点来放置再生器
				while (nOfm.hasNext()) {
					passflag = false;
					int[] set = nOfm.next(); // 随机产生的再生器放置位置
					for (int num : ShareReg) {
						for (int k = 0; k < set.length; k++) {
							if (num == set[k]) {
								break;
							}
							if (k == set.length - 1 && num != set[k]) {
								passflag = true;
							}
						}
						if (passflag)
							break;
					}
					if (passflag)
						continue;// 已有的共享再生器 已经内定所以所有产生的可能性中要包含这些再生器

					// 给定再生器节点之后进行RSA 产生option选项的路径
					rgp.RSAunderSet(LinkOrRoute, sharereglist, ShareReg, set, Route, LinkOnRoute, MixLayer, IPflow,
							regplaceoption, wprlist, nodepair, threshold,nodelistInLinkRoute);
				 
				}
			}
		}

		// part3 当路由上共享再生器的个数大于所需再生器的最小个数时 给定set进行RSA产生regplaceoption
		if (ShareReg.size() > minRegNum) {
			for (int s = minRegNum; s <= internode; s++) {
				if (regplaceoption.size() != 0)
					break;
				Test nOfm = new Test(s, internode); // 在所有中间节点中随机选取m个点来放置再生器
				while (nOfm.hasNext()) {
					passflag = false;
					int[] set = nOfm.next(); // 随机产生的再生器放置位置
					if (s <= ShareReg.size()) { // 此时再生器必须从可共享的再生器里面选择
						for (int p = 0; p < set.length; p++) {
							int p1 = set[p];
							if (!ShareReg.contains(p1)) {
								passflag = true;
								break;
							}
						}
						if (passflag)
							continue;
					}
					if (s > ShareReg.size()) {
						for (int num : ShareReg) {
							for (int k = 0; k < set.length; k++) {
								if (num == set[k]) {
									break;
								}
								if (k == set.length - 1 && num != set[k]) {
									passflag = true;
								}
							}
							if (passflag)
								break;
						}
						if (passflag)
							continue;
					} // 以上主要为了产生set
						// 给定再生器节点之后进行RSA
					rgp.RSAunderSet(false, sharereglist, ShareReg, set, Route, LinkOnRoute, MixLayer, IPflow,
							regplaceoption, wprlist, nodepair, threshold,nodelistInLinkRoute);
				}
			}
		}
		// debug 在选择备选路由之前先观察其各个性能指标
		if (regplaceoption.size() != 0) {
			for (RouteAndRegPlace DebugRegRoute : regplaceoption) {
				ArrayList<Integer> NewRegList = new ArrayList<>();
				file_io.filewrite2(OutFileName, " ");
				// System.out.println();
				// 先输出可共享再生器
				if (DebugRegRoute.getUsedShareReg() != null) {
					file_io.filewrite2(OutFileName, "可共享的再生器 ");
					for (Regenerator reg : DebugRegRoute.getUsedShareReg()) {
						if (reg.getNature() == 0) {
							file_io.filewrite2(OutFileName, reg.getnode().getName() + "  OEO再生器");
						}
						if (reg.getNature() == 1) {
							file_io.filewrite2(OutFileName, reg.getnode().getName() + "  IP再生器");
						}
					}
				}
				// 首先判断可否共享不可共享则为新建
				file_io.filewrite2(OutFileName, "新建再生器： ");
				for (int Reg : DebugRegRoute.getregnode()) {
					boolean share = false;
					Node NewRegNode =new Node(null, 0, null, null, 0, 0);
					if(LinkOrRoute)
						NewRegNode=nodelistInLinkRoute.get(Reg);
					else
					    NewRegNode = DebugRegRoute.getRoute().getNodelist().get(Reg);
					for (Regenerator ShareReg2 : DebugRegRoute.getUsedShareReg()) {
						if (ShareReg2.getnode().getName().equals(NewRegNode.getName())) {// 改点上的再生器可以共享无需新建
							share = true;
							break;
						}
					}
					if (!share) {
						NewRegList.add(Reg);
						if (DebugRegRoute.getIPRegnode().contains(Reg)) { // 新建的再生器是IP再生器
							file_io.filewrite_without(OutFileName, NewRegNode.getName() + "  IP再生器  ");
						} else {
							file_io.filewrite_without(OutFileName, NewRegNode.getName() + "  OEO再生器  ");
						}
					}
				}
				file_io.filewrite2(OutFileName, " ");
				file_io.filewrite2(OutFileName, "剩余的流量： " + DebugRegRoute.getNumRemainFlow());
				file_io.filewrite2(OutFileName, "使用的newFS个数： " + DebugRegRoute.getnewFSnum());
				DebugRegRoute.setNewRegList(NewRegList); // 统计每个备选路径中使用的新再生器个数
			}
		}

		// part4 对产生的备选链路进行筛选并且对选中链路建立IP链路
		RouteAndRegPlace finalRoute = new RouteAndRegPlace(null, 1);
		if (regplaceoption.size() > 0) {
			success = true;
			if (regplaceoption.size() > 1)
				finalRoute = rgp2.optionRouteSelect(regplaceoption, wprlist);// 在符合条件的几条路由中选取最佳的路由作为finaroute
			else
				finalRoute = regplaceoption.get(0);
			// 接下来对该最终链路进行RSA
			rgp2.FinalRouteRSA(LinkOrRoute,finalRoute, MixLayer,IPflow,ptoftransp,wprlist, nodepair,
					request, sharereglist,FSuseOnlink,ShareReg);
		 
			// 对于finalroute进行再生器节点存储！！
		}
		if (regplaceoption.size() == 0) {
			success = false;
		}
		// System.out.println();
		file_io.filewrite2(OutFileName, "");
		if (success) {
			file_io.filewrite_without(OutFileName, "保护路径再生器放置成功并且RSA,放置的再生器个数为");

		} else {
			file_io.filewrite2(OutFileName, "保护路径放置再生器不成功改路径被堵塞");
		}
		return success;
	}// 主函数结束

	public void RSAunderSet(Boolean LinkOrRoute, ArrayList<Regenerator> sharereglist, ArrayList<Integer> ShareReg,
			int[] set, LinearRoute newRoute, ArrayList<Link> linklistOfRoute, Layer MixLayer, double IPflow,
			ArrayList<RouteAndRegPlace> regplaceoption, ArrayList<WorkandProtectRoute> wprlist, NodePair nodepair,
			float threshold,ArrayList<Node> nodelistInLinkRoute ) {
		// 建立新的再生器 并且控制阈值
		boolean partworkflag = false, RSAflag = false, regflag = false;
		double length = 0;
		file_out_put file_io = new file_out_put();
		ArrayList<Link> linklist = new ArrayList<>();
		int FStotal = 0, n = 0;
		RegeneratorPlace rp = new RegeneratorPlace();
		ArrayList<Float> RemainRatio = new ArrayList<>();// 记录每段链路上剩余的flow
		float NumRemainFlow = 0;
		ArrayList<Regenerator> UseShareReg = new ArrayList<>();
		
		int size = 0;
		if (!LinkOrRoute)
			// 此时输入的是route
			size = newRoute.getNodelist().size() - 1;
		else
			size = linklistOfRoute.size();
		

		for (int i = 0; i < set.length + 1; i++) {// RSA的次数比再生器的个数多1
			if (!partworkflag && RSAflag)
				break;
			if (i < set.length) {
				// System.out.println("****************再生器的位置为：" + set[i]); //
				// set里面的数应该是节点的位置+1！
				file_io.filewrite2(OutFileName, "****************再生器的位置为：" + set[i]);
			} else {
				// System.out.println("************最后一个再生器与终结点之间的RSA ");
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
				if (link_loop.getnature_IPorOP() == Constant.NATURE_BOUND)
					continue;
				file_io.filewrite2(OutFileName, link_loop.getName());
				length = length + link_loop.getLength();
				linklist.add(link_loop);
				n = n + 1;
				if (!regflag) {// 未到达最后一段路径的RSA
					if (n == set[i]) {
						ParameterTransfer pt = new ParameterTransfer();
						partworkflag = rp.vertify(false, IPflow, length, linklist, MixLayer, wprlist, nodepair, pt);
						RemainRatio.add(pt.getRemainFlowRatio());
						NumRemainFlow = NumRemainFlow + pt.getNumremainFlow();
						FStotal = FStotal + nodepair.getSlotsnum();
						length = 0;
						RSAflag = true;
						linklist.clear();
						break;
					}
				}
				if (n == size) {
					ParameterTransfer pt = new ParameterTransfer();
					partworkflag = rp.vertify(false, IPflow, length, linklist, MixLayer, wprlist, nodepair, pt);
					NumRemainFlow = NumRemainFlow + pt.getNumremainFlow();
					RemainRatio.add(pt.getRemainFlowRatio());
					FStotal = FStotal + nodepair.getSlotsnum();
				}
				if (!partworkflag && RSAflag)// 如果之前的链路已经RSA失败 剩下的链路也没有RSA的必要
					break;
			} while (n != size);
			// 如果路由成功则保存该路由对于再生器的放置
		}
		if (partworkflag) {// 说明该set下可以RSA 此时需要建立新的再生器
			ArrayList<Integer> setarray = new ArrayList<>();
			ArrayList<Integer> IPRegarray = new ArrayList<>();
			for (int k = 0; k < set.length; k++) {
				setarray.add(set[k]);
				if (!ShareReg.contains(set[k])) {// 该再生器是新建的 需要判断其类型 如果是共享的
					// 那么他的类型已经确定 不需要判断
					if (RemainRatio.get(k) >= threshold || RemainRatio.get(k + 1) >= threshold) {// 只要再生器前面或者后面有一段未充分使用则放置IP再生器
						IPRegarray.add(set[k]);// 存储IP再生器放置节点
					}
				} else {// 使用了该可共享的再生器
					for (Regenerator UsedShareReg : sharereglist) {
						Node node=new Node(null, 0, null, null, 0, 0);
						if(!LinkOrRoute)
							node=newRoute.getNodelist().get(set[k]);
						else
							node=nodelistInLinkRoute.get(set[k]);
						if (UsedShareReg.getnode().getName().equals(node.getName())) {
							UseShareReg.add(UsedShareReg);// 保存使用了的共享再生器
							break;
						}
					}
				}
			}
			
			 if(LinkOrRoute){//link
				 RouteAndRegPlace rarp = new RouteAndRegPlace(linklistOfRoute, 1, 0);
				 rarp.setnewFSnum(FStotal);
				 // file_io.filewrite2(OutFileName, " ");
				 rarp.setUsedShareReg(UseShareReg); // 记录使用的共享再生器
				 rarp.setIPRegnode(IPRegarray);// 注意这里的IP再生器并不是全部的再生器 而是新建的IP再生器
				 rarp.setregnode(setarray);
				 rarp.setregnum(setarray.size());
				 rarp.setNumRemainFlow(NumRemainFlow);
				 regplaceoption.add(rarp);
			 }
			 if(!LinkOrRoute){//link
				 RouteAndRegPlace rarp = new RouteAndRegPlace(newRoute, 1);
				 rarp.setnewFSnum(FStotal);
				 // file_io.filewrite2(OutFileName, " ");
				 rarp.setUsedShareReg(UseShareReg); // 记录使用的共享再生器
				 rarp.setIPRegnode(IPRegarray);// 注意这里的IP再生器并不是全部的再生器 而是新建的IP再生器
				 rarp.setregnode(setarray);
				 rarp.setregnum(setarray.size());
				 rarp.setNumRemainFlow(NumRemainFlow);
				 regplaceoption.add(rarp);
			 }
			file_io.filewrite2(OutFileName, "该路径成功RSA, 已成功RSA的条数为：" + regplaceoption.size());
		}
	}

	public void FinalRouteRSA(Boolean LinkOrRoute, RouteAndRegPlace finalRoute, Layer MixLayer, double IPflow,
			 ParameterTransfer ptOftransp,ArrayList<WorkandProtectRoute> wprlist, 
			NodePair nodepair,Request request,ArrayList<Regenerator> sharereglist,ArrayList<FSshareOnlink> FSoneachLink,ArrayList<Integer> ShareReg) throws IOException {
		// 这里需要将不同的再生器 构造不同的IP虚拟链路加入IP层
		// IP再生器 两端需要加入两段虚拟链路 oeo再生器只需要加入一段虚拟链路
		ArrayList<Link> phyLinklist = new ArrayList<>();
		ParameterTransfer pt = new ParameterTransfer();
		RegeneratorPlace rp = new RegeneratorPlace();
		int count = 0;
		double length2 = 0,OEOLength=0;
		boolean regflag2 = false;
		ArrayList<Link> linklist2 = new ArrayList<>();
		file_out_put file_io = new file_out_put();
		ArrayList<Double> ResFlowOnlinks = new ArrayList<Double>();
		ArrayList<Double>  RegLengthList=new ArrayList<>();

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
				file_io.filewrite2(OutFileName, "");
				file_io.filewrite2(OutFileName, "工作链路RSA：" + link_loop.getName());
				length2 = length2 + link_loop.getLength();
				OEOLength=OEOLength+link_loop.getLength();
				linklist2.add(link_loop);
				count = count + 1;
				if (!regflag2) {// 未到达最后一段路径的RSA
					if (count == finalRoute.getregnode().get(i)) {// 首先该点放置了再生器
						if (!LinkOrRoute)// route
							pt.setEndNode(finalRoute.getRoute().getNodelist().get(count));// 设置终止节点
						else
							pt.setEndNode(nodelistInLinkRoute.get(count));// 设置终止节点
						if (count == finalRoute.getregnode().get(0)) {// 此时为transponder的发出链路
							double costOfStart = rp.transpCostCal(length2);
							ptOftransp.setcost_of_tranp(ptOftransp.getcost_of_tranp() + costOfStart);
							file_io.filewrite2(OutFileName, "transponder起点cost" + costOfStart + "   此时transponder cost="
									+ ptOftransp.getcost_of_tranp());
						}
						// 该点放置了IP再生器
						if (finalRoute.getIPRegnode().contains(count)) {
							// 这里用count计算transponder的cost
							Prolinkcapacitymodify(true, IPflow, length2, linklist2, MixLayer, wprlist, nodepair,
										FSoneachLink, request, sharereglist, pt, ResFlowOnlinks, phyLinklist,ptOftransp);
							file_io.filewrite2(OutFileName, "本次RSA长度为：" + length2);
							RegLengthList.add(length2);
							 phyLinklist.clear();
							length2 = 0;
							OEOLength=0;
							linklist2.clear();
							break;
						}
						// 该点放置纯OEO再生器
						else {
							Prolinkcapacitymodify(false, IPflow, length2, linklist2, MixLayer, wprlist, nodepair,
										FSoneachLink, request, sharereglist, pt, ResFlowOnlinks, phyLinklist,ptOftransp);
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
					Prolinkcapacitymodify(true, IPflow, length2, linklist2, MixLayer, wprlist, nodepair,
										FSoneachLink, request, sharereglist, pt, ResFlowOnlinks, phyLinklist,ptOftransp);
						
					RegLengthList.add(length2);
					OEOLength=0;
					 phyLinklist.clear();
					file_io.filewrite2(OutFileName, "本次RSA长度为：" + length2);
					linklist2.clear();
				}
			} while (count < size);
		}
		ArrayList<Regenerator> regthinglist = new ArrayList<>();
		ArrayList<Regenerator> shareReg = new ArrayList<>();
		ArrayList<Regenerator> newReg = new ArrayList<>();
		Test t = new Test();
		HashMap<Integer, Regenerator> hashregthinglist = new HashMap<Integer, Regenerator>();
		file_io.filewrite2(OutFileName, "保护最终路径上再生器节点的数量：" + finalRoute.getregnode().size());

		for (int i : finalRoute.getregnode()) {// 取出路径上所有再生器节点
			Node regnode=new Node(null, 0, null, null, 0, 0);
			if(LinkOrRoute)
				regnode=nodelistInLinkRoute.get(i);
			else
			  regnode= finalRoute.getRoute().getNodelist().get(i);// 判断再生器是共享来的还是新建的
			
			file_io.filewrite_without(OutFileName, regnode.getName() + " 节点上 放置了再生器");
			if (ShareReg.contains(i)) {// 该再生器是通过共享得到的
				for (Regenerator r : sharereglist) {
					if (r.getnode().equals(regnode)) {
						if (r.getNature() == 0)
							file_io.filewrite_without(OutFileName, "是通过共享得到的纯OEO再生器");
						else if (r.getNature() == 1)
							file_io.filewrite_without(OutFileName, "是通过共享得到的IP再生器");
						regthinglist.add(r);// 找出可共享的再生器 加入再生器集合
						hashregthinglist.put(t.nodeindexofroute(regnode, finalRoute.getRoute()), r); // 建立Hashmap!!!
						shareReg.add(r);// 加入针对于该链路的可共享再生器集合
					}
				}
			} else {// 表示不可以共享 此时要建立新的再生器 并且改变node上面再生器的个数
				regnode.setregnum(regnode.getregnum() + 1);
				int index = regnode.getregnum();
				Regenerator reg = new Regenerator(regnode);
				if (finalRoute.getIPRegnode().contains(i)) {
					reg.setNature(1);// 设置新建的再生器是IP再生器
					file_io.filewrite_without(OutFileName, "是新建的IP再生器");
				} else {
					reg.setNature(0); // 设置新建的再生器是OEO再生器
					file_io.filewrite_without(OutFileName, "是新建纯OEO再生器");
				}
				reg.setindex(index);
				regthinglist.add(reg);
				hashregthinglist.put(t.nodeindexofroute(regnode, finalRoute.getRoute()), reg); // 建立Hashmap!!!
				newReg.add(reg);
			}
		}
		for (WorkandProtectRoute wpr : wprlist) {
			if (wpr.getdemand().equals(nodepair)) {
				wpr.setRegProLengthList(RegLengthList);
				wpr.setFSoneachLink(FSoneachLink);
				wpr.setregthinglist(hashregthinglist);
				wpr.setRegeneratorlist(regthinglist);
				wpr.setnewreglist(newReg);
				wpr.setsharereglist(finalRoute.getUsedShareReg());
			}
		}
		
		
	}

	public boolean Prolinkcapacitymodify(Boolean IPorOEO, double IPflow, double routelength, ArrayList<Link> linklist,
			Layer MixLayer, ArrayList<WorkandProtectRoute> wprlist, NodePair nodepair,
			ArrayList<FSshareOnlink> FSoneachLink, Request request, ArrayList<Regenerator> sharereglist,
			ParameterTransfer pt, ArrayList<Double> ResFlowOnlinks, ArrayList<Link> phyLinklist,ParameterTransfer ptOftransp) {
		// 建立虚拟链路 更改容量 RSA
		double X = 1;
		int slotnum = 0, shareFS = 0;
		boolean opworkflag = false, shareFlag = true;
		Node srcnode = new Node(null, 0, null, MixLayer, 0, 0);
		Node desnode = new Node(null, 0, null, MixLayer, 0, 0);
		file_out_put file_io = new file_out_put();
		double resflow = 0;

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
		resflow = slotnum * X - IPflow;
		ResFlowOnlinks.add(resflow);// 存储由OEO再生器衔接的链路上剩余的流量

		opworkflag = true;
		double length1 = 0;
		double cost = 0;
		ProRouteStab rps = new ProRouteStab();
		ArrayList<Integer> index_wave = new ArrayList<Integer>();
		index_wave = rps.FSassignOnlink(linklist, wprlist, nodepair, slotnum, MixLayer);// 在考虑共享的情况下分配频谱
																						// 但未实施占用

		for (Link link : linklist) {
			ArrayList<Integer> index_wave1 = new ArrayList<Integer>();
			length1 = length1 + link.getLength();
			cost = cost + link.getCost();
			ResourceOnLink ro = new ResourceOnLink(request, link, index_wave.get(0), slotnum);
			link.setMaxslot(slotnum + link.getMaxslot());
			// System.out.print("链路 " + link.getName() + "上分配的FS为 ");
			file_io.filewrite_without(OutFileName, "链路 " + link.getName() + "上分配的FS为 ");
			file_io.filewrite2(OutFileName, "");
			int m = index_wave.get(0);
			for (int n = 0; n < slotnum; n++) {
				index_wave1.add(m);
				file_io.filewrite_without(OutFileName, m + "  ");
				m++;
			}
			file_io.filewrite2(OutFileName, " ");
			FSshareOnlink fsonLink = new FSshareOnlink(link, index_wave1);
			FSoneachLink.add(fsonLink);
			phyLinklist.add(link);// 记录建立的虚拟链路对应的物理链路
		}

		// 以上链路频谱分配完毕 下面开始建立IP层光路
		// 首先取出linklist里面的前两个链路和最后两个链路
		if (IPorOEO) {
			Node startnode = pt.getStartNode();
			Node endnode = pt.getEndNode();

			for (int num = 0; num < MixLayer.getNodelist().size() - 1; num++) {// 在IP层中寻找transparent链路的两端
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

			 
			int index = ptOftransp.getNumOfLink() + 1;
			ptOftransp.setNumOfLink(index);
			String index_inName = String.valueOf(index);
			
			Node helpNode = new Node(null, index, null, MixLayer, 0, 0); // 这里将helpNode设置为中间辅助节点
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
			createlink.setnature_WorkOrPro(Constant.NATURE_PRO);
//			ArrayList<Link> phyLinklist1=phyLinklist;
			createlink.setPhysicallink(phyLinklist);
			createlink.setRestcapacity(minflow);
			MixLayer.addLink(createlink);
			// phyLinklist.clear();
			// debug
			file_io.filewrite2(OutFileName, "放置再生器时建立虚拟路径");
			file_io.filewrite2(OutFileName, "建立虚拟路径" + createlink.getName());
			file_io.filewrite2(OutFileName, "对应的物理链路");
			for (Link link : createlink.getPhysicallink()) {
				file_io.filewrite_without(OutFileName, link.getName()+"  ");
			}

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
		return opworkflag;
	}

	public RouteAndRegPlace optionRouteSelect(ArrayList<RouteAndRegPlace> regplaceoption,
			ArrayList<WorkandProtectRoute> wprlist) throws IOException {
		// 本算法的核心思想 这里还未改正
		// 通过更改该子算法调节不同的再生器选择 来达到网络性能的最优
		// 周一可以写一下final routeRSA + 不同再生器下建立IP链路 并且更改本算法
		file_out_put file_io = new file_out_put();
		RouteAndRegPlace finalRoute = new RouteAndRegPlace(null, 1);
		if (regplaceoption.size() == 1) {
			finalRoute = regplaceoption.get(0);
		} else if (regplaceoption.size() != 0) {
			ArrayList<RouteAndRegPlace> RemoveRoute = new ArrayList<>();

			for (int standard = 0; standard < regplaceoption.size() - 1; standard++) {// 标准
				RouteAndRegPlace StandardRoute = regplaceoption.get(standard);
				int StandardShareIP = 0, CompareShareIP = 0,StandardnewIP = 0, ComparenewIP=0;
				if (RemoveRoute.contains(StandardRoute))
					continue;

				for (int k = standard + 1; k < regplaceoption.size(); k++) {// 比较
					RouteAndRegPlace CompareRoute = regplaceoption.get(k);
					if (RemoveRoute.contains(CompareRoute))
						continue;

					if(StandardRoute.getNewRegList().size()>CompareRoute.getNewRegList().size()){//要选择新建再生器少的链路
						RemoveRoute.add(StandardRoute);
						break;
					}
					else if(StandardRoute.getNewRegList().size()<CompareRoute.getNewRegList().size()){
						RemoveRoute.add(CompareRoute);// 比较的没有标准好
						continue;
					}
					//新建再生器个数一致 此时应选择新建IP再生器 较少的路径
					if(StandardRoute.getIPRegnode().size()>CompareRoute.getIPRegnode().size()){
						RemoveRoute.add(StandardRoute);
						break;
					}
					else if(StandardRoute.getIPRegnode().size()<CompareRoute.getIPRegnode().size()){
						RemoveRoute.add(CompareRoute);// 比较的没有标准好
						continue;
					}
						
					//以上比较了新建再生器 接下来比较共享再生器
					for (Regenerator shareReg : StandardRoute.getUsedShareReg()) {
						if (shareReg.getNature() == 1)
							StandardShareIP++;
					}
//					 file_io.filewrite2(OutFileName, "第一层筛选 标准路由共享的IP再生器个数为："+StandardShareIP);
					for (Regenerator shareReg : CompareRoute.getUsedShareReg()) {
						if (shareReg.getNature() == 1)
							CompareShareIP++;
					}
//					 file_io.filewrite2(OutFileName, "第一层筛选比较路由共享的IP再生器个数为："+CompareShareIP);
				
					if (StandardShareIP < CompareShareIP) {
						RemoveRoute.add(StandardRoute);// 删去共享IP再生器少的路径
						break;
					}
					else if (StandardShareIP > CompareShareIP) {
						RemoveRoute.add(CompareRoute);// 比较的没有标准好
						continue;
					}
				}
			}
			for (RouteAndRegPlace rag : RemoveRoute) {
				regplaceoption.remove(rag);
			}
			RemoveRoute.clear();
			// 第二层比较
			if (regplaceoption.size() == 1) {
				finalRoute = regplaceoption.get(0);
			} else {
				for (int standard = 0; standard < regplaceoption.size() - 1; standard++) {
					RouteAndRegPlace StandardRoute_2 = regplaceoption.get(standard);
					if (RemoveRoute.contains(StandardRoute_2))
						continue;
					// file_io.filewrite2(OutFileName, "第二层筛选
					// 标准路由剩余流量："+StandardRoute_2.getNumRemainFlow());
					for (int k = standard + 1; k < regplaceoption.size(); k++) {
						RouteAndRegPlace CompareRoute_2 = regplaceoption.get(k);
						if (RemoveRoute.contains(CompareRoute_2))
							continue;
						// file_io.filewrite2(OutFileName, "第二层筛选
						// 比较路由剩余流量："+CompareRoute_2.getNumRemainFlow());
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
						// file_io.filewrite2(OutFileName, "第三层筛选
						// 标准路由使用的FS为："+StandardRoute_3.getnewFSnum());

						for (int k = standard + 1; k < regplaceoption.size(); k++) {
							RouteAndRegPlace CompareRoute_3 = regplaceoption.get(k);
							if (RemoveRoute.contains(CompareRoute_3))
								continue;
							// System.out.print("第三层筛选 比较路由使用的FS为：");
							// System.out.println(CompareRoute_3.getnewFSnum());
							// file_io.filewrite2(OutFileName, "第三层筛选
							// 比较路由使用的FS为："+CompareRoute_3.getnewFSnum());
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
				finalRoute = regplaceoption.get(0);// 最终不管是否只剩一条链路 都选择第一条作为最终链路

				// file_io.filewrite2(OutFileName,
				// "！！！！！！此时的nodepair为"+nodepair.getName());
				// if(nodepair.getFinalRoute()!=null){
				// file_io.filewrite2(OutFileName, "！！！！！！ 该工作链路需要再生器");
				// }
			}
		}
		return finalRoute;
	}

	public int getIndexFromArr(ArrayList<Node> nodelist, Node nodeInput) {
		int n = 0;
		for (Node node : nodelist) {
			if (node.getName().equals(nodeInput.getName()))
				break;

			n++;
		}
		return n;

	}
}
