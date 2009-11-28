/**
 * maemo-barcode - barcode detection/recognition application
 *
 * Copyright (c) 2008 Simon Pickering <S.G.Pickering@bath.ac.uk>
 *
 * Various parts of barcode recognition and GStreamer manipulation code written by:
 *       Timothy Terriberry
 *       Adam Harwell
 *       Jonas Hurrelmann
 *
 * Original GStreamer code based on the maemo-examples package
 * Copyright (c) 2007-2008 Nokia Corporation. All rights reserved.
 * Copyright (c) 2006 INdT.
 * @author Talita Menezes <talita.menezes@indt.org.br>
 * @author Cidorvan Leite <cidorvan.leite@indt.org.br>
 * @author Jami Pekkanen <jami.pekkanen@nokia.com>
 */

#ifndef CAMERA_N900_H
#define CAMERA_N900_H

#include <gtk/gtk.h>
#include <hildon/hildon-banner.h>
#include <hildon/hildon-program.h>
#include <sqlite3.h>


gboolean buffer_probe_callback (GstElement * image_sink, GstBuffer * buffer, GstPad * pad);
gboolean bus_callback (GstBus * bus, GstMessage * message);
gboolean initialize_pipeline (int *argc, char ***argv);
void destroy_pipeline (GtkWidget * widget);
gboolean expose_cb (GtkWidget * widget, GdkEventExpose * event, gpointer data);
gboolean on_expose(GtkWidget * widget, GdkEventExpose * event);
void do_refocus(GtkWidget *widget, GdkEventButton *event, gpointer user_data);

#endif
