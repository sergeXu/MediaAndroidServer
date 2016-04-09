import java.net.InetAddress;

public class ChaterInfo {
	
	private InetAddress IpInfo;
	private int port;
	private int heartBeat;
	
	
	public ChaterInfo()
	{
		this.IpInfo=null;
		this.heartBeat=0;
	}
	public ChaterInfo(InetAddress info,int port)
	{
		this.IpInfo=info;
		this.port=port;
		this.heartBeat=10;
	}
	public void SetHeartBeat(int hb)
	{
		this.heartBeat=hb;
	}
	public int GetHeartBeat()
	{
		return this.heartBeat;
	}
	public void minHeartBeat()
	{
		this.heartBeat--;
	}
	public InetAddress GetIpInfo()
	{
		return this.IpInfo;
	}
	public int getPort(){
		return this.port;
	}
	
	public boolean equals(Object obj) {   
        if (obj instanceof ChaterInfo) {   
        	ChaterInfo ChanInfo = (ChaterInfo) obj;   
            return (this.IpInfo.toString().equals(ChanInfo.IpInfo.toString()))&&(this.port==ChanInfo.port);   
        }
        else return super.equals(obj); 
	}
}
