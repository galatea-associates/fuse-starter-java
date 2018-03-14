package org.galatea.starter.entrypoint.file;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class SettlementFileProcessingManagerTest {

  @Mock
  private SettlementFileProcessor fileProcessor;

  @ClassRule
  public final static TemporaryFolder inputFolder = new TemporaryFolder();

  private SettlementFileProcessingManager fileProcessingManager;

  @Before
  public void setUp() {
    fileProcessingManager = new SettlementFileProcessingManager(fileProcessor);
  }

  @Test
  public void duplicateFilesAreNotProcessed() throws Exception {
    File file = inputFolder.newFile();

    Collection<File> processed = fileProcessingManager
        .processFiles(Arrays.asList(file, file));

    verify(fileProcessor, times(1)).processFile(file);
    assertEquals(1, processed.size());
  }

  @Test
  public void fileRemainsInQueueAfterProcessingFailure() throws Exception {
    File file = inputFolder.newFile();
    List<File> files = singletonList(file);

    doThrow(new IOException()).when(fileProcessor).processFile(file);

    Collection<File> processed = fileProcessingManager.processFiles(files);
    assertEquals(0, processed.size());

    doNothing().when(fileProcessor).processFile(file);

    processed = fileProcessingManager.processFiles(files);
    assertEquals(1, processed.size());
  }

}