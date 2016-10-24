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

import java.io.InputStream;
import java.util.List;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 20.10.2016
 */
public interface FtpClientService {

  FtpFile createFile (String path, InputStream inputStream);

  FtpFile createFolder (String path);

  List<FtpFile> listRoot ();

  List<FtpFile> list (String path);

  void deleteFile (String path);

  void deleteFolder (String path);
}
