#!/bin/bash
set -e
cd `dirname $0`

rm -rf ../target/Documentation
mkdir -p ../target/Documentation

0install run http://repo.roscidus.com/devel/doxygen
