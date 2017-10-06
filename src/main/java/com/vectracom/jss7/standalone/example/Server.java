package com.vectracom.jss7.standalone.example;

import org.apache.log4j.Logger;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.sctp.ManagementImpl;
import org.mobicents.protocols.ss7.indicator.NatureOfAddress;
import org.mobicents.protocols.ss7.indicator.RoutingIndicator;
import org.mobicents.protocols.ss7.m3ua.ExchangeType;
import org.mobicents.protocols.ss7.m3ua.Functionality;
import org.mobicents.protocols.ss7.m3ua.IPSPType;
import org.mobicents.protocols.ss7.m3ua.impl.AspImpl;
import org.mobicents.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.mobicents.protocols.ss7.m3ua.parameter.RoutingContext;
import org.mobicents.protocols.ss7.m3ua.parameter.TrafficModeType;
import org.mobicents.protocols.ss7.map.MAPStackImpl;
import org.mobicents.protocols.ss7.map.api.MAPApplicationContext;
import org.mobicents.protocols.ss7.map.api.MAPApplicationContextName;
import org.mobicents.protocols.ss7.map.api.MAPApplicationContextVersion;
import org.mobicents.protocols.ss7.map.api.MAPDialog;
import org.mobicents.protocols.ss7.map.api.MAPException;
import org.mobicents.protocols.ss7.map.api.MAPMessage;
import org.mobicents.protocols.ss7.map.api.MAPProvider;
import org.mobicents.protocols.ss7.map.api.datacoding.CBSDataCodingScheme;
import org.mobicents.protocols.ss7.map.api.dialog.MAPAbortProviderReason;
import org.mobicents.protocols.ss7.map.api.dialog.MAPAbortSource;
import org.mobicents.protocols.ss7.map.api.dialog.MAPNoticeProblemDiagnostic;
import org.mobicents.protocols.ss7.map.api.dialog.MAPRefuseReason;
import org.mobicents.protocols.ss7.map.api.dialog.MAPUserAbortChoice;
import org.mobicents.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.mobicents.protocols.ss7.map.api.primitives.AddressNature;
import org.mobicents.protocols.ss7.map.api.primitives.AddressString;
import org.mobicents.protocols.ss7.map.api.primitives.IMSI;
import org.mobicents.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.mobicents.protocols.ss7.map.api.primitives.NumberingPlan;
import org.mobicents.protocols.ss7.map.api.primitives.USSDString;
import org.mobicents.protocols.ss7.map.api.service.supplementary.ActivateSSRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.ActivateSSResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.DeactivateSSRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.DeactivateSSResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.EraseSSRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.EraseSSResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.GetPasswordRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.GetPasswordResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.InterrogateSSRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.InterrogateSSResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.MAPDialogSupplementary;
import org.mobicents.protocols.ss7.map.api.service.supplementary.ProcessUnstructuredSSRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.ProcessUnstructuredSSResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.RegisterPasswordRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.RegisterPasswordResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.RegisterSSRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.RegisterSSResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.UnstructuredSSNotifyRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.UnstructuredSSNotifyResponse;
import org.mobicents.protocols.ss7.map.api.service.supplementary.UnstructuredSSRequest;
import org.mobicents.protocols.ss7.map.api.service.supplementary.UnstructuredSSResponse;
import org.mobicents.protocols.ss7.map.datacoding.CBSDataCodingSchemeImpl;
import org.mobicents.protocols.ss7.sccp.LoadSharingAlgorithm;
import org.mobicents.protocols.ss7.sccp.OriginationType;
import org.mobicents.protocols.ss7.sccp.RuleType;
import org.mobicents.protocols.ss7.sccp.impl.SccpStackImpl;
import org.mobicents.protocols.ss7.sccp.impl.parameter.BCDEvenEncodingScheme;
import org.mobicents.protocols.ss7.sccp.impl.parameter.GlobalTitle0100Impl;
import org.mobicents.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.mobicents.protocols.ss7.sccp.parameter.EncodingScheme;
import org.mobicents.protocols.ss7.sccp.parameter.GlobalTitle;
import org.mobicents.protocols.ss7.sccp.parameter.SccpAddress;
import org.mobicents.protocols.ss7.tcap.TCAPStackImpl;
import org.mobicents.protocols.ss7.tcap.api.TCAPStack;
import org.mobicents.protocols.ss7.tcap.asn.ApplicationContextName;
import org.mobicents.protocols.ss7.tcap.asn.comp.Problem;


