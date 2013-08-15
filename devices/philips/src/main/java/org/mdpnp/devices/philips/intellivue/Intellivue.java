package org.mdpnp.devices.philips.intellivue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.mdpnp.devices.philips.intellivue.action.ExtendedPollDataRequest;
import org.mdpnp.devices.philips.intellivue.action.ExtendedPollDataResult;
import org.mdpnp.devices.philips.intellivue.action.SinglePollDataRequest;
import org.mdpnp.devices.philips.intellivue.action.SinglePollDataResult;
import org.mdpnp.devices.philips.intellivue.action.impl.ExtendedPollDataRequestImpl;
import org.mdpnp.devices.philips.intellivue.action.impl.SinglePollDataRequestImpl;
import org.mdpnp.devices.philips.intellivue.association.AssociationAbort;
import org.mdpnp.devices.philips.intellivue.association.AssociationAccept;
import org.mdpnp.devices.philips.intellivue.association.AssociationConnect;
import org.mdpnp.devices.philips.intellivue.association.AssociationDisconnect;
import org.mdpnp.devices.philips.intellivue.association.AssociationFinish;
import org.mdpnp.devices.philips.intellivue.association.AssociationMessage;
import org.mdpnp.devices.philips.intellivue.association.AssociationRefuse;
import org.mdpnp.devices.philips.intellivue.association.impl.AssociationConnectImpl;
import org.mdpnp.devices.philips.intellivue.association.impl.AssociationDisconnectImpl;
import org.mdpnp.devices.philips.intellivue.association.impl.AssociationFinishImpl;
import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.devices.philips.intellivue.attribute.AttributeFactory;
import org.mdpnp.devices.philips.intellivue.connectindication.ConnectIndication;
import org.mdpnp.devices.philips.intellivue.data.AttributeId;
import org.mdpnp.devices.philips.intellivue.data.IPAddressInformation;
import org.mdpnp.devices.philips.intellivue.data.Label;
import org.mdpnp.devices.philips.intellivue.data.MdibObjectSupport;
import org.mdpnp.devices.philips.intellivue.data.NomPartition;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.data.ObjectClass;
import org.mdpnp.devices.philips.intellivue.data.PollProfileSupport;
import org.mdpnp.devices.philips.intellivue.data.ProtocolSupport;
import org.mdpnp.devices.philips.intellivue.data.ProtocolSupport.ProtocolSupportEntry;
import org.mdpnp.devices.philips.intellivue.data.RelativeTime;
import org.mdpnp.devices.philips.intellivue.data.TextIdList;
import org.mdpnp.devices.philips.intellivue.dataexport.CommandType;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportInvoke;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportMessage;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportResult;
import org.mdpnp.devices.philips.intellivue.dataexport.ModifyOperator;
import org.mdpnp.devices.philips.intellivue.dataexport.command.Action;
import org.mdpnp.devices.philips.intellivue.dataexport.command.ActionResult;
import org.mdpnp.devices.philips.intellivue.dataexport.command.CommandFactory;
import org.mdpnp.devices.philips.intellivue.dataexport.command.EventReport;
import org.mdpnp.devices.philips.intellivue.dataexport.command.Get;
import org.mdpnp.devices.philips.intellivue.dataexport.command.GetResult;
import org.mdpnp.devices.philips.intellivue.dataexport.command.Set;
import org.mdpnp.devices.philips.intellivue.dataexport.command.SetResult;
import org.mdpnp.devices.philips.intellivue.dataexport.impl.DataExportInvokeImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.impl.DataExportResultImpl;
import org.mdpnp.devices.philips.intellivue.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Intellivue implements NetworkConnection {
	public static final int BROADCAST_PORT = 24005;
	public static final int DEFAULT_UNICAST_PORT = 24105;
	public static final int BUFFER_SIZE = 5000;
	
	protected final NetworkLoop networkLoop;
	private final ByteBuffer inBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
	private final ByteBuffer outBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
	private final Protocol protocol = new CompoundProtocol();
	
	private SelectionKey conn;
//	private final java.util.Set<SelectionKey> beacons = new HashSet<SelectionKey>();
	
	public Intellivue(NetworkLoop networkLoop) {
		this.networkLoop = networkLoop;
		
		inBuffer.order(ByteOrder.BIG_ENDIAN);
		outBuffer.order(ByteOrder.BIG_ENDIAN);

	}
	protected static final Logger log = LoggerFactory.getLogger(Intellivue.class);
	public Intellivue() throws IOException {
		this(new NetworkLoop());
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				networkLoop.runLoop();
				log.debug("runLoop ended");
			}
		}, "Network Loop");
		t.setDaemon(true);
		t.start();
	}
	
	
	private SocketAddress associated = null;
	
	
	protected void transientDisassociation(InetAddress lastRemote, int prefixLength) {
		
	}
	private int connected_port;
	public void reconnect() throws IOException {
		long start = System.currentTimeMillis();
		
		// Send a few finished messages and maybe we'll get a disconnect
		synchronized(this) {
			networkLoop.clearTasks();
			while(associated!=null && (System.currentTimeMillis()-start) <= 2000L) {
				send(new AssociationFinishImpl());

				try {
					
					wait(500L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// In this case we'll call it disassociation
			this.associated = null;
		}
		
		this.lastKeepAliveRecvInvokeId = -1;
		this.lastKeepAliveSentInvokeId = -1;
		transientDisassociation(lastRemote, lastPrefixLength);
		connect(lastRemote, lastPrefixLength, connected_port);
	}
	
	
	public void disconnect() throws IOException {
		long start = System.currentTimeMillis();
		
		synchronized(this) {
			networkLoop.clearTasks();
			while(associated!=null && (System.currentTimeMillis()-start) <= 5000L) {
				send(new AssociationFinishImpl());
				try {
					wait(500L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		// TODO I'm not sure I want to put the NetworkLoop in a final state
//		networkLoop.cancelThreadAndWait();
	}
	protected static final String lineWrap(String str) {
		return lineWrap(str, CHARS_PER_LINE);
	}
	protected static final String lineWrap(String str, int width) {
		StringBuilder sb = new StringBuilder(str);
		for(int i = sb.length() / width; i > 0; i--) {
			sb.insert(i * width, "\n");
		}
		return sb.toString();
	}
	
	private static final int CHARS_PER_LINE = 140;

	protected void handle(SocketAddress sockaddr, Message message, SelectionKey sk) {
		if(null == message) {
			return;
		}
		

		if(log.isTraceEnabled()) {
			time.setTime(System.currentTimeMillis());
//			log.trace("In Message("+simpleDateformat.format(time)+"):\n"+lineWrap(message.toString()));
		}
		if(message instanceof DataExportMessage) {
			handle((DataExportMessage)message);
		} else 	if(message instanceof AssociationMessage) {
			handle(sockaddr, (AssociationMessage)message);
		} else if(message instanceof ConnectIndication) {
			handle((ConnectIndication)message, sk);
		}
	}
	private int lastKeepAliveSentInvokeId = -1;
	private int lastKeepAliveRecvInvokeId = -1;
	protected void handle(SocketAddress sockaddr, AssociationAccept message) {
		PollProfileSupport pps = message.getUserInfo().getPollProfileSupport();
		log.debug("Negotiated " + pps.getMinPollPeriod().toMilliseconds() + "ms min poll period, timeout="+minPollPeriodToTimeout(pps.getMinPollPeriod().toMilliseconds()));
		log.debug("Negotiated " + pps.getMaxMtuTx() + " " + pps.getMaxMtuRx() + " " + pps.getMaxBwTx());
		keepAliveTask = new TaskQueue.TaskImpl<Object>() {
			public Object doExecute(TaskQueue queue) {
				if(lastKeepAliveSentInvokeId >= 0) {
					if(lastKeepAliveSentInvokeId>=0) {
						if(lastKeepAliveSentInvokeId != lastKeepAliveRecvInvokeId) {
							log.error("lastKeepAliveSentInvokeId="+lastKeepAliveSentInvokeId+" != lastKeepAliveRecvInvokeId="+lastKeepAliveSentInvokeId);
							log.error("NOT FAILING ON MISMATCHED KEEPALIVES");
//							try {
//								reconnect();
//							} catch (IOException e) {
//								throw new RuntimeException(e);
//							}
							return null;
						}
					}
				}
				lastKeepAliveSentInvokeId = requestKeepAlive();
				return null;
			}
		};
		
		synchronized(this) {
			associated = sockaddr;
			this.notifyAll();
		}
		
		keepAliveTask.setInterval(minPollPeriodToTimeout(pps.getMinPollPeriod().toMilliseconds())-100L);
		keepAliveTask.setScheduledTime(System.currentTimeMillis()+keepAliveTask.getInterval()-100L);
		networkLoop.add(keepAliveTask);
		
	}

	protected synchronized void handle(SocketAddress sockaddr, AssociationFinish message) {
		AssociationDisconnect disconn = new AssociationDisconnectImpl();
		send(disconn);
		associated = null;
		this.notifyAll();
		
	}
	
	protected synchronized void handle(SocketAddress sockaddr, AssociationRefuse message) {
		associated = null;
		this.notifyAll();
	}
	
	protected synchronized void handle(SocketAddress sockaddr, AssociationDisconnect message) {
		associated = null;
		this.notifyAll();
	}
	
	protected synchronized void handle(SocketAddress sockaddr, AssociationAbort message) {
		associated = null;
		this.notifyAll();
	}
	
	protected synchronized void handle(SocketAddress sockaddr, AssociationConnect message) {
		associated = sockaddr;
		this.notifyAll();
		log.debug("Received connect:"+message);
		
		
		
	}
	
	protected void handle(SocketAddress sockaddr, AssociationMessage message) {

		switch(message.getType()) {
		case Connect:
			handle(sockaddr, (AssociationConnect) message);
			break;
		case Accept:
			handle(sockaddr, (AssociationAccept) message);
			break;
		case Refuse:
			handle(sockaddr, (AssociationRefuse)message);
			break;
		case Disconnect:
			handle(sockaddr, (AssociationDisconnect)message);
			break;
		case Abort:
			handle(sockaddr, (AssociationAbort)message);
			break;
		case Finish:
			handle(sockaddr, (AssociationFinish)message);
			break;
		default:									
			break;
		}
	}
	
	public String[] listenForConnectIndication() throws IOException {
		List<Network.AddressSubnet> broadcastAddresses = Network.getBroadcastAddresses();
		if(broadcastAddresses.isEmpty()) {
			return null;
		} else {
			List<String> hosts = new ArrayList<String>();
			for(Network.AddressSubnet address : broadcastAddresses) {
				final DatagramChannel channel = DatagramChannel.open();
				channel.configureBlocking(false);
				channel.socket().setReuseAddress(true);
				channel.socket().bind(new InetSocketAddress(address.getInetAddress(), BROADCAST_PORT));
				networkLoop.register(this, channel);

				hosts.add(address.getInetAddress().getHostAddress());
			}
			return hosts.toArray(new String[0]);
		}
	}
	
	public static boolean isAcceptable(ProtocolSupportEntry pse) {
		switch(pse.getAppProtocol()) {
		case DataOut:
			break;
		default:
			return false;
		}
		
		switch(pse.getTransProtocol()) {
		case UDP:
			break;
		default:
			return false;
		}
		
		return true;
	}
	public static ProtocolSupportEntry acceptable(ConnectIndication connectIndication) {
		ProtocolSupport ps = connectIndication.getProtocolSupport();
		if(null == ps || ps.getList().isEmpty()) {
			return null;
		}
		for(ProtocolSupportEntry e : ps.getList()) {
			if(isAcceptable(e)) {
				return e;
			}
		}
		return null;
	}
	
	protected void handle(ConnectIndication connectIndication, SelectionKey sk) {
		log.trace("REceived a connectindication:"+connectIndication);
		IPAddressInformation ipinfo = connectIndication.getIpAddressInformation();
		ProtocolSupportEntry pse = acceptable(connectIndication);
		if(null != ipinfo && null != pse) {
			try {
				InetAddress remote = ipinfo.getInetAddress();
				int prefixLength = Network.prefixCount(connectIndication.getIpAddressInformation().getSubnetMask());
				synchronized(this) {
					if(associated!=null) {
						return;
					}
				}
				connect(remote, prefixLength, pse.getPortNumber());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			
		}
	}
	
	private int invoke = 0;
	private int poll = 0;
	
	private final static int MAX_U_SHORT = (1<<Short.SIZE);
	
	private synchronized int nextInvoke() {
		return (invoke=++invoke>=MAX_U_SHORT?0:invoke);
	}
	
	private synchronized int nextPoll() {
		return (poll=++poll>=MAX_U_SHORT?0:poll);
	}
	
	@SuppressWarnings("unused")
    private synchronized int lastPoll() {
		return poll;
	}
	
	private TaskQueue.Task<Object> keepAliveTask;
	
	public int requestKeepAlive() {
		return requestSinglePoll(ObjectClass.NOM_MOC_VMO_AL_MON, AttributeId.NOM_ATTR_GRP_VMO_STATIC);
	}
	
	public int requestSinglePoll(ObjectClass objectType, AttributeId attrGroup) {
		int invoke = nextInvoke();
		DataExportInvoke message = new DataExportInvokeImpl();
		message.setCommandType(CommandType.ConfirmedAction);
		message.setInvoke(invoke);

		Action action = (Action) CommandFactory.buildCommand(CommandType.ConfirmedAction, false);
		action.getManagedObject().setOidType(OIDType.lookup(ObjectClass.NOM_MOC_VMS_MDS.asInt()));
		action.getManagedObject().getGlobalHandle().setMdsContext(0);
		action.getManagedObject().getGlobalHandle().setHandle(0);
		action.setScope(0);
		action.setActionType(OIDType.lookup(ObjectClass.NOM_ACT_POLL_MDIB_DATA.asInt()));
		
		message.setCommand(action);
		
		SinglePollDataRequest req = new SinglePollDataRequestImpl();
		req.setPollNumber(nextPoll());
		req.setPolledAttributeGroup(null==attrGroup?OIDType.lookup(0):attrGroup.asOid());
		req.getPolledObjectType().setNomPartition(NomPartition.Object);
		req.getPolledObjectType().setOidType(OIDType.lookup(objectType.asInt()));
		
		action.setAction(req);
		
		send(message);
		
		return invoke;
	}
	
	public int requestExtendedPoll(ObjectClass objectType, Long time) {
		return requestExtendedPoll(objectType, time, null);
	}
	public int requestExtendedPoll(ObjectClass objectType, Long time, AttributeId attrGroup) {
		
		int invoke = nextInvoke();
		DataExportInvoke message = new DataExportInvokeImpl();
		message.setCommandType(CommandType.ConfirmedAction);
		message.setInvoke(invoke);
		
		Action action = (Action) CommandFactory.buildCommand(CommandType.ConfirmedAction, false);
		action.getManagedObject().setOidType(OIDType.lookup(ObjectClass.NOM_MOC_VMS_MDS.asInt()));
		action.getManagedObject().getGlobalHandle().setMdsContext(0);
		action.getManagedObject().getGlobalHandle().setHandle(0);
		action.setScope(0);
		action.setActionType(OIDType.lookup(ObjectClass.NOM_ACT_POLL_MDIB_DATA_EXT.asInt()));
		
		message.setCommand(action);


		
		ExtendedPollDataRequest req = new ExtendedPollDataRequestImpl();
		req.setPollNumber(nextPoll());
		req.setPolledAttributeGroup(null==attrGroup?OIDType.lookup(0):attrGroup.asOid());
		
		req.getPolledObjectType().setNomPartition(NomPartition.Object);
		req.getPolledObjectType().setOidType(OIDType.lookup(objectType.asInt()));

		if(null != time) {
			Attribute<RelativeTime> timePeriod = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_TIME_PD_POLL, RelativeTime.class);
			timePeriod.getValue().fromMilliseconds(time);
			req.getPollExtra().add(timePeriod);
		}
		
		action.setAction(req);
		
		send(message);
		
		return invoke;
	}
	
	public int requestGet(OIDType oids) {
		return requestGet(Arrays.asList(new OIDType[] {oids}));
	}
	
	public int requestGet(OIDType[] oids) {
		return requestGet(Arrays.asList(oids));
	}
	
	public int requestGet(List<OIDType> oids) {
		int invoke = nextInvoke();
		
		DataExportInvoke message = new DataExportInvokeImpl();
		message.setCommandType(CommandType.Get);
		message.setInvoke(invoke);
		
		Get get = (Get) CommandFactory.buildCommand(CommandType.Get, false);
		get.getManagedObject().setOidType(OIDType.lookup(ObjectClass.NOM_MOC_VMS_MDS.asInt()));
		get.getManagedObject().getGlobalHandle().setMdsContext(0);
		get.getManagedObject().getGlobalHandle().setHandle(0);
		get.getAttributeId().addAll(oids);
		message.setCommand(get);
		send(message);
		return invoke;
	}
	
	public int requestSet(Label[] numerics, Label[] realtimeSampleArrays) {
		int invoke;
		DataExportInvoke message = new DataExportInvokeImpl();
		message.setCommandType(CommandType.ConfirmedSet);
		message.setInvoke(invoke = nextInvoke());
		Set set = (Set) CommandFactory.buildCommand(CommandType.ConfirmedSet, false);
		set.getManagedObject().setOidType(ObjectClass.NOM_MOC_VMS_MDS);
		set.getManagedObject().getGlobalHandle().setMdsContext(0);
		set.getManagedObject().getGlobalHandle().setHandle(0);
		
		if(numerics != null) {
			Attribute<TextIdList> ati = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_POLL_NU_PRIO_LIST, TextIdList.class);
			for(Label l : numerics) {
				ati.getValue().addTextId(l.asLong());
			}
			set.add(ModifyOperator.Replace, ati);
		}
		
		
		if(realtimeSampleArrays != null) {
			Attribute<TextIdList> ati = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_POLL_RTSA_PRIO_LIST, TextIdList.class);
			for(Label l : realtimeSampleArrays) {
				ati.getValue().addTextId(l.asLong());
			}
			set.add(ModifyOperator.Replace, ati);
		}

		message.setCommand(set);
		send(message);
		
		return invoke;
	}
	
	protected void handle(EventReport eventReport, boolean confirm) {
		if(confirm) {
			{
				DataExportResult message = new DataExportResultImpl();
				message.setCommandType(CommandType.ConfirmedEventReport);
				message.setInvoke(eventReport.getMessage().getInvoke());
				message.setCommand(eventReport.createConfirm());
				
				send(message);
			}
		}
		if(log.isTraceEnabled()) {
		    log.trace(eventReport.toString());
		}
	}
	
	@SuppressWarnings("incomplete-switch")
    protected void handle(SinglePollDataResult result) {
		switch(ObjectClass.valueOf(result.getPolledObjType().getOidType().getType())) {
			case NOM_MOC_VMO_AL_MON:
				switch(AttributeId.valueOf(result.getPolledAttributeGroup().getType())) {
				case NOM_ATTR_GRP_VMO_STATIC:
					lastKeepAliveRecvInvokeId = result.getAction().getMessage().getInvoke();
					break;
				}
		}
		
	}
	
	protected void handle(ExtendedPollDataResult result) {
		
	}
	protected void handle(SetResult result, boolean confirmed) {
	
	}
	
	
	protected void handle(Get get) {
		
	}
	protected void handle(Set set, boolean confirmed) {
		if(confirmed) {
			DataExportResult message = new DataExportResultImpl();
			message.setCommandType(CommandType.ConfirmedSet);
			message.setInvoke(set.getMessage().getInvoke());
			message.setCommand(set.createResult());
			send(message);
		}

		
	}
	
	protected void handle(GetResult result) {
	}
	
	protected void handle(ActionResult action, boolean request) {
		ObjectClass objectclass = ObjectClass.valueOf(action.getActionType().getType());

		if(null == objectclass) {
			return;
		}
		if(!request) {
			switch(objectclass) {
			case NOM_ACT_POLL_MDIB_DATA:
				handle((SinglePollDataResult) action.getAction());
				break;
			case NOM_ACT_POLL_MDIB_DATA_EXT:
				handle((ExtendedPollDataResult) action.getAction());
				break;
			default:
				log.warn("Unknown action="+action);
				break;
			}
		} else {
			switch(objectclass) {
			case NOM_ACT_POLL_MDIB_DATA:
				handle((SinglePollDataRequest) action.getAction());
				break;
			case NOM_ACT_POLL_MDIB_DATA_EXT:
				handle((ExtendedPollDataRequest) action.getAction());
				break;
			default:
				log.warn("Unknown action="+action);
				break;
			}
		}
	}
	
	protected void handle(SinglePollDataRequest action) {
		
	}


	protected void handle(ExtendedPollDataRequest action) {
	
	}


	protected void handle(DataExportResult message) {
		switch(message.getCommandType()) {
		case ConfirmedAction:
			handle((ActionResult) message.getCommand(), false);
			break;
		case Get:
			handle((GetResult) message.getCommand());
			break;
		case ConfirmedSet:
			handle( (SetResult) message.getCommand(), true);
			break;
		case Set:
			handle( (SetResult) message.getCommand(), false);
			break;
		default:
			log.warn("Unknown CommandType="+message.getCommandType());
			break;
		}
	}
	
	protected void handle(DataExportInvoke message) {
		switch(message.getCommandType()) {
		case ConfirmedEventReport:
			handle((EventReport) message.getCommand(), true);
			break;
		case EventReport:
			handle((EventReport) message.getCommand(), false);
			break;
		case ConfirmedAction:
			handle((Action) message.getCommand(), true);
			break;
		case ConfirmedSet:
			handle((Set)message.getCommand(), true);
			break;
		case Get:
			handle((Get)message.getCommand());
			break;
		case Set:
			handle((Set)message.getCommand(), false);
			break;
		default:
			log.warn("Unknown invoke="+message);
			break;
		}
	}
	
	protected void handle(DataExportMessage message) {
		if(null == message) {
			return;
		}
		switch(message.getRemoteOperation()) {
		case Invoke:
			handle((DataExportInvoke) message);
			break;
		case Result:
		case LinkedResult:
			handle((DataExportResult) message);
			break;
		default:
			log.warn("Unknown remoteOperation:"+message.getRemoteOperation());
			break;
		}
		
	}
	
	private static long minPollPeriodToTimeout(long minPollPeriod) {
		if(minPollPeriod<=3300L) {
			return 10000L;
		} else if(minPollPeriod <= 43000L) {
			return 3 * minPollPeriod;
		} else {
			return 130000L;
		}
	}
	
	private void requestAssociation() {
		AssociationConnect req = new AssociationConnectImpl();
		PollProfileSupport pps = req.getUserInfo().getPollProfileSupport();
		
		pps.getMinPollPeriod().fromMilliseconds(500L);
		MdibObjectSupport obj = req.getUserInfo().getMdibObjectSupport();
		obj.addClass(ObjectClass.NOM_MOC_VMS_MDS, 1);
		obj.addClass(ObjectClass.NOM_MOC_VMO_METRIC_NU, 0xC9);
		obj.addClass(ObjectClass.NOM_MOC_VMO_METRIC_SA_RT, 0x3C);
		obj.addClass(ObjectClass.NOM_MOC_VMO_METRIC_ENUM, 0x10);
		obj.addClass(ObjectClass.NOM_MOC_PT_DEMOG, 1);
		obj.addClass(ObjectClass.NOM_MOC_VMO_AL_MON, 1);
		
//		obj.addClass(ObjectClass.NOM_MOC_SCAN, 1);
//		obj.addClass(ObjectClass.NOM_MOC_SCAN_CFG, 1);
//		obj.addClass(ObjectClass.NOM_MOC_SCAN_CFG_EPI, 1);
//		obj.addClass(ObjectClass.NOM_MOC_SCAN_CFG_PERI, 1);
//		obj.addClass(ObjectClass.NOM_MOC_SCAN_CFG_PERI_FAST, 1);
		
//		obj.addClass(ObjectClass.NOM_MOC_SCAN_UCFG, 1);
//		obj.addClass(ObjectClass.NOM_MOC_SCAN_UCFG_ALSTAT, 1);
//		obj.addClass(ObjectClass.NOM_MOC_SCAN_UCFG_CTXT, 1);
		

		send(req);


	}
	
	
	private List<Message> messageQueue = new ArrayList<Message>();
	public void connect(InetAddress remote) throws IOException {
		connect(remote, -1, DEFAULT_UNICAST_PORT);
	}
	
	private InetAddress lastRemote;
	private int lastPrefixLength;
	
	
	public void register(SelectableChannel channel) throws ClosedChannelException {
		this.conn = networkLoop.register(this, channel);
	}
	
	public void connect(InetAddress remote, int prefixLength, int port) throws IOException {
		InetAddress local = null;
		
		lastRemote = remote;
		lastPrefixLength = prefixLength;
	
		this.connected_port = port;
		
		final DatagramChannel channel = DatagramChannel.open();
		channel.configureBlocking(false);
		channel.socket().setReuseAddress(true);
		
		if(prefixLength >= 0) {
			local = Network.getLocalAddresses(remote, (short)prefixLength, true).get(0);
			channel.socket().bind(new InetSocketAddress(local, 0));
		}
		
		channel.connect(associated = new InetSocketAddress(remote, port));

		conn = networkLoop.register(this, channel);

		requestAssociation();
	}
	
	public static void main(String[] args) throws IOException {
//		System.setProperty("java.net.preferIPv4Stack","true"); 
//		NetworkLoop loop = new NetworkLoop();
//		final Intellivue intellivue = new Intellivue();
//		JFrame frame = new JFrame("INTELLIVUE");
//		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
//		frame.addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosing(WindowEvent e) {
//				super.windowClosing(e);
//				try {
//					intellivue.disconnect();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//			}
//		});
//		frame.setSize(640, 480);
//		
//		frame.setVisible(true);
//		
////		intellivue.listenForConnectIndication();
//		intellivue.connect(InetAddress.getByAddress(new byte[] {-64, -88, 1, -57}), 24);
//
//		
//		loop.runLoop();
//		
//		System.exit(0);
	}
	
	private static final Method getRemoteAddress;
	static {
		Method m = null;
		try {
			m = DatagramChannel.class.getMethod("getRemoteAddress");
		} catch(Exception e) {
			try {
				m = DatagramChannel.class.getMethod("getRemoteSocketAddress");
			} catch(Exception e1) {
				m = null;
			}
		}
		getRemoteAddress = m;
	}
	
	private static final SocketAddress getRemoteAddress(DatagramChannel channel) {
		try {
			return (SocketAddress) getRemoteAddress.invoke(channel);
		} catch (Exception e) {
			return null;
		}
	}
	
	
	private final synchronized int write(DatagramChannel channel, Message message) throws IOException {
		outBuffer.clear();
		protocol.format(message, outBuffer);
		outBuffer.flip();
		outBuffer.mark();
		if(associated != null) {
			int cnt = channel.send(outBuffer, associated);
			
			if(cnt > 0 && log.isTraceEnabled()) {
				outBuffer.reset();
				time.setTime(System.currentTimeMillis());
				log.trace("To " + associated + " at " + simpleDateformat.format(time));
				log.trace(Util.dump(outBuffer, 20));
			}
			return cnt;
		} else {
			return -1;
		}
		
	}
	
	@Override
	public synchronized void write(SelectionKey sk) throws IOException {
		@SuppressWarnings("unused")
        DatagramChannel channel = (DatagramChannel) sk.channel();
		
		Message message = null;
		
		message = messageQueue.isEmpty() ? null : messageQueue.remove(0);
		
		if(write((DatagramChannel) sk.channel(), message)==0) {
			messageQueue.add(0, message);
		}
		
		if(messageQueue.isEmpty()) {
			sk.interestOps(sk.interestOps() & ~SelectionKey.OP_WRITE);
		} else {
			sk.interestOps(sk.interestOps() | SelectionKey.OP_WRITE);
		}
	}
	
	/**
	 * Called externally to send a message
	 * @param message
	 * @return
	 * @throws IOException
	 */
	public synchronized boolean send(Message message) {
		if(null == message) {
			return false;
		}
		if(null == associated) {
			return false;
		}
		if(log.isTraceEnabled()) {
			time.setTime(System.currentTimeMillis());
			log.trace("Out Message(" + simpleDateformat.format(time) + "):\n"+lineWrap(message.toString()));
		}
		
		// Try to write the datagram, if unavailable then set interestOps
		try {
			int cnt = write((DatagramChannel) conn.channel(), message); 
			if(cnt==0) {
				conn.interestOps(conn.interestOps() | SelectionKey.OP_WRITE);
				messageQueue.add(message);
				networkLoop.wakeup();
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			try {
				reconnect();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return false;
		}

	}
	
	private final Date time = new Date();
	private final DateFormat simpleDateformat = new SimpleDateFormat("HH:mm:ss.SSS");
	

	@Override
	public void read(SelectionKey sk) throws IOException {
		if(sk.channel() instanceof DatagramChannel) {
			DatagramChannel channel = (DatagramChannel) sk.channel();

			inBuffer.clear();
			SocketAddress sockaddr = channel.receive(inBuffer);
			inBuffer.flip();
			if(inBuffer.hasRemaining()) {
				if(log.isTraceEnabled()) { 
					time.setTime(System.currentTimeMillis());
					log.trace("From " + getRemoteAddress(channel) + " on " + channel.socket().getLocalAddress() + " at " + simpleDateformat.format(time));
					log.trace(Util.dump(inBuffer, 20));
				}
				handle(sockaddr, protocol.parse(inBuffer), sk);
			}
		}
	}

}
