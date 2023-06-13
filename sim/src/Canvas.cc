#include "Canvas.h"

#include <QGraphicsPixmapItem>
#include <QPainter>
#include <cstddef>
#include <qdebug.h>
#include <qrgb.h>
#include <qtpreprocessorsupport.h>

Canvas::Canvas(QWidget *parent) : Canvas(640, 480, parent) {}

Canvas::Canvas(uint16_t width, uint16_t height, QWidget *parent)
    : Canvas(width, height, 1, parent) {}

Canvas::Canvas(uint16_t width, uint16_t height, uint16_t scale, QWidget *parent)
    : QGraphicsView(parent), width_(width), height_(height), scale_(scale) {
  setCanvasSize(width, height);

  cur_disp_image_ = nullptr;

  scene_ = new QGraphicsScene(this);
  scene_->setSceneRect(0, 0, width_, height_);
  scene_->setBackgroundBrush(Qt::black);

  item_ = scene_->addPixmap(pixmap_);

  this->setScene(scene_);
  this->setHorizontalScrollBarPolicy(Qt::ScrollBarAlwaysOff);
  this->setVerticalScrollBarPolicy(Qt::ScrollBarAlwaysOff);

  timer_ = new QTimer(this);
  timer_->setInterval(1000 / 60);
  connect(timer_, &QTimer::timeout, this, &Canvas::onUpdateImageFromBuffer);
  timer_->start();

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

void Canvas::onUpdateImageFromBuffer() {
  if (cur_disp_image_) [[likely]] {
    pixmap_ = QPixmap::fromImage(*cur_disp_image_);
  }
  item_->setPixmap(pixmap_);
}
