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
package fm.last.moji.tracker.impl;

import static fm.last.moji.tracker.impl.ErrorCode.KEY_EXISTS;
import static fm.last.moji.tracker.impl.ErrorCode.UNKNOWN_CLASS;
import static fm.last.moji.tracker.impl.ErrorCode.UNKNOWN_COMMAND;
import static fm.last.moji.tracker.impl.ErrorCode.UNKNOWN_KEY;
import static fm.last.moji.tracker.impl.ResponseStatus.OK;

import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fm.last.moji.tracker.Destination;
import fm.last.moji.tracker.KeyExistsAlreadyException;
import fm.last.moji.tracker.Tracker;
import fm.last.moji.tracker.TrackerException;
import fm.last.moji.tracker.UnknownCommandException;
import fm.last.moji.tracker.UnknownKeyException;
import fm.last.moji.tracker.UnknownStorageClassException;
import fm.last.moji.tracker.impl.Request.Builder;

class TrackerImpl implements Tracker {

  private static final Logger log = LoggerFactory.getLogger(TrackerImpl.class);

  private final Socket socket;
  private final RequestHandler requestHandler;

  public TrackerImpl(Socket socket, RequestHandler requestHandler) {
    this.socket = socket;
    this.requestHandler = requestHandler;
  }

  @Override
  public List<URL> getPaths(String key, String domain) throws TrackerException {
    GetPathsOperation operation = new GetPathsOperation(requestHandler, domain, key, false);
    operation.execute();
    return operation.getPaths();
  }

  @Override
  public Map<String, String> fileInfo(String key, String domain) throws TrackerException {
    String command = "file_info";
    Request request = new Request.Builder(10).command(command).arg("domain", domain).arg("key", key).build();
    Response response = requestHandler.performRequest(request);
    if (response.getStatus() != OK) {
      String message = response.getMessage();
      handleUnknownKeyException(key, domain, message);
      handleUnknownCommandException(command, message);
      throw new TrackerException(message);
    }
    return response.getValueMap();
  }

  @Override
  public List<Destination> createOpen(String key, String domain, String storageClass) throws TrackerException {
    CreateOpenOperation operation = new CreateOpenOperation(requestHandler, domain, key, storageClass, true);
    operation.execute();
    return operation.getDestinations();
  }

  @Override
  public List<String> list(String domain, String keyPrefix, Integer limit) throws TrackerException {
    ListKeysOperation operation = new ListKeysOperation(requestHandler, domain, keyPrefix, limit);
    operation.execute();
    return operation.getKeys();
  }

  @Override
  public void createClose(String key, String domain, Destination destination, long size) throws TrackerException {
    Request request = new Builder(6).command("create_close").arg("domain", domain).arg("key", key)
        .arg("devid", destination.getDevId()).arg("path", destination.getPath()).arg("fid", destination.getFid())
        .arg("size", size).build();
    Response response = requestHandler.performRequest(request);
    handleGeneralResponseError(response);
  }

  @Override
  public void delete(String key, String domain) throws TrackerException {
    Request request = new Request.Builder(2).command("delete").arg("domain", domain).arg("key", key).build();
    Response response = requestHandler.performRequest(request);
    if (response.getStatus() != OK) {
      String message = response.getMessage();
      handleUnknownKeyException(key, domain, message);
      throw new TrackerException(message);
    }
  }

  @Override
  public void rename(String fromKey, String domain, String toKey) throws TrackerException {
    Request request = new Request.Builder(3).command("rename").arg("domain", domain).arg("from_key", fromKey)
        .arg("to_key", toKey).build();
    Response response = requestHandler.performRequest(request);
    if (response.getStatus() != OK) {
      String message = response.getMessage();
      handleUnknownKeyException(fromKey, domain, message);
      handleKeyAlreadyExists(domain, toKey, message);
      throw new TrackerException(message);
    }
  }

  @Override
  public void updateStorageClass(String key, String domain, String newStorageClass) throws TrackerException {
    Request request = new Request.Builder(3).command("updateclass").arg("domain", domain).arg("key", key)
        .arg("class", newStorageClass).build();
    Response response = requestHandler.performRequest(request);
    if (response.getStatus() != OK) {
      String message = response.getMessage();
      handleUnknownKeyException(key, domain, message);
      handleUnknownStorageClass(newStorageClass, message);
      throw new TrackerException(message);
    }
  }

  @Override
  public Map<String, Map<String, String>> getDeviceStatuses(String domain) throws TrackerException {
    GetDeviceStatusesOperation operation = new GetDeviceStatusesOperation(requestHandler, domain);
    operation.execute();
    return operation.getParametersByDevice();
  }

  @Override
  public void noop() throws TrackerException {
    Request request = new Request.Builder(0).command("noop").build();
    Response response = requestHandler.performRequest(request);
    handleGeneralResponseError(response);
  }

  @Override
  public void close() {
    if (requestHandler != null) {
      requestHandler.close();
    }
    IOUtils.closeQuietly(socket);
    log.debug("Closed");
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("TrackerImpl [socket=");
    builder.append(socket);
    builder.append(", requestHandler=");
    builder.append(requestHandler);
    builder.append("]");
    return builder.toString();
  }

  private void handleUnknownStorageClass(String storageClass, String message) throws UnknownStorageClassException {
    if (UNKNOWN_CLASS.isContainedInLine(message)) {
      throw new UnknownStorageClassException(storageClass);
    }
  }

  private void handleKeyAlreadyExists(String domain, String key, String message) throws KeyExistsAlreadyException {
    if (KEY_EXISTS.isContainedInLine(message)) {
      throw new KeyExistsAlreadyException(domain, key);
    }
  }

  private void handleUnknownKeyException(String key, String domain, String message) throws UnknownKeyException {
    if (UNKNOWN_KEY.isContainedInLine(message)) {
      throw new UnknownKeyException(domain, key);
    }
  }

  private void handleUnknownCommandException(String command, String message) throws UnknownCommandException {
    if (UNKNOWN_COMMAND.isContainedInLine(message)) {
      throw new UnknownCommandException(command);
    }
  }

  private void handleGeneralResponseError(Response response) throws TrackerException {
    if (response.getStatus() != OK) {
      throw new TrackerException(response.getMessage());
    }
  }

}
