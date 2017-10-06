package com.vectracom.jss7.standalone.example;

import org.apache.log4j.Logger;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.sctp.AssociationImpl;
import org.mobicents.protocols.sctp.ManagementImpl;
import org.mobicents.protocols.ss7.m3ua.As;
import org.mobicents.protocols.ss7.m3ua.Asp;
import org.mobicents.protocols.ss7.m3ua.AspFactory;
import org.mobicents.protocols.ss7.m3ua.M3UAManagementEventListener;
import org.mobicents.protocols.ss7.m3ua.State;
import org.mobicents.protocols.ss7.map.api.MAPDialog;
import org.mobicents.protocols.ss7.map.api.MAPMessage;
import org.mobicents.protocols.ss7.map.api.errors.MAPErrorMessage;
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
import org.mobicents.protocols.ss7.map.api.service.supplementary.MAPServiceSupplementaryListener;
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
import org.mobicents.protocols.ss7.sccp.RemoteSccpStatus;
import org.mobicents.protocols.ss7.sccp.SccpListener;
import org.mobicents.protocols.ss7.sccp.SignallingPointStatus;
import org.mobicents.protocols.ss7.sccp.message.SccpDataMessage;
import org.mobicents.protocols.ss7.sccp.message.SccpNoticeMessage;
import org.mobicents.protocols.ss7.tcap.asn.comp.Problem;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class SimpleTest {
	private static Logger logger = Logger.getLogger(SimpleTest.class);
	
	private Client client = null;
	private Server server = null;
	
	private AssociationImpl serverAssociation = null;
	private AssociationImpl clientAssociation = null;
	
	private ManagementImpl clientManagement = null;
	
	private SignallingPointStatus clientSignallingPointStatus = null;
	private SignallingPointStatus serverSignallingPointStatus = null;
	
	private ProcessUnstructuredSSRequest processUnstructuredSSRequest = null;
	private UnstructuredSSRequest unstructuredSSRequest = null;
	private UnstructuredSSResponse unstructuredSSResponse = null;
	private ProcessUnstructuredSSResponse processUnstructuredSSResponse = null;
	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}
	
	public void setUp() throws Exception {
		server = new Server();
		client = new Client();
		client.initializeStack(IpChannelType.SCTP);
		server.initializeStack(IpChannelType.SCTP);

	}

	@Test
	public void testConnection() throws Exception {
		this.setUp();
		
		// there is only one listener possible in association !!!
	//	client.getAssociation().setAssociationListener(new ClientAssociationListenerImpl());
	//	client.getManagement().startAssociation("clientAssociation");
	//	server.getAssociation().setAssociationListener(new ServerAssociationListenerImpl());
	//	server.getManagement().startAssociation("serverAssociation");
		
		client.getMtp3Management().addM3UAManagementEventListener(new ClientMtp3MangementListenerImpl());
		server.getMtp3Management().addM3UAManagementEventListener(new ServerMtp3ManagementListenerImpl());
		
		client.getSccpStack().getSccpProvider().registerSccpListener(1, new ClientSccpListenerImpl());
		server.getSccpStack().getSccpProvider().registerSccpListener(1, new ServerSccpListenerImpl());
		
		client.getMapStack().getMAPProvider().getMAPServiceSupplementary().addMAPServiceListener(new ClientMAPServiceSupplementaryListenerImpl());
		server.getMapStack().getMAPProvider().getMAPServiceSupplementary().addMAPServiceListener(new ServerMAPServiceSupplementaryListenerImpl());
		
		while(clientSignallingPointStatus == null) {
				Thread.sleep(1000);
		}
		Assert.assertEquals(clientSignallingPointStatus == SignallingPointStatus.ACCESSIBLE, true);
		Assert.assertEquals(serverSignallingPointStatus == SignallingPointStatus.ACCESSIBLE, true);
		
		// server initiates MS-initiated USSD request
		server.initiateUSSD();
		
		while(processUnstructuredSSRequest == null) {
			Thread.sleep(500);
		}

		while(unstructuredSSRequest == null) {
			Thread.sleep(500);
		}

		while(unstructuredSSResponse == null) {
			Thread.sleep(500);
		}
		
		while(processUnstructuredSSResponse == null) {
			Thread.sleep(500);
		}
		
	}
	
	private class ServerMAPServiceSupplementaryListenerImpl implements MAPServiceSupplementaryListener {

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
			processUnstructuredSSRequest = arg0;
		}

		public void onProcessUnstructuredSSResponse(ProcessUnstructuredSSResponse arg0) {
			// TODO Auto-generated method stub
			processUnstructuredSSResponse = arg0;
			logger.info("server onProcessUnstructuredSSResponse");
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

		public void onUnstructuredSSRequest(UnstructuredSSRequest arg0) {
			// TODO Auto-generated method stub
			unstructuredSSRequest = arg0;
			logger.info("server onUnstructuredSSRequest");
		}

		public void onUnstructuredSSResponse(UnstructuredSSResponse arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class ClientMAPServiceSupplementaryListenerImpl implements MAPServiceSupplementaryListener {

		public void onErrorComponent(MAPDialog arg0, Long arg1, MAPErrorMessage arg2) {
		}

		public void onInvokeTimeout(MAPDialog arg0, Long arg1) {
		}

		public void onMAPMessage(MAPMessage arg0) {
		}

		public void onRejectComponent(MAPDialog arg0, Long arg1, Problem arg2, boolean arg3) {
		}

		public void onActivateSSRequest(ActivateSSRequest arg0) {
		}

		public void onActivateSSResponse(ActivateSSResponse arg0) {
		}

		public void onDeactivateSSRequest(DeactivateSSRequest arg0) {
		}

		public void onDeactivateSSResponse(DeactivateSSResponse arg0) {
		}

		public void onEraseSSRequest(EraseSSRequest arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onEraseSSResponse(EraseSSResponse arg0) {
		}

		public void onGetPasswordRequest(GetPasswordRequest arg0) {
		}

		public void onGetPasswordResponse(GetPasswordResponse arg0) {
		}

		public void onInterrogateSSRequest(InterrogateSSRequest arg0) {
		}

		public void onInterrogateSSResponse(InterrogateSSResponse arg0) {
		}

		public void onProcessUnstructuredSSRequest(ProcessUnstructuredSSRequest arg0) {
			processUnstructuredSSRequest = arg0;
			logger.info("client onProcessUnstructuredSSRequest");
		}

		public void onProcessUnstructuredSSResponse(ProcessUnstructuredSSResponse arg0) {
			// TODO Auto-generated method stub
			
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

		public void onUnstructuredSSRequest(UnstructuredSSRequest arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onUnstructuredSSResponse(UnstructuredSSResponse arg0) {
			// TODO Auto-generated method stub
			unstructuredSSResponse = arg0;
			logger.info("client onUnstructuredSSResponse");
		}
		
	}

	
	private class ServerSccpListenerImpl implements SccpListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void onCoordRequest(int arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		public void onCoordResponse(int arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		public void onMessage(SccpDataMessage arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onNotice(SccpNoticeMessage arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onPcState(int pointcode, SignallingPointStatus spStatus, int arg2, RemoteSccpStatus arg3) {
//			logger.info("server onPcState" + pointcode + spStatus + arg3);
			serverSignallingPointStatus = spStatus;
		}

		public void onState(int arg0, int arg1, boolean arg2, int arg3) {
			logger.info("server onState");
		
		}
		
	}
	
	private class ClientSccpListenerImpl implements SccpListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void onCoordRequest(int arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		public void onCoordResponse(int arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		public void onMessage(SccpDataMessage arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onNotice(SccpNoticeMessage arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onPcState(int pointcode, SignallingPointStatus spStatus, int arg2, RemoteSccpStatus arg3) {
//			logger.info("client onPcState" + pointcode + spStatus + arg3);
			clientSignallingPointStatus = spStatus;
		}

		public void onState(int arg0, int arg1, boolean arg2, int arg3) {
			// TODO Auto-generated method stub
			logger.info("client onState");
			
		}
		
	}
	
	private class ServerMtp3ManagementListenerImpl implements M3UAManagementEventListener {

		public void onAsActive(As arg0, State arg1) {
		//	logger.info("server onAsActive" + arg0.getState() + arg1);
			Assert.assertEquals(arg0.isUp(), true);

		}

		public void onAsCreated(As arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onAsDestroyed(As arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onAsDown(As arg0, State arg1) {
			logger.info("server onAsDown");
		
		}

		public void onAsInactive(As arg0, State arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onAsPending(As arg0, State arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onAspActive(Asp arg0, State arg1) {
			//logger.info("server onAspActive" + arg0.isUp() + arg1);
			Assert.assertEquals(arg0.isUp(), true);
		}

		public void onAspAssignedToAs(As arg0, Asp arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onAspDown(Asp arg0, State arg1) {
			logger.info("server onAspDown");
			
		}

		public void onAspFactoryCreated(AspFactory arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onAspFactoryDestroyed(AspFactory arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onAspFactoryStarted(AspFactory arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onAspFactoryStopped(AspFactory arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onAspInactive(Asp arg0, State arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onAspUnassignedFromAs(As arg0, Asp arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onRemoveAllResources() {
			// TODO Auto-generated method stub
			
		}

		public void onServiceStarted() {
			// TODO Auto-generated method stub
			
		}

		public void onServiceStopped() {
			// TODO Auto-generated method stub
			
		}

		
	}
		
	
	
	private class ClientMtp3MangementListenerImpl implements M3UAManagementEventListener {

		public void onAsActive(As arg0, State arg1) {
		//	logger.info("client onAsActive");
			Assert.assertEquals(arg0.isUp(), true);
		}
		public void onAsCreated(As arg0) {
			// TODO Auto-generated method stub
			
		}
		public void onAsDestroyed(As arg0) {
			// TODO Auto-generated method stub
			
		}
		public void onAsDown(As arg0, State arg1) {
			
		}
		public void onAsInactive(As arg0, State arg1) {
			// TODO Auto-generated method stub
			
		}
		public void onAsPending(As arg0, State arg1) {
			// TODO Auto-generated method stub
			
		}
		public void onAspActive(Asp arg0, State arg1) {
			Assert.assertEquals(arg0.isUp(), true);
		}
		public void onAspAssignedToAs(As arg0, Asp arg1) {
			// TODO Auto-generated method stub
		
		}
		public void onAspDown(Asp arg0, State arg1) {			
		}
		public void onAspFactoryCreated(AspFactory arg0) {
			// TODO Auto-generated method stub
			
		}
		public void onAspFactoryDestroyed(AspFactory arg0) {
			// TODO Auto-generated method stub
		
		}
		public void onAspFactoryStarted(AspFactory arg0) {
			// TODO Auto-generated method stub
			
		}
		public void onAspFactoryStopped(AspFactory arg0) {
			// TODO Auto-generated method stub
			
		}
		public void onAspInactive(Asp arg0, State arg1) {
			// TODO Auto-generated method stub
			
		}
		public void onAspUnassignedFromAs(As arg0, Asp arg1) {
			// TODO Auto-generated method stub
			
		}
		public void onRemoveAllResources() {
			// TODO Auto-generated method stub
			
		}
		public void onServiceStarted() {
			// TODO Auto-generated method stub
			
		}
		public void onServiceStopped() {		
		}
		
	}

}
