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
import subgraph.LinearRoute;

public class ProRouteStab {
	String OutFileName = MainOfAulixiaryRegenetor.OutFileName;

	public void proroutestab(Layer MixLayer, ArrayList<WorkandProtectRoute> wprlist, NodePair nodepair, float threshold,
			ParameterTransfer ptoftransp) throws IOException {
		// 首先删除属性为工作的虚拟链路这个最后才需要恢复
		file_out_put file_io = new file_out_put();
		ArrayList<Link> DelWorkVLink = new ArrayList<>();
		ArrayList<Link> DelpsyLink = new ArrayList<>();
		ArrayList<Link> DelCrossNowDemandVLink = new ArrayList<>();
		ArrayList<Link> DelTwoDemCroProVLink = new ArrayList<>();
		Test t = new Test();
		ArrayList<Double> ProLengthList = new ArrayList<>();
		WorkandProtectRoute nowdemand = new WorkandProtectRoute(null);
		for (WorkandProtectRoute wpr : wprlist) {
			if (wpr.getdemand().equals(nodepair)) {
				nowdemand = wpr;// 先从所有建立的业务中找出本次业务
			}
		}
		HashMap<String, Link> linklist = MixLayer.getLinklist();
		Iterator<String> iter1 = linklist.keySet().iterator();
		while (iter1.hasNext()) {
			Link link = (Link) (linklist.get(iter1.next()));
			if (link.getnature_IPorOP() == Constant.NATURE_BOUND) {
				link.setRestcapacity(Constant.MaxNum);
				continue;
			}
			if (link.getnature_IPorOP() == Constant.NATURE_IP && link.getnature_WorkOrPro() == Constant.NATURE_WORK) {
				DelWorkVLink.add(link);
				continue;
			} else if (link.getnature_IPorOP() == Constant.NATURE_OP) {
				DelpsyLink.add(link);
				continue;
			}
			// 删除与当前工作路径的物理链路相交的虚拟保护链路
			if (link.getnature_IPorOP() == Constant.NATURE_IP) {
				boolean delFlag = false;
				for (Link phylink : link.getPhysicallink()) {
					if (nowdemand.getworklinklist().contains(phylink)) {
						DelCrossNowDemandVLink.add(link);
						delFlag = true;
						break;
					}
				}
				if (delFlag)
					continue;
			}
		}
		for (WorkandProtectRoute wpr : wprlist) {
			if (wpr.getdemand().equals(nodepair)) {
				continue;
			}
			int cross = t.linklistcompare(wpr.getworklinklist(), nowdemand.getworklinklist());// 两个工作业务相交删去之前建立业务的保护链路
			if (cross == 1) {
				for (Link vlink : wpr.getprovlinklist()) {
					if (!DelCrossNowDemandVLink.contains(vlink)&&!DelTwoDemCroProVLink.contains(vlink)&&linklist.containsValue(vlink)) {
						DelTwoDemCroProVLink.add(vlink);
					}
				}
			}
		}
		ProRouteStab prs = new ProRouteStab();
		prs.delLinkInMixLayer(MixLayer, DelWorkVLink);
		prs.delLinkInMixLayer(MixLayer, DelpsyLink);
		prs.delLinkInMixLayer(MixLayer, DelCrossNowDemandVLink);
		prs.delLinkInMixLayer(MixLayer, DelTwoDemCroProVLink);
		// 以上删除了flowsplitting需要删除的链路
		ArrayList<FlowUseOnLink> fuoList = new ArrayList<>();
		ArrayList<Link> totallink = new ArrayList<>();
		ArrayList<Double> RegLengthList = new ArrayList<>();
		ArrayList<Link> ProVlinklist = new ArrayList<>();

		double UnfinishFlow = 0;
		boolean ProFlag = false;
		FlowSplitting fs = new FlowSplitting();
		UnfinishFlow = fs.flowsplitting(true, MixLayer, nodepair, totallink, fuoList, ProVlinklist);
		// flowsplitting 之后 恢复所有的物理链路
		for (Link link : DelpsyLink) {
			MixLayer.addLink(link);
		}

		if (UnfinishFlow == 0)
			ProFlag = true;
		if (!ProFlag) {
			ptoftransp.setNumOfTransponder(ptoftransp.getNumOfTransponder() + 2);
			MixGrooming mg = new MixGrooming();
			file_io.filewrite2(OutFileName, "纯虚拟链路保护路由结束，未完成的流量： " + UnfinishFlow);
			prs.DelNowdempsyLink(MixLayer, nowdemand);// 删除本次业务工作链路经过的物理链路
			Request ProRequest=new Request(nodepair);
			ProFlag = mg.MixGrooming(false, nodepair, MixLayer, UnfinishFlow, ptoftransp, null,ProLengthList, wprlist,
					threshold, totallink, fuoList, ProVlinklist,ProRequest);
			if (!ProFlag) {// Mixgrooming 不成功 此时在光层建立物理链路
				PurePhyWorkRouteSta rpwrs = new PurePhyWorkRouteSta();
				LinearRoute newRoute = new LinearRoute(null, 0, null);
				newRoute = rpwrs.purephyworkroutesta(false, nodepair, MixLayer, wprlist, threshold, ptoftransp,
						RegLengthList,ProRequest);
				if (newRoute.getNodelist().size() != 0 && newRoute != null) {
					totallink = newRoute.getLinklist();
					nowdemand.setprolinklist(totallink);
					ProFlag = true;
				}
			}
		}
		for (Link link : nowdemand.getworklinklist())
			MixLayer.addLink(link);  

		prs.LinkReco(MixLayer, DelWorkVLink);
		prs.LinkReco(MixLayer, DelCrossNowDemandVLink);
		prs.LinkReco(MixLayer, DelTwoDemCroProVLink);

	 
		// 保护路径路由成功之后需要进行的配置
		// if (success && routelength < 4000) {
		// for (WorkandProtectRoute wpr : wprlist) {
		// if (wpr.getdemand().equals(nodepair)) {
//		 wpr.setproroute(opPrtectRoute);
		// ArrayList<Link> totallink = new ArrayList<>();
		// totallink = opPrtectRoute.getLinklist();
		// wpr.setrequest(request);
		// wpr.setprolinklist(totallink); c
		// wpr.setproroute(opPrtectRoute);
		// wpr.setFSoneachLink(FSuseOnlink);
		// wpr.setprovirtuallinklist(provirtuallinklist);c
		// wpr.setregthinglist(null);
		// }
		// }
		// }
	}

