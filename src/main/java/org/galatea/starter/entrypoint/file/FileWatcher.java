package org.galatea.starter.entrypoint.file;

import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Watches a directory for new file entries using the Java WatchService
 */
public class FileWatcher {

  @NonNull
  private final WatchService watchService;

  @NonNull
  private final String directory;

  public FileWatcher(final String directory) throws IOException {
    this.directory = directory;
    this.watchService = FileSystems.getDefault().newWatchService();
    initialize(directory);
  }

  private void initialize(String directory) throws IOException {
    // watch for NEW files (ignore modification and deletion events)
    Paths.get(directory).register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
  }

  public Collection<File> poll() {
    Collection<File> files = new ArrayList<>();

    WatchKey key = watchService.poll();
    if (key != null) {
      for (WatchEvent<?> event : key.pollEvents()) {
        if (event.context() instanceof Path) {
          Path dir = (Path) key.watchable();
          Path path = (Path) event.context();
          Path resolvedPath = dir.resolve(path);
          files.add(resolvedPath.toFile());
        }
      }
      // must reset to receive further events
      key.reset();
    }

    return files;
  }

  public String getDirectory() {
    return directory;
  }

}