public class Server extends AbstractBase {
	private static Logger logger = Logger.getLogger(Server.class);

	// SCTP
	private ManagementImpl sctpManagement;
//	private NettySctpManagementImpl sctpManagement;

	// M3UA
	private M3UAManagementImpl serverM3UAMgmt;
	// SCCP
	private SccpStackImpl sccpStack;
	// TCAP
	private TCAPStack tcapStack;

	// MAP
	private MAPStackImpl mapStack;
	private MAPProvider mapProvider;

	
	private void initSCTP(IpChannelType ipChannelType) throws Exception {
		logger.debug("Initializing SCTP Stack ....");
		
		if (Configuration.Serverside == true) {
		this.sctpManagement = new ManagementImpl("Server");
//		this.sctpManagement = new NettySctpManagementImpl("Server");
//		this.sctpManagement.setSingleThread(true);
		this.sctpManagement.start();
		this.sctpManagement.removeAllResourses();

		this.sctpManagement.setConnectDelay(10000);
		// 1. Create SCTP Server
		sctpManagement.addServer(SERVER_NAME, SERVER_IP, SERVER_PORT, ipChannelType, null);

		// 2. Create SCTP Server Association
		sctpManagement
		.addServerAssociation(CLIENT_IP, CLIENT_PORT, SERVER_NAME, SERVER_ASSOCIATION_NAME, ipChannelType);
//		serverAssociation.setAssociationListener(new ServerAssociationListener());
		
		// 3. Start Server
		sctpManagement.startServer(SERVER_NAME);
		}
		else {
			this.sctpManagement = new ManagementImpl("Server");
			this.sctpManagement.setSingleThread(true);
			this.sctpManagement.start();
			this.sctpManagement.setConnectDelay(5000);
			this.sctpManagement.removeAllResourses();

			// 1. Create SCTP Association
			sctpManagement.addAssociation(SERVER_IP, SERVER_PORT, CLIENT_IP, CLIENT_PORT, 
					SERVER_ASSOCIATION_NAME, ipChannelType, null);		
		}
		logger.debug("Initialized SCTP Stack ....");
}	

	public M3UAManagementImpl getMtp3Management() {
		return this.serverM3UAMgmt;
	}

	private void initM3UA() throws Exception {
		logger.debug("Initializing M3UA Stack ....");
		
		if (Configuration.Serverside == true) {
		this.serverM3UAMgmt = new M3UAManagementImpl("Server-Mtp3UserPart", null);
		this.serverM3UAMgmt.setTransportManagement(this.sctpManagement);
	 //this.serverM3UAMgmt.setDeliveryMessageThreadCount(DELIVERY_TRANSFER_MESSAGE_THREAD_COUNT);
		this.serverM3UAMgmt.start();
		this.serverM3UAMgmt.removeAllResourses();

		// Step 1 : Create App Server

		RoutingContext rc = factory.createRoutingContext(new long[] { ROUTING_CONTEXT });
		TrafficModeType trafficModeType = factory.createTrafficModeType(TrafficModeType.Loadshare);
		this.serverM3UAMgmt.createAs("RAS1", Functionality.SGW, ExchangeType.SE, IPSPType.CLIENT, rc,
				trafficModeType, 1, null);

		// Step 2 : Create ASP
		this.serverM3UAMgmt.createAspFactory("RASP1", SERVER_ASSOCIATION_NAME);

		// Step3 : Assign ASP to AS
		this.serverM3UAMgmt.assignAspToAs("RAS1", "RASP1");

		// Step 4: Add Route. Remote point code is 2
		this.serverM3UAMgmt.addRoute(CLIENT_SPC, -1, -1, "RAS1");
	   }
		else {
			// client side configuration of M3UA
			this.serverM3UAMgmt = new M3UAManagementImpl("Server", null);
			this.serverM3UAMgmt.setTransportManagement(this.sctpManagement);
			this.serverM3UAMgmt.start();
			this.serverM3UAMgmt.removeAllResourses();

						// m3ua as create rc <rc> <ras-name>
			RoutingContext rc = factory.createRoutingContext(new long[] { ROUTING_CONTEXT });
			TrafficModeType trafficModeType = factory.createTrafficModeType(TrafficModeType.Loadshare);
			this.serverM3UAMgmt.createAs("RAS1", Functionality.IPSP, ExchangeType.SE, IPSPType.CLIENT, rc, 
							trafficModeType, 1, null);

						// Step 2 : Create ASP
			this.serverM3UAMgmt.createAspFactory("RASP1", SERVER_ASSOCIATION_NAME);
					// Step3 : Assign ASP to AS
			AspImpl asp = this.serverM3UAMgmt.assignAspToAs("RAS1", "RASP1");
				// Step 4: Add Route. Remote point code is 2
			serverM3UAMgmt.addRoute(CLIENT_SPC, -1, -1, "RAS1");	
		}
		
		logger.debug("Initialized M3UA Stack ....");
	}	

