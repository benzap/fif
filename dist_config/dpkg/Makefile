# Makefile for generating dpkgs for the native-image compiled 'fif' executable

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

DMAKE_DIR := $(DIST_DIR)/dpkg/$(ARCH)/$(FIF_EXE_NAME)
PROJ_FIF_EXE := $(PROJECT_ROOT_DIR)/bin/$(FIF_EXE_NAME)
DPKG_FIF_EXE := $(DMAKE_DIR)/$(FIF_EXE_NAME)

init:
	mkdir -p $(DMAKE_DIR)
	cd $(DMAKE_DIR) && dh_make --copyright custom \
				   --copyrightfile $(PROJECT_ROOT_DIR)/LICENSE \
				   --email $(EMAIL) \
				   --single --native --yes
	rm -f $(DMAKE_DIR)/debian/README
	rm -f $(DMAKE_DIR)/debian/*.ex
	cp dbuild.mk.template $(DMAKE_DIR)/Makefile
	sed -i "s/FIF_NAME_EXE/$(FIF_EXE_NAME)/g" $(DMAKE_DIR)/Makefile

	sed -i "s/Section:.*/Section: interpreters/" $(DMAKE_DIR)/debian/control
	sed -i "s/Homepage:.*/Homepage: http:\/\/github.com\/benzap\/fif/" $(DMAKE_DIR)/debian/control
	sed -i "s/Architecture:.*/Architecture: any-$(ARCH)/" $(DMAKE_DIR)/debian/control
	sed -i "s/Description:.*/Description: Interpreter and Repl for the fif language/" \
		$(DMAKE_DIR)/debian/control
	sed -i "s/ <insert long description.*//g" $(DMAKE_DIR)/debian/control


$(PROJ_FIF_EXE):
	echo "Building Native Image..."
	cd $(PROJECT_ROOT_DIR) && ./build-native.sh


$(DPKG_FIF_EXE): $(PROJ_FIF_EXE)
	cp $(PROJ_FIF_EXE) $(DPKG_FIF_EXE)


prepare: $(DPKG_FIF_EXE)





clean:
	rm -rf $(DMAKE_DIR)


dpkg: clean init prepare

