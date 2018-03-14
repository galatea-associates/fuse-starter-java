package org.galatea.starter.entrypoint.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Collection;

public class FilePollerTest {

  // files with extension .txt
  private static final String FILE_REGEX = "^.*\\.txt$";

  @ClassRule
  public final static TemporaryFolder inputFolder = new TemporaryFolder();

  private FilePoller filePoller;
  private File preexistingFile;

  @Before
  public void setUp() throws Exception {
    // this must be created BEFORE FilePoller is initialized
    preexistingFile = inputFolder.newFile("first.txt");
    File directory = inputFolder.getRoot();
    filePoller = new FilePoller(directory.toPath().toString(), FILE_REGEX);
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
  public void onlyNewFilesReturnedAfterFirstPoll() throws Exception {
    filePoller.poll();

    File secondFile = inputFolder.newFile("second.txt");
    Collection<File> secondPoll = filePoller.poll();

    assertEquals(1, secondPoll.size());
    assertEquals(secondFile, secondPoll.iterator().next());

    secondFile.delete();
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