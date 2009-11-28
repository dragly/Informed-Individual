#ifndef OPINIONBARCODE_H
#define OPINIONBARCODE_H

#include <QtGui/QMainWindow>

namespace Ui {
    class OpinionBarcode;
}

class OpinionBarcode : public QMainWindow {
    Q_OBJECT
public:
    OpinionBarcode(QWidget *parent = 0);
    ~OpinionBarcode();

protected:
    void changeEvent(QEvent *e);

private:
    Ui::OpinionBarcode *m_ui;
};

#endif // OPINIONBARCODE_H
