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

import static java.util.stream.Collectors.toList;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 20.10.2016
 */
@Slf4j
@Service
class BasicFtpClientService implements FtpClientService {

    @Autowired
    private FtpClientProperties ftpClientProperties;

    @Override
    @SneakyThrows
    public FtpFile createFile (String path, InputStream inputStream) {
        FTPClient ftpClient = createFtpClient();
        ftpClient.storeFile(path, inputStream);
        val result = findFtpFile(path, ftpClient);
        close(ftpClient);
        return result;
    }

    @Override
    @SneakyThrows
    public FtpFile createFolder (String path) {
        FTPClient ftpClient = createFtpClient();
        ftpClient.makeDirectory(path);
        val result = findFtpFile(path, ftpClient);
        close(ftpClient);
        return result;
    }

    @Override
    public List<FtpFile> listRoot () {
        return list(null);
    }

    @Override
    @SneakyThrows
    public List<FtpFile> list (String path) {
        val parent = path == null ? "/" : path;
        FTPClient ftpClient = createFtpClient();
        val result = Stream.of(ftpClient.listFiles(parent))
                .map(it -> new FtpFile(parent, it))
                .collect(toList());
        close(ftpClient);
        return result;
    }

    @Override
    @SneakyThrows
    public void deleteFile (String path) {
        FTPClient ftpClient = createFtpClient();
        ftpClient.deleteFile(path);
        close(ftpClient);
    }

    @Override
    @SneakyThrows
    public void deleteFolder (String path) {
        FTPClient ftpClient = createFtpClient();
        deleteFolder(path, ftpClient);
        close(ftpClient);
    }

    @SneakyThrows
    private FTPClient createFtpClient () {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setAutodetectUTF8(true);
        ftpClient.connect(ftpClientProperties.getHost(), ftpClientProperties.getPort());

        ftpClient.enterLocalPassiveMode();
        ftpClient.enterRemotePassiveMode();

        if (!ftpClient.login(ftpClientProperties.getUsername(), ftpClientProperties.getPassword()) ||
            !FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {

            ftpClient.disconnect();
            throw new RuntimeException("Invalid FTP credentials");
        }
        return ftpClient;
    }

    @SneakyThrows
    private void close (FTPClient ftpClient) {
        ftpClient.logout();
        ftpClient.disconnect();
    }

    @SneakyThrows
    private void deleteFolder (String path, FTPClient ftpClient) {
        for (FTPFile file : ftpClient.listFiles(path)) {
            String fullPath = path + "/" + file.getName();
            if (file.isDirectory()) {
                deleteFolder(fullPath, ftpClient);
            } else {
                ftpClient.deleteFile(fullPath);
            }
        }
        ftpClient.removeDirectory(path);
    }

    @SneakyThrows
    private FtpFile findFtpFile (String path, FTPClient ftpClient) {
        int nameStartIndex = path.lastIndexOf("/");
        String name = path.substring(nameStartIndex + 1);
        String parent = nameStartIndex > 0
                     ? path.substring(0, nameStartIndex)
                     : "/";
        return Stream.of(ftpClient.listFiles(parent))
                .filter(it -> it.getName().equals(name))
                .findFirst()
                .map(it -> new FtpFile(parent, it))
                .orElse(null);
    }
}
