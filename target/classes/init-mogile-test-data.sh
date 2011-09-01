#!/bin/bash
TRACKERS=localhost:7001
DOMAIN=testdomain
KEY_PREFIX=lcmfi-test-
CLASS=testclass1
IFS=$'\n';

function clear_test_data {
  KEYS=`mogtool listkey $KEY_PREFIX --trackers=$TRACKERS --domain=$DOMAIN`

  for key in $KEYS
  do
    if [[ $key == *" files found"* ]]
    then
      break;
    fi
    echo "Deleting: '$key' ..."
    mogtool delete $key --trackers=$TRACKERS --domain=$DOMAIN
  done;
}

function upload_new_random_file {
  head /dev/urandom | uuencode -m - | mogupload --trackers=$TRACKERS --domain=$DOMAIN --key="$KEY_PREFIX$1" --class=$CLASS --file="-"
  echo "Created mogile file: '$KEY_PREFIX$1'"
}
  
function upload_file {
  mogupload --trackers=$TRACKERS --domain=$DOMAIN --key="$KEY_PREFIX$1" --class=$CLASS --file="$2"
  echo "Created mogile file: '$KEY_PREFIX$1'"
}

if [ -z "$KEY_PREFIX" ]; then
    echo "You MUST declare a key prefix"
    exit 1;
fi

clear_test_data
upload_new_random_file overwriteThenReadBack
upload_new_random_file exists
upload_new_random_file notExistsAfterDelete
upload_new_random_file rename
upload_new_random_file renameExistingKey1
upload_new_random_file renameExistingKey2
upload_new_random_file updateStorageClass
upload_new_random_file updateStorageClassToUnknown
upload_new_random_file list1
upload_new_random_file list2
upload_new_random_file list3
upload_new_random_file getPaths

upload_file fileOfKnownSize data/fileOfKnownSize.dat
upload_file mogileFileCopyToFile data/mogileFileCopyToFile.dat
exit 0
