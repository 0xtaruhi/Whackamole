#include <QDebug>

#include "HWDut.h"

HWDut::HWDut(QObject *parent) : QObject(parent) {
  timer_.setInterval(10);
  connect(&timer_, &QTimer::timeout, this, [this]() { onNextFrame(); });
  timer_.start();
}

auto HWDut::onNextRow(int rows) -> void {
  static QVector<QRgb> row(640);
  static int y_pos = 0;

  static uint8_t r_color = 0;

  for (int i = 0; i < rows; ++i) {
    for (int j = 0; j < 640; ++j) {
      row[j] = qRgb(r_color, 0, 0);
    }
    emit receiveRowData(row);
    y_pos++;
    if (y_pos == 525) {
      y_pos = 0;
      emit newFrame();
      r_color++;
    }
  }
}

auto HWDut::onNextFrame() -> void {
  static QVector<QVector<QRgb>> frame(480, QVector<QRgb>(640));

  static uint8_t r_color = 0;

  for (int i = 0; i < 480; ++i) {
    for (int j = 0; j < 640; ++j) {
      frame[i][j] = qRgb(r_color, j, 5 * i);
    }
  }
  emit receiveFrameData(frame);
  emit newFrame();
  r_color++;
}
