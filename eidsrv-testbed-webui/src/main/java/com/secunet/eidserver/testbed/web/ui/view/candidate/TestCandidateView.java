package com.secunet.eidserver.testbed.web.ui.view.candidate;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.viritin.ListContainer;

import com.secunet.eidserver.testbed.common.constants.Bitlengths;
import com.secunet.eidserver.testbed.common.exceptions.InvalidObjectException;
import com.secunet.eidserver.testbed.common.ics.IcsCa;
import com.secunet.eidserver.testbed.common.ics.IcsCaDomainparams;
import com.secunet.eidserver.testbed.common.ics.IcsEllipticcurve;
import com.secunet.eidserver.testbed.common.ics.IcsMandatoryprofile;
import com.secunet.eidserver.testbed.common.ics.IcsOptionalprofile;
import com.secunet.eidserver.testbed.common.ics.IcsTlsCiphersuite;
import com.secunet.eidserver.testbed.common.ics.IcsTlsSignaturealgorithms;
import com.secunet.eidserver.testbed.common.ics.IcsTlsVersion;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionContentUri;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionKeyAgreementUri;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionKeyAgreementWrappingUri;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecEncryptionKeyTransportUri;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureCanonicalization;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureDigest;
import com.secunet.eidserver.testbed.common.ics.IcsXmlsecSignatureUri;
import com.secunet.eidserver.testbed.common.interfaces.beans.CandidateController;
import com.secunet.eidserver.testbed.common.interfaces.beans.CertificateController;
import com.secunet.eidserver.testbed.common.interfaces.dao.TestCandidateDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.TlsDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.XmlEncryptionKeyAgreementDAO;
import com.secunet.eidserver.testbed.common.interfaces.dao.XmlEncryptionKeyTransportDAO;
import com.secunet.eidserver.testbed.common.interfaces.entities.CertificateX509;
import com.secunet.eidserver.testbed.common.interfaces.entities.TestCandidate;
import com.secunet.eidserver.testbed.common.interfaces.entities.Tls;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlEncryptionKeyAgreement;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlEncryptionKeyTransport;
import com.secunet.eidserver.testbed.common.interfaces.entities.XmlSignature;
import com.secunet.eidserver.testbed.web.core.Log;
import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.i18n.Icon;
import com.secunet.eidserver.testbed.web.nav.TestBedView;
import com.secunet.eidserver.testbed.web.ui.comp.AbstractTestbedView;
import com.secunet.eidserver.testbed.web.ui.comp.CollectionComponentField;
import com.secunet.eidserver.testbed.web.ui.comp.ExceptionWindow;
import com.secunet.eidserver.testbed.web.ui.comp.SortedMultiValueListSelection;
import com.secunet.eidserver.testbed.web.ui.comp.StringToIntegerParameterConverter;
import com.secunet.eidserver.testbed.web.ui.comp.TogglePanelLayout;
import com.secunet.eidserver.testbed.web.ui.core.TestbedTheme;
import com.secunet.eidserver.testbed.web.ui.util.CommitHandler;
import com.secunet.eidserver.testbed.web.ui.util.NavigatorHelper;
import com.secunet.eidserver.testbed.web.ui.util.ParameterHandler;
import com.vaadin.cdi.CDIView;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.UIEvents.PollEvent;
import com.vaadin.event.UIEvents.PollListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;

@CDIView("candidate")
public class TestCandidateView extends AbstractTestbedView implements View, PropertyChangeListener
{
	private static final Logger logger = LogManager.getRootLogger();

	@EJB
	private TestCandidateDAO testCandidateDAO;

	@EJB
	private TlsDAO tlsDAO;

	@EJB
	private XmlEncryptionKeyAgreementDAO encKeyAgreeDAO;

	@EJB
	private XmlEncryptionKeyTransportDAO encKeyTransDAO;

	@EJB
	private CandidateController candidateController;

	@EJB
	private CertificateController certificateController;

	/** generated */
	protected static final long serialVersionUID = -2505308085488410482L;

	protected TestCandidateModel candidateViewModel;
	protected BeanFieldGroup<TestCandidate> fieldGroup;

	// fields: add
	protected Upload uploadICS;

	// fields: edit/add
	@PropertyId("profileName")
	protected TextField profileName;
	@PropertyId("candidateName")
	protected /* String */ TextField candidateName;
	@PropertyId("vendor")
	protected /* String */ TextField vendor;
	@PropertyId("versionMajor")
	protected /* int */ TextField versionMajor;
	@PropertyId("versionMinor")
	protected /* int */ TextField versionMinor;
	@PropertyId("versionSubminor")
	protected /* int */ TextField versionSubminor;
	@PropertyId("multiClientCapable")
	protected /* boolean */ CheckBox multiClientCapable;
	@PropertyId("apiMajor")
	protected /* int */ TextField apiMajor;
	@PropertyId("apiMinor")
	protected /* int */ TextField apiMinor;
	@PropertyId("apiSubminor")
	protected /* int */ TextField apiSubminor;
	@PropertyId("ecardapiUrl")
	protected /* String */ TextField ecardapiUrl;
	@PropertyId("eidinterfaceUrl")
	protected /* String */ TextField eidinterfaceUrl;
	@PropertyId("samlUrl")
	protected /* String */ TextField samlUrl;
	@PropertyId("attachedTcTokenUrl")
	protected /* String */ TextField attachedUrl;
	@PropertyId("mandatoryProfiles")
	protected TwinColSelect mandatoryProfiles;
	@PropertyId("optionalProfiles")
	protected TwinColSelect optionalProfiles;
	@PropertyId("xmlEncryptionAlgorithms")
	protected TwinColSelect xmlEncryptionAlgorithms;
	@PropertyId("chipAuthenticationAlgorithms")
	protected TwinColSelect chipAuthIcsAlgos;
	@PropertyId("chipAuthenticationDomainParameters")
	protected TwinColSelect chipAuthIcsCADomainParas;

