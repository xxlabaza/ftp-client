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

import java.util.Date;
import lombok.Data;
import org.apache.commons.net.ftp.FTPFile;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 20.10.2016
 */
@Data
public class FtpFile {

    private final String parent;

    private final String name;

    private final String path;

    private final FtpFileType type;

    private final long size;

    private final Date timestamp;

    FtpFile (String parent, FTPFile file) {
        this.parent = parent;
        name = file.getName();
        path = parent + "/" + name;

        type = FtpFileType.of(file.getType());
        size = file.getSize();
        timestamp = file.getTimestamp().getTime();
    }

    public enum FtpFileType {

        DIRECTORY,
        FILE,
        SYMBOLIC_LINK,
        UNDEFINED;

        static FtpFileType of (int type) {
            switch (type) {
            case 0:
                return FILE;
            case 1:
                return DIRECTORY;
            case 2:
                return SYMBOLIC_LINK;
            case 3:
            default:
                return UNDEFINED;
            }
        }
    }
}
