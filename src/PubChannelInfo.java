
public class PubChannelInfo {
	private String infoMsg;
	private int heartBeat;
	
	public PubChannelInfo()
	{
		this.infoMsg="";
		this.heartBeat=0;
	}
	public PubChannelInfo(String info)
	{
		this.infoMsg=info;
		this.heartBeat=5;
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
	public String GetinfoMsg()
	{
		return this.infoMsg;
	}
	public boolean equals(Object obj) {   
        if (obj instanceof PubChannelInfo) {   
        	PubChannelInfo ChanInfo = (PubChannelInfo) obj;   
            return this.infoMsg.equals(ChanInfo.infoMsg);   
        }
        else return super.equals(obj); 
	}
}
