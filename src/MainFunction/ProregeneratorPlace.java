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

	// ��RSAunderSet ���������ֵ
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

		// ����������linklist (Mixgrooming) ����route����������·������
		ArrayList<Node> nodelistInLinkRoute = new ArrayList<>();
		if (LinkOrRoute) {// �������ArrayList
			ArrayList<Link> linkListInput = new ArrayList<>();
			linkListInput = LinkOnRoute;
			for (int n = 0; n < linkListInput.size() - 1; n++) {
				Link link0 = linkListInput.get(n);
				Link link1 = linkListInput.get(n + 1);
				if (link0.getNodeA().equals(link1.getNodeA().getName())
						|| link0.getNodeA().getName().equals(link1.getNodeB().getName())) {// ȡ��û���ظ��Ľڵ�
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
			// ��ʱ�������route
			size = Route.getNodelist().size() - 1;
		else
			size = LinkOnRoute.size();

		// part1 �ҵ��ñ�����·�����Ѵ��ڵĹ���������
		for (WorkandProtectRoute nowwpr : wprlist) {// �����ұ���nodepair��Ӧ�� wpr
			if (nowwpr.getdemand().equals(nodepair)) {
				nowdemand = nowwpr;
				break;
			}
		}

		for (WorkandProtectRoute wpr : wprlist) {// ���Ѵ��ڵ�ҵ���� �ҳ���ҵ�����Ѵ��ڵĹ���������
			int cross = t.linklistcompare(nowdemand.getworklinklist(), wpr.getworklinklist());
			if (cross == 0) {// �����ж�������²������������Ƿ���Թ���

				for (Regenerator newreg : wpr.getnewreglist()) {// ֻ������·����û���½���������
					Node node = newreg.getnode();
					if ((LinkOrRoute && nodelistInLinkRoute.contains(node))
							|| (!LinkOrRoute && Route.getNodelist().contains(node))) {// ����link
						// ���֮ǰ��ҵ����ĳһ�ڵ����Ѿ�������������
						// �жϸ�ҵ������ҵ��ɷ���������������ҵ��Ĺ�����·��Ӧ��������·�Ƿ񽻲棩
						int already = 0, newregg = 0;
						boolean noshareFlag = false;
						for (WorkandProtectRoute comwpr : wprlist) {
							if (wpr.getdemand().equals(comwpr.getdemand()))
								continue;
							for (Regenerator haveshareReg : comwpr.getsharereglist()) {
								if (haveshareReg.equals(newreg)) {// ����ҵ�������������������
									int cross_second = t.linklistcompare(nowdemand.getworklinklist(),
											comwpr.getworklinklist());
									if (cross_second == 1) {
										noshareFlag = true;
										break;
									}
								}
							}
						} // �����ж�֮ǰҵ��ĳһ�ڵ���������ɷ��ڱ���ҵ��ڵ��Ϲ���

						if (!noshareFlag) {// ��ʾ�ýڵ����������ڱ�ҵ����Ҳ���Թ���
							/*
							 * �������޸� ����ѡ��IP������ �ж��IP������ʱѡ�񱣻�·����������� OEO������ͬ��
							 */
							int po = 0;
							if (!LinkOrRoute)
								po = t.nodeindexofroute(node, Route);// ��������·�Ͽ��Թ������������λ��
							if (LinkOrRoute)
								po = prp.getIndexFromArr(nodelistInLinkRoute, node);

							if (po != 0 && po != size) {// �ж�����·���Ѵ��ڵ��������Ƿ�����·������
								/*
								 * comnodelist�洢�����Ѿ����ڿɹ����������Ľڵ� sharereglist
								 * �洢���Ǿ��жϿ��Թ���������� һ�½���һ���ڵ��϶��������������ɸѡ
								 */
								if (comnodelist.contains(node)) {// ˵���ýڵ����Ѵ��ڿɹ����������
																	// ��ʱ��Ҫѡ�����ĸ�����������
									for (Regenerator alreadyReg : sharereglist) {
										// sharereglist��������Ѿ��жϿ��Թ����������
										// sharelist����ÿ���ڵ���ֻ��һ�����ŵĹ���������
										if (alreadyReg.getnode().equals(node)) {// ��ʱalreadyReg��ʾ�����б����Ѵ��ڵ�reg

											if (alreadyReg.getNature() == 0 && newreg.getNature() == 1) {// ��������������IP������
																											// ԭ�����������Ǵ�OEO����
																											// ��ʱѡ���µ�������
												removereglist.add(alreadyReg);
												addreglist.add(newreg);
											} else if (alreadyReg.getNature() == 1 && newreg.getNature() == 0) {// ��������������OEO������
																												// ԭ������������IP����
																												// ��ʱѡ��ԭ����������
											} else {// ��ʾԭ�������������µ���������������һ��
													// ��ʱ��Ҫ�Ƚ����Ǳ���·������

												for (WorkandProtectRoute comwpr : wprlist) {// һ�±Ƚ��ĸ�������ʹ�õĶ�
													if (comwpr.getRegeneratorlist().contains(alreadyReg)) {
														already++;
													}
													if (comwpr.getRegeneratorlist().contains(newreg)) {
														newregg++;
													}
												}
												if (already < newregg) {// ˵�������ӵ�reg����ı�����·�Ƚ϶�
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
									removereglist.clear();// ԭ������Ҫ�𣿣���
									for (Regenerator addReg : addreglist) {
										if (!sharereglist.contains(addReg))
											sharereglist.add(addReg);
									}
									addreglist.clear();// ԭ������Ҫ�𣿣���

								} else {// �²�����������
									comnodelist.add(node);
									sharereglist.add(newreg);
								}
								// System.out.println("�������ĸ�����"+sharereglist.size());
								// for(Regenerator reg:sharereglist){
								// System.out.println(reg.getnode().getName());
								// }
								if (!ShareReg.contains(po))
									ShareReg.add(po); // �������µ�ҵ������Щ�ڵ�������������
							}
						}
					}
				}
			}
		}
		// part1 finish �洢�����и���·�Ͽɹ�����������λ��

		boolean success = false, passflag = false;
		int minRegNum = (int) Math.floor(routelength / 4000);
		int internode = 0;
		if (!LinkOrRoute) {
			// ��ʱ�������route
			internode = Route.getNodelist().size() - 2;
		} else {
			internode = LinkOnRoute.size() - 1;
		}
		// debug
		file_io.filewrite2(OutFileName, "");
		file_io.filewrite2(OutFileName, "�ɹ����������ĸ�����" + ShareReg.size() + "��Ҫ������������������" + minRegNum);

		for (Regenerator reg : sharereglist) {
			file_io.filewrite_without(OutFileName, "�ɹ����������� " + reg.getnode().getName() + "�ڵ���,  ");
			if (reg.getNature() == 0) {
				file_io.filewrite2(OutFileName, "���Ǵ�OEO������ ");
			}
			if (reg.getNature() == 1) {
				file_io.filewrite2(OutFileName, "����IP������ ");
			}
		}

		/*
		 * // part2 ��·���Ϲ����������ĸ���С����������������С����ʱ ����set����RSA ����regplaceoption
		 * ����ɹ����������ĸ���С��������Ҫ�����ĸ���ʱ
		 */

		if (ShareReg.size() <= minRegNum) {
			for (int s = minRegNum; s <= internode; s++) {
				if (regplaceoption.size() != 0)
					break;
				Test nOfm = new Test(s, internode); // �������м�ڵ������ѡȡm����������������
				while (nOfm.hasNext()) {
					passflag = false;
					int[] set = nOfm.next(); // �������������������λ��
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
						continue;// ���еĹ��������� �Ѿ��ڶ��������в����Ŀ�������Ҫ������Щ������

					// �����������ڵ�֮�����RSA ����optionѡ���·��
					rgp.RSAunderSet(LinkOrRoute, sharereglist, ShareReg, set, Route, LinkOnRoute, MixLayer, IPflow,
							regplaceoption, wprlist, nodepair, threshold,nodelistInLinkRoute);
				 
				}
			}
		}

		// part3 ��·���Ϲ����������ĸ���������������������С����ʱ ����set����RSA����regplaceoption
		if (ShareReg.size() > minRegNum) {
			for (int s = minRegNum; s <= internode; s++) {
				if (regplaceoption.size() != 0)
					break;
				Test nOfm = new Test(s, internode); // �������м�ڵ������ѡȡm����������������
				while (nOfm.hasNext()) {
					passflag = false;
					int[] set = nOfm.next(); // �������������������λ��
					if (s <= ShareReg.size()) { // ��ʱ����������ӿɹ��������������ѡ��
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
					} // ������ҪΪ�˲���set
						// �����������ڵ�֮�����RSA
					rgp.RSAunderSet(false, sharereglist, ShareReg, set, Route, LinkOnRoute, MixLayer, IPflow,
							regplaceoption, wprlist, nodepair, threshold,nodelistInLinkRoute);
				}
			}
		}
		// debug ��ѡ��ѡ·��֮ǰ�ȹ۲����������ָ��
		if (regplaceoption.size() != 0) {
			for (RouteAndRegPlace DebugRegRoute : regplaceoption) {
				ArrayList<Integer> NewRegList = new ArrayList<>();
				file_io.filewrite2(OutFileName, " ");
				// System.out.println();
				// ������ɹ���������
				if (DebugRegRoute.getUsedShareReg() != null) {
					file_io.filewrite2(OutFileName, "�ɹ���������� ");
					for (Regenerator reg : DebugRegRoute.getUsedShareReg()) {
						if (reg.getNature() == 0) {
							file_io.filewrite2(OutFileName, reg.getnode().getName() + "  OEO������");
						}
						if (reg.getNature() == 1) {
							file_io.filewrite2(OutFileName, reg.getnode().getName() + "  IP������");
						}
					}
				}
				// �����жϿɷ����ɹ�����Ϊ�½�
				file_io.filewrite2(OutFileName, "�½��������� ");
				for (int Reg : DebugRegRoute.getregnode()) {
					boolean share = false;
					Node NewRegNode =new Node(null, 0, null, null, 0, 0);
					if(LinkOrRoute)
						NewRegNode=nodelistInLinkRoute.get(Reg);
					else
					    NewRegNode = DebugRegRoute.getRoute().getNodelist().get(Reg);
					for (Regenerator ShareReg2 : DebugRegRoute.getUsedShareReg()) {
						if (ShareReg2.getnode().getName().equals(NewRegNode.getName())) {// �ĵ��ϵ����������Թ��������½�
							share = true;
							break;
						}
					}
					if (!share) {
						NewRegList.add(Reg);
						if (DebugRegRoute.getIPRegnode().contains(Reg)) { // �½�����������IP������
							file_io.filewrite_without(OutFileName, NewRegNode.getName() + "  IP������  ");
						} else {
							file_io.filewrite_without(OutFileName, NewRegNode.getName() + "  OEO������  ");
						}
					}
				}
				file_io.filewrite2(OutFileName, " ");
				file_io.filewrite2(OutFileName, "ʣ��������� " + DebugRegRoute.getNumRemainFlow());
				file_io.filewrite2(OutFileName, "ʹ�õ�newFS������ " + DebugRegRoute.getnewFSnum());
				DebugRegRoute.setNewRegList(NewRegList); // ͳ��ÿ����ѡ·����ʹ�õ�������������
			}
		}

		// part4 �Բ����ı�ѡ��·����ɸѡ���Ҷ�ѡ����·����IP��·
		RouteAndRegPlace finalRoute = new RouteAndRegPlace(null, 1);
		if (regplaceoption.size() > 0) {
			success = true;
			if (regplaceoption.size() > 1)
				finalRoute = rgp2.optionRouteSelect(regplaceoption, wprlist);// �ڷ��������ļ���·����ѡȡ��ѵ�·����Ϊfinaroute
			else
				finalRoute = regplaceoption.get(0);
			// �������Ը�������·����RSA
			rgp2.FinalRouteRSA(LinkOrRoute,finalRoute, MixLayer,IPflow,ptoftransp,wprlist, nodepair,
					request, sharereglist,FSuseOnlink,ShareReg);
		 
			// ����finalroute�����������ڵ�洢����
		}
		if (regplaceoption.size() == 0) {
			success = false;
		}
		// System.out.println();
		file_io.filewrite2(OutFileName, "");
		if (success) {
			file_io.filewrite_without(OutFileName, "����·�����������óɹ�����RSA,���õ�����������Ϊ");

		} else {
			file_io.filewrite2(OutFileName, "����·���������������ɹ���·��������");
		}
		return success;
	}// ����������

	public void RSAunderSet(Boolean LinkOrRoute, ArrayList<Regenerator> sharereglist, ArrayList<Integer> ShareReg,
			int[] set, LinearRoute newRoute, ArrayList<Link> linklistOfRoute, Layer MixLayer, double IPflow,
			ArrayList<RouteAndRegPlace> regplaceoption, ArrayList<WorkandProtectRoute> wprlist, NodePair nodepair,
			float threshold,ArrayList<Node> nodelistInLinkRoute ) {
		// �����µ������� ���ҿ�����ֵ
		boolean partworkflag = false, RSAflag = false, regflag = false;
		double length = 0;
		file_out_put file_io = new file_out_put();
		ArrayList<Link> linklist = new ArrayList<>();
		int FStotal = 0, n = 0;
		RegeneratorPlace rp = new RegeneratorPlace();
		ArrayList<Float> RemainRatio = new ArrayList<>();// ��¼ÿ����·��ʣ���flow
		float NumRemainFlow = 0;
		ArrayList<Regenerator> UseShareReg = new ArrayList<>();
		
		int size = 0;
		if (!LinkOrRoute)
			// ��ʱ�������route
			size = newRoute.getNodelist().size() - 1;
		else
			size = linklistOfRoute.size();
		

		for (int i = 0; i < set.length + 1; i++) {// RSA�Ĵ������������ĸ�����1
			if (!partworkflag && RSAflag)
				break;
			if (i < set.length) {
				// System.out.println("****************��������λ��Ϊ��" + set[i]); //
				// set�������Ӧ���ǽڵ��λ��+1��
				file_io.filewrite2(OutFileName, "****************��������λ��Ϊ��" + set[i]);
			} else {
				// System.out.println("************���һ�����������ս��֮���RSA ");
				file_io.filewrite2(OutFileName, "************���һ�����������ս��֮���RSA ");
				regflag = true;
			}
			do {// ͨ��һ��
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
				if (!regflag) {// δ�������һ��·����RSA
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
				if (!partworkflag && RSAflag)// ���֮ǰ����·�Ѿ�RSAʧ�� ʣ�µ���·Ҳû��RSA�ı�Ҫ
					break;
			} while (n != size);
			// ���·�ɳɹ��򱣴��·�ɶ����������ķ���
		}
		if (partworkflag) {// ˵����set�¿���RSA ��ʱ��Ҫ�����µ�������
			ArrayList<Integer> setarray = new ArrayList<>();
			ArrayList<Integer> IPRegarray = new ArrayList<>();
			for (int k = 0; k < set.length; k++) {
				setarray.add(set[k]);
				if (!ShareReg.contains(set[k])) {// �����������½��� ��Ҫ�ж������� ����ǹ����
					// ��ô���������Ѿ�ȷ�� ����Ҫ�ж�
					if (RemainRatio.get(k) >= threshold || RemainRatio.get(k + 1) >= threshold) {// ֻҪ������ǰ����ߺ�����һ��δ���ʹ�������IP������
						IPRegarray.add(set[k]);// �洢IP���������ýڵ�
					}
				} else {// ʹ���˸ÿɹ����������
					for (Regenerator UsedShareReg : sharereglist) {
						Node node=new Node(null, 0, null, null, 0, 0);
						if(!LinkOrRoute)
							node=newRoute.getNodelist().get(set[k]);
						else
							node=nodelistInLinkRoute.get(set[k]);
						if (UsedShareReg.getnode().getName().equals(node.getName())) {
							UseShareReg.add(UsedShareReg);// ����ʹ���˵Ĺ���������
							break;
						}
					}
				}
			}
			
			 if(LinkOrRoute){//link
				 RouteAndRegPlace rarp = new RouteAndRegPlace(linklistOfRoute, 1, 0);
				 rarp.setnewFSnum(FStotal);
				 // file_io.filewrite2(OutFileName, " ");
				 rarp.setUsedShareReg(UseShareReg); // ��¼ʹ�õĹ���������
				 rarp.setIPRegnode(IPRegarray);// ע�������IP������������ȫ���������� �����½���IP������
				 rarp.setregnode(setarray);
				 rarp.setregnum(setarray.size());
				 rarp.setNumRemainFlow(NumRemainFlow);
				 regplaceoption.add(rarp);
			 }
			 if(!LinkOrRoute){//link
				 RouteAndRegPlace rarp = new RouteAndRegPlace(newRoute, 1);
				 rarp.setnewFSnum(FStotal);
				 // file_io.filewrite2(OutFileName, " ");
				 rarp.setUsedShareReg(UseShareReg); // ��¼ʹ�õĹ���������
				 rarp.setIPRegnode(IPRegarray);// ע�������IP������������ȫ���������� �����½���IP������
				 rarp.setregnode(setarray);
				 rarp.setregnum(setarray.size());
				 rarp.setNumRemainFlow(NumRemainFlow);
				 regplaceoption.add(rarp);
			 }
			file_io.filewrite2(OutFileName, "��·���ɹ�RSA, �ѳɹ�RSA������Ϊ��" + regplaceoption.size());
		}
	}

	public void FinalRouteRSA(Boolean LinkOrRoute, RouteAndRegPlace finalRoute, Layer MixLayer, double IPflow,
			 ParameterTransfer ptOftransp,ArrayList<WorkandProtectRoute> wprlist, 
			NodePair nodepair,Request request,ArrayList<Regenerator> sharereglist,ArrayList<FSshareOnlink> FSoneachLink,ArrayList<Integer> ShareReg) throws IOException {
		// ������Ҫ����ͬ�������� ���첻ͬ��IP������·����IP��
		// IP������ ������Ҫ��������������· oeo������ֻ��Ҫ����һ��������·
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
			pt.setStartNode(finalRoute.getRoute().getNodelist().get(0));// �������ø���·����ʼ�ڵ�

		// ������linklist��nodelist
		ArrayList<Node> nodelistInLinkRoute = new ArrayList<>();
		if (LinkOrRoute) {// �������ArrayList
			ArrayList<Link> linkListInput = new ArrayList<>();
			linkListInput = finalRoute.getLinklistOnRoute();
			for (int n = 0; n < linkListInput.size() - 1; n++) {
				Link link0 = linkListInput.get(n);
				Link link1 = linkListInput.get(n + 1);
				if (link0.getNodeA().equals(link1.getNodeA().getName())
						|| link0.getNodeA().getName().equals(link1.getNodeB().getName())) {// ȡ��û���ظ��Ľڵ�
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

		pt.setMinRemainFlowRSA(10000);// ���ȳ�ʼ��
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
				file_io.filewrite2(OutFileName, "������·RSA��" + link_loop.getName());
				length2 = length2 + link_loop.getLength();
				OEOLength=OEOLength+link_loop.getLength();
				linklist2.add(link_loop);
				count = count + 1;
				if (!regflag2) {// δ�������һ��·����RSA
					if (count == finalRoute.getregnode().get(i)) {// ���ȸõ������������
						if (!LinkOrRoute)// route
							pt.setEndNode(finalRoute.getRoute().getNodelist().get(count));// ������ֹ�ڵ�
						else
							pt.setEndNode(nodelistInLinkRoute.get(count));// ������ֹ�ڵ�
						if (count == finalRoute.getregnode().get(0)) {// ��ʱΪtransponder�ķ�����·
							double costOfStart = rp.transpCostCal(length2);
							ptOftransp.setcost_of_tranp(ptOftransp.getcost_of_tranp() + costOfStart);
							file_io.filewrite2(OutFileName, "transponder���cost" + costOfStart + "   ��ʱtransponder cost="
									+ ptOftransp.getcost_of_tranp());
						}
						// �õ������IP������
						if (finalRoute.getIPRegnode().contains(count)) {
							// ������count����transponder��cost
							Prolinkcapacitymodify(true, IPflow, length2, linklist2, MixLayer, wprlist, nodepair,
										FSoneachLink, request, sharereglist, pt, ResFlowOnlinks, phyLinklist,ptOftransp);
							file_io.filewrite2(OutFileName, "����RSA����Ϊ��" + length2);
							RegLengthList.add(length2);
							 phyLinklist.clear();
							length2 = 0;
							OEOLength=0;
							linklist2.clear();
							break;
						}
						// �õ���ô�OEO������
						else {
							Prolinkcapacitymodify(false, IPflow, length2, linklist2, MixLayer, wprlist, nodepair,
										FSoneachLink, request, sharereglist, pt, ResFlowOnlinks, phyLinklist,ptOftransp);
							RegLengthList.add(length2);
							file_io.filewrite2(OutFileName, "����RSA����Ϊ��" + length2);
							length2 = 0;
							linklist2.clear();
							break;
						}
					}
				}

				if (count == size) {// ���һ�����������յ�֮���RSA
					double costOfEnd = rp.transpCostCal(length2);
					ptOftransp.setcost_of_tranp(ptOftransp.getcost_of_tranp() + costOfEnd);
					file_io.filewrite2(OutFileName,
							"transponder�յ�cost" + costOfEnd + "   ��ʱtransponder cost=" + ptOftransp.getcost_of_tranp());
					if (!LinkOrRoute)
						pt.setEndNode(finalRoute.getRoute().getNodelist().get(count));// ������ֹ�ڵ�
					else
						pt.setEndNode(nodelistInLinkRoute.get(count));// ������ֹ�ڵ�
					Prolinkcapacitymodify(true, IPflow, length2, linklist2, MixLayer, wprlist, nodepair,
										FSoneachLink, request, sharereglist, pt, ResFlowOnlinks, phyLinklist,ptOftransp);
						
					RegLengthList.add(length2);
					OEOLength=0;
					 phyLinklist.clear();
					file_io.filewrite2(OutFileName, "����RSA����Ϊ��" + length2);
					linklist2.clear();
				}
			} while (count < size);
		}
		ArrayList<Regenerator> regthinglist = new ArrayList<>();
		ArrayList<Regenerator> shareReg = new ArrayList<>();
		ArrayList<Regenerator> newReg = new ArrayList<>();
		Test t = new Test();
		HashMap<Integer, Regenerator> hashregthinglist = new HashMap<Integer, Regenerator>();
		file_io.filewrite2(OutFileName, "��������·�����������ڵ��������" + finalRoute.getregnode().size());

		for (int i : finalRoute.getregnode()) {// ȡ��·���������������ڵ�
			Node regnode=new Node(null, 0, null, null, 0, 0);
			if(LinkOrRoute)
				regnode=nodelistInLinkRoute.get(i);
			else
			  regnode= finalRoute.getRoute().getNodelist().get(i);// �ж��������ǹ������Ļ����½���
			
			file_io.filewrite_without(OutFileName, regnode.getName() + " �ڵ��� ������������");
			if (ShareReg.contains(i)) {// ����������ͨ������õ���
				for (Regenerator r : sharereglist) {
					if (r.getnode().equals(regnode)) {
						if (r.getNature() == 0)
							file_io.filewrite_without(OutFileName, "��ͨ������õ��Ĵ�OEO������");
						else if (r.getNature() == 1)
							file_io.filewrite_without(OutFileName, "��ͨ������õ���IP������");
						regthinglist.add(r);// �ҳ��ɹ���������� ��������������
						hashregthinglist.put(t.nodeindexofroute(regnode, finalRoute.getRoute()), r); // ����Hashmap!!!
						shareReg.add(r);// ��������ڸ���·�Ŀɹ�������������
					}
				}
			} else {// ��ʾ�����Թ��� ��ʱҪ�����µ������� ���Ҹı�node�����������ĸ���
				regnode.setregnum(regnode.getregnum() + 1);
				int index = regnode.getregnum();
				Regenerator reg = new Regenerator(regnode);
				if (finalRoute.getIPRegnode().contains(i)) {
					reg.setNature(1);// �����½�����������IP������
					file_io.filewrite_without(OutFileName, "���½���IP������");
				} else {
					reg.setNature(0); // �����½�����������OEO������
					file_io.filewrite_without(OutFileName, "���½���OEO������");
				}
				reg.setindex(index);
				regthinglist.add(reg);
				hashregthinglist.put(t.nodeindexofroute(regnode, finalRoute.getRoute()), reg); // ����Hashmap!!!
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
		// ����������· �������� RSA
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
		slotnum = (int) Math.ceil(IPflow / X);// ����ȡ��
		if (slotnum < Constant.MinSlotinLightpath) {
			slotnum = Constant.MinSlotinLightpath;
		}
		resflow = slotnum * X - IPflow;
		ResFlowOnlinks.add(resflow);// �洢��OEO�������νӵ���·��ʣ�������

		opworkflag = true;
		double length1 = 0;
		double cost = 0;
		ProRouteStab rps = new ProRouteStab();
		ArrayList<Integer> index_wave = new ArrayList<Integer>();
		index_wave = rps.FSassignOnlink(linklist, wprlist, nodepair, slotnum, MixLayer);// �ڿ��ǹ��������·���Ƶ��
																						// ��δʵʩռ��

		for (Link link : linklist) {
			ArrayList<Integer> index_wave1 = new ArrayList<Integer>();
			length1 = length1 + link.getLength();
			cost = cost + link.getCost();
			ResourceOnLink ro = new ResourceOnLink(request, link, index_wave.get(0), slotnum);
			link.setMaxslot(slotnum + link.getMaxslot());
			// System.out.print("��· " + link.getName() + "�Ϸ����FSΪ ");
			file_io.filewrite_without(OutFileName, "��· " + link.getName() + "�Ϸ����FSΪ ");
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
			phyLinklist.add(link);// ��¼������������·��Ӧ��������·
		}

		// ������·Ƶ�׷������ ���濪ʼ����IP���·
		// ����ȡ��linklist�����ǰ������·�����������·
		if (IPorOEO) {
			Node startnode = pt.getStartNode();
			Node endnode = pt.getEndNode();

			for (int num = 0; num < MixLayer.getNodelist().size() - 1; num++) {// ��IP����Ѱ��transparent��·������
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
			for (double resflow2 : ResFlowOnlinks) {// Ѱ�Ҳ�ͬ��·��ʣ��������С����·
				if (minflow > resflow2) {
					minflow = resflow2;
				}
			}
			ResFlowOnlinks.clear();

			 
			int index = ptOftransp.getNumOfLink() + 1;
			ptOftransp.setNumOfLink(index);
			String index_inName = String.valueOf(index);
			
			Node helpNode = new Node(null, index, null, MixLayer, 0, 0); // ���ｫhelpNode����Ϊ�м丨���ڵ�
			helpNode.setName(srcnode.getName() + "(" + index_inName + ")");
			MixLayer.addNode(helpNode);

			length1 = length1 / 1000;
			cost = cost / 1000;
			String name = null;
			Link createlink = new Link(null, 0, null, null, null, null, 0, 0);
			if (desnode.getIndex() < helpNode.getIndex()) {
				// ȷ����ӵ�����·��������
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
			file_io.filewrite2(OutFileName, "����������ʱ��������·��");
			file_io.filewrite2(OutFileName, "��������·��" + createlink.getName());
			file_io.filewrite2(OutFileName, "��Ӧ��������·");
			for (Link link : createlink.getPhysicallink()) {
				file_io.filewrite_without(OutFileName, link.getName()+"  ");
			}

			String boundLink_name = null;
			Link boundlink = new Link(null, 0, null, null, null, null, 0, 0);
			if (srcnode.getIndex() < helpNode.getIndex()) {
				// ȷ����ӵ�����·��������
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

			// �����Ѿ��ɹ�����IP������· Ҫ���Ĵ˴ε���ֹ�ڵ�Ϊ�´ε���ʼ�ڵ� ���ҳ�ʼ����·����Сʣ������
			pt.setMinRemainFlowRSA(10000);
		}
		return opworkflag;
	}

	public RouteAndRegPlace optionRouteSelect(ArrayList<RouteAndRegPlace> regplaceoption,
			ArrayList<WorkandProtectRoute> wprlist) throws IOException {
		// ���㷨�ĺ���˼�� ���ﻹδ����
		// ͨ�����ĸ����㷨���ڲ�ͬ��������ѡ�� ���ﵽ�������ܵ�����
		// ��һ����дһ��final routeRSA + ��ͬ�������½���IP��· ���Ҹ��ı��㷨
		file_out_put file_io = new file_out_put();
		RouteAndRegPlace finalRoute = new RouteAndRegPlace(null, 1);
		if (regplaceoption.size() == 1) {
			finalRoute = regplaceoption.get(0);
		} else if (regplaceoption.size() != 0) {
			ArrayList<RouteAndRegPlace> RemoveRoute = new ArrayList<>();

			for (int standard = 0; standard < regplaceoption.size() - 1; standard++) {// ��׼
				RouteAndRegPlace StandardRoute = regplaceoption.get(standard);
				int StandardShareIP = 0, CompareShareIP = 0,StandardnewIP = 0, ComparenewIP=0;
				if (RemoveRoute.contains(StandardRoute))
					continue;

				for (int k = standard + 1; k < regplaceoption.size(); k++) {// �Ƚ�
					RouteAndRegPlace CompareRoute = regplaceoption.get(k);
					if (RemoveRoute.contains(CompareRoute))
						continue;

					if(StandardRoute.getNewRegList().size()>CompareRoute.getNewRegList().size()){//Ҫѡ���½��������ٵ���·
						RemoveRoute.add(StandardRoute);
						break;
					}
					else if(StandardRoute.getNewRegList().size()<CompareRoute.getNewRegList().size()){
						RemoveRoute.add(CompareRoute);// �Ƚϵ�û�б�׼��
						continue;
					}
					//�½�����������һ�� ��ʱӦѡ���½�IP������ ���ٵ�·��
					if(StandardRoute.getIPRegnode().size()>CompareRoute.getIPRegnode().size()){
						RemoveRoute.add(StandardRoute);
						break;
					}
					else if(StandardRoute.getIPRegnode().size()<CompareRoute.getIPRegnode().size()){
						RemoveRoute.add(CompareRoute);// �Ƚϵ�û�б�׼��
						continue;
					}
						
					//���ϱȽ����½������� �������ȽϹ���������
					for (Regenerator shareReg : StandardRoute.getUsedShareReg()) {
						if (shareReg.getNature() == 1)
							StandardShareIP++;
					}
//					 file_io.filewrite2(OutFileName, "��һ��ɸѡ ��׼·�ɹ����IP����������Ϊ��"+StandardShareIP);
					for (Regenerator shareReg : CompareRoute.getUsedShareReg()) {
						if (shareReg.getNature() == 1)
							CompareShareIP++;
					}
//					 file_io.filewrite2(OutFileName, "��һ��ɸѡ�Ƚ�·�ɹ����IP����������Ϊ��"+CompareShareIP);
				
					if (StandardShareIP < CompareShareIP) {
						RemoveRoute.add(StandardRoute);// ɾȥ����IP�������ٵ�·��
						break;
					}
					else if (StandardShareIP > CompareShareIP) {
						RemoveRoute.add(CompareRoute);// �Ƚϵ�û�б�׼��
						continue;
					}
				}
			}
			for (RouteAndRegPlace rag : RemoveRoute) {
				regplaceoption.remove(rag);
			}
			RemoveRoute.clear();
			// �ڶ���Ƚ�
			if (regplaceoption.size() == 1) {
				finalRoute = regplaceoption.get(0);
			} else {
				for (int standard = 0; standard < regplaceoption.size() - 1; standard++) {
					RouteAndRegPlace StandardRoute_2 = regplaceoption.get(standard);
					if (RemoveRoute.contains(StandardRoute_2))
						continue;
					// file_io.filewrite2(OutFileName, "�ڶ���ɸѡ
					// ��׼·��ʣ��������"+StandardRoute_2.getNumRemainFlow());
					for (int k = standard + 1; k < regplaceoption.size(); k++) {
						RouteAndRegPlace CompareRoute_2 = regplaceoption.get(k);
						if (RemoveRoute.contains(CompareRoute_2))
							continue;
						// file_io.filewrite2(OutFileName, "�ڶ���ɸѡ
						// �Ƚ�·��ʣ��������"+CompareRoute_2.getNumRemainFlow());
						if (StandardRoute_2.getNumRemainFlow() < CompareRoute_2.getNumRemainFlow()) {
							RemoveRoute.add(StandardRoute_2);// ɾȥʣ�������ٵ�·��
							break;
						}
						if (StandardRoute_2.getNumRemainFlow() > CompareRoute_2.getNumRemainFlow()) {
							RemoveRoute.add(CompareRoute_2);// �Ƚϵ�û�б�׼��
						}
					}
				}
				for (RouteAndRegPlace rag : RemoveRoute) {
					regplaceoption.remove(rag);
				}
				RemoveRoute.clear();

				// ������ѡ�� ѡ����ʹ��FS���ٵ�·��
				if (regplaceoption.size() == 1) {
					finalRoute = regplaceoption.get(0);
				} else {
					for (int standard = 0; standard < regplaceoption.size() - 1; standard++) {
						RouteAndRegPlace StandardRoute_3 = regplaceoption.get(standard);
						if (RemoveRoute.contains(StandardRoute_3))
							continue;
						// System.out.print("������ɸѡ ��׼·��ʹ�õ�FSΪ��");
						// System.out.println(StandardRoute_3.getnewFSnum());
						// file_io.filewrite2(OutFileName, "������ɸѡ
						// ��׼·��ʹ�õ�FSΪ��"+StandardRoute_3.getnewFSnum());

						for (int k = standard + 1; k < regplaceoption.size(); k++) {
							RouteAndRegPlace CompareRoute_3 = regplaceoption.get(k);
							if (RemoveRoute.contains(CompareRoute_3))
								continue;
							// System.out.print("������ɸѡ �Ƚ�·��ʹ�õ�FSΪ��");
							// System.out.println(CompareRoute_3.getnewFSnum());
							// file_io.filewrite2(OutFileName, "������ɸѡ
							// �Ƚ�·��ʹ�õ�FSΪ��"+CompareRoute_3.getnewFSnum());
							if (StandardRoute_3.getnewFSnum() > CompareRoute_3.getnewFSnum()) {
								RemoveRoute.add(StandardRoute_3);// ɾȥʹ��FS�϶��·��
								break;
							}
							if (StandardRoute_3.getnewFSnum() < CompareRoute_3.getnewFSnum()) {
								RemoveRoute.add(CompareRoute_3);// �Ƚϵ�û�б�׼��
							}
						}
					}
					for (RouteAndRegPlace rag : RemoveRoute) {
						regplaceoption.remove(rag);
					}
					RemoveRoute.clear();
				}
				finalRoute = regplaceoption.get(0);// ���ղ����Ƿ�ֻʣһ����· ��ѡ���һ����Ϊ������·

				// file_io.filewrite2(OutFileName,
				// "��������������ʱ��nodepairΪ"+nodepair.getName());
				// if(nodepair.getFinalRoute()!=null){
				// file_io.filewrite2(OutFileName, "������������ �ù�����·��Ҫ������");
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
