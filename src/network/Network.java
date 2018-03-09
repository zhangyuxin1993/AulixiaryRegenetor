package network;

import general.CommonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;


public class Network extends CommonObject{
	private int nature;
	
	private HashMap<String, Layer> layerlist = null;// the list of layers contained within the network
		
	
	public Network(String name, int index, String comments) {
		super(name, index, comments);
		this.layerlist = new HashMap<String, Layer>(10);
		
		/**
		 * add layers to the network
		 */
		/*physical layer*/
		Layer physical = new Layer("Physical", 0, "", this);
		this.addLayer(physical);
		physical.setAssociateNetwork(this);
		physical.setServerlayer(null);
		/*layer0*/
		Layer layer0 = new Layer("Layer0", 1, "", this);
		this.addLayer(layer0);
		physical.setClientlayer(layer0);
		layer0.setAssociateNetwork(this);
		layer0.setServerlayer(physical);
	}

	
		
	
		public HashMap<String, Layer> getLayerlist() {
		return layerlist;
	}
	public void setLayerlist(HashMap<String, Layer> layerlist) {
		this.layerlist = layerlist;
	}
	
	
	/**
	 * add one layer to the network
	 */
	public void addLayer(Layer layer){
		this.layerlist.put(layer.getName(), layer);
		layer.setAssociateNetwork(this);
	}	
	/**
	 * remove the layer from the network
	 */
	public void removeLayer(String layername){
		this.layerlist.get(layername).setAssociateNetwork(null);
		this.layerlist.remove(layername);
	}
	/**
	 * read in physical topology from a data file csv file
	 * and meanwhile generate node pairs for each layer
	 */
	
	public void readPhysicalTopology(String filename){
		
		Layer layer = this.getLayerlist().get("Physical");
		String[] data = new String[10];
		File file = new File(filename);
		BufferedReader bufRdr = null;
		try {
			bufRdr = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line = null;
		int col = 0;
		try {
			line = bufRdr.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//read the first title line
		//read each line of text file
		try {
			boolean link = false;
			while((line = bufRdr.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line,",");
				while (st.hasMoreTokens()){
					data[col] = st.nextToken();
					col++;
				}
				col=0;
				String name = data[0];
				if(name.equals("Link")){
					link=true;						
				}
				if(!link)//node operation
				{
					int x = Integer.parseInt(data[1]);
					int y = Integer.parseInt(data[2]);					
					int index = layer.getNodelist().size();
					Node newnode = new Node(name, index, "", layer, x, y);
					layer.addNode(newnode);
				}
				else{ //link operation
					if(!(name.equals("Link"))){
						Node nodeA = layer.getNodelist().get(data[1]);						
						Node nodeB = layer.getNodelist().get(data[2]);
						double length = Double.parseDouble(data[3]);
						double cost = Double.parseDouble(data[4]);
						int index = layer.getLinklist().size();
						Link newlink = new Link(name,index,"",layer,nodeA,nodeB,length,cost);
						layer.addLink(newlink);
						//update the neighbor node list
						//nodeA.addNeiNode(nodeB);
						//nodeB.addNeiNode(nodeA);
					}					
				}				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		try {
			bufRdr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/**
	 * copy nodes to all the other layers from the physical layer
	 */
	public void copyNodes(){
		Layer layer = this.getLayerlist().get("Physical");
		Layer currentlayer = this.getLayerlist().get("Layer0");
		currentlayer.copyNodes(layer);
//		Layer currentlayer2 = this.getLayerlist().get("ipcopylayer");
//		currentlayer2.copyNodes(layer);
		
	}
	/**
	 * create node pairs for each layer
	 */
	public void createNodepair(){
		HashMap<String, Layer> map = this.getLayerlist();
		Iterator<String> iter = map.keySet().iterator();
		while (iter.hasNext()) {
		    Layer layer = (Layer)(map.get(iter.next()));
		    layer.generateNodepairs();
		} 
	}
	

}


