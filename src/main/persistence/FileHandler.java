package persistence;

import org.json.JSONObject;

import java.io.IOException;

public abstract class FileHandler {
    protected String source;
    protected String fileName;
    protected JSONObject jsonObj;

    public FileHandler(String path, String fileName) {
        this.source = path;
        this.fileName = fileName;
    }

    //produce JSONObject
    protected abstract JSONObject toJson() throws IOException;

}