	public void LinkReco(Layer MixLayer, ArrayList<Link> linklist) {
		for (Link link : linklist) {
			MixLayer.addLink(link);
		}
	}

	public void delLinkInMixLayer(Layer MixLayer, ArrayList<Link> DelLinklist) {
		for (Link link : DelLinklist) {
			MixLayer.removeLink(link);
		}
	}

	public void DelNowdempsyLink(Layer Mixlayer, WorkandProtectRoute wpr) {
		for (Link link : wpr.getworklinklist()) {
			Mixlayer.removeLink(link);
		}

	}

	public ArrayList<Integer> FSassignOnlink(ArrayList<Link> linklist, ArrayList<WorkandProtectRoute> wprlist,
			NodePair nodePair, int slotnum, Layer Mixlayer) {
		// 本方法实现在给定一个保护路由的链路集合时候，通过判断能否与之前建立的保护路径共享 然后对其分配频谱 实现最大化共享FS
		file_out_put file_io = new file_out_put();
		Test t = new Test();
		ArrayList<Integer> RemoveslotIndex = new ArrayList<>();
		HashMap<WorkandProtectRoute, ArrayList<Integer>> shareslotWPR = new HashMap<WorkandProtectRoute, ArrayList<Integer>>();
		HashMap<WorkandProtectRoute, ArrayList<Integer>> NoShareWPR = new HashMap<WorkandProtectRoute, ArrayList<Integer>>();
		WorkandProtectRoute nowwpr = new WorkandProtectRoute(null);
		ArrayList<FSshareOnlink> fsonLinklist = new ArrayList<>();

		for (WorkandProtectRoute wpr : wprlist) {
			if (wpr.getdemand().equals(nodePair)) {
				nowwpr = wpr;
			}
		}

		for (Link link : linklist) {
			ArrayList<Integer> shareslotIndex = new ArrayList<>();
			ArrayList<Integer> NoShareslotIndex = new ArrayList<>();
			shareslotWPR.clear();
			for (WorkandProtectRoute wpr : wprlist) {
				if (wpr.getdemand().equals(nodePair))
					continue;
				// System.out.println("此时的节点对为 "+wpr.getdemand().getName()+"
				// 此时的link 为"+ link.getName());
				// file_io.filewrite2(OutFileName,"此时的节点对为
				// "+wpr.getdemand().getName()+" 此时的link 为"+ link.getName());
				if (wpr.getprolinklist().contains(link)) {
					int cross = t.linklistcompare(nowwpr.getworklinklist(), wpr.getworklinklist());
					if (cross == 0) {// 表示该链路上面的FS可以共享
						ArrayList<FSshareOnlink> FSShareOnlink = wpr.getFSoneachLink();
						// file_io.filewrite2(OutFileName,"此时的WPR 为
						// "+wpr.getdemand().getName());
						if (FSShareOnlink != null) {
							for (FSshareOnlink FSOnoneLink : FSShareOnlink) {
								if (FSOnoneLink.getlink().equals(link)) {
									for (int share : FSOnoneLink.getslotIndex()) {
										if (!shareslotIndex.contains(share))
											// System.out.println("可以共享的FS
											// 为"+share);
											// file_io.filewrite2(OutFileName,"可以共享的FS
											// 为 "+share);
											shareslotIndex.add(share);
									}
								}
							}
						}
					}
					if (cross == 1) {// 表示该链路上面的FS不可以共享
						ArrayList<FSshareOnlink> FSShareOnlink = wpr.getFSoneachLink();
						if (FSShareOnlink != null) {
							for (FSshareOnlink FSOnoneLink : FSShareOnlink) {
								if (FSOnoneLink.getlink().equals(link)) {
									for (int NOshare : FSOnoneLink.getslotIndex()) {
										if (!NoShareslotIndex.contains(NOshare)) {
											// System.out.println("不可以共享的FS 为
											// "+NOshare);
											// file_io.filewrite2(OutFileName,"不可以共享的FS
											// 为 "+NOshare);
											NoShareslotIndex.add(NOshare);
										}
									}
								}
							}

						}
					}

					if (shareslotIndex.size() != 0)
						shareslotWPR.put(wpr, shareslotIndex);
					if (NoShareslotIndex.size() != 0)
						NoShareWPR.put(wpr, NoShareslotIndex);
				}
			} // 对每一段link上面的可共享和不可共享FS进行统计保存
				// file_io.filewrite2(OutFileName,"");
				// file_io.filewrite2(OutFileName,"FS移除测试");
			for (WorkandProtectRoute wpr : wprlist) {
				RemoveslotIndex.clear();
				if (shareslotWPR.keySet().contains(wpr)) {

					for (int re : shareslotWPR.get(wpr)) {// 取出可以共享的FS
						// file_io.filewrite2(OutFileName," ");
						// file_io.filewrite2(OutFileName,"可以共享的FS "+re);
						for (WorkandProtectRoute comwpr : wprlist) {
							// file_io.filewrite2(OutFileName,"用来比较的WPR
							// "+comwpr.getdemand().getName());
							if (NoShareWPR.keySet().contains(comwpr)) {
								if (NoShareWPR.get(comwpr).contains(re)) {// 说明该FS在其他业务上是不可以共享的
									// file_io.filewrite2(OutFileName,"该FS不可以共享
									// "+re);
									if (!RemoveslotIndex.contains(re)) {
										RemoveslotIndex.add(re);
										break;
									}
								}
							}
						}
					}
					// test
					// for (int remove : RemoveslotIndex) {
					// file_io.filewrite_without(OutFileName,"需要移除的FS为"+remove+"
					// ");
					// }
					// file_io.filewrite2(OutFileName,"");
					// for(int share:shareslotWPR.get(wpr)){
					// file_io.filewrite_without(OutFileName,"可以共享的FS为 "+share+"
					// ");
					// }
					// file_io.filewrite2(OutFileName,"");
					if (RemoveslotIndex.size() != 0 && RemoveslotIndex != null) {
						for (int remove : RemoveslotIndex) {

							// file_io.filewrite2(OutFileName,"可以共享的FS数目还剩"+shareslotWPR.get(wpr).size());
							// for(int last:shareslotWPR.get(wpr)){
							// file_io.filewrite_without(OutFileName,last+" ");
							// }
							// file_io.filewrite2(OutFileName,"");
							int index = shareslotWPR.get(wpr).indexOf(remove);
							// file_io.filewrite2(OutFileName," "+remove);
							// file_io.filewrite2(OutFileName,"需要移除的链路index为
							// "+index);
							shareslotWPR.get(wpr).remove(index);
							// file_io.filewrite2(OutFileName,"已经移除的FS为
							// "+remove);
						} // 将每个WPR上面不可以共享的FS去掉
					}
					FSshareOnlink fsol = new FSshareOnlink(link, shareslotIndex);
					fsol.setwpr(wpr);
					fsonLinklist.add(fsol);
				}
			}

			for (WorkandProtectRoute wpr : wprlist) {
				if (shareslotWPR.keySet().contains(wpr)) {
					if (shareslotWPR.get(wpr).size() != 0) {
						// file_io.filewrite_without(OutFileName, "链路 " +
						// link.getName() + " 上可以共享的slot为 ");
						// System.out.print("链路 " + link.getName() + "
						// 上可以共享的slot为 ");
						for (int release : shareslotWPR.get(wpr)) {// 释放可共享资源
							Request request = wpr.getrequest();

							// System.out.println("可共享链路的业务为"+request.getNodepair().getName()+"
							// 可共享的链路为："+link.getName()+"
							// 链路上的FS为："+release);//test);
							// file_io.filewrite2(OutFileName,"可共享链路的业务为
							// "+request.getNodepair().getName()+"
							// 可共享的链路为："+link.getName()+"
							// 链路上的FS为："+release);//test
							// test

							// System.out.println("该链路上的request个数"+link.getSlotsarray().get(release).getoccupiedreqlist().size());
							// file_io.filewrite2(OutFileName,"该链路上的request个数
							// "+link.getSlotsarray().get(release).getoccupiedreqlist().size());
							// for(Request
							// re:link.getSlotsarray().get(release).getoccupiedreqlist()){
							// System.out.println("占用该链路该FS的节点对为
							// "+re.getNodepair().getName());
							// file_io.filewrite2(OutFileName,"占用该链路该FS的节点对为
							// "+re.getNodepair().getName());
							// }

							// link.getSlotsarray().get(release).getoccupiedreqlist().get(0);
							// link.getSlotsarray().get(release).getoccupiedreqlist().remove(res);
							link.getSlotsarray().get(release).getoccupiedreqlist().remove(request);
							// test
							// file_io.filewrite_without(OutFileName, release +
							// " ");
							// System.out.print(release + " ");
						}
						// file_io.filewrite2(OutFileName, " ");
					}
				}
			}

		} // 每一段link上面的FS均释放完毕
			// file_io.filewrite2(OutFileName, " ");

		// link上面可以共享的资源释放完毕 之后进行RSA
		ArrayList<Integer> index_wave = new ArrayList<Integer>();
		MainOfAulixiaryRegenetor mm = new MainOfAulixiaryRegenetor();
		// file_io.filewrite2(OutFileName,"每段链路上需要的FS数为： "+slotnum );
		index_wave = mm.spectrumallocationOneRoute(false, null, linklist, slotnum); // 每个link上面均占用这么多

		if (index_wave != null && index_wave.size() != 0) {
			file_io.filewrite2(OutFileName, "此次RSA分配的slot起点为 " + index_wave.get(0) + " ,长度为 " + slotnum);
			// System.out.println("此次RSA分配的slot起点为 " + index_wave.get(0) + "
			// ,长度为 " + slotnum);
			int share = 0, newFS = 0;
			for (Link link : linklist) {// 恢复之前占用的
				for (FSshareOnlink fl : fsonLinklist) {// 对于每一段link要遍历之前所有的业务
					if (fl.getlink().equals(link)) {
						Request request = fl.getwpr().getrequest();
						for (int recovery : fl.getslotIndex()) {
							link.getSlotsarray().get(recovery).getoccupiedreqlist().add(request);

							for (int co = index_wave.get(0); co < index_wave.get(0) + slotnum; co++) {
								if (co == recovery) {
									share++;
									break;
								}
							}

						}
					}
				}
				if (slotnum < share) {
					share = slotnum;
				}
				newFS = newFS + slotnum - share;

			}
			nodePair.setSlotsnum(newFS);
			file_io.filewrite2(OutFileName, "此次RSA需要的新slot数为 " + newFS);
		}

		// file_io.filewrite2(OutFileName,"");
		// file_io.filewrite2(OutFileName,"恢复占用之后");
		// for(Link link:linklist){
		// for(int n=0;n<link.getSlotsarray().size();n++){
		// if (link.getSlotsarray().get(n).getoccupiedreqlist().size() !=
		// 0){//说明该FS有占用
		// System.out.println("链路"+link.getName()+"上FS "+n+" 已被
		// "+link.getSlotsarray().get(n).getoccupiedreqlist().size()+" 个业务占用");
		// file_io.filewrite2(OutFileName,"链路"+link.getName()+"上FS "+n+" 已被
		// "+link.getSlotsarray().get(n).getoccupiedreqlist().size()+" 个业务占用");

		// for(Request re:link.getSlotsarray().get(n).getoccupiedreqlist()){
		// if(re!=null){
		// System.out.println("链路"+link.getName()+"上FS "+n+"
		// 已被业务占用"+re.getNodepair().getName());
		// file_io.filewrite2(OutFileName,"链路"+link.getName()+"上FS "+n+" 已被业务
		// "+re.getNodepair().getName()+"占用");
		// }
		// }
		// }
		// }
		// }

		return index_wave;

	}

}