	// self handling fields
	protected CollectionComponentField<Set<Tls>, Tls> pskTLSCollCompField;
	protected CollectionComponentField<Set<Tls>, Tls> tlsEcardApiAttachedCollCompField;
	protected CollectionComponentField<Set<Tls>, Tls> tlsEidInterfaceCollCompField;
	protected CollectionComponentField<Set<XmlEncryptionKeyAgreement>, XmlEncryptionKeyAgreement> xmlkeyAgreesField;
	protected CollectionComponentField<Set<XmlEncryptionKeyTransport>, XmlEncryptionKeyTransport> xmlkeyTransField;
	protected CollectionComponentField<Set<Tls>, Tls> tlsSamlCollCompField;

	protected HorizontalLayout headerLayout;
	protected Button submitButton;
	protected Button submitTopButton;

	protected Set<XmlSignature> xmlSignatureAlgorithms;
	protected Set<CertificateX509> x509Certificates;
	// not needed Set<Report> reports;

	// groupings:
	protected TogglePanelLayout icsPanel;
	protected TogglePanelLayout probantSelectionPanel;
	protected TogglePanelLayout swPanel;
	protected TogglePanelLayout serverAdressPanel;
	protected TogglePanelLayout profilesPanel;
	protected TogglePanelLayout pskPanel;
	protected TogglePanelLayout attachedServerPanel;
	protected TogglePanelLayout tlsEIDPanel;
	protected TogglePanelLayout xmlSignEIDPanel;
	protected TogglePanelLayout tlsSamlPanel;
	protected TogglePanelLayout xmlSignSamlPanel;
	protected TogglePanelLayout xmlEncrySamlPanel;
	protected TogglePanelLayout chipAuthPanel;

	protected List<FormLayout> forms = new ArrayList<>();

	@PostConstruct
	public void setupView()
	{
		setSpacing(true);
		setMargin(new MarginInfo(true, true, true, false));
	}

	@Override
	public void enter(ViewChangeEvent event)
	{
		TestCandidate tcEntity = null;
		String editUUID = null;
		candidateViewModel = new TestCandidateModel();
		ParameterHandler paramHandler = new ParameterHandler(event.getParameters());
		boolean createModeUI = !paramHandler.isEdit();

		if (paramHandler.getAssociatedUUID().isPresent())
		{
			try
			{
				editUUID = paramHandler.getAssociatedUUID().get();
				tcEntity = candidateController.getCandidate(editUUID);
			}
			catch (InvalidObjectException ioe)
			{
				Log.log().info(Log.getStackTrace(ioe));

				ExceptionWindow.showNewWindow(I18NHandler.getText(I18NText.Error_InvalidEntry), I18NHandler.getText(I18NText.Error_InvalidEntry0ofType1, editUUID, TestCandidate.class.getSimpleName()),
						ioe);
				return;
			}
			catch (Exception exc)
			{
				Log.log().error(Log.getStackTrace(exc));
				ExceptionWindow.showNewWindow(I18NHandler.getText(I18NText.Error_GeneralError), I18NHandler.getText(I18NText.Instruction_SelectOtherComponentOrReloadPage), exc);
				return;
			}
		}

		if (createModeUI)
			tcEntity = testCandidateDAO.createNew();

		candidateViewModel.setTestCandidate(tcEntity);
		candidateViewModel.addPropertyChangeListener(this);

		try
		{
			setupView(createModeUI, candidateViewModel);
		}
		catch (Exception e)
		{
			logger.error(e);
			// TODO the exc window
			ExceptionWindow.showNewWindow(e.getClass().getSimpleName(), e.getMessage(), e);
		}
	}


	protected void setupView(boolean create, TestCandidateModel model)
	{
		removeAllComponents();
		candidateViewModel = model;

		generateHeader(create);


		if (create)
		{
			UploadReceiver uploadReceiver = new UploadReceiver(this);
			uploadICS = new Upload(I18NHandler.getText(I18NText.View_TestCandidate_UploadICSFile), uploadReceiver);
			uploadICS.setIcon(FontAwesome.UPLOAD);
			uploadICS.addSucceededListener(uploadReceiver);
			uploadICS.setImmediate(true);

			FormLayout uploadForm = new FormLayout(uploadICS);
			uploadForm.setWidth(100, Unit.PERCENTAGE);
			uploadForm.setMargin(true);
			uploadForm.setSpacing(true);

			icsPanel = new TogglePanelLayout(I18NHandler.getText(I18NText.View_TestCandidate_Panel_ICSFile), null, uploadForm, false, 100, Unit.PERCENTAGE);
			addComponent(icsPanel);

			showMainForms(true, model);
		}
		else
		{
			if (model.getTestCandidate() == null)
			{
				showTestCandidateSelection();
			}
			else
			{
				showMainForms(create, model);
			}
		}
	}

	public void generateHeader(boolean create)
	{
		Label header = new Label((create ? I18NHandler.getText(I18NText.View_TestCandidate_Caption_create) : I18NHandler.getText(I18NText.View_TestCandidate_Caption_edit)));
		header.setStyleName(TestbedTheme.LABEL_H1);

		headerLayout = new HorizontalLayout(header);
		headerLayout.setWidth(100, Unit.PERCENTAGE);
		headerLayout.setExpandRatio(header, 1);
		addComponent(headerLayout);
	}

	public Button generateSubmitButton(boolean create, TestCandidateModel model)
	{
		Button submitButton = new Button(create ? I18NHandler.getText(I18NText.Button_Add) : I18NHandler.getText(I18NText.Button_Edit), e -> submit(model.getTestCandidate()));
		submitButton.setIcon(Icon.SAVE.getIcon());
		submitButton.addStyleName(TestbedTheme.BUTTON_PRIMARY);
		if (profileName == null || null == profileName.getValue() || profileName.getValue().length() == 0)
			submitButton.setEnabled(false);

		return submitButton;
	}


