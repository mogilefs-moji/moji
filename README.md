# About
A file-like [MogileFS](http://danga.com/mogilefs/ "Danga Interactive - MogileFS") client for Java.

## Start using
You can obtain Moji from Maven Central: 

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/fm.last/moji/badge.svg?subject=fm.last:moji)](https://maven-badges.herokuapp.com/maven-central/fm.last/moji) ![GitHub license](https://img.shields.io/github/license/mogilefs-moji/moji.svg)
[![Javadocs](http://javadoc.io/badge/fm.last/moji.svg)](http://javadoc.io/doc/fm.last/moji)

# Features
* `java.io.File` like API
* Supports writing streams of unknown length
* Unit/Integration tests
* Spring friendly
* Tracker connection pooling with balancing between hosts and strategies for dealing with failed nodes
* Local file system implementation for faking in tests (`fm.last.moji.local.LocalFileSystemMoji`)

# Configuration
### Using plain-old-Java
        SpringMojiBean moji = new SpringMojiBean();
        moji.setAddressesCsv("192.168.0.1:7001,192.168.0.2:7001");
        moji.setDomain("testdomain");
        moji.initialise();
        moji.setTestOnBorrow(true);
### Using the Spring framework
Set some properties for your context:

        moji.tracker.address=192.168.0.1:7001,192.168.0.2:7001
        moji.domain=testdomain
        
Import the Moji Spring context:

        <import resource="moji-context.xml" />
  
*Or* create a Moji spring bean:

        <bean id="moji" class="fm.last.moji.spring.SpringMojiBean">
          <property name="addressesCsv" value="${moji.tracker.address}" />
          <property name="domain" value="${moji.domain}" />
          <property name="maxActive" value="${moji.pool.max.active:100}" />
          <property name="maxIdle" value="${moji.pool.max.idle:10}" />
          <property name="testOnBorrow" value="${moji.pool.test.on.borrow:true}" />
        </bean>

# Usage
#### Create/update a remote file
        MojiFile rickRoll = moji.getFile("rick-astley");
        moji.copyToMogile(new File("never-gonna-give-you-up.mp3"), rickRoll);
        
Or in a given storage class:

        MojiFile rickRoll = moji.getFile("rick-astley", "music-meme");

#### Get the remote file size
        long length = rickRoll.length();
#### Rename the remote file
        rickRoll.rename("stairway-to-heaven");
#### Check the existence of a remote file
        MojiFile abba = moji.getFile("voulez-vous");
        if (abba.exists()) {
          ...
#### Delete the remote file
        abba.delete();
#### Download a remote file
        MojiFile fooFighters = moji.getFile("stacked-actors");
        fooFighters.copyToFile(new File("foo-fighters.mp3"));
#### Modify the storage class of a remote file
        fooFighters.modifyStorageClass("awesome");

#### Stream from a remote file
        InputStream stream = null;
        try {
          stream = fooFighters.getInputStream();
          // Do something streamy
          //   stream.read();
        } finally {
          stream.close();
        }

#### Stream to a remote file
This will either create a new file or overwrite an existing file's contents

        OutputStream stream = null;
        try {
          stream = fooFighters.getOutputStream();
          // Do something streamy
          //   stream.write(...);
          stream.flush();
        } finally {
          stream.close();
        }
#### List remote files by prefix
        List<MojiFile> files = moji.list("abba-");
        for(MojiFile file : files) {
          // abba-waterloo, abba-voulez-vous, abba-fernado, etc.
        }

Impose a limit on the number of items returned:

        List<MojiFile> files = moji.list("abba-", 10);
        for(MojiFile file : files) {
          // abba-waterloo, abba-voulez-vous, abba-fernado, etc. - maximum of 10
        }

#### Get the locations of a remote file
        File fooFighters = moji.getFile("in-your-honour"); 
        List<URL> paths = fooFighters.getPaths();
        // http://192.168.0.2:7500/dev2/0/000/000/0000000819.fid, http://192.168.0.4:7500/dev3/0/000/000/0000000819.fid, etc

#### Get the attributes of a remote file
Note: this is only supported on more recent versions of MogileFS.

        File fooFighters = moji.getFile("in-your-honour"); 
        MojiFileAttributes attributes = fooFighters.getAttibutes();
        
        String storageClass = attributes.getStorageClass();
        int deviceCount = attributes.getDeviceCount();
        int fid = attributes.getFid();

# Running the integration tests
To run the integration tests, you can make use of ready-to-use [docker image](https://hub.docker.com/r/hrchu/mogile-moji/), or setup the environment manually. For manual setup, you need:

* A test MogileFS tracker and a storage node ([installation instructions](https://github.com/hrchu/mogilefs/blob/wiki/InstallHowTo.md))

MogileFS integration test properties config:

* These properties should be set in `/moji.properties` on the classpath.
* Set your Tracker address with the property:

        moji.tracker.hosts
* Declare your Mogile domain with the property:

        moji.domain
* Declare two storage classes (class assigned here should have devcount=1 if there are multiple storage nodes in the environment) in your Mogile instance and assign them with these properties:

        test.moji.class.a
        test.moji.class.b
* Choose a key prefix to avoid any key clashes with real data (you're using a test instance right?) or other tests. Otherwise we might get unexpected behaviour and file deletions:

        test.moji.key.prefix

# Building
This project uses the [Maven](http://maven.apache.org/) build system.

# Contributing
All contributions are welcome. Please use the [Last.fm codeformatting profile](https://github.com/lastfm/lastfm-oss-config/blob/master/src/main/resources/fm/last/last.fm.eclipse-codeformatter-profile.xml) found in the `lastfm-oss-config` project for formatting your changes.

# Legal
Copyright 2012-2017 [Last.fm](http://www.last.fm/) & The "mogilefs-moji" committers.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
