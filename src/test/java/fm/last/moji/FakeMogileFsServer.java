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
package fm.last.moji;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class FakeMogileFsServer {

  private static final Logger log = LoggerFactory.getLogger(FakeMogileFsServer.class);

  private ServerSocket trackerSocket;
  private Thread trackerThread;

  FakeMogileFsServer(final Builder builder) throws Exception {
    startTracker(builder);
  }

  public String getAddressAsString() {
    return trackerSocket.getInetAddress().getHostName() + ":" + trackerSocket.getLocalPort();
  }

  public InetAddress getInetAddress() {
    return trackerSocket.getInetAddress();
  }

  public InetSocketAddress getInetSocketAddress() {
    return new InetSocketAddress(trackerSocket.getInetAddress().getHostName(), trackerSocket.getLocalPort());
  }

  public int getPort() {
    return trackerSocket.getLocalPort();
  }

  public void close() throws Exception {
    log.info("Closing");
    try {
      trackerThread.interrupt();
    } finally {
      trackerSocket.close();
    }
  }

  private void startTracker(final Builder builder) throws IOException {
    trackerSocket = new ServerSocket(0);
    trackerThread = new TrackerServer(builder.conversation);
    trackerThread.start();
    log.info("Tracker server running on: {}", getAddressAsString());
  }

  private final class TrackerServer extends Thread {
    private final List<Stanza> conversation;

    private TrackerServer(List<Stanza> conversation) {
      this.conversation = conversation;
    }

    @Override
    public void run() {
      Socket accept = null;
      BufferedReader reader = null;
      OutputStreamWriter writer = null;
      try {
        accept = trackerSocket.accept();
        writer = new OutputStreamWriter(accept.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(accept.getInputStream()));

        for (Stanza stanza : conversation) {
          String line = reader.readLine();
          for (String element : stanza.responses) {
            if (!line.contains(element)) {
              return;
            }
          }
          writer.write(stanza.request);
          writer.flush();
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        IOUtils.closeQuietly(reader);
        IOUtils.closeQuietly(writer);
        IOUtils.closeQuietly(accept);
      }
    }
  }

  public static class Builder {

    private final List<Stanza> conversation;
    private Stanza current;

    public Builder() {
      conversation = new ArrayList<FakeMogileFsServer.Stanza>();
    }

    public Builder whenRequestContains(String... strings) {
      current = new Stanza();
      current.responses = new HashSet<String>(Arrays.asList(strings));
      return this;
    }

    public Builder thenRespond(String string) {
      current.request = string;
      conversation.add(current);
      current = null;
      return this;
    }

    public FakeMogileFsServer build() throws Exception {
      return new FakeMogileFsServer(this);
    }

  }

  private static class Stanza {
    private String request;
    private Set<String> responses;

    private Stanza() {
    }
  }

}