	protected void showTestCandidateSelection()
	{
		try
		{
			Set<TestCandidate> candidates = candidateController.getAllCandidates();

			ComboBox combobox = new ComboBox(I18NHandler.getText(I18NText.View_TestCandidate_CandidateSelection), candidates);
			combobox.setWidth(100, Unit.PERCENTAGE);
			candidates.forEach(tp -> combobox.setItemCaption(tp,
					(tp.getProfileName() + " (" + tp.getCandidateName() + " v" + tp.getVersionMajor() + "." + tp.getVersionMinor() + "." + tp.getVersionSubminor() + ")")));
			combobox.addValueChangeListener(e -> candidateViewModel.setTestCandidate(((TestCandidate) e.getProperty().getValue())));
			combobox.setNullSelectionAllowed(false);

			FormLayout form = new FormLayout();
			form.setWidth(100, Unit.PERCENTAGE);
			form.setSpacing(true);
			form.setMargin(true);
			form.addComponent(combobox);

			probantSelectionPanel = new TogglePanelLayout(I18NHandler.getText(I18NText.View_TestCandidate_Panel_CandidateSelection), null, form, false, 100, Unit.PERCENTAGE);
			addComponent(probantSelectionPanel);
		}
		catch (Exception exc)
		{
			// TODO backend exceptions
			// TODO rest "software runtime exceptions"
			Log.log().error(Log.getStackTrace(exc));
			ExceptionWindow.showNewWindow(I18NHandler.getText(I18NText.Error_GeneralError), I18NHandler.getText(I18NText.Instruction_SelectOtherComponentOrReloadPage), exc);
		}
	}

	protected void navigateTo(boolean editMode, String id)
	{
		if (editMode)
		{
			List<String> params = new ArrayList<>();
			params.add("edit");
			params.add(id);
			String navString = NavigatorHelper.getNavigationString(TestBedView.CANDIDATE, params);
			UI.getCurrent().getNavigator().navigateTo(navString);
		}
	}

	public void showMainForms(boolean create, TestCandidateModel model)
	{
		forms.clear();

		generatePanelMainAttributes();
		generatePanelServerAdresses(create);
		generatePanelProfiles();
		generatePanelPSK(model);
		generatePanelAttachedServer(model);
		generatePanelTLSEID();
		generatePanelXMLSignEID();
		generatePanelTLSSaml();
		generatePanelXMLSignSaml();
		generatePanelXMLEncrSaml();
		generatePanelChipAuth();

		// now match the bean to the fields
		fieldGroup = new BeanFieldGroup<TestCandidate>(TestCandidate.class);
		fieldGroup.bindMemberFields(this);
		fieldGroup.setItemDataSource(model.getTestCandidate());

		// let the user submit the input data
		submitButton = generateSubmitButton(create, model);
		submitTopButton = generateSubmitButton(create, model);
		headerLayout.addComponent(submitTopButton);
		headerLayout.setComponentAlignment(submitTopButton, Alignment.TOP_RIGHT);

		addComponent(submitButton);
		setComponentAlignment(submitButton, Alignment.MIDDLE_RIGHT);
	}


	public void generatePanelChipAuth()
	{
		List<IcsCa> allIcsCas = Arrays.asList(IcsCa.values());
		chipAuthIcsAlgos = generateTwinColSelect(I18NHandler.getText(I18NText.TestCandidate_ChipAuthenticationAlgorithms), allIcsCas);
		allIcsCas.forEach(b -> chipAuthIcsAlgos.setItemCaption(b, I18NHandler.getText(b)));

		List<IcsCaDomainparams> allIcsCADomainParas = Arrays.asList(IcsCaDomainparams.values());
		chipAuthIcsCADomainParas = generateTwinColSelect(I18NHandler.getText(I18NText.TestCandidate_ChipAuthenticationDomainParameters), allIcsCADomainParas);
		allIcsCADomainParas.forEach(b -> chipAuthIcsCADomainParas.setItemCaption(b, I18NHandler.getText(b)));

		FormLayout form = generateForm(chipAuthIcsAlgos, chipAuthIcsCADomainParas);
		chipAuthPanel = new TogglePanelLayout(I18NHandler.getText(I18NText.View_TestCandidate_Panel_chipAuthPanel), null, form, false, 100, Unit.PERCENTAGE);
		addComponent(chipAuthPanel);
	}


	public void generatePanelXMLEncrSaml()
	{
		List<IcsXmlsecEncryptionContentUri> allXmlEncryptionAlgorithm = Arrays.asList(IcsXmlsecEncryptionContentUri.values());
		xmlEncryptionAlgorithms = generateTwinColSelect(I18NHandler.getText(I18NText.TestCandidate_xmlEncryptionAlgorithm), allXmlEncryptionAlgorithm);
		allXmlEncryptionAlgorithm.forEach(b -> xmlEncryptionAlgorithms.setItemCaption(b, I18NHandler.getText(b)));

		// XmlEncryptionKeyAgreement
		Set<XmlEncryptionKeyAgreement> actualDataEncKeyAgr = candidateViewModel.getTestCandidate().getXmlKeyAgreement();
		String captionKeyAgr = I18NHandler.getText(I18NText.View_TestCandidate_Panel_xmlSignSaml);
		xmlkeyAgreesField = generateXmlEncryKeyAgreementComponentField(actualDataEncKeyAgr, captionKeyAgr);

		// XmlEncryptionKeyTransport
		Set<XmlEncryptionKeyTransport> actualDataEncKeyTrans = candidateViewModel.getTestCandidate().getXmlKeyTransport();
		String captionKeyTrans = I18NHandler.getText(I18NText.View_TestCandidate_Panel_xmlSignSaml);
		xmlkeyTransField = generateXmlEncryKeyTransportComponentField(actualDataEncKeyTrans, captionKeyTrans);

		FormLayout form = generateForm(xmlEncryptionAlgorithms, xmlkeyAgreesField, xmlkeyTransField);
		xmlEncrySamlPanel = new TogglePanelLayout(I18NHandler.getText(I18NText.View_TestCandidate_Panel_xmlEncrySamlPanel), null, form, false, 100, Unit.PERCENTAGE);
		addComponent(xmlEncrySamlPanel);
	}


