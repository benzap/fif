# Makefile for generating dpkgs from native-image

EMAIL := benzaporzan@gmail.com

GIT_ROOT_FOLDER := $(shell git rev-parse --show-toplevel)

OS := $(shell uname)
ARCH := $(shell uname -i)

FIF_EXISTS := $(shell which fif >/dev/null && echo "True" || echo "False")

# Use fif to retrieve the project version, since it's faster
ifeq ($(FIF_EXISTS), True)
FIF_VERSION := $(shell fif -e \"$(GIT_ROOT_FOLDER)/project.clj\" read-file first 2 nth println)
else
FIF_VERSION := $(shell lein project-version)
endif

DMAKE_DIR := $(ARCH)/fif-$(FIF_VERSION)

FIF_EXE_NAME := fif-$(FIF_VERSION)

PROJ_FIF_EXE := $(GIT_ROOT_FOLDER)/bin/$(FIF_EXE_NAME)
DPKG_FIF_EXE := $(DMAKE_DIR)/$(FIF_EXE_NAME)

init_dpkg_config: 
	mkdir -p $(ARCH)
	mkdir -p $(DMAKE_DIR)
	cd $(DMAKE_DIR) && dh_make --copyright custom \
				   --copyrightfile $(GIT_ROOT_FOLDER)/LICENSE \
				   --email $(EMAIL) \
				   --single --native --yes
	rm -f $(DMAKE_DIR)/debian/README
	rm -f $(DMAKE_DIR)/debian/*.ex
	sed -i "s/Section:.*/Section: interpreters/" $(DMAKE_DIR)/debian/control
	sed -i "s/Homepage:.*/Homepage: http:\/\/github.com\/benzap\/fif/" $(DMAKE_DIR)/debian/control
	sed -i "s/Architecture:.*/Architecture: any-$(ARCH)/" $(DMAKE_DIR)/debian/control
	sed -i "s/Description:.*/Description: Interpreter and Repl for the fif language/" \
		$(DMAKE_DIR)/debian/control
	sed -i "s/ <insert long description.*//g" $(DMAKE_DIR)/debian/control


$(PROJ_FIF_EXE):
	echo "Building Native Image"
	cd $(GIT_ROOT_FOLDER) && ./build-native.sh


$(DPKG_FIF_EXE): $(PROJ_FIF_EXE)
	cp $(PROJ_FIF_EXE) $(DPKG_FIF_EXE)


clean_dpkg_config:
	rm -rf $(ARCH)/fif-$(FIF_VERSION)


dpkg_config: clean_dpkg_config init_dpkg_config
