package com.oneandone.typedrest.vaadin.views;

import com.google.common.base.Charsets;
import com.google.common.eventbus.EventBus;
import com.google.common.io.CharStreams;
import java.io.*;
import com.oneandone.typedrest.*;
import com.vaadin.data.Validator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import java.nio.charset.Charset;
import lombok.*;
import org.apache.http.entity.ContentType;

/**
 * View component providing plain-text editing for a {@link BlobEndpoint}.
 */
public class TextBlobView extends AbstractBlobView {

    private final TextArea textArea = new TextArea();

    protected final Button saveButton = new Button("Save", x -> upload());
    protected final HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, deleteButton);

    protected final VerticalLayout masterLayout = new VerticalLayout(textArea, buttonsLayout);

    /**
     * Creates a new REST text component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send event between components.
     */
    public TextBlobView(BlobEndpoint endpoint, EventBus eventBus) {
        super(endpoint, eventBus);

        textArea.setSizeFull();

        saveButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        buttonsLayout.setSpacing(true);

        masterLayout.setExpandRatio(textArea, 1);
        masterLayout.setComponentAlignment(buttonsLayout, Alignment.MIDDLE_RIGHT);
        masterLayout.setMargin(true);
        masterLayout.setSpacing(true);
        masterLayout.setSizeFull();
        setCompositionRoot(masterLayout);
    }

    /**
     * The charset used for interpreting the blob as text.
     */
    @Getter
    @Setter
    private Charset charset = Charsets.UTF_8;

    @Override
    protected void onLoad() throws IOException, IllegalArgumentException, IllegalAccessException, IllegalStateException {
        try {
            InputStream stream = endpoint.download();
            textArea.setReadOnly(false);
            textArea.setValue(CharStreams.toString(new InputStreamReader(stream, charset)));
        } catch (FileNotFoundException ex) {
            Notification.show("Warning", ex.getLocalizedMessage(), Notification.Type.WARNING_MESSAGE);
        }

        handleAllowedVerbs();
    }

    @Override
    public void setDownloadEnabled(boolean val) {
    }

    @Override
    public void setUploadEnabled(boolean val) {
        textArea.setReadOnly(!val);
        saveButton.setVisible(val);
    }

    @Override
    protected void onUpload()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        byte[] data = textArea.getValue().getBytes(charset);
        endpoint.upload(data, ContentType.create("text/plain", charset));
    }
}
