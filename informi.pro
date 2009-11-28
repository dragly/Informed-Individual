# -------------------------------------------------
# Project created by QtCreator 2009-11-27T23:14:43
# -------------------------------------------------
QT += network \
    xml
TARGET = informi
TEMPLATE = app
CONFIG += link_pkgconfig
PKGCONFIG = zbar-qt \
    gstreamer-0.10
SOURCES += main.cpp \
    mainwindow.cpp \
    opinionbarcode.cpp
HEADERS += mainwindow.h \
    camera_n900.h \
    opinionbarcode.h
FORMS += mainwindow.ui \
    opinionbarcode.ui
OTHER_FILES += mainwindow.backup
