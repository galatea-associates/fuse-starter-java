package org.galatea.starter.entrypoint.file;

import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Polls a directory for files, only including files that have not been returned in previous polls.
 */
public class FilePoller implements IFilePoller {

  @NonNull
  private final File directory;

  @NonNull
  private final String regex;

  @NonNull
  private final FileWatcher fileWatcher;

  private boolean firstPoll = true;

  public FilePoller(final FileWatcher fileWatcher, final String regex) {
    this.regex = regex;
    this.fileWatcher = fileWatcher;
    this.directory = new File(fileWatcher.getDirectory());
  }

  @Override
  public Collection<File> poll() throws IOException {
    Collection<File> files;

    if (firstPoll) {
      files = filesInDirectory();
      firstPoll = false;
    } else {
      files = fileWatcher.poll();
    }

    return filesMatchingRegex(files);
  }

  private Collection<File> filesInDirectory() throws IOException {
    try (Stream<Path> filePaths = Files.walk(directory.toPath())) {
      return filePaths
          .filter(Files::isRegularFile)
          .map(Path::toFile)
          .collect(Collectors.toList());
    }
  }

  private Collection<File> filesMatchingRegex(final Collection<File> files) {
    return files.stream().filter(f -> f.getName().matches(regex)).collect(Collectors.toList());
  }

}
