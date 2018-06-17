# Main makefile for building distributable packages of fif executable

FIF_EXISTS := $(shell which fif >/dev/null && echo "True" || echo "False")

# Use fif to retrieve the project version, since it's faster
ifeq ($(FIF_EXISTS), True)
FIF_VERSION := $(shell fif -e \"project.clj\" read-file first 2 nth println)
else
FIF_VERSION := $(shell lein project-version)
endif

FIF_EXE_NAME := fif-$(FIF_VERSION)

PROJ_FIF_EXE := bin/$(FIF_EXE_NAME)

# default
all: clean build-native


# Generate `fif` native executable
build-native: $(PROJ_FIF_EXE)


$(PROJ_FIF_EXE):
	./build-native.sh


# Generate .deb Package for native executable
dpkg: $(PROJ_FIF_EXE)
	make -C dist_config/dpkg/ dpkg


# Generate .tar.gz Distribution for native executable
tar: $(PROJ_FIF_EXE)
	make -C dist_config/tar/ tar


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
