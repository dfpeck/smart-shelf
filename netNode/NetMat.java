package netNode;

/** @brief extends netClient to specify its identity as a mat*/
public class NetMat extends NetClient {
	public NetMat(String ip){
		super(ip);
		identity = "mat~";	
	}
}