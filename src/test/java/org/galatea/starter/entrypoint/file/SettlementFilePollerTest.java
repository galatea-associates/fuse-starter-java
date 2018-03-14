package org.galatea.starter.entrypoint.file;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.galatea.starter.ASpringTest;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

import java.io.File;
import java.util.Collections;

public class SettlementFilePollerTest extends ASpringTest {

  private SettlementFilePoller fileListener;

  @Mock
  private SettlementFileProcessingManager fileProcessingManager;

  @Mock
  private IFilePoller filePoller;

  @ClassRule
  public final static TemporaryFolder inputFolder = new TemporaryFolder();

  @Before
  public void setUp() {
    fileListener = new SettlementFilePoller(fileProcessingManager, filePoller);
  }

  @Test
  public void passesFilesToFileProcessingManager() throws Exception {
    File file = inputFolder.newFile();

    when(filePoller.poll()).thenReturn(Collections.singletonList(file));

    fileListener.processFiles();

    verify(fileProcessingManager).processFiles(Collections.singletonList(file));
  }

}