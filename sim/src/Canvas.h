#ifndef CANVAS_H
#define CANVAS_H

#include <QGraphicsScene>
#include <QGraphicsView>
#include <QImage>
#include <QRgb>
#include <QTimer>
#include <QVector>
#include <QWidget>
#include <cstdint>

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

  /// @brief Set the scale of the canvas.
  /// @param scale The scale of the canvas.
  /// @note The scale must be a positive integer. The scale is applied to both
  /// the width and height of the canvas.
  auto setScale(uint16_t scale) noexcept -> void;

  /// @brief Whether the cur_x_ and cur_y_ are within the display area.
  /// @return True if the cur_x_ and cur_y_ are within the display area.
  auto inDisplayArea() -> bool { return cur_x_ < width_ && cur_y_ < height_; }

public slots:
  /// @brief Update the image displayed on the canvas. It receives a pointer
  /// to the image to be displayed. The image is from the HWDut class.
  /// @param image The image to be displayed.
  /// @note The image will not be displayed immediately, but will be displayed
  /// after the timer times out. The image will not be changed until the
  /// pointer is updated.
  auto onUpdateImage(QImage *image) -> void { cur_disp_image_ = image; }

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
  QImage *cur_disp_image_;

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
