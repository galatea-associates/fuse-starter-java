package org.galatea.starter.entrypoint.file;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public interface IFilePoller {

  Collection<File> poll() throws IOException;

}