	public void generatePanelXMLSignSaml()
	{
		Set<XmlSignature> actualData = candidateViewModel.getTestCandidate().getXmlSignatureAlgorithmsSaml();
		String caption = I18NHandler.getText(I18NText.View_TestCandidate_Panel_xmlSignSaml);

		CollectionComponentField<Set<XmlSignature>, XmlSignature> collCompField = generateXMLSignatureEditPanel(actualData, caption);

		xmlSignSamlPanel = new TogglePanelLayout(I18NHandler.getText(I18NText.View_TestCandidate_Panel_xmlSignSaml), null, collCompField, false, 100, Unit.PERCENTAGE);
		addComponent(xmlSignSamlPanel);
	}


	public void generatePanelTLSSaml()
	{
		Set<Tls> actualData = candidateViewModel.getTestCandidate().getTlsSaml();
		String caption = I18NHandler.getText(I18NText.View_TestCandidate_Panel_tlsSaml);

		tlsSamlCollCompField = generateTLSField(actualData, caption);

		tlsSamlPanel = new TogglePanelLayout(I18NHandler.getText(I18NText.View_TestCandidate_Panel_tlsSaml), null, tlsSamlCollCompField, false, 100, Unit.PERCENTAGE);
		addComponent(tlsSamlPanel);
	}


	public void generatePanelXMLSignEID()
	{
		Set<XmlSignature> actualData = candidateViewModel.getTestCandidate().getXmlSignatureAlgorithmsEid();
		String caption = I18NHandler.getText(I18NText.View_TestCandidate_Panel_XmlSignEID);

		CollectionComponentField<Set<XmlSignature>, XmlSignature> collCompField = generateXMLSignatureEditPanel(actualData, caption);

		xmlSignEIDPanel = new TogglePanelLayout(I18NHandler.getText(I18NText.View_TestCandidate_Panel_XmlSignEID), null, collCompField, false, 100, Unit.PERCENTAGE);
		addComponent(xmlSignEIDPanel);
	}


	public void generatePanelTLSEID()
	{
		Set<Tls> actualData = candidateViewModel.getTestCandidate().getTlsEidInterface();
		String caption = I18NHandler.getText(I18NText.View_TestCandidate_Panel_tlsEID);

		tlsEidInterfaceCollCompField = generateTLSField(actualData, caption);

		tlsEIDPanel = new TogglePanelLayout(I18NHandler.getText(I18NText.View_TestCandidate_Panel_tlsEID), null, tlsEidInterfaceCollCompField, false, 100, Unit.PERCENTAGE);
		addComponent(tlsEIDPanel);
	}


	public void generatePanelPSK(TestCandidateModel model)
	{
		Set<Tls> actualData = model.getTestCandidate().getTlsEcardApiPsk();
		String caption = I18NHandler.getText(I18NText.View_TestCandidate_Panel_pskeCardAPI);

		pskTLSCollCompField = generateTLSField(actualData, caption);
		pskTLSCollCompField.setCaptionConverter(o -> I18NHandler.getText(o.getTlsVersion()));

		pskPanel = new TogglePanelLayout(I18NHandler.getText(I18NText.View_TestCandidate_Panel_pskeCardAPI), null, pskTLSCollCompField, false, 100, Unit.PERCENTAGE);

		Button collButton = new Button(null, e -> pskTLSCollCompField.hideEditor());
		collButton.setIcon(FontAwesome.CARET_UP);
		collButton.addStyleName(TestbedTheme.BUTTON_BORDERLESS);
		pskPanel.setExtraHeaderContent(Arrays.asList(collButton));

		addComponent(pskPanel);
	}

	public void generatePanelAttachedServer(TestCandidateModel model)
	{
		Set<IcsTlsCiphersuite> allTlsCiphersuites = new HashSet<>(Arrays.asList(IcsTlsCiphersuite.values()));
		SortedMultiValueListSelection<IcsTlsCiphersuite> tlsCiphersuites = generateSortedMultiValueList(allTlsCiphersuites, I18NHandler.getText(I18NText.TLS_tlsCiphersuites));
		tlsCiphersuites.setCaptionConverter(ec -> ec.name());

		List<IcsEllipticcurve> allEllipticCurves = Arrays.asList(IcsEllipticcurve.values());
		TwinColSelect ellipticCurves = generateTwinColSelect(I18NHandler.getText(I18NText.TLS_ellipticCurves), allEllipticCurves);
		allEllipticCurves.forEach(ec -> ellipticCurves.setItemCaption(ec, I18NHandler.getText(ec)));

		List<IcsTlsSignaturealgorithms> allSignatureAlgorithms = Arrays.asList(IcsTlsSignaturealgorithms.values());
		TwinColSelect signatureAlgorithms = generateTwinColSelect(I18NHandler.getText(I18NText.TLS_signatureAlgorithm), allSignatureAlgorithms);
		allSignatureAlgorithms.forEach(sa -> signatureAlgorithms.setItemCaption(sa, I18NHandler.getText(sa)));

		List<IcsTlsVersion> tlsversions = Arrays.asList(IcsTlsVersion.values());
		ComboBox tlsVersion = new ComboBox(I18NHandler.getText(I18NText.TLS_TlsVersion), tlsversions);
		tlsversions.forEach(b -> tlsVersion.setItemCaption(b, I18NHandler.getText(b)));

		FormLayout form = generateForm(tlsCiphersuites, ellipticCurves, signatureAlgorithms, tlsVersion);

		BeanFieldGroup<Tls> fieldGroupTls = new BeanFieldGroup<Tls>(Tls.class);
		fieldGroupTls.bind(tlsCiphersuites, "tlsCiphersuites");
		fieldGroupTls.bind(ellipticCurves, "ellipticCurves");
		fieldGroupTls.bind(signatureAlgorithms, "signatureAlgorithms");
		fieldGroupTls.bind(tlsVersion, "tlsVersion");

		Set<Tls> actualData = model.getTestCandidate().getTlsEcardApiAttached();
		tlsEcardApiAttachedCollCompField = new CollectionComponentField<>(I18NHandler.getText(I18NText.View_TestCandidate_Panel_AttachedServer), actualData, null, form, fieldGroupTls, Tls.class,
				tlsDAO);
		tlsEcardApiAttachedCollCompField.setCaptionConverter(o -> I18NHandler.getText(o.getTlsVersion()));

		attachedServerPanel = new TogglePanelLayout(I18NHandler.getText(I18NText.View_TestCandidate_Panel_AttachedServer), null, tlsEcardApiAttachedCollCompField, false, 100, Unit.PERCENTAGE);

		Button collButton = new Button(null, e -> tlsEcardApiAttachedCollCompField.hideEditor());
		collButton.setIcon(FontAwesome.CARET_UP);
		collButton.addStyleName(TestbedTheme.BUTTON_BORDERLESS);
		attachedServerPanel.setExtraHeaderContent(Arrays.asList(collButton));

		addComponent(attachedServerPanel);
	}


