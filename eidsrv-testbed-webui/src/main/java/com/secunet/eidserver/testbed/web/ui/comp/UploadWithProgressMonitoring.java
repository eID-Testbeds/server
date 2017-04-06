package com.secunet.eidserver.testbed.web.ui.comp;


import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.secunet.eidserver.testbed.web.i18n.I18NHandler;
import com.secunet.eidserver.testbed.web.i18n.I18NText;
import com.secunet.eidserver.testbed.web.ui.util.Humanizer;
import com.secunet.eidserver.testbed.web.ui.view.candidate.UploadReceiver;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class UploadWithProgressMonitoring extends Panel
{

	/** generated */
	private static final long serialVersionUID = -4074963875539619947L;


	private byte[] rawData;

	private Label fileNameOrHash = new Label();
	private Label sizeAndProgress = new Label();

	private ProgressBar progressbar = new ProgressBar();
	/*
	 * Currently deactived, to due the small file sizes.F
	 * private Button cancelProcessing;
	 */
	private Button downloadButton;
	private StreamResource streamResource;
	private FileDownloader fileDownload;
	private Upload upload;

	private boolean renderAsHorizontal;


	public UploadWithProgressMonitoring(String caption, boolean horizontalLayouting, byte[] existingRawData, UploadReceiver receiver, FinishedListener finishedListener)
	{
		this.rawData = existingRawData;
		this.renderAsHorizontal = horizontalLayouting;

		// setup myself
		setCaption(caption);
		setIcon(FontAwesome.FILE);

		// setup layout
		AbstractOrderedLayout mainLayout = new HorizontalLayout();
		if (!renderAsHorizontal)
			mainLayout = new VerticalLayout();

		// mainLayout.setWidth(100, Unit.PERCENTAGE);
		mainLayout.setSpacing(true);
		mainLayout.setMargin(new MarginInfo(false, false, false, true));
		AbstractOrderedLayout infoLayout = new VerticalLayout();
		infoLayout.setSpacing(true);
		// formLayout.setMargin(new MarginInfo(false, false, false, true));
		AbstractOrderedLayout buttonLayout = new VerticalLayout();
		if (!renderAsHorizontal)
			buttonLayout = new HorizontalLayout();

		buttonLayout.setMargin(true);
		buttonLayout.setSpacing(true);
		mainLayout.addComponent(infoLayout);
		mainLayout.addComponent(buttonLayout);
		setContent(mainLayout);

		// setup BUTTONS
		upload = new Upload("", receiver);
		upload.setImmediate(true);
		upload.setButtonCaption(I18NHandler.getText(I18NText.Button_UploadFile));
		upload.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		final int buttonWidth = (int) ((double) upload.getButtonCaption().length() / 4d * 3);
		upload.setWidth(buttonWidth, Unit.REM);
		buttonLayout.addComponent(upload);
		buttonLayout.setComponentAlignment(upload, Alignment.TOP_CENTER);


		/*
		 * (!) Currently we deactived cancelation, due to the fact, that the file size is to small (!)
		 * cancelProcessing = new Button(I18NResource.getText(I18NTextResource.ButtonCancel));
		 * cancelProcessing.addClickListener(e -> upload.interruptUpload());
		 * cancelProcessing.setEnabled(false);
		 * cancelProcessing.setStyleName("small");
		 * cancelProcessing.setWidth(buttonWidth, Unit.REM);
		 * buttonLayout.addComponent(cancelProcessing);
		 * buttonLayout.setComponentAlignment(cancelProcessing, Alignment.BOTTOM_CENTER);
		 */


		downloadButton = new Button(I18NHandler.getText(I18NText.Button_Download), FontAwesome.FILE);
		downloadButton.addClickListener(e -> upload.interruptUpload());
		downloadButton.setEnabled((this.rawData != null));
		downloadButton.setStyleName("small");
		downloadButton.setWidth(buttonWidth, Unit.REM);
		buttonLayout.addComponent(downloadButton);
		buttonLayout.setComponentAlignment(downloadButton, Alignment.BOTTOM_CENTER);

		// FEATURE here we could be in need of a more complex way for bigger files
		// see: https://vaadin.com/forum#!/thread/2864064
		streamResource = new StreamResource(new StreamSource() {
			private static final long serialVersionUID = -7308680845393515801L;

			@Override
			public InputStream getStream()
			{
				if (UploadWithProgressMonitoring.this.rawData == null)
					return new ByteArrayInputStream(new byte[0]);
				else
					return new ByteArrayInputStream(UploadWithProgressMonitoring.this.rawData);
			}
		}, "rawData.bin");
		// FEATURE use new OnDemandFileDownloader to customize file name matching key-name
		fileDownload = new FileDownloader(streamResource);
		fileDownload.extend(downloadButton);


		// Setup Labels and other UI elements
		fileNameOrHash.setCaption(I18NHandler.getText(I18NText.Upload_Hash));
		fileNameOrHash.setWidth(25, Unit.EM);
		fileNameOrHash.setValue((this.rawData != null) ? " " + Humanizer.getHumanizedSHA1Hash(existingRawData) : "");
		infoLayout.addComponent(fileNameOrHash);


		sizeAndProgress.setCaption(I18NHandler.getText(I18NText.Upload_FileSize));
		sizeAndProgress.setValue(this.rawData != null ? this.rawData.length + " bytes" : "");
		infoLayout.addComponent(sizeAndProgress);

		// FEATURE we should setup the push/poll of ui for better responsiveness while downloading, and setting back the timing after upload/cancel
		progressbar.setCaption(I18NHandler.getText(I18NText.Upload_Progress));
		progressbar.setVisible(false);
		infoLayout.addComponent(progressbar);


		// listeners
		upload.addStartedListener(event -> uploadStarted(event));

		upload.addProgressListener(generateProgressListener());

		upload.addSucceededListener(receiver);

		upload.addFailedListener(event -> uploadFailed(event));

		upload.addFinishedListener(finishedListener);
		upload.addFinishedListener(event -> uploadFinished(receiver, event));

	}


	public void uploadFinished(UploadReceiver receiver, FinishedEvent event)
	{
		// get data
		this.rawData = receiver.getUploadedRawData();

		// update UI
		fileNameOrHash.setValue((this.rawData != null) ? " " + Humanizer.getHumanizedSHA1Hash(this.rawData) : "");
		sizeAndProgress.setValue(this.rawData != null ? this.rawData.length + " bytes" : "");
		downloadButton.setEnabled((this.rawData != null));
		progressbar.setVisible(false);

		/*
		 * Currently deactived due to small file sizes
		 * cancelProcessing.setEnabled(false);
		 */
	}


	public void uploadFailed(FailedEvent event)
	{
		sizeAndProgress.setValue(I18NHandler.getText(I18NText.Upload_FailedAt0, ("" + Math.round(100 * (Float) progressbar.getValue()))));
	}


	public ProgressListener generateProgressListener()
	{
		return new Upload.ProgressListener() {
			private static final long serialVersionUID = -1308034540743569364L;

			@Override
			public void updateProgress(long readBytes, long contentLength)
			{
				progressbar.setValue(new Float(readBytes / (float) contentLength));
				sizeAndProgress.setValue(readBytes + " bytes / " + contentLength + " bytes");
			}

		};
	}


	public void uploadStarted(StartedEvent event)
	{
		progressbar.setValue(0f);
		progressbar.setVisible(true);
		fileNameOrHash.setValue(event.getFilename());

		/*
		 * deactived, small file sizes
		 * cancelProcessing.setEnabled(true);
		 */
	}

}
