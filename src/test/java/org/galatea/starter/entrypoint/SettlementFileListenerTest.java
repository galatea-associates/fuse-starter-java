package org.galatea.starter.entrypoint;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.service.SettlementFileProcessor;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class SettlementFileListenerTest extends ASpringTest {

  // files with extension .txt
  private final String FILE_REGEX = "^.*\\.txt$";

  private SettlementFileListener fileListener;

  @Mock
  private SettlementFileProcessor fileProcessor;

  @ClassRule
  public final static TemporaryFolder inputFolder = new TemporaryFolder();

  private File inputDirectory;

  @Before
  public void setUp() {
    inputDirectory = inputFolder.getRoot();
    fileListener = new SettlementFileListener(inputDirectory, FILE_REGEX, fileProcessor);
  }

  @Test
  public void readsFilesMatchingRegexFromDirectory() {
    createFileInDirectory("validFile.txt");
    createFileInDirectory("invalidFile.wrong");

    File[] validFiles = inputDirectory.listFiles((FileFilter) new RegexFileFilter(FILE_REGEX));

    fileListener.processFilesInDirectory();

    verify(fileProcessor).processFiles(Collections.singletonList(validFiles[0]));
  }

  @Test
  public void processedFilesAreNotReprocessed() {
    createFileInDirectory("validFile.txt");

    // assume all files are processed successfully
    when(fileProcessor.processFiles(any())).thenAnswer(i -> i.getArguments()[0]);

    // first pass - should mark file as processed
    Collection<File> processed = fileListener.processFilesInDirectory();
    assertEquals(1, processed.size());

    // second pass - should process no files
    processed = fileListener.processFilesInDirectory();
    assertEquals(0, processed.size());
  }

  @Test
  public void nonExistentDirectoryReturnsEmptyList() {
    fileListener = new SettlementFileListener(new File("nonDir"), FILE_REGEX, fileProcessor);

    Collection<File> processed = fileListener.processFilesInDirectory();

    assertEquals(0, processed.size());
  }

  private void createFileInDirectory(String fileName) {
    try {
      inputFolder.newFile(fileName);
    } catch (IOException exception) {
      // file already exists, do nothing
    }
  }

}