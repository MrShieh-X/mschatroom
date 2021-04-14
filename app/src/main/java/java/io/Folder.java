package java.io;

import com.mrshiehx.mschatroom.utils.FileUtils;

import java.net.URI;
import java.util.Objects;

public class Folder extends File {
    public Folder(String pathname) {
        super(pathname);
    }

    public Folder(String parent, String child) {
        super(parent, child);
    }

    public Folder(File parent, String child) {
        super(parent, child);
    }

    public Folder(URI uri) {
        super(uri);
    }

    @Override
    public String getAbsolutePath() {
        return super.getAbsolutePath() + separator;
    }

    @Override
    public boolean createNewFile() {
        throw new UnsupportedOperationException("Please use the method toFile()!");
    }

    public File toFile() {
        return new File(super.getAbsolutePath());
    }

    @Override
    public boolean delete() {
        return FileUtils.deleteFiles(this);
    }

    @Override
    public String getCanonicalPath() throws IOException {
        return super.getCanonicalPath() + separator;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    public long getSize() {
        try {
            return FileUtils.getFolderSize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long length() {
        throw new UnsupportedOperationException("Please use the method toFile()!");
    }
}