	protected SccpStackImpl getSccpStack() {
		return this.sccpStack;
	}

	private void initSCCP() throws Exception {
		logger.debug("Initializing SCCP Stack ....");
		this.sccpStack = new SccpStackImpl("Server-SccpStack");
		this.sccpStack.setMtp3UserPart(1, this.serverM3UAMgmt);

		this.sccpStack.start();
		this.sccpStack.removeAllResourses();

		 this.sccpStack.getSccpResource().addRemoteSpc(1, CLIENT_SPC, 0, 0);
         this.sccpStack.getSccpResource().addRemoteSsn(1, CLIENT_SPC,  CLIENT_SSN, 0, false);
//         this.sccpStack.getSccpResource().addRemoteSsn(2, 6632,  6, 0, false);

         this.sccpStack.getRouter().addMtp3ServiceAccessPoint(1, 1, SERVER_SPC, NETWORK_INDICATOR, 0);
         this.sccpStack.getRouter().addMtp3Destination(1, 1, CLIENT_SPC, CLIENT_SPC, 0, 255, 255);
         // configure gtt address
         EncodingScheme ec = new BCDEvenEncodingScheme();
         GlobalTitle gt = null;
         gt = new GlobalTitle0100Impl("-", 0,  ec, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,
                 NatureOfAddress.INTERNATIONAL);

         SccpAddress localAddress = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, CLIENT_SPC, 0 );
         this.sccpStack.getRouter().addRoutingAddress(1, localAddress);
         gt = new GlobalTitle0100Impl("*", 0,  ec, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,
                 NatureOfAddress.INTERNATIONAL);
      SccpAddress pattern = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, CLIENT_SPC, 0 );
         this.sccpStack.getRouter().addRule(1, RuleType.SOLITARY, LoadSharingAlgorithm.Undefined, OriginationType.LOCAL, pattern, "K", 1, -1, null, 0);

         gt = new GlobalTitle0100Impl("-", 0,  ec, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,
                 NatureOfAddress.INTERNATIONAL);
         SccpAddress remoteAddress = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, SERVER_SPC, 0 );
         this.sccpStack.getRouter().addRoutingAddress(2, remoteAddress);
         gt = new GlobalTitle0100Impl("*", 0,  ec, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,
                 NatureOfAddress.INTERNATIONAL);
       pattern = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, SERVER_SPC, 0 );
         this.sccpStack.getRouter().addRule(2, RuleType.SOLITARY, LoadSharingAlgorithm.Undefined, OriginationType.REMOTE, pattern, "K", 2, -1, null, 0);

		
		
		logger.debug("Initialized SCCP Stack ....");
	}

	private void initTCAP() throws Exception {
		logger.debug("Initializing TCAP Stack ....");
		this.tcapStack = new TCAPStackImpl("Server-TcapStack", this.sccpStack.getSccpProvider(), SERVER_SSN);
		this.tcapStack.start();
		this.tcapStack.setDialogIdleTimeout(60000);
		this.tcapStack.setInvokeTimeout(30000);
		this.tcapStack.setMaxDialogs(2000);
		logger.debug("Initialized TCAP Stack ....");
	}

	protected MAPStackImpl getMapStack() {
		return this.mapStack;
	}
	
	
	private void initMAP() throws Exception {
		logger.debug("Initializing MAP Stack ....");
		this.mapStack = new MAPStackImpl("Server-MapStack", this.tcapStack.getProvider());
	
		this.mapProvider = this.mapStack.getMAPProvider();

		this.mapProvider.addMAPDialogListener( this);


        this.mapProvider.getMAPServiceSupplementary().addMAPServiceListener(this);
        this.mapProvider.getMAPServiceSupplementary().acivate();
	
		
		this.mapStack.start();
		logger.debug("Initialized MAP Stack ....");
	}

	protected void initializeStack(IpChannelType ipChannelType) throws Exception {

		this.initSCTP(ipChannelType);

		this.initM3UA();
		this.initSCCP();
		this.initTCAP();
		this.initMAP();

		// 7. Start ASP
		serverM3UAMgmt.startAsp("RASP1");

		logger.debug("[[[[[[[[[[    Started Server       ]]]]]]]]]]");
	}

	protected void initiateUSSD() throws MAPException {
    	EncodingScheme es = new BCDEvenEncodingScheme();

     	GlobalTitle gtcalling = new GlobalTitle0100Impl(
     			"923330055101", 0, es, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, 
      			NatureOfAddress.INTERNATIONAL );

//		final SccpAddress cgpa = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, null,
	//	            SERVER_SPC, CLIENT_SSN);
    	SccpAddress cgpa = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gtcalling, SERVER_SPC, CLIENT_SSN);

      	GlobalTitle GTMSC_from_msisdn = new GlobalTitle0100Impl(
      			"923335681111", 0, es, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,
      			NatureOfAddress.INTERNATIONAL );

