#include "Canvas.h"

#include <QGraphicsPixmapItem>
#include <QPainter>
#include <qdebug.h>
#include <qrgb.h>
#include <qtpreprocessorsupport.h>

Canvas::Canvas(QWidget *parent) : Canvas(640, 480, parent) {}

Canvas::Canvas(uint16_t width, uint16_t height, QWidget *parent)
    : Canvas(width, height, 1, parent) {}

Canvas::Canvas(uint16_t width, uint16_t height, uint16_t scale, QWidget *parent)
    : QGraphicsView(parent), width_(width), height_(height), scale_(scale) {
  setCanvasSize(width, height);
  resetBuffer();

  timer_ = new QTimer(this);
  timer_->setInterval(1000 / 60);
  connect(timer_, &QTimer::timeout, this, &Canvas::onUpdateImageFromBuffer);
  timer_->start();

  scene_ = new QGraphicsScene(this);
  scene_->setSceneRect(0, 0, width_, height_);
  scene_->setBackgroundBrush(Qt::black);

  item_ = scene_->addPixmap(pixmap_);

  this->setScene(scene_);
  this->setHorizontalScrollBarPolicy(Qt::ScrollBarAlwaysOff);
  this->setVerticalScrollBarPolicy(Qt::ScrollBarAlwaysOff);
}

void Canvas::setCanvasSize(uint16_t width, uint16_t height) noexcept {
  width_ = width;
  height_ = height;

  setFixedSize(width_ * scale_, height_ * scale_);
}

auto Canvas::setScale(uint16_t scale) noexcept -> void {
  scale_ = scale;
  setCanvasSize(width_, height_);
}

void Canvas::resetBuffer() {
  buffer1_.resize(height_);
  buffer2_.resize(height_);

  for (auto &row : buffer1_) {
    row.fill(qRgb(0, 0, 0), width_);
  }
  for (auto &row : buffer2_) {
    row.fill(qRgb(0, 0, 0), width_);
  }

  current_buffer_ = &buffer1_;
}

void Canvas::receiveRowData(const QVector<QRgb> &vga_row) {
  auto &write_buf = *currentWriteBuffer();
  if (Q_LIKELY(inDisplayArea())) {
    for (uint16_t x = 0; x < width_; ++x) {
      write_buf[cur_y_][x] = vga_row[x];
    }
  }
  moveToNextRow();
}

void Canvas::receiveFrameData(const QVector<QVector<QRgb>> &vga_frame) {
  auto &write_buf = *currentWriteBuffer();
  for (uint16_t y = 0; y < height_; ++y) {
    for (uint16_t x = 0; x < width_; ++x) {
      write_buf[y][x] = vga_frame[y][x];
    }
  }
}

void Canvas::onUpdateImageFromBuffer() {
  auto &disp_buf = *currentDispBuffer();

  QImage image(width_, height_, QImage::Format_RGB32);
  for (uint16_t y = 0; y < height_; ++y) {
    for (uint16_t x = 0; x < width_; ++x) {
      image.setPixel(x, y, disp_buf[y][x]);
    }
  }

  pixmap_ = QPixmap::fromImage(image);
  item_->setPixmap(pixmap_);
}
