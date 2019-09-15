#!/bin/bash

mkdir -p $HOME/.gradle
cat <<EOF >> $HOME/.gradle/gradle.properties
alter.upload_key_password=$ALTER_UPLOAD_KEY_PASSWORD
alter.upload_store_password=$ALTER_UPLOAD_STORE_PASSWORD
alter.bugsnag_key=$ALTER_BUGSNAG_KEY
EOF

echo "$ALTER_PUBLISHKEY_JSON_B64" | base64 -d > app/publishkey.json
echo "$ALTER_UPLOAD_KEY_B64" | base64 -d > app/upload.keystore
