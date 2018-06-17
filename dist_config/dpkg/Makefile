# Makefile for generating dpkgs for the native-image compiled 'fif' executable
# Requires: leiningen
# Optional: fif

EMAIL := benzaporzan@gmail.com

GIT_ROOT_DIR := $(shell git rev-parse --show-toplevel)
PROJECT_ROOT_DIR := $(GIT_ROOT_DIR)
DIST_DIR := $(PROJECT_ROOT_DIR)/dist

OS := $(shell uname)
#ARCH := $(shell uname -i)
ARCH := $(shell dpkg --print-architecture)

FIF_EXISTS := $(shell which fif >/dev/null && echo "True" || echo "False")

# Use fif to retrieve the project version, since it's faster
ifeq ($(FIF_EXISTS), True)
FIF_VERSION := $(shell fif -e \"$(PROJECT_ROOT_DIR)/project.clj\" read-file first 2 nth println)
else
FIF_VERSION := $(shell lein project-version)
endif

FIF_EXE_NAME := fif-$(FIF_VERSION)


DEB_NAME := $(FIF_EXE_NAME)-$(ARCH)
DEB_VERSION := 1

DMAKE_DIR := $(DIST_DIR)/dpkg/$(DEB_NAME)
PROJ_FIF_EXE := $(PROJECT_ROOT_DIR)/bin/$(FIF_EXE_NAME)
DPKG_FIF_EXE := $(DMAKE_DIR)/usr/bin/$(FIF_EXE_NAME)


# default
all: dpkg


init:
	mkdir -p $(DMAKE_DIR)/DEBIAN
	touch $(DMAKE_DIR)/DEBIAN/control
	echo "Package: fif" >> $(DMAKE_DIR)/DEBIAN/control
	echo "Version: $(FIF_VERSION)-$(DEB_VERSION)" >> $(DMAKE_DIR)/DEBIAN/control
	echo "Section: interpreters" >> $(DMAKE_DIR)/DEBIAN/control
	echo "Priority: optional" >> $(DMAKE_DIR)/DEBIAN/control
	echo "Homepage: http://github.com/benzap/fif" >> $(DMAKE_DIR)/DEBIAN/control
	echo "Architecture: $(ARCH)" >> $(DMAKE_DIR)/DEBIAN/control
	echo "Maintainer: Benjamin Zaporzan <benzaporzan@gmail.com>" >> $(DMAKE_DIR)/DEBIAN/control
	echo "Description: Interpreter and Repl for the fif language" >> $(DMAKE_DIR)/DEBIAN/control

	cp prerm.template $(DMAKE_DIR)/DEBIAN/prerm
	chmod 775 $(DMAKE_DIR)/DEBIAN/prerm
	sed -i "s/FIF_EXE_NAME/$(FIF_EXE_NAME)/g" $(DMAKE_DIR)/DEBIAN/prerm

	cp postinst.template $(DMAKE_DIR)/DEBIAN/postinst
	chmod 775 $(DMAKE_DIR)/DEBIAN/postinst
	sed -i "s/FIF_EXE_NAME/$(FIF_EXE_NAME)/g" $(DMAKE_DIR)/DEBIAN/postinst

	cp $(PROJECT_ROOT_DIR)/LICENSE $(DMAKE_DIR)/DEBIAN/license

$(PROJ_FIF_EXE):
	echo "Building Native Image..."
	cd $(PROJECT_ROOT_DIR) && ./build-native.sh


$(DPKG_FIF_EXE): $(PROJ_FIF_EXE)
	mkdir -p $(DMAKE_DIR)/usr/bin
	cp $(PROJ_FIF_EXE) $(DPKG_FIF_EXE)
	chmod 755 $(DPKG_FIF_EXE)


prepare: $(DPKG_FIF_EXE)
	find $(DMAKE_DIR) -type d | xargs chmod 755


build:
	dpkg-deb --build $(DMAKE_DIR)


clean:
	rm -rf $(DMAKE_DIR)


dpkg: clean init prepare build

