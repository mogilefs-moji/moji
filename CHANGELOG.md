# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [3.0.0] - 2017-07-13
### Added
- Checksum added to MojiFileAttributes (https://github.com/mogilefs-moji/moji/pull/34).

### Changed
- Made ManagedTrackerHost timer a daemon thread - (https://github.com/mogilefs-moji/moji/pull/35).
- Updated all license headers.

## [2.0.0] - 2016-11-16
### Added
- Log more HTTP info when an error occurs closing the file upload stream (https://github.com/mogilefs-moji/moji/pull/23).

### Changed
- Changed fid from an int to a long (https://github.com/mogilefs-moji/moji/pull/22).

## [1.4.2] - 2016-02-03
### Changed
- File size defaults to zero if no content-length header is returned (https://github.com/mogilefs-moji/moji/pull/16).
- Try multiple remote destinations when performing tracker operations (instead of only the first one) (https://github.com/mogilefs-moji/moji/pull/19).

## [1.4.1] - 2015-05-29
### Changed
- Fix to prevent ConcurrentModificationException (thanks @jyukutyo).
- Fixed int overflow for files >2GB (thanks @hrchu).

## [1.4.0] - 2013-05-01
### Added
- Added @kazabubu/@orgerson's device status implementation.

## [1.3.0] - 2013-01-28
### Changed
- Use fm.last.commons:lastcommons-lang:3.0.0 for Clock dependency.
- moved to Apache Commons pool commons-pool:commons-pool:1.6 (thanks @jeeZzzup).
- Fix bug in MultiHostTrackerPool.getTracker() when host is unreachable it always tries the same host (thanks @igieon).
- setTrackerSoTimeout -> setTrackerReadTimeout in SpringMojiBean.
- fixed LocalFileSystemMoji.list() to handle storageclass properly.
- Migrated project to lastfm-oss-parent.

## [1.2.3] - 2012-06-07
### Changed
- Integrated igieon's multitracker fixes.

## [1.2.2] - 2012-05-14
### Changed
- Copyright messages.
- Updated POM to conform to Maven central requirements.
- Changed groupId to 'fm.last'.
- Using the Sonatype OSS parent POM for Maven central deployment.

## [1.2.1] - 2012-05-09
### Changed
- Lifecycle of SpringMojiBean was ill-conceived - this has been resolved.

## [1.2.0] - 2012-05-04
### Added
- Added ability to configure network timeouts.

### Changed
- SpringMojiBean must now be initialised by the Spring context (this used to occur in the constructor). 
- Merged cclien's content length fix for file sizes > Integer.MAX_LENGTH.

## [1.1.2] - 2012-03-01
### Changed
- Fixed issue with LocalMojiFile where internal file was not updated to the new destination.
- Support for storage backend like Apache that responses a "201 Created" instead of "200 OK".

## [1.1.1] - 2012-01-27
### Changed
- Now supports file_info command via MojiFile.getAttributes();
- Default local moji file now uses base64 encoding for filenames to avoid escaping issues.
- Default local moji file supports modification of storage class.

## [1.1.0] - 2012-01-20
### Changed
- Using mogdelete, moglistkeys in favour of mogtool.
- Initial release.
