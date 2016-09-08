package com.oneandone.typedrest.vaadin.views;

import com.google.common.eventbus.EventBus;
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

    private final Button downloadButton = new Button("Download");
    private final FileDownloader fileDownloader = new FileDownloader(new StreamResource(() -> {
        try {
            return endpoint.download();
        } catch (IOException | IllegalArgumentException | IllegalAccessException | IllegalStateException ex) {
            onError(ex);
            return null;
        }
    }, "")) {
        // Get downloadFileName on-demand instead of ahead-of-time
        @Override
        public boolean handleConnectorRequest(VaadinRequest request, VaadinResponse response, String path)
                throws IOException {
            ((StreamResource) this.getResource("dl")).setFilename(downloadFileName);
            return super.handleConnectorRequest(request, response, path);
        }
    };

    private final Upload uploadButton;
    private File uploadTarget;
    private String uploadMimeType;

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

        fileDownloader.extend(downloadButton);

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
        uploadButton.addSucceededListener(x -> uploadFrom());

        HorizontalLayout masterLayout = new HorizontalLayout(uploadButton, downloadButton);
        masterLayout.setComponentAlignment(uploadButton, Alignment.MIDDLE_LEFT);
        masterLayout.setComponentAlignment(downloadButton, Alignment.BOTTOM_RIGHT);
        masterLayout.setMargin(true);
        masterLayout.setSpacing(true);
        setCompositionRoot(masterLayout);
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
    }

    /**
     * The file name reported to the browser when downloading the blob content.
     */
    @Getter
    @Setter
    private String downloadFileName = "blob";

    /**
     * Controls whether a download button is shown.
     *
     * @param val Turns the feature on or off.
     */
    public void setDownloadEnabled(boolean val) {
        downloadButton.setVisible(val);
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
     * Called directly after uploaded to {@link BlobEndpoint} succeeded.
     */
    protected void onUploadSuccess() {
        Notification.show("Success", String.format("File '%s' has successfully been uploaded", uploadTarget.getName()), Notification.Type.TRAY_NOTIFICATION);
    }
}
