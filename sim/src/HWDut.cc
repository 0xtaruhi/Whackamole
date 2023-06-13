#include <QDebug>

#include "HWDut.h"

HWDut::HWDut(QObject *parent) : QObject(parent) {
  image1_ = QImage(640, 480, QImage::Format_RGB888);
  image2_ = QImage(640, 480, QImage::Format_RGB888);

  timer_.setInterval(1000 / 60);
  connect(&timer_, &QTimer::timeout, this, &HWDut::onEmitNewFrame);
  timer_.start();
  cur_write_image_ = &image1_;
}

auto HWDut::onEmitNewFrame() -> void {
  static int count = 0;
  for (int i = 0; i < 640; ++i) {
    for (int j = 0; j < 480; ++j) {
      curWriteImage()->setPixel(
          i, j,
          qRgb((i + count) % 256, (j + count) % 256, (i + j + count) % 256));
    }
  }
  emit newFrame(curWriteImage());
  count++;
  swapImages();
}