	protected void generatePanelProfiles()
	{
		List<IcsMandatoryprofile> allMandProfiles = Arrays.asList(IcsMandatoryprofile.values());
		mandatoryProfiles = generateTwinColSelect(I18NHandler.getText(I18NText.TestCandidate_mandatoryProfiles), allMandProfiles);
		allMandProfiles.forEach(p -> mandatoryProfiles.setItemCaption(p, I18NHandler.getText(p)));

		List<IcsOptionalprofile> allOptProfiles = Arrays.asList(IcsOptionalprofile.values());
		optionalProfiles = generateTwinColSelect(I18NHandler.getText(I18NText.TestCandidate_optionalProfiles), allOptProfiles);
		allOptProfiles.forEach(p -> optionalProfiles.setItemCaption(p, I18NHandler.getText(p)));

		FormLayout form = generateForm(mandatoryProfiles, optionalProfiles);
		profilesPanel = new TogglePanelLayout(I18NHandler.getText(I18NText.View_TestCandidate_Panel_Profiles), null, form, false, 100, Unit.PERCENTAGE);
		addComponent(profilesPanel);
	}


	protected void generatePanelServerAdresses(boolean create)
	{
		ecardapiUrl = generateTextField(I18NHandler.getText(I18NText.TestCandidate_ecardapiUrl));
		ecardapiUrl.setConverter(new StringToUrlConverter());
		eidinterfaceUrl = generateTextField(I18NHandler.getText(I18NText.TestCandidate_eidinterfaceUrl));
		eidinterfaceUrl.setConverter(new StringToUrlConverter());
		samlUrl = generateTextField(I18NHandler.getText(I18NText.TestCandidate_samlUrl));
		samlUrl.setConverter(new StringToUrlConverter());
		attachedUrl = generateTextField(I18NHandler.getText(I18NText.TestCandidate_attachedUrl));
		attachedUrl.setConverter(new StringToUrlConverter());
		attachedUrl.setRequired(false);
		FormLayout form = generateForm(ecardapiUrl, eidinterfaceUrl, samlUrl, attachedUrl);

		if (!create)
		{
			Button certdownlButton = new Button(I18NHandler.getText(I18NText.Button_Download));
			certdownlButton.setDescription(I18NHandler.getText(I18NText.Button_Download));
			certdownlButton.setIcon(FontAwesome.DOWNLOAD);
			StreamResource streamResource = new StreamResource(new StreamSource() {
				private static final long serialVersionUID = -7308680845393515801L;

				@Override
				public InputStream getStream()
				{
					try
					{
						byte[] certificates = certificateController.downloadCertificates(candidateViewModel.getTestCandidate()).get(5, TimeUnit.SECONDS);
						return new ByteArrayInputStream(certificates);
					}
					catch (Exception e)
					{
						logger.error(e);
						return null;
					}
				}
			}, "certificates.zip");
			FileDownloader fileDownloader = new FileDownloader(streamResource);
			fileDownloader.extend(certdownlButton);
			form.addComponent(certdownlButton);
			form.setComponentAlignment(certdownlButton, Alignment.MIDDLE_RIGHT);
		}

		serverAdressPanel = new TogglePanelLayout(I18NHandler.getText(I18NText.View_TestCandidate_Panel_ServerAdressPanel), null, form, false, 100, Unit.PERCENTAGE);
		addComponent(serverAdressPanel);
	}


	public void generatePanelMainAttributes()
	{
		profileName = generateTextField(I18NHandler.getText(I18NText.TestCandidate_profileName));

		// NOTE: Due to Vaadin timing errors the UniqueCandidateProfileNameValidator cannot be used here
		//
		// profileName.addValidator(
		// new UniqueCandidateProfileNameValidator(I18NHandler.getText(I18NText.Warning_ValueAlreadyTaken), candidateController, candidateViewModel.getTestCandidate().getProfileName()));
		profileName.addTextChangeListener(new TextChangeListener() {
			private static final long serialVersionUID = -1484764597270042772L;

			@Override
			public void textChange(TextChangeEvent event)
			{
				if (event.getText().length() > 0)
				{
					if (candidateController.isCandidateNameAlreadyTaken(event.getText()))
					{
						submitButton.setEnabled(false);
						submitTopButton.setEnabled(false);
						profileName.setComponentError(new UserError(I18NHandler.getText(I18NText.Warning_ValueAlreadyTaken)));
					}
					else
					{
						submitButton.setEnabled(true);
						submitTopButton.setEnabled(true);
						profileName.setComponentError(null);
					}
				}
				else
				{
					submitButton.setEnabled(false);
					submitTopButton.setEnabled(false);
					profileName.setComponentError(null);
				}
			}
		});

		candidateName = generateTextField(I18NHandler.getText(I18NText.TestCandidate_candidateName));
		vendor = generateTextField(I18NHandler.getText(I18NText.TestCandidate_vendor));
		versionMajor = generateTextField(I18NHandler.getText(I18NText.TestCandidate_versionMajor));
		versionMajor.setConverter(new StringToIntegerParameterConverter(false));
		versionMinor = generateTextField(I18NHandler.getText(I18NText.TestCandidate_versionMinor));
		versionMinor.setConverter(new StringToIntegerParameterConverter(false));
		versionSubminor = generateTextField(I18NHandler.getText(I18NText.TestCandidate_versionSubminor));
		versionSubminor.setConverter(new StringToIntegerParameterConverter(false));
		apiMajor = generateTextField(I18NHandler.getText(I18NText.TestCandidate_apiMajor));
		apiMajor.setConverter(new StringToIntegerParameterConverter(false));
		apiMinor = generateTextField(I18NHandler.getText(I18NText.TestCandidate_apiMinor));
		apiMinor.setConverter(new StringToIntegerParameterConverter(false));
		apiSubminor = generateTextField(I18NHandler.getText(I18NText.TestCandidate_apiSubminor));
		apiSubminor.setConverter(new StringToIntegerParameterConverter(false));
		multiClientCapable = new CheckBox(I18NHandler.getText(I18NText.TestCandidate_multiClient));
		FormLayout form = generateForm(profileName, candidateName, vendor, versionMajor, versionMinor, versionSubminor, apiMajor, apiMinor, apiSubminor, multiClientCapable);
		swPanel = new TogglePanelLayout(I18NHandler.getText(I18NText.View_TestCandidate_Panel_MainAttributes), null, form, false, 100, Unit.PERCENTAGE);
		addComponent(swPanel);
	}