//		   final SccpAddress cdpa = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, null,
	//	            CLIENT_SPC, CLIENT_SSN);
	    	SccpAddress cdpa = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, GTMSC_from_msisdn,
	    			CLIENT_SPC, 8);

		
	   	  AddressString origRef = this.mapProvider.getMAPParameterFactory()
	              .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "12345");
	      AddressString destRef = this.mapProvider.getMAPParameterFactory()
	              .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "67890");
		
		// First create Dialog
		MAPDialogSupplementary mapDialog = this.mapProvider.getMAPServiceSupplementary().createNewDialog(
				MAPApplicationContext.getInstance(MAPApplicationContextName.networkUnstructuredSsContext,
						MAPApplicationContextVersion.version2), 
						cgpa, origRef, cdpa, destRef);

		//byte ussdDataCodingScheme = 0x0f;
		CBSDataCodingScheme cbs = new CBSDataCodingSchemeImpl(0x0f);
	
		// USSD String: *125*+31628839999#
		// The Charset is null, here we let system use default Charset (UTF-7 as
		// explained in GSM 03.38. However if MAP User wants, it can set its own
		// impl of Charset
		USSDString ussdString = this.mapProvider.getMAPParameterFactory().createUSSDString("*123#");

//		USSDString ussdStrObj = this.mapProvider.getMAPParameterFactory().createUSSDString(
	//			"Welcome User <CR> 1. Balance <CR> 2. Recharge");

		
		ISDNAddressString msisdn = this.mapProvider.getMAPParameterFactory().createISDNAddressString(
				AddressNature.international_number, NumberingPlan.ISDN, "31628838002");

		mapDialog.addProcessUnstructuredSSRequest(cbs, ussdString, null, msisdn);

		// This will initiate the TC-BEGIN with INVOKE component
		mapDialog.send();
	}	
	
	public static void main(String[] args) {
		logger.info("Hello Server");
		final Server server = new Server();
		try {
			server.initializeStack(IpChannelType.SCTP);
			Thread.sleep(10000);

			server.initiateUSSD();
			

			Thread.sleep(20000);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onDialogAccept(MAPDialog arg0, MAPExtensionContainer arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogClose(MAPDialog arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogDelimiter(MAPDialog arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogNotice(MAPDialog arg0, MAPNoticeProblemDiagnostic arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogProviderAbort(MAPDialog arg0, MAPAbortProviderReason arg1, MAPAbortSource arg2,
			MAPExtensionContainer arg3) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogReject(MAPDialog arg0, MAPRefuseReason arg1, ApplicationContextName arg2,
			MAPExtensionContainer arg3) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogRelease(MAPDialog arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogRequest(MAPDialog arg0, AddressString arg1, AddressString arg2, MAPExtensionContainer arg3) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogRequestEricsson(MAPDialog arg0, AddressString arg1, AddressString arg2, IMSI arg3,
			AddressString arg4) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogTimeout(MAPDialog arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogUserAbort(MAPDialog arg0, MAPUserAbortChoice arg1, MAPExtensionContainer arg2) {
		// TODO Auto-generated method stub
		
	}

	public void onActivateSSRequest(ActivateSSRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onActivateSSResponse(ActivateSSResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onDeactivateSSRequest(DeactivateSSRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onDeactivateSSResponse(DeactivateSSResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onEraseSSRequest(EraseSSRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onEraseSSResponse(EraseSSResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onGetPasswordRequest(GetPasswordRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onGetPasswordResponse(GetPasswordResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onInterrogateSSRequest(InterrogateSSRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onInterrogateSSResponse(InterrogateSSResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onProcessUnstructuredSSRequest(ProcessUnstructuredSSRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onProcessUnstructuredSSResponse(ProcessUnstructuredSSResponse procUnstrResInd) {
		try {
			logger.info(String.format("Rx ProcessUnstructuredSSResponseIndication.  USSD String=%s", procUnstrResInd
					.getUSSDString().getString(null)));
		} catch (MAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public void onRegisterPasswordRequest(RegisterPasswordRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onRegisterPasswordResponse(RegisterPasswordResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onRegisterSSRequest(RegisterSSRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onRegisterSSResponse(RegisterSSResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onUnstructuredSSNotifyRequest(UnstructuredSSNotifyRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onUnstructuredSSNotifyResponse(UnstructuredSSNotifyResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onUnstructuredSSRequest(UnstructuredSSRequest unstrReqInd) {
		try {
			logger.info(String.format("Rx UnstructuredSSRequestIndication.  USSD String=%s", unstrReqInd
					.getUSSDString().getString(null)));
		} catch (MAPException e) {
			e.printStackTrace();
		}
		
		MAPDialogSupplementary mapDialog = unstrReqInd.getMAPDialog();

		try {
		    CBSDataCodingScheme cbsDataCodingScheme = new CBSDataCodingSchemeImpl(0x0f);

			USSDString ussdString = this.mapProvider.getMAPParameterFactory().createUSSDString("1", cbsDataCodingScheme, null);
			mapDialog.addUnstructuredSSResponse(unstrReqInd.getInvokeId(), cbsDataCodingScheme, ussdString);
			mapDialog.send();

		} catch (MAPException e) {
			logger.error(String.format("Error while sending UnstructuredSSResponse for Dialog=%d",
					mapDialog.getLocalDialogId()));
		}		
		
	}

	public void onUnstructuredSSResponse(UnstructuredSSResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onErrorComponent(MAPDialog arg0, Long arg1, MAPErrorMessage arg2) {
		// TODO Auto-generated method stub
		
	}

	public void onInvokeTimeout(MAPDialog arg0, Long arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onMAPMessage(MAPMessage arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onRejectComponent(MAPDialog arg0, Long arg1, Problem arg2, boolean arg3) {
		// TODO Auto-generated method stub
		
	}

	public final class Configuration {
		  //set to false to allow compiler to identify and eliminate
		  //unreachable code
		
		  // HLR should not be set as serverside=false because client can not multiclient sctp as server
		  public static final boolean Serverside = true;
		}
	
}
