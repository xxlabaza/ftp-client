/*
 * Copyright 2016 Artem Labazin <xxlabaza@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xxlabaza.test.ftp.client;

import static com.xxlabaza.test.ftp.client.FtpFile.FtpFileType.DIRECTORY;

import java.io.InputStream;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 21.10.2016
 */
@SpringBootTest
@SpringBootApplication
@RunWith(SpringRunner.class)
public class FtpClientIT {

    @Autowired
    private FtpClientService ftpClientService;

    @After
    @Before
    public void before () {
        ftpClientService.listRoot().forEach(it -> {
            if (it.getType().equals(DIRECTORY)) {
                ftpClientService.deleteFolder(it.getPath());
            } else {
                ftpClientService.deleteFile(it.getPath());
            }
        });
    }

    @Test
    public void listRoot () {
        List<FtpFile> listRoot = ftpClientService.listRoot();

        Assert.assertTrue(listRoot.isEmpty());
    }

    @Test
    public void createFolder () {
        ftpClientService.createFolder("/popa");

        List<FtpFile> listRoot = ftpClientService.listRoot();
        Assert.assertFalse(listRoot.isEmpty());
        Assert.assertEquals(1, listRoot.size());
        Assert.assertEquals("popa", listRoot.get(0).getName());
    }

    @Test
    public void createFile () throws Exception {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.yml")) {
            ftpClientService.createFile("/application.yml", input);
        }

        List<FtpFile> listRoot = ftpClientService.listRoot();
        Assert.assertFalse(listRoot.isEmpty());
        Assert.assertEquals(1, listRoot.size());
        Assert.assertEquals("application.yml", listRoot.get(0).getName());
    }

    @Test
    public void deleteFileInFolder () throws Exception {
        FtpFile folder = ftpClientService.createFolder("/popa");
        FtpFile file;
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.yml")) {
            file = ftpClientService.createFile(folder.getPath() + "/application.yml", input);
        }

        List<FtpFile> listFolder = ftpClientService.list(folder.getPath());
        Assert.assertFalse(listFolder.isEmpty());
        Assert.assertEquals(1, listFolder.size());
        Assert.assertEquals("application.yml", listFolder.get(0).getName());

        ftpClientService.deleteFile(file.getPath());

        listFolder = ftpClientService.list(folder.getPath());
        Assert.assertTrue(listFolder.isEmpty());
    }

    @Test
    public void deleteFolderWithFiles () throws Exception {
        List<FtpFile> listRoot = ftpClientService.listRoot();
        Assert.assertTrue(listRoot.isEmpty());

        FtpFile folder = ftpClientService.createFolder("/popa");
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.yml")) {
            ftpClientService.createFile(folder.getPath() + "/application.yml", input);
        }

        List<FtpFile> listFolder = ftpClientService.list(folder.getPath());
        Assert.assertFalse(listFolder.isEmpty());
        Assert.assertEquals(1, listFolder.size());
        Assert.assertEquals("application.yml", listFolder.get(0).getName());

        listRoot = ftpClientService.listRoot();
        Assert.assertFalse(listRoot.isEmpty());

        ftpClientService.deleteFolder(folder.getPath());

        listRoot = ftpClientService.listRoot();
        Assert.assertTrue(listRoot.isEmpty());
    }
}
