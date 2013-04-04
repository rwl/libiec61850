LIBIEC_HOME=..

PROJECT_BINARY_NAME = main
PROJECT_SOURCES = main.c
PROJECT_SOURCES += static_model.c

UNAME := $(shell uname)

MIPSEL_TOOLCHAIN_PREFIX=mipsel-openwrt-linux-
ARM_TOOLCHAIN_PREFIX=arm-linux-gnueabihf-
MINGW_TOOLCHAIN_PREFIX=i586-mingw32msvc-

ifndef TARGET
ifeq ($(UNAME), Linux)
TARGET=POSIX
else ifeq ($(findstring MINGW,$(UNAME)), MINGW)
TARGET=WIN32
endif
endif

ifeq ($(TARGET), WIN32)
ifeq ($(UNAME), Linux)
TOOLCHAIN_PREFIX=$(MINGW_TOOLCHAIN_PREFIX)
else
TOOLCHAIN_PREFIX=
endif
endif

ifeq ($(TARGET), LINUX-MIPSEL)
TOOLCHAIN_PREFIX=$(MIPSEL_TOOLCHAIN_PREFIX)
endif

ifeq ($(TARGET), LINUX-ARM)
TOOLCHAIN_PREFIX=$(ARM_TOOLCHAIN_PREFIX)
endif

ifeq ($(TARGET), WIN32)
HAL_IMPL = WIN32
LIB_OBJS_DIR = $(LIBIEC_HOME)/build_win32
CFLAGS=-g -DWIN32
LDLIBS=-lws2_32
else
HAL_IMPL = POSIX

ifeq ($(TARGET), LINUX-MIPSEL)
LIB_OBJS_DIR = $(LIBIEC_HOME)/build-mipsel
else ifeq ($(TARGET), LINUX-ARM)
LIB_OBJS_DIR = $(LIBIEC_HOME)/build-arm
else
LIB_OBJS_DIR = $(LIBIEC_HOME)/build
endif

CFLAGS=-g
LDLIBS=-lpthread
endif

CC=$(TOOLCHAIN_PREFIX)gcc
CPP=$(TOOLCHAIN_PREFIX)g++
AR=$(TOOLCHAIN_PREFIX)ar
RANLIB=$(TOOLCHAIN_PREFIX)ranlib

ifeq ($(TARGET), WIN32)
PROJECT_BINARY_NAME := $(PROJECT_BINARY_NAME).exe
endif


LIB_NAME = $(LIB_OBJS_DIR)/libiec61850.a

INCLUDES = -I$(LIBIEC_HOME)/inc
INCLUDES += -I$(LIBIEC_HOME)/src/common
INCLUDES += -I$(LIBIEC_HOME)/src/mms/iso_presentation
INCLUDES += -I$(LIBIEC_HOME)/src/mms/iso_session
INCLUDES += -I$(LIBIEC_HOME)/src/mms/iso_cotp 
INCLUDES += -I$(LIBIEC_HOME)/src/mms/iso_acse
INCLUDES += -I$(LIBIEC_HOME)/src/mms/iso_mms/common
INCLUDES += -I$(LIBIEC_HOME)/src/mms/iso_mms/client
INCLUDES += -I$(LIBIEC_HOME)/src/mms/iso_mms/server
INCLUDES += -I$(LIBIEC_HOME)/src/mms/iso_client
INCLUDES += -I$(LIBIEC_HOME)/src/mms/iso_server
INCLUDES += -I$(LIBIEC_HOME)/src/mms/asn1
INCLUDES += -I$(LIBIEC_HOME)/src/mms_mapping
INCLUDES += -I$(LIBIEC_HOME)/src/model
INCLUDES += -I$(LIBIEC_HOME)/src/api
INCLUDES += -I$(LIBIEC_HOME)/src/hal
INCLUDES += -I$(LIBIEC_HOME)/src/hal/thread
INCLUDES += -I$(LIBIEC_HOME)/src/hal/socket

all:	$(PROJECT_BINARY_NAME)


$(PROJECT_BINARY_NAME):	$(PROJECT_SOURCES)
	$(CC) $(CFLAGS) -o $(PROJECT_BINARY_NAME) $(PROJECT_SOURCES) $(INCLUDES) $(LIB_NAME) $(LDLIBS)

clean:
	rm -f $(PROJECT_BINARY_NAME)

