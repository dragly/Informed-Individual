#include "opinionbarcode.h"
#include "ui_opinionbarcode.h"

OpinionBarcode::OpinionBarcode(QWidget *parent) :
    QMainWindow(parent),
    m_ui(new Ui::OpinionBarcode)
{
    m_ui->setupUi(this);
}

OpinionBarcode::~OpinionBarcode()
{
    delete m_ui;
}

void OpinionBarcode::changeEvent(QEvent *e)
{
    QMainWindow::changeEvent(e);
    switch (e->type()) {
    case QEvent::LanguageChange:
        m_ui->retranslateUi(this);
        break;
    default:
        break;
    }
}
