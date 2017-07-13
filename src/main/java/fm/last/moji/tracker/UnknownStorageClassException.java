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
package fm.last.moji.tracker;

/**
 * An attempt was made to assign an non-existent storage class to a {@link fm.last.moji.MojiFile}.
 */
public class UnknownStorageClassException extends TrackerException {

  private static final long serialVersionUID = 1L;

  private final String storageClass;

  public UnknownStorageClassException(String storageClass) {
    super("storageClass=" + storageClass);
    this.storageClass = storageClass;
  }

  public String getStorageClass() {
    return storageClass;
  }

}
