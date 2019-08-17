# Main makefile for building distributable packages of native fif
# executable
#
# For Debian-based systems:
#
# $ make deb
#
# For RPM-based systems:
#
# $ make rpm
#
# For a compressed archive:
#
# $ make tar
#
# To Install Locally from the repository:
#
# $ make install
#
# Configuration:
#
# Requires: leiningen
#
# Requires: GraalVM with GRAAL_HOME environment variable set to the
# root of the graal folder (might work if you just have native-image on the path)
.PHONY: all build-native clean distclean

FIF_VERSION := $(shell lein project-version)
FIF_EXE_NAME := fif-$(FIF_VERSION)
PROJ_FIF_EXE := bin/$(FIF_EXE_NAME)


# default
all: clean build-native


# Generate fif native executable
build-native:
	sh ./build-native.sh

# Generate deb Package for native executable
# Note: Tested on Ubuntu 17.10
dpkg: $(PROJ_FIF_EXE)
	make -C dist_config/dpkg/


# Generate tar.gz Distribution for native executable
tar: $(PROJ_FIF_EXE)
	make -C dist_config/tar/


# Generate rpm Package for native executable
# Note: Tested on Fedora 28
rpm: $(PROJ_FIF_EXE)
	make -C dist_config/rpmpkg/


# Install Native Executable
# Note: Tested in linux
install: $(PROJ_FIF_EXE)
	cp $(PROJ_FIF_EXE) /usr/bin/$(FIF_EXE_NAME)
	chmod 755 /usr/bin/$(FIF_EXE_NAME)
	rm -f /usr/bin/fif
	ln -s /usr/bin/$(FIF_EXE_NAME) /usr/bin/fif


clean:
	rm -f $(PROJ_FIF_EXE)
	rm -rf dist


distclean:
	rm -f /usr/bin/$(FIF_EXE_NAME)
	rm -f /usr/bin/fif


