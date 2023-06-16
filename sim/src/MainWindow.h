#ifndef MAIN_WINDOW_H
#define MAIN_WINDOW_H

#include <QMainWindow>
#include <QThread>
#include <QLabel>
#include <QPushButton>
#include <qpushbutton.h>

#include "Canvas.h"
#include "HWDut.h"
#include "Keypad.h"

class MainWindow final : public QMainWindow {
  Q_OBJECT

public:
  MainWindow(QWidget *parent = nullptr);
  ~MainWindow();

private slots:
  void onUpdateFrameRate();
  void onNewFrame() { ++frame_count_; }

private:
  Canvas *canvas_;
  Keypad *keypad_;
  HWDut *hw_dut_;
  QPushButton* reset_button_;
  QPushButton* start_button_;

  QLabel* frame_rate_label_;
  QTimer* second_timer_;
  int frame_count_ = 0;

  QThread *hw_dut_thread_;

  auto initMembers() -> void;
  auto initLayout() -> void;
};

#endif // MAIN_WINDOW_H
