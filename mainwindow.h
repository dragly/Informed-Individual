#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QtGui/QMainWindow>
#include <zbar/QZBar.h>

class QLabel;
class QProgressBar;
class QNetworkAccessManager;
class QNetworkReply;

using namespace zbar;

namespace Ui
{
    class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(QWidget *parent = 0);
    ~MainWindow();

private:
    Ui::MainWindow *ui;
    QNetworkAccessManager *m_network;
    QZBar *zbar;

private slots:
    void slotTest();
    void slotTestWeb();
    void slotDecode(int type, const QString &text);
    void slotTestDestroy();
    void slotRequestFinished(QNetworkReply *reply);
};

#endif // MAINWINDOW_H