	public CollectionComponentField<Set<XmlSignature>, XmlSignature> generateXMLSignatureEditPanel(Set<XmlSignature> actualData, String caption)
	{
		List<IcsXmlsecSignatureUri> allIcsXmlsecSignatureUri = Arrays.asList(IcsXmlsecSignatureUri.values());
		ComboBox signatureAlgorithm = generateComboBox(I18NHandler.getText(I18NText.XmlSignature_SignatureAlgorithm), allIcsXmlsecSignatureUri);
		allIcsXmlsecSignatureUri.forEach(b -> signatureAlgorithm.setItemCaption(b, I18NHandler.getText(b)));

		List<BigInteger> allBitLengths = Bitlengths.ALLOWED_BIT_LENGTHS;
		TwinColSelect bitLengths = generateTwinColSelect(I18NHandler.getText(I18NText.XmlSignature_BitLengths), allBitLengths);
		allBitLengths.forEach(b -> bitLengths.setItemCaption(bitLengths, I18NHandler.getText(b)));

		List<IcsEllipticcurve> allEllipticCurves = Arrays.asList(IcsEllipticcurve.values());
		TwinColSelect ellipticCurves = generateTwinColSelect(I18NHandler.getText(I18NText.XmlSignature_EllipticCurves), allEllipticCurves);
		allEllipticCurves.forEach(b -> ellipticCurves.setItemCaption(bitLengths, I18NHandler.getText(b)));

		List<IcsXmlsecSignatureCanonicalization> allCanonicalizationMethods = Arrays.asList(IcsXmlsecSignatureCanonicalization.values());
		ComboBox canonicalizationMethods = generateComboBox(I18NHandler.getText(I18NText.XmlSignature_SignatureAlgorithm), allCanonicalizationMethods);
		allCanonicalizationMethods.forEach(b -> canonicalizationMethods.setItemCaption(b, I18NHandler.getText(b)));

		List<IcsXmlsecSignatureDigest> allDigestMethods = Arrays.asList(IcsXmlsecSignatureDigest.values());
		ComboBox digestMethod = generateComboBox(I18NHandler.getText(I18NText.XmlSignature_DigestMethod), allDigestMethods);
		allDigestMethods.forEach(b -> digestMethod.setItemCaption(b, I18NHandler.getText(b)));


		FormLayout form = generateForm(signatureAlgorithm, bitLengths, ellipticCurves, canonicalizationMethods, digestMethod);

		BeanFieldGroup<XmlSignature> fieldGroup = new BeanFieldGroup<XmlSignature>(XmlSignature.class);
		fieldGroup.bind(signatureAlgorithm, "signatureAlgorithm");
		fieldGroup.bind(bitLengths, "bitLengths");
		fieldGroup.bind(ellipticCurves, "ellipticCurves");
		fieldGroup.bind(canonicalizationMethods, "canonicalizationMethod");
		fieldGroup.bind(digestMethod, "digestMethod");

		CollectionComponentField<Set<XmlSignature>, XmlSignature> collCompField = new CollectionComponentField<>(caption, actualData, null, form, fieldGroup, XmlSignature.class, tlsDAO);
		collCompField.setCaptionConverter(o -> o.getSignatureAlgorithm().value());
		return collCompField;
	}

	protected CollectionComponentField<Set<Tls>, Tls> generateTLSField(Set<Tls> actualData, String caption)
	{
		Set<IcsTlsCiphersuite> allTlsCiphersuites = new HashSet<>(Arrays.asList(IcsTlsCiphersuite.values()));
		SortedMultiValueListSelection<IcsTlsCiphersuite> tlsCiphersuites = generateSortedMultiValueList(allTlsCiphersuites, I18NHandler.getText(I18NText.TLS_tlsCiphersuites));

		List<IcsTlsVersion> tlsversions = Arrays.asList(IcsTlsVersion.values());
		ComboBox tlsVersion = new ComboBox(I18NHandler.getText(I18NText.TLS_TlsVersion), tlsversions);
		tlsversions.forEach(b -> tlsVersion.setItemCaption(b, I18NHandler.getText(b)));

		FormLayout form = generateForm(tlsCiphersuites, tlsVersion);

		BeanFieldGroup<Tls> fieldGroupTls = new BeanFieldGroup<Tls>(Tls.class);
		fieldGroupTls.bind(tlsCiphersuites, "tlsCiphersuites");
		fieldGroupTls.bind(tlsVersion, "tlsVersion");

		CollectionComponentField<Set<Tls>, Tls> collCompField = new CollectionComponentField<>(caption, actualData, null, form, fieldGroupTls, Tls.class, tlsDAO);
		collCompField.setCaptionConverter(o -> I18NHandler.getText(o.getTlsVersion()));
		collCompField.selectFirst();

		return collCompField;
	}

