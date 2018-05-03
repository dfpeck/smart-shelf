package netNode;

/** @brief extends netClient to specify its identity as a UI*/
public class NetUI extends NetClient {
	public NetUI(String ip){
		super(ip);
		identity = "ui~";	
	}
}