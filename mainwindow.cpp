#include "mainwindow.h"
#include "ui_mainwindow.h"
#include <zbar/QZBar.h>
#include <iostream>
#include <QDebug>
#include <QThread>
#include <QtCore/QUrl>
#include <QtGui/QLabel>
#include <QtGui/QProgressBar>
#include <QtGui/QVBoxLayout>
#include <QtNetwork/QNetworkAccessManager>
#include <QtNetwork/QNetworkRequest>
#include <QtNetwork/QNetworkReply>
#include <QtNetwork/QNetworkProxy>
#include <QtXml/QDomDocument>

static const char *REQUEST_URL = "http://jan-ken.appspot.com/myservlet/";
static const char *USER = "user";
static const char *PASSWORD = "asas";

using namespace std;
using namespace zbar;

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent), ui(new Ui::MainWindow)
{
    ui->setupUi(this);
    connect(ui->btn_test,SIGNAL(clicked()),SLOT(slotTest()));
    connect(ui->btn_cancel,SIGNAL(clicked()),SLOT(slotTestDestroy()));
    zbar = new QZBar(this);
    zbar->hide();
    connect(zbar,SIGNAL(decoded(int,QString)),SLOT(slotDecode(int,QString)));
}

MainWindow::~MainWindow()
{
    delete ui;
}

void MainWindow::slotTest() {
    zbar->setVideoDevice("/dev/video0");
    zbar->setVideoEnabled(true);
    if(!zbar->isVisible()) {
        zbar->show();
    }
    ui->verticalLayout->addWidget(zbar);
}

void MainWindow::slotDecode(int type, const QString &text) {
    ui->lbl_test->setText(text);
    slotTestDestroy();
}

void MainWindow::slotTestDestroy() {
    ui->verticalLayout->removeWidget(zbar);
    zbar->hide();
    zbar->setVideoDevice("");
    zbar->setVideoEnabled(false);
}

void MainWindow::slotTestWeb() {
    m_network = new QNetworkAccessManager(this);
    QNetworkRequest request;
    request.setUrl(QUrl(REQUEST_URL));
    QNetworkReply *reply = m_network->get(request);
    QObject::connect(m_network, SIGNAL(finished(QNetworkReply *)),
                                SLOT(slotRequestFinished(QNetworkReply *)));
}
void MainWindow::slotRequestFinished(QNetworkReply *reply)
{
    //m_progress->setValue(0);
    if (reply->error() > 0) {
        QString string = ("Error number = " + reply->errorString());
        cout << string.toStdString() << endl;

    }
    else {
        QByteArray data = reply->readAll();
        QDomDocument doc;
        doc.setContent(data);
        QDomNodeList nodes = doc.elementsByTagName("INFO");

        if (nodes.size() > 0) {
            cout << (nodes.at(0).toElement().attribute("CASH")).toStdString() << endl;
        }
    }
}