	protected CollectionComponentField<Set<XmlEncryptionKeyAgreement>, XmlEncryptionKeyAgreement> generateXmlEncryKeyAgreementComponentField(Set<XmlEncryptionKeyAgreement> actualData, String caption)
	{
		// fields
		List<IcsXmlsecEncryptionKeyAgreementUri> availableUris = Arrays.asList(IcsXmlsecEncryptionKeyAgreementUri.values());
		ComboBox uriSelection = generateComboBox(I18NHandler.getText(I18NText.IcsXmlsecEncryptionKeyTransportUri), availableUris);
		availableUris.forEach(b -> uriSelection.setItemCaption(b, I18NHandler.getText(b)));

		List<IcsXmlsecEncryptionKeyAgreementWrappingUri> availableWrapperUris = Arrays.asList(IcsXmlsecEncryptionKeyAgreementWrappingUri.values());
		ComboBox wrapperUriSelection = generateComboBox(I18NHandler.getText(I18NText.IcsXmlsecEncryptionKeyAgreementWrappingUri), availableWrapperUris);
		availableWrapperUris.forEach(b -> wrapperUriSelection.setItemCaption(b, I18NHandler.getText(b)));

		List<BigInteger> allBitLengths = Bitlengths.ALLOWED_BIT_LENGTHS;
		TwinColSelect bitLengths = generateTwinColSelect(I18NHandler.getText(I18NText.XmlSignature_BitLengths), allBitLengths);
		allBitLengths.forEach(b -> bitLengths.setItemCaption(bitLengths, I18NHandler.getText(b)));

		List<IcsEllipticcurve> allEllipticCurves = Arrays.asList(IcsEllipticcurve.values());
		TwinColSelect ellipticCurves = generateTwinColSelect(I18NHandler.getText(I18NText.XmlSignature_EllipticCurves), allEllipticCurves);
		allEllipticCurves.forEach(b -> ellipticCurves.setItemCaption(bitLengths, I18NHandler.getText(b)));


		FormLayout form = generateForm(uriSelection, wrapperUriSelection, bitLengths, ellipticCurves);
		form.setMargin(false);

		// binding
		BeanFieldGroup<XmlEncryptionKeyAgreement> fieldGroup = new BeanFieldGroup<XmlEncryptionKeyAgreement>(XmlEncryptionKeyAgreement.class);
		fieldGroup.bind(uriSelection, "keyAgreementAlgorithm");
		fieldGroup.bind(wrapperUriSelection, "keyWrappingAlgorithm");
		fieldGroup.bind(bitLengths, "bitLengths");
		fieldGroup.bind(ellipticCurves, "ellipticCurves");

		// actual field
		CollectionComponentField<Set<XmlEncryptionKeyAgreement>, XmlEncryptionKeyAgreement> collCompField = new CollectionComponentField<>(caption, actualData, null, form, fieldGroup,
				XmlEncryptionKeyAgreement.class, encKeyAgreeDAO);
		collCompField.setCaptionConverter(o -> I18NHandler.getText(o));
		collCompField.setCaption(caption);
		collCompField.selectFirst();

		return collCompField;
	}

	protected CollectionComponentField<Set<XmlEncryptionKeyTransport>, XmlEncryptionKeyTransport> generateXmlEncryKeyTransportComponentField(Set<XmlEncryptionKeyTransport> actualData, String caption)
	{
		// fields
		List<IcsXmlsecEncryptionKeyTransportUri> availableUris = Arrays.asList(IcsXmlsecEncryptionKeyTransportUri.values());
		ComboBox uriSelection = generateComboBox(I18NHandler.getText(I18NText.IcsXmlsecEncryptionKeyTransportUri), availableUris);
		availableUris.forEach(b -> uriSelection.setItemCaption(b, I18NHandler.getText(b)));

		List<BigInteger> allBitLengths = Bitlengths.ALLOWED_BIT_LENGTHS;
		TwinColSelect bitLengths = generateTwinColSelect(I18NHandler.getText(I18NText.XmlSignature_BitLengths), allBitLengths);
		allBitLengths.forEach(b -> bitLengths.setItemCaption(bitLengths, I18NHandler.getText(b)));

		List<IcsEllipticcurve> allEllipticCurves = Arrays.asList(IcsEllipticcurve.values());
		TwinColSelect ellipticCurves = generateTwinColSelect(I18NHandler.getText(I18NText.XmlSignature_EllipticCurves), allEllipticCurves);
		allEllipticCurves.forEach(b -> ellipticCurves.setItemCaption(bitLengths, I18NHandler.getText(b)));


		FormLayout form = generateForm(uriSelection, bitLengths, ellipticCurves);
		form.setMargin(false);

		// binding
		BeanFieldGroup<XmlEncryptionKeyTransport> fieldGroup = new BeanFieldGroup<XmlEncryptionKeyTransport>(XmlEncryptionKeyTransport.class);
		fieldGroup.bind(uriSelection, "transportAlgorithm");
		fieldGroup.bind(bitLengths, "bitLengths");
		fieldGroup.bind(ellipticCurves, "ellipticCurves");

		// actual field
		CollectionComponentField<Set<XmlEncryptionKeyTransport>, XmlEncryptionKeyTransport> collCompField = new CollectionComponentField<>(caption, actualData, null, form, fieldGroup,
				XmlEncryptionKeyTransport.class, encKeyTransDAO);
		collCompField.setCaptionConverter(o -> I18NHandler.getText(o));
		collCompField.setCaption(caption);
		collCompField.selectFirst();

		return collCompField;
	}

	private ComboBox generateComboBox(String caption, List<?> availableOptions)
	{
		ComboBox comboBox = new ComboBox(caption, new ListContainer<>(availableOptions));
		comboBox.setWidth(100, Unit.PERCENTAGE);
		comboBox.setNullSelectionAllowed(false);
		comboBox.setRequired(true);

		return comboBox;
	}

