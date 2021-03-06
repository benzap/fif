#!/usr/bin/env bash

##
## Configuration
##

# Requires: Defined GRAAL_HOME to point at the root of your GRAALVM folder.
# Requires: Leiningen


# Set GRAAL VM to JAVA_HOME
export JAVA_HOME=$GRAAL_HOME

# Add GRAAL VM to the PATH, should include native-image
export PATH=$GRAAL_HOME/bin:$PATH

# Retrieve the current fif version
echo "Getting Project Version..."
FIF_VERSION=`lein project-version`
echo "Project Version: " $FIF_VERSION
echo ""

echo "Generating Uberjar..."
lein uberjar
echo ""

echo "Building Native Image..."
native-image -jar target/fif-$FIF_VERSION-standalone.jar \
  -H:Name="fif-${FIF_VERSION}" \
  --initialize-at-build-time \
  --no-server \
  --report-unsupported-elements-at-runtime \
  --static \
  --no-fallback
echo ""

echo "Post Configuration..."
mkdir -p bin
chmod 744 fif-${FIF_VERSION}
mv fif-$FIF_VERSION ./bin/
rm -f ./bin/fif
ln -s ./bin/fif-$FIF_VERSION ./bin/fif
echo ""

echo "Built executable can be found at ./bin/fif-${FIF_VERSION}"
