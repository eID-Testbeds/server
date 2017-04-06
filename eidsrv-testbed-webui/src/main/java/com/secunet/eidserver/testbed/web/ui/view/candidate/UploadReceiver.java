package com.secunet.eidserver.testbed.web.ui.view.candidate;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

/**
 * Simplified Vaadin mechanic to upload files into the vaadin application.
 */
public class UploadReceiver implements Receiver, SucceededListener
{

	/** generated */
	private static final long serialVersionUID = -8799235995642622906L;
	private ByteArrayOutputStream byteArrayOutStream = null;
	private byte[] uploadedRawData = null;
	private String filename = null;
	private String mimeType = null;
	private TestCandidateView callingView;

	public UploadReceiver(TestCandidateView view)
	{
		this.callingView = view;
	}


	@Override
	public OutputStream receiveUpload(String filename, String mimeType)
	{
		this.filename = filename;
		this.mimeType = mimeType;

		byteArrayOutStream = new ByteArrayOutputStream();
		return byteArrayOutStream;
	}


	@Override
	public void uploadSucceeded(SucceededEvent event)
	{
		uploadedRawData = byteArrayOutStream.toByteArray();
		callingView.uploadICStoBackEnd(uploadedRawData);
	}

	public byte[] getUploadedRawData()
	{
		return uploadedRawData;
	}

	public String getFilename()
	{
		return filename;
	}

	public String getMimeType()
	{
		return mimeType;
	}
}
