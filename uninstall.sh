#!/usr/bin/env bash

thisDir=$(cd `dirname $0` && pwd)

pushd "$thisDir/server"
echo "============== installing server ==============="
make uninstallArgo
popd