	protected <T> SortedMultiValueListSelection<T> generateSortedMultiValueList(Set<T> available, String caption)
	{
		SortedMultiValueListSelection<T> list = new SortedMultiValueListSelection<>(available, new ArrayList<>(), caption, null);
		list.setWidth(100, Unit.PERCENTAGE);

		return list;
	}

	protected TwinColSelect generateTwinColSelect(String caption, Collection<?> addAvailableData)
	{
		TwinColSelect tcs = new TwinColSelect(caption, addAvailableData);
		tcs.setWidth(100, Unit.PERCENTAGE);

		return tcs;
	}


	protected FormLayout generateForm(Field<?>... fields)
	{
		FormLayout layout = new FormLayout();
		layout.setMargin(true);
		layout.setSpacing(true);

		if (fields != null)
			for (int i = 0; i < fields.length; i++)
				layout.addComponent(fields[i]);

		forms.add(layout);

		return layout;
	}


	protected TextField generateTextField(String caption)
	{
		TextField tf = new TextField(caption);
		tf.setNullRepresentation("");
		tf.setValidationVisible(true);
		tf.setRequired(true);
		tf.setWidth(100, Unit.PERCENTAGE);

		return tf;
	}


	/**
	 * Save the given a new {@link TestCandidate} or modify an existing one.
	 * 
	 * @param candidate
	 */
	protected void submit(TestCandidate candidate)
	{
		// save the data
		try
		{
			fieldGroup.commit();

			candidateController.saveCandidate(candidate);
			navigateTo(true, candidate.getId());
			Notification.show("Success", "The test candidate has been saved successfully", Notification.Type.ASSISTIVE_NOTIFICATION);
		}
		catch (CommitException e)
		{
			CommitHandler.displayUserErrorMessageInvalidFields(e);

			// TODO logging
			ExceptionWindow.showNewWindow(I18NHandler.getText(I18NText.Error_GeneralError),
					I18NHandler.getText(I18NText.Warning_CorrectInvalidInput0, e.getClass().getSimpleName() + ": " + e.getMessage()), e);
		}
		catch (Exception e)
		{
			// TODO logging
			ExceptionWindow.showNewWindow(I18NHandler.getText(I18NText.Error_GeneralError), e.getClass().getSimpleName() + ": " + e.getMessage(), e);
		}
	}


	protected void uploadICStoBackEnd(byte[] uploadedRawData)
	{
		if (uploadedRawData == null)
		{
			ExceptionWindow.showNewWindow("The selected ICS file was empty or could not be processed.", "Select a valid file or try again.", "Select a valid file or try again.");
			return;
		}

		try
		{
			Future<TestCandidate> pendingCandidate = candidateController.createCandidate(new String(uploadedRawData));

			// submitted to backend without failure:
			forms.forEach(f -> f.setVisible(false));
			ProgressBar wait = new ProgressBar();
			wait.setIndeterminate(true);
			wait.setValue(0.5f);
			wait.setSizeFull();

			removeComponent(icsPanel);
			addComponent(wait);
			setComponentAlignment(wait, Alignment.MIDDLE_CENTER);

			final PollListener pollListener = new PollListener() {
				private static final long serialVersionUID = -672908335500609086L;

				@Override
				public void poll(PollEvent event)
				{
					if (pendingCandidate.isDone())
					{
						UI.getCurrent().removePollListener(this);
						removeComponent(wait);
						forms.forEach(f -> f.setVisible(true));
						try
						{
							candidateViewModel.setTestCandidate(pendingCandidate.get());
						}
						catch (InterruptedException | ExecutionException e)
						{
							logger.error(e);
						}
					}
				}
			};
			UI.getCurrent().getNavigator().addViewChangeListener(new ViewChangeListener() {
				private static final long serialVersionUID = -3966295373991132417L;

				@Override
				public boolean beforeViewChange(ViewChangeEvent event)
				{
					return true;
				}

				@Override
				public void afterViewChange(ViewChangeEvent event)
				{
					if (UI.getCurrent() != null)
					{
						UI.getCurrent().setPollInterval(-1);
						UI.getCurrent().removePollListener(pollListener);
					}
					UI.getCurrent().getNavigator().removeViewChangeListener(this);
				}
			});

			// now add poll handler
			UI.getCurrent().addPollListener(pollListener);
			UI.getCurrent().setPollInterval(100);
		}
		catch (Exception exc)
		{
			// TODO
			// release backend, show message
			ExceptionWindow.showNewWindow(exc.getMessage(), exc.getMessage(), exc);
		}
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		try
		{
			if (evt.getSource() == candidateViewModel && TestCandidateModel.PROPERTY_TESTCANDIDATE_SELECTION.equals(evt.getPropertyName()))
			{
				removeAllComponents();
				boolean create = null != evt.getOldValue();
				generateHeader(create);
				showMainForms(create, candidateViewModel);
			}
		}
		catch (Exception e)
		{
			logger.error(e);
			// TODO the exc window
			ExceptionWindow.showNewWindow(e.getLocalizedMessage(), e.getMessage(), e);
		}
	}

	private class StringToUrlConverter implements Converter<String, URL>
	{
		private static final long serialVersionUID = 8308014496038127914L;

		@Override
		public URL convertToModel(String urlString, Class<? extends URL> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException
		{
			if (!urlString.isEmpty())
			{
				try
				{
					return new URL(urlString);
				}
				catch (MalformedURLException e)
				{
					logger.error("Could not convert input '" + urlString + "' to model: " + e.getMessage());
					throw new ConversionException("Could not convert input '" + urlString + "' to model: " + e.getMessage());
				}
			}
			return null;
		}

		@Override
		public String convertToPresentation(URL value, Class<? extends String> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException
		{
			if (value != null)
			{
				return value.toString();
			}
			else
			{
				return new String();
			}
		}

		@Override
		public Class<URL> getModelType()
		{
			return URL.class;
		}

		@Override
		public Class<String> getPresentationType()
		{
			return String.class;
		}
	}
}
