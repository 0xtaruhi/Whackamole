#ifndef KEY_PAD_H
#define KEY_PAD_H

#include <QWidget>

class Key final : public QWidget {
  Q_OBJECT

public:
  Key(QWidget *parent = nullptr);

signals:
  void clicked();
  void released();

protected:
  void paintEvent(QPaintEvent *event) override;
  void mousePressEvent(QMouseEvent *event) override;
  void mouseReleaseEvent(QMouseEvent *event) override;

private:
  bool is_pressed_ = false;
};

class Keypad final : public QWidget {
  Q_OBJECT

public:
  Keypad(int row_num, int col_num, QWidget *parent = nullptr);
  ~Keypad() = default;

signals:
  void clicked(QPair<int, int> position);
  void released(QPair<int, int> position);

private:
  QSize keypad_size_;

  QHash<Key *, QPair<int, int>> key_positions_;
  QVector<Key *> keys_;
};

#endif // KEY_PAD_H
