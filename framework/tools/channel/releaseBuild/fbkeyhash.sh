#!/bin/bash
keytool -exportcert -alias poly -keystore ./poly.keystore | openssl sha1 -binary | openssl base64
keytool -list -v -keystore wt.keystore