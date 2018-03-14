package org.galatea.starter.entrypoint.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.Collection;

@RunWith(MockitoJUnitRunner.class)
public class FilePollerTest {

  // files with extension .txt
  private static final String FILE_REGEX = "^.*\\.txt$";

  @ClassRule
  public final static TemporaryFolder inputFolder = new TemporaryFolder();

  @Mock
  private FileWatcher fileWatcher;

  private FilePoller filePoller;
  private File preexistingFile;

  @Before
  public void setUp() throws Exception {
    when(fileWatcher.getDirectory()).thenReturn(inputFolder.getRoot().getPath());

    // this must be created BEFORE FilePoller is initialized
    preexistingFile = inputFolder.newFile("first.txt");

    filePoller = new FilePoller(fileWatcher, FILE_REGEX);
  }

  @After
  public void cleanUp() {
    preexistingFile.delete();
  }

  @Test
  public void firstPollReturnsPreexistingFiles() throws Exception {
    Collection<File> files = filePoller.poll();

    assertEquals(1, files.size());
    assertEquals(preexistingFile, files.iterator().next());
  }

  @Test
  public void fileWatcherPolledAfterFirstPoll() throws Exception {
    filePoller.poll(); // first poll

    verify(fileWatcher, times(0)).poll();

    filePoller.poll(); // second poll

    verify(fileWatcher, times(1)).poll();
  }

  @Test
  public void filtersFilesBasedOnRegex() throws Exception {
    File invalid = inputFolder.newFile("invalid.ext");

    Collection<File> filesMatchingRegex = filePoller.poll();

    assertEquals(1, filesMatchingRegex.size());
    assertNotSame(invalid, filesMatchingRegex.iterator().next());

    invalid.delete();
  }

}