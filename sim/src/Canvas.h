#ifndef CANVAS_H
#define CANVAS_H

#include <QGraphicsScene>
#include <QGraphicsView>
#include <QImage>
#include <QTimer>
#include <QVector>
#include <QWidget>
#include <cstdint>
#include <qgraphicsscene.h>
#include <qrgb.h>

#include "GraphicsSignals.h"

class Canvas : public QGraphicsView {
  Q_OBJECT
  Q_DISABLE_COPY(Canvas)

public:
  Canvas(QWidget *parent = nullptr);
  Canvas(uint16_t width, uint16_t height, QWidget *parent = nullptr);
  Canvas(uint16_t width, uint16_t height, uint16_t scale,
         QWidget *parent = nullptr);

  ~Canvas() = default;

  void setCanvasSize(uint16_t width, uint16_t height) noexcept;

  auto width() const noexcept -> uint16_t { return width_; }
  auto height() const noexcept -> uint16_t { return height_; }

  auto setScale(uint16_t scale) noexcept -> void;

  auto currentWriteBuffer() noexcept -> QVector<QVector<QRgb>> * {
    return current_buffer_;
  }

  auto currentDispBuffer() noexcept -> QVector<QVector<QRgb>> * {
    return current_buffer_ == &buffer1_ ? &buffer2_ : &buffer1_;
  }

  /// @brief Receive data from the hardware design and update the write buffer.
  /// After receiving data, the cur_x_ and cur_y_ are updated accordingly.
  [[deprecated("Use `receiveFrameData() instead")]] auto
  receivePixelData(graphics::VgaSignals vga) -> void;

  [[deprecated("Use `receiveFrameData() instead")]] auto
  receiveRowData(const QVector<QRgb> &vga_row) -> void;

  auto receiveFrameData(const QVector<QVector<QRgb>> &vga_frame) -> void;

  auto inDisplayArea() -> bool { return cur_x_ < width_ && cur_y_ < height_; }

public slots:

  [[deprecated("Use `receiveFrameData() instead")]] void
  onReceiveRowData(const QVector<QRgb> &vga_row) {
    receiveRowData(vga_row);
  }
  
  void onReceiveFrameData(const QVector<QVector<QRgb>> &vga_frame) {
    receiveFrameData(vga_frame);
  }

  void onNewFrame() {
    moveToFrameStart();
    swapBuffers();
  }

private:
  uint16_t width_;
  uint16_t height_;
  uint16_t scale_ = 1;

  uint16_t cur_x_ = 0;
  uint16_t cur_y_ = 0;

  QPixmap pixmap_;
  QTimer *timer_;
  QGraphicsScene *scene_;
  QGraphicsPixmapItem *item_;

  QVector<QVector<QRgb>> buffer1_;
  QVector<QVector<QRgb>> buffer2_;

  QVector<QVector<QRgb>> *current_buffer_;

  void resetBuffer();

  auto swapBuffers() noexcept -> void {
    current_buffer_ = current_buffer_ == &buffer1_ ? &buffer2_ : &buffer1_;
  }

  auto moveToNextPixel() noexcept -> void { cur_x_++; }
  auto moveToNextRow() noexcept -> void {
    cur_y_++;
    cur_x_ = 0;
  }
  auto moveToFrameStart() noexcept -> void { cur_x_ = cur_y_ = 0; }

private slots:
  auto onUpdateImageFromBuffer() -> void;
};

#endif // CANVAS_H
