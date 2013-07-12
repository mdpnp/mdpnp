#include "form.h"
#include "ui_form.h"

Form::Form(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::Form)
{
    ui->setupUi(this);
}

Form::~Form()
{
    delete ui;
}


void Form::setModel(QAbstractItemModel *model) {
    ui->tableView->setModel(model);
}
//void Form::setModel2(QAbstractItemModel *model) {
//    ui->tableView_2->setModel(model);
//}
