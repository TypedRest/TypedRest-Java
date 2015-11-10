package com.oneandone.typedrest.vaadin;

import javax.naming.*;
import java.io.*;
import com.oneandone.typedrest.*;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import org.apache.http.*;
import lombok.*;

/**
 * Component operating on a {@link BlobEndpoint}.
 */
public class BlobComponent extends AbstractComponent<BlobEndpoint> {

    protected final String TMP_DIR = System.getProperty("java.io.tmpdir") + File.separator;
    private final Button downloadButton;
    private final Upload uploadButton;
    @Getter
    @Setter
    protected File uploadTarget;
    @Getter
    @Setter
    protected File downloadTarget;

    /**
     * Creates a new REST blob component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param caption A caption for the blob.
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public BlobComponent(BlobEndpoint endpoint, String caption) {
        super(endpoint);
        setCaption(caption);

        uploadButton = new Upload("", (fileName, mimeType) -> {
            try {
                uploadTarget = new File(TMP_DIR + fileName);
                return new FileOutputStream(uploadTarget);
            } catch (FileNotFoundException e) {
                getErrorHandler().error(new com.vaadin.server.ErrorEvent(e));
                return null;
            }
        });

        uploadButton.addSucceededListener(succeededEvent -> uploadFrom(uploadTarget));

        downloadButton = new Button("Download");
        downloadButton.addClickListener(clickEvent -> downloadTo(downloadTarget));

        setContent(getLayout());
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
     * Called after upload to local instance succeeded.
     *
     * @param file the file to upload to the {@link BlobEndpoint}.
     */
    protected void uploadFrom(File file) {
        try {
            endpoint.uploadFrom(file);
            onUploadSuccess();
        } catch (IOException | IllegalAccessException | IllegalArgumentException | HttpException |
                OperationNotSupportedException e) {
            getErrorHandler().error(new com.vaadin.server.ErrorEvent(e));
        }
    }

    /**
     * Called on {@link BlobComponent#downloadButton}-click. Downloads the file
     * from {@link BlobEndpoint} to the given {@link File}. !CAUTION! File won't
     * get deleted after User's download succeeded. !CAUTION!
     *
     * @param file the file, the file retrieved should be saved to.
     */
    protected void downloadTo(File file) {
        try {
            endpoint.downloadTo(new FileOutputStream(file));
            onDownloadSuccess();

            StreamResource streamResource = new StreamResource(() -> {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    getErrorHandler().error(new com.vaadin.server.ErrorEvent(e));
                    return null;
                }
            }, file.getName());

            setResource("file-download", streamResource);

            ResourceReference resourceReference = ResourceReference.create(streamResource, this, "file-download");
            Page.getCurrent().open(resourceReference.getURL(), null);

        } catch (IOException | IllegalAccessException | HttpException | OperationNotSupportedException e) {
            getErrorHandler().error(new com.vaadin.server.ErrorEvent(e));
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
