/**
 * Copyright (C) 2012-2017 Last.fm & The "mogilefs-moji" committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fm.last.moji.local;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Used by {@link fm.last.moji.local.LocalFileSystemMoji LocalFileSystemMoji} to generate and resolve local filenames
 * for given keys and domains.
 */
public interface LocalFileNamingStrategy {

  String newFileName(String domain, String key, String storageClass);

  String domainForFileName(String fileName);

  String keyForFileName(String fileName);

  File folderForDomain(String domain);

  FilenameFilter filterForPrefix(String domain, String keyPrefix);

  String storageClassForFileName(String fileName);

}
