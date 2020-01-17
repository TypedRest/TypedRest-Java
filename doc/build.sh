#!/bin/bash
set -e
cd `dirname $0`

rm -rf ../target/doc
mkdir -p ../target/doc

0install run https://apps.0install.net/devel/doxygen.xml

cp .nojekyll CNAME ../target/doc/
