#ifndef FORM_H
#define FORM_H

#include <QWidget>
#include <QAbstractItemModel>

namespace Ui {
class Form;
}

class Form : public QWidget
{
    Q_OBJECT
    
public:
    explicit Form(QWidget *parent = 0);
    ~Form();
    void setModel(QAbstractItemModel*);
//    void setModel2(QAbstractItemModel*);
private:
    Ui::Form *ui;
};

#endif // FORM_H
