package com.oneandone.typedrest.vaadin.views;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import java.io.*;
import com.oneandone.typedrest.*;
import com.oneandone.typedrest.vaadin.events.BlobUploadEvent;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import lombok.*;

/**
 * View component operating on a {@link BlobEndpoint}.
 */
public class BlobView extends AbstractEndpointView<BlobEndpoint> {

    private static final String TYPED_REST_BLOB = "typed-rest-blob";
    protected final String TMP_DIR = System.getProperty("java.io.tmpdir");
    protected File uploadTarget;
    protected File downloadTarget;

    private final Button downloadButton;
    private final Upload uploadButton;
    private String uploadMimeType;
    private String downloadMimeType;

    /**
     * Creates a new REST blob component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param caption A caption for the blob.
     */
    @SuppressWarnings("OverridableMethodCallInConstructor") // False positive due to lambda
    public BlobView(BlobEndpoint endpoint, EventBus eventBus, String caption) {
        super(endpoint, eventBus);
        setCaption(caption);

        uploadButton = new Upload("", (fileName, mimeType) -> {
            try {
                uploadMimeType = mimeType;
                if (uploadTarget != null) {
                    uploadTarget.delete();
                }

                uploadTarget = File.createTempFile(TYPED_REST_BLOB, "upload", new File(TMP_DIR));
                return new FileOutputStream(uploadTarget);
            } catch (IOException ex) {
                onError(ex);
                return null;
            }
        });

        uploadButton.addSucceededListener(succeededEvent -> uploadFrom());

        downloadButton = new Button("Download");
        downloadButton.addClickListener(clickEvent -> {
            if (downloadTarget != null) {
                downloadTarget.delete();
            }

            try {
                downloadTarget = File.createTempFile(TYPED_REST_BLOB, "download", new File(TMP_DIR));
                downloadTo(downloadTarget);
            } catch (IOException ex) {
                onError(ex);
            }
        });

        setCompositionRoot(getLayout());
    }

    @Override
    protected void onLoad() {
        try {
            endpoint.probe();
        } catch (IOException | IllegalAccessException | RuntimeException ex) {
            // HTTP OPTIONS server-side implementation is optional
        }

        endpoint.isDownloadAllowed().ifPresent(this::setDownloadEnabled);
        endpoint.isUploadAllowed().ifPresent(this::setUploadEnabled);
    }

    @Override
    public void detach() {
        super.detach();
        if (uploadTarget != null) {
            uploadTarget.delete();
        }
        if (downloadTarget != null) {
            downloadTarget.delete();
        }
    }

    /**
     * Generates the layout containing the control elements.
     *
     * @return the Layout to use {@link Window#setContent(Component)} with.
     */
    protected HorizontalLayout getLayout() {
        HorizontalLayout masterLayout = new HorizontalLayout();
        masterLayout.addComponent(uploadButton);
        masterLayout.addComponent(downloadButton);
        masterLayout.setComponentAlignment(uploadButton, Alignment.MIDDLE_LEFT);
        masterLayout.setComponentAlignment(downloadButton, Alignment.BOTTOM_RIGHT);
        masterLayout.setMargin(true);
        masterLayout.setSpacing(true);
        return masterLayout;
    }

    /**
     * Controls whether an upload button is shown.
     *
     * @param val Turns the feature on or off.
     */
    public void setUploadEnabled(boolean val) {
        uploadButton.setVisible(val);
    }

    /**
     * Called after upload to local instance succeeded.
     */
    protected void uploadFrom() {
        try {
            endpoint.uploadFrom(uploadTarget, uploadMimeType);
            eventBus.post(new BlobUploadEvent(endpoint));
            onUploadSuccess();
        } catch (IOException | IllegalArgumentException | IllegalAccessException | IllegalStateException ex) {
            onError(ex);
        }
    }

    /**
     * Controls whether a download button is shown.
     *
     * @param val Turns the feature on or off.
     */
    public void setDownloadEnabled(boolean val) {
        downloadButton.setVisible(val);
    }

    /**
     * The file name reported to the browser when downloading the blob content.
     */
    @Getter
    @Setter
    private String downloadFileName = "blob";

    /**
     * Downloads the file from {@link BlobEndpoint} to the given {@link File}.
     * !CAUTION! File won't get deleted after User's download succeeded.
     * !CAUTION!
     *
     * @param file the file, the file retrieved should be saved to.
     */
    protected void downloadTo(File file) {
        try {
            downloadMimeType = endpoint.downloadTo(new FileOutputStream(file));
            onDownloadSuccess();

            StreamResource streamResource = new StreamResource(() -> {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException ex) {
                    onError(ex);
                    return null;
                }
            }, file.getName());

            streamResource.setFilename(downloadFileName);
            streamResource.setMIMEType(downloadMimeType);
            setResource("file-download", streamResource);

            ResourceReference resourceReference = ResourceReference.create(streamResource, this, "file-download");
            Page.getCurrent().open(resourceReference.getURL(), null);
        } catch (IOException | IllegalArgumentException | IllegalAccessException | IllegalStateException ex) {
            onError(ex);
        }
    }

    /**
     * Called directly after download from {@link BlobEndpoint} succeeded.
     */
    protected void onDownloadSuccess() {
    }

    /**
     * Called directly after uploaded to {@link BlobEndpoint} succeeded.
     */
    protected void onUploadSuccess() {
        Notification.show("Success", String.format("File '%s' has successfully been uploaded", uploadTarget.getName()), Notification.Type.TRAY_NOTIFICATION);
    }
}
