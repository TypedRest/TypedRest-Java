package com.oneandone.typedrest.vaadin.views;

import com.google.common.eventbus.EventBus;
import java.io.*;
import com.oneandone.typedrest.*;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.*;
import org.apache.http.entity.ContentType;

/**
 * View component providing file upload and download for a {@link BlobEndpoint}.
 */
public class BlobView extends AbstractBlobView {

    protected final String TMP_DIR = System.getProperty("java.io.tmpdir");

    /**
     * The file name reported to the browser when downloading the blob content.
     */
    @Getter
    @Setter
    private String downloadFileName = "blob";

    protected final Button downloadButton = new Button("Download") {
        {
            addStyleName(ValoTheme.BUTTON_PRIMARY);
        }
    };
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

    protected final Upload uploadButton;
    private File uploadTarget;
    private ContentType uploadContentType;

    protected final HorizontalLayout masterLayout;

    /**
     * Creates a new REST blob component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send event between components.
     */
    @SuppressWarnings("OverridableMethodCallInConstructor") // False positive due to lambda
    public BlobView(BlobEndpoint endpoint, EventBus eventBus) {
        super(endpoint, eventBus);

        fileDownloader.extend(downloadButton);

        uploadButton = new Upload("", (fileName, mimeType) -> {
            try {
                uploadContentType = ContentType.create(mimeType);
                if (uploadTarget != null) {
                    uploadTarget.delete();
                }

                uploadTarget = File.createTempFile("typed-rest-blob", "upload", new File(TMP_DIR));
                return new FileOutputStream(uploadTarget);
            } catch (IOException ex) {
                onError(ex);
                return null;
            }
        }) {
            {
                addStyleName(ValoTheme.BUTTON_FRIENDLY);
                addSucceededListener(x -> upload());
            }
        };

        masterLayout = new HorizontalLayout(uploadButton, downloadButton, deleteButton);
        masterLayout.setComponentAlignment(uploadButton, Alignment.MIDDLE_LEFT);
        masterLayout.setComponentAlignment(downloadButton, Alignment.BOTTOM_RIGHT);
        masterLayout.setComponentAlignment(deleteButton, Alignment.BOTTOM_RIGHT);
        masterLayout.setMargin(true);
        masterLayout.setSpacing(true);
        setCompositionRoot(masterLayout);
    }

    @Override
    public void detach() {
        super.detach();
        if (uploadTarget != null) {
            uploadTarget.delete();
        }
    }

    @Override
    public void setDownloadEnabled(boolean val) {
        downloadButton.setVisible(val);
    }

    @Override
    public void setUploadEnabled(boolean val) {
        uploadButton.setVisible(val);
    }

    @Override
    protected void onUpload()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        endpoint.upload(uploadTarget, uploadContentType);
    }
}
