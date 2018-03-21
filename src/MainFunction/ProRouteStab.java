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
		// ����ɾ������Ϊ������������·���������Ҫ�ָ�
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
				nowdemand = wpr;// �ȴ����н�����ҵ�����ҳ�����ҵ��
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
			// ɾ���뵱ǰ����·����������·�ཻ�����Ᵽ����·
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
			int cross = t.linklistcompare(wpr.getworklinklist(), nowdemand.getworklinklist());// ��������ҵ���ཻɾȥ֮ǰ����ҵ��ı�����·
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
		// ����ɾ����flowsplitting��Ҫɾ������·
		ArrayList<FlowUseOnLink> fuoList = new ArrayList<>();
		ArrayList<Link> totallink = new ArrayList<>();
		ArrayList<Double> RegLengthList = new ArrayList<>();
		ArrayList<Link> ProVlinklist = new ArrayList<>();

		double UnfinishFlow = 0;
		boolean ProFlag = false;
		FlowSplitting fs = new FlowSplitting();
		UnfinishFlow = fs.flowsplitting(true, MixLayer, nodepair, totallink, fuoList, ProVlinklist);
		// flowsplitting ֮�� �ָ����е�������·
		for (Link link : DelpsyLink) {
			MixLayer.addLink(link);
		}

		if (UnfinishFlow == 0)
			ProFlag = true;
		if (!ProFlag) {
			ptoftransp.setNumOfTransponder(ptoftransp.getNumOfTransponder() + 2);
			MixGrooming mg = new MixGrooming();
			file_io.filewrite2(OutFileName, "��������·����·�ɽ�����δ��ɵ������� " + UnfinishFlow);
			prs.DelNowdempsyLink(MixLayer, nowdemand);// ɾ������ҵ������·������������·
			Request ProRequest=new Request(nodepair);
			ProFlag = mg.MixGrooming(false, nodepair, MixLayer, UnfinishFlow, ptoftransp, null,ProLengthList, wprlist,
					threshold, totallink, fuoList, ProVlinklist,ProRequest);
			if (!ProFlag) {// Mixgrooming ���ɹ� ��ʱ�ڹ�㽨��������·
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

	 
		// ����·��·�ɳɹ�֮����Ҫ���е�����
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
		// ������ʵ���ڸ���һ������·�ɵ���·����ʱ��ͨ���ж��ܷ���֮ǰ�����ı���·������ Ȼ��������Ƶ�� ʵ����󻯹���FS
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
				// System.out.println("��ʱ�Ľڵ��Ϊ "+wpr.getdemand().getName()+"
				// ��ʱ��link Ϊ"+ link.getName());
				// file_io.filewrite2(OutFileName,"��ʱ�Ľڵ��Ϊ
				// "+wpr.getdemand().getName()+" ��ʱ��link Ϊ"+ link.getName());
				if (wpr.getprolinklist().contains(link)) {
					int cross = t.linklistcompare(nowwpr.getworklinklist(), wpr.getworklinklist());
					if (cross == 0) {// ��ʾ����·�����FS���Թ���
						ArrayList<FSshareOnlink> FSShareOnlink = wpr.getFSoneachLink();
						// file_io.filewrite2(OutFileName,"��ʱ��WPR Ϊ
						// "+wpr.getdemand().getName());
						if (FSShareOnlink != null) {
							for (FSshareOnlink FSOnoneLink : FSShareOnlink) {
								if (FSOnoneLink.getlink().equals(link)) {
									for (int share : FSOnoneLink.getslotIndex()) {
										if (!shareslotIndex.contains(share))
											// System.out.println("���Թ����FS
											// Ϊ"+share);
											// file_io.filewrite2(OutFileName,"���Թ����FS
											// Ϊ "+share);
											shareslotIndex.add(share);
									}
								}
							}
						}
					}
					if (cross == 1) {// ��ʾ����·�����FS�����Թ���
						ArrayList<FSshareOnlink> FSShareOnlink = wpr.getFSoneachLink();
						if (FSShareOnlink != null) {
							for (FSshareOnlink FSOnoneLink : FSShareOnlink) {
								if (FSOnoneLink.getlink().equals(link)) {
									for (int NOshare : FSOnoneLink.getslotIndex()) {
										if (!NoShareslotIndex.contains(NOshare)) {
											// System.out.println("�����Թ����FS Ϊ
											// "+NOshare);
											// file_io.filewrite2(OutFileName,"�����Թ����FS
											// Ϊ "+NOshare);
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
			} // ��ÿһ��link����Ŀɹ���Ͳ��ɹ���FS����ͳ�Ʊ���
				// file_io.filewrite2(OutFileName,"");
				// file_io.filewrite2(OutFileName,"FS�Ƴ�����");
			for (WorkandProtectRoute wpr : wprlist) {
				RemoveslotIndex.clear();
				if (shareslotWPR.keySet().contains(wpr)) {

					for (int re : shareslotWPR.get(wpr)) {// ȡ�����Թ����FS
						// file_io.filewrite2(OutFileName," ");
						// file_io.filewrite2(OutFileName,"���Թ����FS "+re);
						for (WorkandProtectRoute comwpr : wprlist) {
							// file_io.filewrite2(OutFileName,"�����Ƚϵ�WPR
							// "+comwpr.getdemand().getName());
							if (NoShareWPR.keySet().contains(comwpr)) {
								if (NoShareWPR.get(comwpr).contains(re)) {// ˵����FS������ҵ�����ǲ����Թ����
									// file_io.filewrite2(OutFileName,"��FS�����Թ���
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
					// file_io.filewrite_without(OutFileName,"��Ҫ�Ƴ���FSΪ"+remove+"
					// ");
					// }
					// file_io.filewrite2(OutFileName,"");
					// for(int share:shareslotWPR.get(wpr)){
					// file_io.filewrite_without(OutFileName,"���Թ����FSΪ "+share+"
					// ");
					// }
					// file_io.filewrite2(OutFileName,"");
					if (RemoveslotIndex.size() != 0 && RemoveslotIndex != null) {
						for (int remove : RemoveslotIndex) {

							// file_io.filewrite2(OutFileName,"���Թ����FS��Ŀ��ʣ"+shareslotWPR.get(wpr).size());
							// for(int last:shareslotWPR.get(wpr)){
							// file_io.filewrite_without(OutFileName,last+" ");
							// }
							// file_io.filewrite2(OutFileName,"");
							int index = shareslotWPR.get(wpr).indexOf(remove);
							// file_io.filewrite2(OutFileName," "+remove);
							// file_io.filewrite2(OutFileName,"��Ҫ�Ƴ�����·indexΪ
							// "+index);
							shareslotWPR.get(wpr).remove(index);
							// file_io.filewrite2(OutFileName,"�Ѿ��Ƴ���FSΪ
							// "+remove);
						} // ��ÿ��WPR���治���Թ����FSȥ��
					}
					FSshareOnlink fsol = new FSshareOnlink(link, shareslotIndex);
					fsol.setwpr(wpr);
					fsonLinklist.add(fsol);
				}
			}

			for (WorkandProtectRoute wpr : wprlist) {
				if (shareslotWPR.keySet().contains(wpr)) {
					if (shareslotWPR.get(wpr).size() != 0) {
						// file_io.filewrite_without(OutFileName, "��· " +
						// link.getName() + " �Ͽ��Թ����slotΪ ");
						// System.out.print("��· " + link.getName() + "
						// �Ͽ��Թ����slotΪ ");
						for (int release : shareslotWPR.get(wpr)) {// �ͷſɹ�����Դ
							Request request = wpr.getrequest();

							// System.out.println("�ɹ�����·��ҵ��Ϊ"+request.getNodepair().getName()+"
							// �ɹ������·Ϊ��"+link.getName()+"
							// ��·�ϵ�FSΪ��"+release);//test);
							// file_io.filewrite2(OutFileName,"�ɹ�����·��ҵ��Ϊ
							// "+request.getNodepair().getName()+"
							// �ɹ������·Ϊ��"+link.getName()+"
							// ��·�ϵ�FSΪ��"+release);//test
							// test

							// System.out.println("����·�ϵ�request����"+link.getSlotsarray().get(release).getoccupiedreqlist().size());
							// file_io.filewrite2(OutFileName,"����·�ϵ�request����
							// "+link.getSlotsarray().get(release).getoccupiedreqlist().size());
							// for(Request
							// re:link.getSlotsarray().get(release).getoccupiedreqlist()){
							// System.out.println("ռ�ø���·��FS�Ľڵ��Ϊ
							// "+re.getNodepair().getName());
							// file_io.filewrite2(OutFileName,"ռ�ø���·��FS�Ľڵ��Ϊ
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

		} // ÿһ��link�����FS���ͷ����
			// file_io.filewrite2(OutFileName, " ");

		// link������Թ������Դ�ͷ���� ֮�����RSA
		ArrayList<Integer> index_wave = new ArrayList<Integer>();
		MainOfAulixiaryRegenetor mm = new MainOfAulixiaryRegenetor();
		// file_io.filewrite2(OutFileName,"ÿ����·����Ҫ��FS��Ϊ�� "+slotnum );
		index_wave = mm.spectrumallocationOneRoute(false, null, linklist, slotnum); // ÿ��link�����ռ����ô��

		if (index_wave != null && index_wave.size() != 0) {
			file_io.filewrite2(OutFileName, "�˴�RSA�����slot���Ϊ " + index_wave.get(0) + " ,����Ϊ " + slotnum);
			// System.out.println("�˴�RSA�����slot���Ϊ " + index_wave.get(0) + "
			// ,����Ϊ " + slotnum);
			int share = 0, newFS = 0;
			for (Link link : linklist) {// �ָ�֮ǰռ�õ�
				for (FSshareOnlink fl : fsonLinklist) {// ����ÿһ��linkҪ����֮ǰ���е�ҵ��
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
			file_io.filewrite2(OutFileName, "�˴�RSA��Ҫ����slot��Ϊ " + newFS);
		}

		// file_io.filewrite2(OutFileName,"");
		// file_io.filewrite2(OutFileName,"�ָ�ռ��֮��");
		// for(Link link:linklist){
		// for(int n=0;n<link.getSlotsarray().size();n++){
		// if (link.getSlotsarray().get(n).getoccupiedreqlist().size() !=
		// 0){//˵����FS��ռ��
		// System.out.println("��·"+link.getName()+"��FS "+n+" �ѱ�
		// "+link.getSlotsarray().get(n).getoccupiedreqlist().size()+" ��ҵ��ռ��");
		// file_io.filewrite2(OutFileName,"��·"+link.getName()+"��FS "+n+" �ѱ�
		// "+link.getSlotsarray().get(n).getoccupiedreqlist().size()+" ��ҵ��ռ��");

		// for(Request re:link.getSlotsarray().get(n).getoccupiedreqlist()){
		// if(re!=null){
		// System.out.println("��·"+link.getName()+"��FS "+n+"
		// �ѱ�ҵ��ռ��"+re.getNodepair().getName());
		// file_io.filewrite2(OutFileName,"��·"+link.getName()+"��FS "+n+" �ѱ�ҵ��
		// "+re.getNodepair().getName()+"ռ��");
		// }
		// }
		// }
		// }
		// }

		return index_wave;

	}

}
